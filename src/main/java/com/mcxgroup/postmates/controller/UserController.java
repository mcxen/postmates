package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.User;
import com.mcxgroup.postmates.service.UserService;
import com.mcxgroup.postmates.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private RedisTemplate redisTemplate;

    //实现邮箱登录
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        log.info("收到user请求 {}",user.getPhone());
        String phone = user.getPhone();
        String subjetc = "[PostMates]登录验证码";
        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 默认设置一下，想要随机生成验证么就注释下一行
            code = "8888";
            String context = "[PostMates] 登录验证码为：【 "+code+" 】,五分钟内有效，请妥善保管";
            log.info("code = {}",code);
            // 默认设置一下，想要随机生成验证么就取消注释下一行
//            userService.sendMsg(phone,subjetc,context);
            httpSession.setAttribute(phone,code);
            return R.success("验证码发送成功，请查收邮箱.");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        //获得手机的验证码和手机号
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object codeInSession = session.getAttribute(phone);
        if (codeInSession!=null && codeInSession.equals(code)){
            //登录成功
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            if (user==null){
                user = new User();//当前为新用户，直接注册一个
                user.setPhone(phone);
                user.setStatus(1);
                user.setName(phone.substring(0,6));
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            log.info(user.getName()+"已经登录系统,ID为{}",user.getId());
            BaseContext.setCurrentId(user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("已退出登录！！");
    }

}
