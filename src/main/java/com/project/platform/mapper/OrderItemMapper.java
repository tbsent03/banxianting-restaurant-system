package com.project.platform.mapper;

import com.project.platform.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Insert("INSERT INTO order_item(order_id, dish_id, dish_name, cover_url, price, quantity, subtotal) " +
            "VALUES(#{orderId}, #{dishId}, #{dishName}, #{coverUrl}, #{price}, #{quantity}, #{subtotal})")
    int insert(OrderItem orderItem);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(Integer orderId);
}
