package com.project.platform.mapper;

import com.project.platform.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {
    /// 1. 分页查询 (带条件)
    List<Category> queryPage(@Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize,
                             @Param("query") Map<String, Object> query);

    // 2. 查询总条数
    Integer queryCount(@Param("query") Map<String, Object> query);

    // 3. 根据ID查询单个 (编辑回显用)
    Category selectById(Integer id);

    // 4. 新增
    int insert(Category category);

    // 5. 更新
    int updateById(Category category);

    // 6. 批量删除
    int removeByIds(@Param("ids") List<Integer> ids);

    // (可选) 如果前台需要直接获取所有分类的下拉列表，可以加一个不分页的方法
    List<Category> selectAll();

    Category selectByName(String name);
}
