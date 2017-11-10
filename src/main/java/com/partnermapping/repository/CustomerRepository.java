package com.partnermapping.repository;

import com.partnermapping.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT a FROM Customer a WHERE lower(a.login) = lower(:loginName)")
    Customer findCustomerByLogin(@Param("loginName") String loginName);
}
