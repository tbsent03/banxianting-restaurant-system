package com.project.platform.service;
import com.project.platform.entity.Review;
import com.project.platform.vo.PageVO;

import java.util.Map;

public interface ReviewService {
    PageVO<Review> page(Map<String, Object> query, Integer pageNum, Integer pageSize);
    void addReview(Review review);
    void replyReview(Integer id, String reply);
}