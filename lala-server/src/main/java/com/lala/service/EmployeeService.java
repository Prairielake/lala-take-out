package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.EmployeeDTO;
import com.lala.dto.EmployeeLoginDTO;
import com.lala.dto.EmployeePageQueryDTO;
import com.lala.entity.Employee;
import com.lala.result.PageResult;
import com.lala.result.Result;

import java.math.BigInteger;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);

    Employee getById(Long id);

    void updateEmployeeData(EmployeeDTO employeeDTO);
}
