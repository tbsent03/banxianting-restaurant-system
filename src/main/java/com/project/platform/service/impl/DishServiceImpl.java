package com.project.platform.service.impl;

import com.project.platform.entity.Dish;
import com.project.platform.mapper.DishMapper;
import com.project.platform.service.DishService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Override
    public PageVO<Dish> page(Map<String,Object> query,Integer pageNum,Integer pageSize){
        PageVO<Dish> page = new PageVO<>();
        page.setList(dishMapper.queryPage((pageNum - 1) * pageSize,pageSize,query));
        page.setTotal(dishMapper.queryCount(query));
        return page;
    }

    @Override
    public void insert(Dish dish){ dishMapper.insert(dish); }

    @Override
    public void updateById(Dish dish){ dishMapper.updateById(dish); }

    @Override
    public void removeByIds(List<Integer> ids){ dishMapper.removeByIds(ids); }

    @Override
    public Dish selectById(Integer id){ return dishMapper.selectById(id);}
}
