package com.project.gui.service;

import com.project.gui.model.ChangePassword;
import com.project.gui.model.UserDto;
import com.project.gui.util.ApiUtil;

import java.io.IOException;

public class UserServiceGui {
    public static UserDto getUserByUsername(String username)  {
        return ApiUtil.get("/user/"+username, UserDto.class);
    }
    public static String changePassword(ChangePassword changePassword,long userId) {
        ApiUtil.post("/user/change-password/" + userId, changePassword, UserDto.class);
        return "Đổi mật khẩu thành công";
    }

}
