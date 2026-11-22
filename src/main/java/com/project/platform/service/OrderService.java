package com.project.platform.service;

import com.project.platform.dto.OrderSubmitDTO;
import com.project.platform.entity.Order;
import com.project.platform.vo.PageVO;

import java.util.Map;

public interface OrderService {
    /**
     * 提交订单
     * @param dto 前端传来的订单数据
     * @param userId 当前登录用户ID
     * @return 生成的订单号
     */
    String submitOrder(OrderSubmitDTO dto, Integer userId);
    PageVO<Order> page(Map<String,Object> query,Integer pageNum,Integer pageSize);
    Order getDetail(String orderNo);
    void updateStatus(Integer id,String status);
}
