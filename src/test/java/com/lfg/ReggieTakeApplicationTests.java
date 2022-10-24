package com.lfg;

import com.lfg.entity.Employee;
import com.lfg.mapper.EmployeeMapper;
import com.lfg.service.impl.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

@SpringBootTest
class ReggieTakeApplicationTests {
    @Autowired
        private EmployeeMapper employeeMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    void contextLoads() {
//        List<Employee> employees = employeeMapper.selectList(null);
//        System.out.println(employees);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("name","dashabi");
        Object name = valueOperations.get("name");
        System.out.println(name);
    }

}
