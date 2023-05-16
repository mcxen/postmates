package com.mcxgroup.postmates.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcxgroup.postmates.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
