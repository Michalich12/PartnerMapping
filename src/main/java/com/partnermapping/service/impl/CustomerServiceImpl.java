package com.partnermapping.service.impl;

import com.partnermapping.model.Customer;
import com.partnermapping.repository.CustomerRepository;
import com.partnermapping.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer getCustomer(int customerId) {
        return customerRepository.findOne(customerId);
    }

    @Override
    public Customer getCustomerByLogin(String loginName) {
        return customerRepository.findCustomerByLogin(loginName);
    }
}
