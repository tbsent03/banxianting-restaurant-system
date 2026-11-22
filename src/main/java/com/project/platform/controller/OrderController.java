package com.project.platform.controller;

import com.project.platform.dto.CurrentUserDTO;
import com.project.platform.dto.OrderSubmitDTO;
import com.project.platform.entity.Order;
import com.project.platform.service.OrderService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/submit")
    public ResponseVO<String> submit(@RequestBody OrderSubmitDTO dto){
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        String orderNo = orderService.submitOrder(dto,userId);
        return ResponseVO.ok(orderNo);
    }

    @GetMapping("/page")
    public ResponseVO<PageVO<Order>> page(@RequestParam Map<String, Object> query,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize){
        return ResponseVO.ok(orderService.page(query,pageNum,pageSize));
    }

    @GetMapping("/detail/{orderNo}")
    public ResponseVO<Order> detail(@PathVariable String orderNo){
        return ResponseVO.ok(orderService.getDetail(orderNo));
    }

    @PostMapping("/updateStatus")
    public ResponseVO<Void> updateStatus(@RequestBody Order order){
        orderService.updateStatus(order.getId(),order.getStatus());
        return ResponseVO.ok();
    }

    @GetMapping("/myPage")
    public ResponseVO<PageVO<Order>> myPage(@RequestParam Map<String, Object> query,
                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        //从ThreadLocal获取当前登录用户ID，强制查询自己的订单
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        query.put("userId",userId);
        return ResponseVO.ok(orderService.page(query,pageNum,pageSize));
    }
}
