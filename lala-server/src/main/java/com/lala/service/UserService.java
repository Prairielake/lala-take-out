package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.UserLoginDTO;
import com.lala.entity.User;

public interface UserService extends IService<User> {
    User wxLogin(UserLoginDTO userLoginDTO);
}
