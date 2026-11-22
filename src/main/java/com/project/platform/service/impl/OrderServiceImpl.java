package com.project.platform.service.impl;

import com.project.platform.dto.OrderSubmitDTO;
import com.project.platform.entity.DiningTable;
import com.project.platform.entity.Dish;
import com.project.platform.entity.Order;
import com.project.platform.entity.OrderItem;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.DiningTableMapper;
import com.project.platform.mapper.DishMapper;
import com.project.platform.mapper.OrderItemMapper;
import com.project.platform.mapper.OrderMapper;
import com.project.platform.service.OrderService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DiningTableMapper diningTableMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) //  开启事务，任何报错全部回滚
    public String submitOrder(OrderSubmitDTO dto, Integer userId) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new CustomException("购物车不能为空");
        }
        // 新增核心逻辑 1：堂食桌位占用校验与锁定
        String orderType = dto.getOrderType() == null ? "堂食" : dto.getOrderType();
        if ("堂食".equals(orderType) && dto.getTableId() != null) {
            DiningTable table = diningTableMapper.selectById(dto.getTableId());
            if (table == null) {
                throw new CustomException("桌位不存在");
            }
            if ("占用".equals(table.getStatus())) {
                throw new CustomException("该桌位正在用餐中，请勿重复下单或扫描其他桌位");
            }
            // 校验通过，瞬间将桌位状态改为占用
            diningTableMapper.updateStatus(table.getId(), "占用");
        }
        // 1. 生成订单号 (时间戳 + 随机数)
        String orderNo = "OD" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (100 + new Random().nextInt(900));

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. 创建订单主表对象
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTableId(dto.getTableId()); // 接收桌号
        order.setOrderType(orderType);
        order.setStatus("待接单");
        order.setContactName(dto.getContactName());
        order.setContactTel(dto.getContactTel());
        order.setRemark(dto.getRemark());

        // 3. 遍历明细，扣库存，算总价
        for (OrderItem item : dto.getItems()) {
            // 查一下菜品最新信息（不信任前端传来的价格）
            Dish dish = dishMapper.selectById(item.getDishId());
            if (dish == null || !"上架".equals(dish.getStatus())) {
                throw new CustomException("菜品 [" + (dish != null ? dish.getName() : "未知") + "] 已下架");
            }

            // 核心：扣减库存（乐观锁）
            int affectedRows = dishMapper.deductStock(dish.getId(), item.getQuantity());
            if (affectedRows == 0) {
                // 返回 0 说明库存不足，抛出异常触发回滚！
                throw new CustomException("菜品 [" + dish.getName() + "] 库存不足，请减少数量");
            }

            // 组装明细快照
            item.setDishName(dish.getName());
            item.setCoverUrl(dish.getCoverUrl());
            item.setPrice(dish.getPrice());
            item.setSubtotal(dish.getPrice().multiply(new BigDecimal(item.getQuantity())));

            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 4. 保存订单主表
        order.setTotalAmount(totalAmount);
        orderMapper.insert(order);

        // 5. 保存订单明细表
        for (OrderItem item : dto.getItems()) {
            item.setOrderId(order.getId()); // 拿到刚才主表自增的 ID
            orderItemMapper.insert(item);
        }

        return orderNo; // 返回订单号给前端展示
    }

    @Override
    public PageVO<Order> page (Map<String, Object> query, Integer pageNum, Integer pageSize){
        PageVO<Order> page = new PageVO<>();
        page.setList(orderMapper.queryPage((pageNum - 1) * pageSize,pageSize,query));
        page.setTotal(orderMapper.queryCount(query));
        return page;
    }

    @Override
    public Order getDetail(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //查明细
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer id,String status){
        //状态白名单校验（防篡改）
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        orderMapper.updateStatus(id, status);
        //  新增核心逻辑 2：订单完成或取消时，释放桌位
        if ("已完成".equals(status) || "已取消".equals(status)) {
            if ("堂食".equals(order.getOrderType()) && order.getTableId() != null) {
                diningTableMapper.updateStatus(order.getTableId(), "空闲");
            }
        }
    }
}