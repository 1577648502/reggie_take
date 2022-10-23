package com.lfg;

import com.lfg.entity.Employee;
import com.lfg.mapper.EmployeeMapper;
import com.lfg.service.impl.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ReggieTakeApplicationTests {
    @Autowired
        private EmployeeMapper employeeMapper;
    @Test
    void contextLoads() {
        List<Employee> employees = employeeMapper.selectList(null);
        System.out.println(employees);
    }

}
