package com.mcxgroup.postmates.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mcxgroup.postmates.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends IService<User> {
    void sendMsg(String to,String subject,String context);
}
