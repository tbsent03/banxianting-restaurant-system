package com.project.platform.mapper;

import com.project.platform.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Mapper
public interface DishMapper{
//    根据查询条件query，从数据库中分页查询菜品，从offset位置开始，取pageSize条记录，返回Dish列表
    List<Dish> queryPage(@Param("offset") Integer offset,
                         @Param("pageSize") Integer pageSize,
                         @Param("query") Map<String,Object> query);
    Integer queryCount(@Param("query") Map<String,Object> query);
    Dish selectById(Integer id);
    int insert(Dish dish);
    int updateById(Dish dish);
    int removeByIds(@Param("ids") List<Integer> ids);

    /**
     * 为什么不先SELECT查库存再UPDATE 因为多线程下会超卖。这句SQL作用就是把
     * 判断和扣减合并成了一个原子操作。如果返回影响行数为0，就说明抢光了。
     */
    @Update("UPDATE dish SET stock = stock - #{quantity},sales = sales + #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Integer id,@Param("quantity") Integer quantity);

    @Select("SELECT * FROM dish")
    List<Dish> selectAll();
}
