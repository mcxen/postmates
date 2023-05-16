package com.mcxgroup.postmates.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mcxgroup.postmates.entity.Employee;
import com.mcxgroup.postmates.mapper.EmployeeMapper;
import com.mcxgroup.postmates.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: EmployeeServiceImpl
 * @Description:  用户服务实现类
 * @author: MCXEN
 * MCXEN
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
