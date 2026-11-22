package com.project.platform.service;

import com.project.platform.entity.Category;
import com.project.platform.vo.PageVO;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    PageVO<Category> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
    Category selectById(Integer id);
    List<Category> list();
    void insert(Category entity);
    void updateById(Category entity);
    void removeByIds(List<Integer> ids);
}