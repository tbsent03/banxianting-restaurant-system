package com.project.platform.service.impl;

import com.project.platform.entity.DiningTable;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.DiningTableMapper;
import com.project.platform.service.DiningTableService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DiningTableServiceImpl implements DiningTableService {

    @Resource
    private DiningTableMapper tableMapper;

    @Override
    public PageVO<DiningTable> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<DiningTable> page = new PageVO<>();
//
//        //  取出前端传的日期和时段，如果没有就默认今天和晚市
//        String date = query.get("date") != null ? query.get("date").toString() : java.time.LocalDate.now().toString();
//        String period = query.get("period") != null ? query.get("period").toString() : "晚市";
//
//        page.setList(tableMapper.queryPage((pageNum - 1) * pageSize, pageSize, query));
//        page.setTotal(tableMapper.queryCount(query));
//        return page;
        int offset = (pageNum - 1) * pageSize;
        List<DiningTable> list = tableMapper.queryPage(offset, pageSize, query);
        Integer total = tableMapper.queryCount(query);
        page.setList(list);
        page.setTotal(total);
        return page;

    }

    @Override
    public List<DiningTable> list() {
        return tableMapper.selectAll();
    }

    @Override
    public void insert(DiningTable entity) {
        // 生成随机 32位 token 作为二维码参数
        entity.setQrToken(UUID.randomUUID().toString().replace("-", ""));
        if (entity.getStatus() == null) entity.setStatus("空闲");
        tableMapper.insert(entity);
    }

    @Override
    public void updateById(DiningTable entity) {
        tableMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        tableMapper.removeByIds(ids);
    }

    @Override
    public DiningTable getByToken(String token) {
        return tableMapper.selectByToken(token);
    }

    @Override
    public List<DiningTable> getAvailableTables(String reserveDate, String startTime, String endTime, Integer peopleCount) {
        // 直接调用 Mapper 里的方法
        return tableMapper.selectAvailableTables(reserveDate, startTime, endTime, peopleCount);
    }

    @Override
    public List<DiningTable> listWithStatus(String date, String period) {
        return tableMapper.selectAllWithStatus(date, period);
    }
}
