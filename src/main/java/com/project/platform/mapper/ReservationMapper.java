package com.project.platform.mapper;
import com.project.platform.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationMapper {
    List<Reservation> queryPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("query") Map<String, Object> query);
    Integer queryCount(@Param("query") Map<String, Object> query);

    int insert(Reservation reservation);
    int updateById(Reservation reservation);

    //根据ID查询
    @Select("SELECT * FROM reservation WHERE id = #{id}")
    Reservation selectById(Integer id);
    @Select("SELECT count(id) FROM reservation WHERE reserve_date = #{date} " +
            "AND reserve_time >= #{startTime} AND reserve_time <= #{endTime} " +
            "AND table_id = #{tableId} AND status IN ('待确认', '已确认', '已到店') " +
            "AND id != #{excludeId}")
    Integer checkConflict(@Param("date") String date, @Param("startTime") String startTime,
                          @Param("endTime") String endTime, @Param("tableId") Integer tableId,
                          @Param("excludeId") Integer excludeId);
}