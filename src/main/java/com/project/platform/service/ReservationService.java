package com.project.platform.service;
import com.project.platform.entity.Reservation;
import com.project.platform.vo.PageVO;
import java.util.List;
import java.util.Map;

public interface ReservationService {
    PageVO<Reservation> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
    void insert(Reservation entity);
    void updateById(Reservation entity);
}