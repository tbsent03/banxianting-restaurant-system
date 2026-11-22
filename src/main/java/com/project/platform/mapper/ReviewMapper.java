package com.project.platform.mapper;
import com.project.platform.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper {
    List<Review> queryPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("query") Map<String, Object> query);
    Integer queryCount(@Param("query") Map<String, Object> query);
    int insert(Review review);
    int reply(@Param("id") Integer id, @Param("reply") String reply);
    Review selectByOrderId(Integer orderId);
}