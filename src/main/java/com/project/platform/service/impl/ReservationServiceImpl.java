package com.project.platform.service.impl;

import com.project.platform.entity.DiningTable;
import com.project.platform.entity.Reservation;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.DiningTableMapper;
import com.project.platform.mapper.ReservationMapper;
import com.project.platform.service.ReservationService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Resource
    private ReservationMapper reservationMapper;
    @Resource
    private DiningTableMapper diningTableMapper;

    @Override
    public PageVO<Reservation> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Reservation> page = new PageVO<>();
        page.setList(reservationMapper.queryPage((pageNum - 1) * pageSize, pageSize, query));
        page.setTotal(reservationMapper.queryCount(query));
        return page;
    }

    @Override
    public void insert(Reservation entity) {
        // 新建预订时，状态默认是 待确认
        entity.setStatus("待确认");
        reservationMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Reservation entity) {
        //  核心防御：如果老板确认了预订，并且分配了桌位
        if ("已确认".equals(entity.getStatus()) && entity.getTableId() != null) {
            // 查出这单原本的时间
            Reservation original = reservationMapper.selectById(entity.getId());
            if (original != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                String dateStr = sdf.format(original.getReserveDate());

                String startTime = "10:00", endTime = "14:59";
                int hour = Integer.parseInt(original.getReserveTime().split(":")[0]);
                if (hour >= 15) { startTime = "15:00"; endTime = "23:59"; }

                // 去数据库查，这个桌位在这个时间段是不是已经有单了
                Integer conflictCount = reservationMapper.checkConflict(
                        dateStr, startTime, endTime, entity.getTableId(), original.getId()
                );

                if (conflictCount != null && conflictCount > 0) {
                    throw new CustomException("分配失败：该桌位在此时段已被其他顾客预订！");
                }
            }

            diningTableMapper.updateStatus(entity.getTableId(), "预留");
        }
        // 如果老板点击了“标记到店”，把桌位状态改为“占用”
        if ("已到店".equals(entity.getStatus()) && entity.getTableId() != null) {
            diningTableMapper.updateStatus(entity.getTableId(), "占用");
        }
        // 如果老板点击了“取消”，把桌位恢复为“空闲”
        if ("已取消".equals(entity.getStatus()) && entity.getTableId() != null) {
            diningTableMapper.updateStatus(entity.getTableId(), "空闲");
        }

        reservationMapper.updateById(entity);
    }
}