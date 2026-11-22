package com.project.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatMapper {

    @Select("SELECT count(id) FROM orders WHERE DATE(create_time) = CURDATE() AND status != '已取消'")
    Integer getTodayOrderCount();

    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM orders WHERE DATE(create_time) = CURDATE() AND status != '已取消'")
    BigDecimal getTodayRevenue();

    @Select("SELECT count(id) FROM orders WHERE status = '待接单'")
    Integer getPendingOrderCount();

    @Select("SELECT count(id) FROM user")
    Integer getUserCount();

    //  修复：使用 ANY_VALUE 绕过严格模式，并使用别名防止 JSON 转换冲突
    @Select("SELECT ANY_VALUE(DATE_FORMAT(create_time, '%m-%d')) as `day_str`, IFNULL(SUM(total_amount), 0) as `revenue_val` " +
            "FROM orders WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND status != '已取消' " +
            "GROUP BY DATE(create_time) ORDER BY DATE(create_time) ASC")
    List<Map<String, Object>> getTrendData();

    //  修复：热销 Top 5，使用别名
    @Select("SELECT ANY_VALUE(d.name) as `dish_name`, sum(oi.quantity) as `sales_num` FROM order_item oi " +
            "JOIN dish d ON oi.dish_id = d.id JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status != '已取消' GROUP BY d.id ORDER BY sales_num DESC LIMIT 5")
    List<Map<String, Object>> getHotDishes();

    //  修复：分类占比，使用别名
    @Select("SELECT ANY_VALUE(c.name) as `cat_name`, IFNULL(SUM(oi.subtotal), 0) as `total_val` FROM order_item oi " +
            "JOIN dish d ON oi.dish_id = d.id JOIN category c ON d.category_id = c.id " +
            "JOIN orders o ON oi.order_id = o.id WHERE o.status != '已取消' " +
            "GROUP BY c.id")
    List<Map<String, Object>> getCategoryRatio();

    @Select("SELECT count(id) FROM orders WHERE DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND status != '已取消'")
    Integer getYesterdayOrderCount();

    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM orders WHERE DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND status != '已取消'")
    BigDecimal getYesterdayRevenue();
}