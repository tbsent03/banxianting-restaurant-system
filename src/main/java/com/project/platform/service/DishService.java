package com.project.platform.service;

import com.project.platform.entity.Dish;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface DishService {
    PageVO<Dish> page(Map<String,Object> query,Integer pageNum,Integer pageSize);
    Dish selectById(Integer id);
    void insert(Dish dish);
    void updateById(Dish dish);
    void removeByIds(List<Integer> ids);
}
