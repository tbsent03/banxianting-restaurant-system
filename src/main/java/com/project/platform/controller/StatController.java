package com.project.platform.controller;

import com.project.platform.mapper.StatMapper;
import com.project.platform.vo.DashboardVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import com.project.platform.exception.CustomException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stat")
public class StatController {

    @Resource
    private StatMapper statMapper;

    @GetMapping("/overview")
    public ResponseVO<DashboardVO> overview() {
        DashboardVO vo = new DashboardVO();

        try {
            vo.setTodayOrderCount(statMapper.getTodayOrderCount());
            vo.setTodayRevenue(statMapper.getTodayRevenue());
            vo.setPendingOrderCount(statMapper.getPendingOrderCount());
            vo.setUserCount(statMapper.getUserCount());
            vo.setYesterdayOrderCount(statMapper.getYesterdayOrderCount());
            vo.setYesterdayRevenue(statMapper.getYesterdayRevenue());

            //  处理趋势数据（适配新别名 day_str 和 revenue_val）
            List<Map<String, Object>> trendList = statMapper.getTrendData();
            List<String> dates = new ArrayList<>();
            List<BigDecimal> revenues = new ArrayList<>();
            for (Map<String, Object> map : trendList) {
                dates.add(map.get("day_str") != null ? map.get("day_str").toString() : "");
                revenues.add(map.get("revenue_val") != null ? new BigDecimal(map.get("revenue_val").toString()) : BigDecimal.ZERO);
            }
            vo.setTrendDates(dates);
            vo.setTrendRevenues(revenues);

            vo.setHotDishes(statMapper.getHotDishes());
            vo.setCategoryRatio(statMapper.getCategoryRatio());

            return ResponseVO.ok(vo);
        } catch (Exception e) {
            e.printStackTrace(); // 打印报错到后台控制台
            throw new CustomException("看板数据加载失败: " + e.getMessage());
        }
    }
}