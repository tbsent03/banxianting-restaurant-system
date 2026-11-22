package com.project.platform.mapper;

import com.project.platform.entity.DiningTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DiningTableMapper {
    List<DiningTable> queryPage(@Param("offset") Integer offset,
                                @Param("pageSize") Integer pageSize,
                                @Param("query") Map<String, Object> query);
//                                @Param("date") String date,
//                                @Param("period") String period);

    Integer queryCount(@Param("query") Map<String, Object> query);
    List<DiningTable> selectAll(); // 平面图直接查所有

    @Select("SELECT * FROM dining_table WHERE qr_token = #{qrToken}")
    DiningTable selectByToken(String qrToken);

    int insert(DiningTable table);
    int updateById(DiningTable table);
    int removeByIds(@Param("ids") List<Integer> ids);
    @Update("UPDATE dining_table SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Select("SELECT * FROM dining_table WHERE id = #{id}")
    DiningTable selectById(Integer id);

    // 查询某天某时段符合条件的空闲桌位
    List<DiningTable> selectAvailableTables(@Param("reserveDate") String reserveDate,
                                            @Param("startTime") String startTime,
                                            @Param("endTime") String endTime,
                                            @Param("peopleCount") Integer peopleCount);
    List<DiningTable> selectAllWithStatus(@Param("date") String date, @Param("period") String period);
}
