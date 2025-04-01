package com.lala.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lala.dto.EmployeeDTO;
import com.lala.dto.EmployeeLoginDTO;
import com.lala.entity.Employee;
import com.lala.result.Result;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);
}
