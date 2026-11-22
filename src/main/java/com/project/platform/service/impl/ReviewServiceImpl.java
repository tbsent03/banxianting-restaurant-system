package com.project.platform.service.impl;
import com.project.platform.entity.Review;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.ReviewMapper;
import com.project.platform.service.ReviewService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public PageVO<Review> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Review> page = new PageVO<>();
        page.setList(reviewMapper.queryPage((pageNum - 1) * pageSize, pageSize, query));
        page.setTotal(reviewMapper.queryCount(query));
        return page;
    }

    @Override
    public void addReview(Review review) {
        // 防重复评价
        if (reviewMapper.selectByOrderId(review.getOrderId()) != null) {
            throw new CustomException("该订单已评价，请勿重复操作");
        }
        reviewMapper.insert(review);
    }

    @Override
    public void replyReview(Integer id, String reply) {
        reviewMapper.reply(id, reply);
    }
}