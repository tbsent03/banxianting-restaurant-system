package com.project.platform.service.impl;

import com.project.platform.mapper.StatMapper;
import com.project.platform.service.StatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    @Resource
    private StatMapper statMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();

        // 1. 基础 KPI 数据
        data.put("todayOrderCount", statMapper.getTodayOrderCount());
        data.put("todayRevenue", statMapper.getTodayRevenue());
        data.put("pendingOrderCount", statMapper.getPendingOrderCount());
        data.put("userCount", statMapper.getUserCount());
        data.put("yesterdayOrderCount", statMapper.getYesterdayOrderCount());
        data.put("yesterdayRevenue", statMapper.getYesterdayRevenue());

        // 2. 图表数据
        data.put("trendData", statMapper.getTrendData()); // List<Map>
        data.put("hotDishes", statMapper.getHotDishes()); // List<Map>
        data.put("categoryRatio", statMapper.getCategoryRatio()); // List<Map>

        return data;
    }
}