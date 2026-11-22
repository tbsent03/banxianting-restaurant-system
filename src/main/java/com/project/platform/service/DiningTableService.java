package com.project.platform.service;

import com.project.platform.entity.DiningTable;
import com.project.platform.mapper.DiningTableMapper;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

public interface DiningTableService {
    PageVO<DiningTable> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
    List<DiningTable> list();
    void insert(DiningTable entity);
    void updateById(DiningTable entity);
    void removeByIds(List<Integer> ids);
    DiningTable getByToken(String token);
    //获取可用桌位方法
    List<DiningTable> getAvailableTables(String reserveDate, String startTime, String endTime, Integer peopleCount);
    List<DiningTable> listWithStatus(String date, String period);
}
