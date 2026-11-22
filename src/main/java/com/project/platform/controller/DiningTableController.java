package com.project.platform.controller;

import com.project.platform.entity.DiningTable;
import com.project.platform.mapper.DiningTableMapper;
import com.project.platform.service.DiningTableService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/table")
public class DiningTableController {
    @Resource
    private DiningTableMapper diningTableMapper;
    @Resource
    private DiningTableService tableService;

    @GetMapping("/page")
    public ResponseVO<PageVO<DiningTable>> page(@RequestParam Map<String, Object> query,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return ResponseVO.ok(tableService.page(query, pageNum, pageSize));
    }

//    // 供平面图渲染和前台下拉框使用
//    @GetMapping("/list")
//    public ResponseVO<List<DiningTable>> list() {
//        return ResponseVO.ok(tableService.list());
//    }

    // 【修改点】供平面图渲染使用，增加 date 和 period 参数
    @GetMapping("/list")
    public ResponseVO<List<DiningTable>> list(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String period) {

        // 如果前端没有传日期（比如刚进页面），默认查今天的
        if (date == null || date.trim().isEmpty()) {
            date = java.time.LocalDate.now().toString(); // 获取当前日期 "YYYY-MM-DD"
        }

        // 如果前端传了参数，走带状态的查询
        return ResponseVO.ok(tableService.listWithStatus(date, period));
    }

    // 顾客扫码后，根据 token 查桌子信息
    @GetMapping("/token/{token}")
    public ResponseVO<DiningTable> getByToken(@PathVariable String token) {
        return ResponseVO.ok(tableService.getByToken(token));
    }

    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody DiningTable table) {
        tableService.insert(table);
        return ResponseVO.ok();
    }

    @PutMapping("/update")
    public ResponseVO<Void> update(@RequestBody DiningTable table) {
        tableService.updateById(table);
        return ResponseVO.ok();
    }

    @DeleteMapping("/delBatch")
    public ResponseVO<Void> delBatch(@RequestBody List<Integer> ids) {
        tableService.removeByIds(ids);
        return ResponseVO.ok();
    }

    @GetMapping("/available")
    public ResponseVO<List<DiningTable>> available(
            @RequestParam String reserveDate,
            @RequestParam String reserveTime,
            @RequestParam Integer peopleCount) {

        // 👇 根据时间拆分午市或晚市的时间段范围
        String startTime = "10:00";
        String endTime = "14:59";
        try {
            int hour = Integer.parseInt(reserveTime.split(":")[0]);
            if (hour >= 15) {
                startTime = "15:00";
                endTime = "23:59";
            }
        } catch (Exception e) {}

        return ResponseVO.ok(tableService.getAvailableTables(reserveDate, startTime, endTime, peopleCount));
    }
}
