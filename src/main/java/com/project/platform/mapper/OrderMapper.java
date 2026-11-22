package com.project.platform.mapper;

import com.project.platform.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders(order_no, user_id, order_type, total_amount, status, contact_name, contact_tel, remark,table_id) " +
            "VALUES(#{orderNo}, #{userId}, #{orderType}, #{totalAmount}, #{status}, #{contactName}, #{contactTel}, #{remark},#{tableId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    //动态查询订单列表
    List<Order> queryPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("query")Map<String,Object> query);
    Integer queryCount(@Param("query") Map<String,Object> query);

    // 根据订单号查询

    Order selectByOrderNo(String orderNo);

    //更新状态
    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id,@Param("status") String status);

    Order selectById(Integer id);

}