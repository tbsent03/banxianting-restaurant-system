package com.project.platform.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Category {
    private Integer id;
    private String name;
    private String nameEn; // 注意驼峰，对应数据库的 name_en
    private Integer sort;
    private String status;
    private Date createTime;
}
