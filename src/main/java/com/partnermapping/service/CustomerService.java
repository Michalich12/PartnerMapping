package com.partnermapping.service;

import com.partnermapping.model.Customer;

/**
 * Customer service interface
 */
public interface CustomerService {

    /**
     * Get customer by the given Id
     * @param customerId
     * @return
     */
    Customer getCustomer(int customerId);

    /**
     * Get customer by login name
     * @param loginName
     * @return
     */
    Customer getCustomerByLogin(String loginName);
}
