package com.project.platform.dto;

import lombok.Data;

@Data
public class RetrievePasswordDTO {
    private String type;
    private String tel;
    private String code;//判断验证码
    private String password;
}
