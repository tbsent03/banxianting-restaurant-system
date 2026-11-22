package com.project.platform.controller;

import com.project.platform.entity.Reservation;
import com.project.platform.exception.CustomException;
import com.project.platform.service.ReservationService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    // 后台分页查询
    @GetMapping("/page")
    public ResponseVO<PageVO<Reservation>> page(@RequestParam Map<String, Object> query,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseVO.ok(reservationService.page(query, pageNum, pageSize));
    }

    // 前台顾客提交预订
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody Reservation reservation) {
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        reservation.setUserId(userId);
        reservationService.insert(reservation);
        return ResponseVO.ok();
    }

    // 后台更新状态/分配桌位
    @PutMapping("/update")
    public ResponseVO<Void> update(@RequestBody Reservation reservation) {
        reservationService.updateById(reservation);
        return ResponseVO.ok();
    }

    //顾客端：查看自己的预订记录
    @GetMapping("/my")
    public ResponseVO<List<Reservation>> myReservations(){
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        Map<String,Object> query = new java.util.HashMap<>();
        query.put("userId",userId);//强制塞入当前用户ID

        //复用分页查询逻辑，但直接返回全部，按时间倒序
        PageVO<Reservation> page = reservationService.page(query,1,100);
        return ResponseVO.ok(page.getList());
    }

    // 顾客端：取消预订
    @PostMapping("/cancel/{id}")
    public ResponseVO<Void> cancelReservation(@PathVariable Integer id) {
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();

        // 1. 查询该预订是否属于当前用户
        // (这里简单处理，直接通过分页查，或者在 Service 里补一个 selectById)
        java.util.Map<String, Object> query = new java.util.HashMap<>();
        query.put("userId", userId);
        java.util.List<Reservation> myList = reservationService.page(query, 1, 100).getList();

        Reservation target = myList.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
        if (target == null) {
            throw new CustomException("找不到该预订记录或无权操作");
        }

        // 2. 校验状态
        if (!"待确认".equals(target.getStatus())) {
            throw new CustomException("当前状态不可取消");
        }

        // 3. 执行取消（复用 updateById）
        Reservation updateEntity = new Reservation();
        updateEntity.setId(id);
        updateEntity.setStatus("已取消");
        reservationService.updateById(updateEntity);

        return ResponseVO.ok();
    }
}