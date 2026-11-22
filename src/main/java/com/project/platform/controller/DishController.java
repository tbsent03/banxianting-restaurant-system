package com.project.platform.controller;

import com.project.platform.entity.Dish;
import com.project.platform.service.DishService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @GetMapping("/page")
    public ResponseVO<PageVO<Dish>> page(@RequestParam Map<String,Object> query,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10")Integer pageSize){
        return ResponseVO.ok(dishService.page(query,pageNum,pageSize));
    }

    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody Dish dish){
        dishService.insert(dish);
        return ResponseVO.ok();
    }

    @PutMapping("/update")
    public ResponseVO<Void> update(@RequestBody Dish dish){
        dishService.updateById(dish);
        return ResponseVO.ok();
    }

    @DeleteMapping("/delBatch")
    public ResponseVO<Void> delBatch(@RequestBody List<Integer> ids){
        dishService.removeByIds(ids);
        return ResponseVO.ok();
    }
    //获取菜品详情GET /dish/selectById/{id}
    @GetMapping("/selectById/{id}")
    public ResponseVO<Dish> selectById(@PathVariable Integer id){
        return ResponseVO.ok(dishService.selectById(id));
    }
}
