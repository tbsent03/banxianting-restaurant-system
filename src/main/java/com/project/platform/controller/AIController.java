package com.project.platform.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.project.platform.entity.Dish;
import com.project.platform.entity.Order;
import com.project.platform.entity.Reservation;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.DishMapper;
import com.project.platform.mapper.OrderMapper;
import com.project.platform.mapper.ReservationMapper;
import com.project.platform.mapper.StatMapper;
import com.project.platform.service.StatService;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
public class AIController {

    // 把这里换成你刚才复制的 API Key！
    private static final String API_KEY = "YOUR_DEEPSEEK_API_KEY";
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    @Resource
    private DishMapper dishMapper; // 用于查数据库里的菜品
    @Resource
    private StatMapper statMapper;
    @Resource
    private StatService statService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private ReservationMapper reservationMapper;

    @PostMapping("/chat")
    public ResponseVO<String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String role = request.get("role");
        String userIdStr = request.get("userId");
        Integer userId = userIdStr != null ? Integer.parseInt(userIdStr) : null;

        String systemPrompt = "";

        // 1. 根据角色构造不同的 Prompt
        if ("USER".equals(role)) {
            String contextData = "";

            // 意图 1：查订单 (使用现有的 queryPage 方法)
            if (userMessage.contains("订单") || userMessage.contains("菜做好") || userMessage.contains("进度")) {
                if (userId != null) {
                    // 构造查询条件，放入 userId
                    Map<String, Object> queryMap = new HashMap<>();
                    queryMap.put("userId", userId);
                    List<Order> orders = orderMapper.queryPage(0, 3, queryMap);

                    if (orders == null || orders.isEmpty()) {
                        contextData = "系统未查询到该用户有任何订单记录。";
                    } else {
                        contextData = "该用户最近的订单如下：";
                        contextData += orders.stream().map(o ->
                                "订单号:" + o.getOrderNo() +
                                        ", 状态:" + o.getStatus() +
                                        ", 金额:￥" + o.getTotalAmount()
                        ).collect(Collectors.joining(" | "));
                    }
                } else {
                    contextData = "用户未登录，无法查询订单。";
                }
            }
            // 意图 2：查订位
            else if (userMessage.contains("订位") || userMessage.contains("座位") || userMessage.contains("几号桌")) {
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("userId", userId);
                List<Reservation> reservations = reservationMapper.queryPage(0, 3, queryMap);
                if (reservations == null || reservations.isEmpty()) {
                    contextData = "系统未查询到该用户有任何预订记录。";
                } else {
                    contextData = reservations.stream().map(r ->
                        "日期:" + r.getReserveDate() + ", 时间:" + r.getReserveTime() + ", 状态:" + r.getStatus()
                    ).collect(Collectors.joining(" | "));
                }
            }
            // 意图 3：常规点餐推荐
            else {
                List<Dish> dishes = dishMapper.selectAll();
                String menuStr = dishes.stream()
                        .map(d -> d.getName() + "(￥" + d.getPrice() + ")").collect(Collectors.joining("; "));
                contextData = "今日菜单如下：" + menuStr;
            }
            systemPrompt = "你是高级餐厅'半闲庭'的AI点餐管家。请根据以下参考数据回答顾客。参考数据：[" + contextData + "]。\n" +
                    "要求：如果顾客问订单/订位，直接播报状态；如果问推荐，请根据菜单推荐。使用优雅、简练的语言。";
        } else {
            Integer todayRev = statMapper.getTodayRevenue() != null ? statMapper.getTodayRevenue().intValue() : 0;
            Integer yesterdayRev = statMapper.getYesterdayRevenue() != null ? statMapper.getYesterdayRevenue().intValue() : 0;
//            Integer todayOrders = statMapper.getTodayOrderCount();
//            Integer pending = statMapper.getPendingOrderCount();

            // 查一下热销榜
            List<Map<String, Object>> hotDishes = statMapper.getHotDishes();
            String hotStr = hotDishes.stream()
                    .map(m -> m.get("dish_name") + "卖出" + m.get("sales_num") + "份")
                    .collect(Collectors.joining(", "));
            if (hotStr.isEmpty()) hotStr = "暂无热销数据";

            systemPrompt = "你是'半闲庭'的智能经营参谋。当前系统数据库中的真实实时数据如下：\n"
                    + "今日营业额: " + todayRev + "元\n"
                    + "昨日营业额: " + yesterdayRev + "元\n"
                    + "热销Top5菜品: " + hotStr + "\n"
                    + "【重要指令】如果营业额为0元，说明该时段确实没有产生收益，这也是一种真实数据，请直接如实对比汇报，绝对不要回答“没有取到数据”或“无法对比”。请用专业、简练的商业语调回答老板的问题，并给出合理的分析建议。";
        }

        // 2. 构造发给大模型的 JSON 请求体
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject().set("role", "system").set("content", systemPrompt));
        messages.add(new JSONObject().set("role", "user").set("content", userMessage));

        JSONObject bodyJson = new JSONObject();
        bodyJson.set("model", "deepseek-chat"); // 使用的模型
        bodyJson.set("messages", messages);

        try {
            // 3. 发送 HTTP 请求给 DeepSeek
            String responseStr = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(bodyJson.toString())
                    .execute()
                    .body();

            // 4. 解析返回值
            JSONObject responseJson = JSONUtil.parseObj(responseStr);
            JSONArray choices = responseJson.getJSONArray("choices");
            String aiReply = choices.getJSONObject(0).getJSONObject("message").getStr("content");

            return ResponseVO.ok(aiReply);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("AI大脑开小差了，请稍后再试");
        }
    }
}