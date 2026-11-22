package com.project.platform.controller;

import com.project.platform.entity.Category;
import com.project.platform.service.CategoryService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")  // 🔥 这里的路径决定了前端怎么请求
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    // 分页查询 GET /category/page?pageNum=1&pageSize=10
    @GetMapping("/page")
    public ResponseVO<PageVO<Category>> page(@RequestParam Map<String, Object> query,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Category> pageVO = categoryService.page(query, pageNum, pageSize);
        return ResponseVO.ok(pageVO);
    }

    // 查询所有 GET /category/list
    @GetMapping("/list")
    public ResponseVO<List<Category>> list() {
        return ResponseVO.ok(categoryService.list());
    }

    // 新增 POST /category/add
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody Category category) {
        categoryService.insert(category);
        return ResponseVO.ok();
    }

    // 更新 PUT /category/update
    @PutMapping("/update")
    public ResponseVO<Void> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return ResponseVO.ok();
    }

    // 批量删除 DELETE /category/delBatch
    @DeleteMapping("/delBatch")
    public ResponseVO<Void> delBatch(@RequestBody List<Integer> ids) {
        categoryService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
