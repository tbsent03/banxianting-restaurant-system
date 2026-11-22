package com.project.platform.service.impl;

import com.project.platform.entity.Category;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.CategoryMapper;
import com.project.platform.service.CategoryService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public PageVO<Category> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Category> page = new PageVO<>();
        // 计算分页偏移量
        int offset = (pageNum - 1) * pageSize;
        page.setList(categoryMapper.queryPage(offset, pageSize, query));
        page.setTotal(categoryMapper.queryCount(query));
        return page;
    }

    @Override
    public Category selectById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> list() {
        // 这里对应你之前接口里定义的 list() 方法
        return categoryMapper.selectAll();
    }

    @Override
    public void insert(Category entity) {
        checkNameUnique(entity);
        categoryMapper.insert(entity);
    }

    @Override
    public void updateById(Category entity) {
        checkNameUnique(entity);
        categoryMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        categoryMapper.removeByIds(ids);
    }

    /**
     * 自定义校验逻辑：判断分类名称是否重复
     */
    private void checkNameUnique(Category entity) {
        // 去 Mapper 里查有没有同名的分类
        Category existCategory = categoryMapper.selectByName(entity.getName());
        // 如果查到了同名分类，并且不是它自己（说明是在编辑别的数据），就报错
        if (existCategory != null && !existCategory.getId().equals(entity.getId())) {
            throw new CustomException("该分类名称已存在，请勿重复添加");
        }
    }
}