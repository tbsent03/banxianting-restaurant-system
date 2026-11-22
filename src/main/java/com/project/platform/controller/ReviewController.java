package com.project.platform.controller;
import com.project.platform.entity.Review;
import com.project.platform.service.ReviewService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Resource
    private ReviewService reviewService;

    // 后台查评价列表
    @GetMapping("/page")
    public ResponseVO<PageVO<Review>> page(@RequestParam Map<String, Object> query,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseVO.ok(reviewService.page(query, pageNum, pageSize));
    }

    // 顾客提交评价
    @PostMapping("/add")
    public ResponseVO<Void> add(@RequestBody Review review) {
        Integer userId = CurrentUserThreadLocal.getCurrentUser().getId();
        review.setUserId(userId);
        reviewService.addReview(review);
        return ResponseVO.ok();
    }

    // 老板回复评价
    @PostMapping("/reply")
    public ResponseVO<Void> reply(@RequestBody Review review) {
        reviewService.replyReview(review.getId(), review.getReply());
        return ResponseVO.ok();
    }
}