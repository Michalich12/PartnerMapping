package com.partnermapping.service;

import com.partnermapping.model.PartnerMapping;

import java.util.List;

/**
 * Partner mapping service interface
 */
public interface PartnerMappingService {

    /**
     * Get partner mapping for customer ID by client id
     * @param customerID
     * @param clientId
     * @return
     */
    PartnerMapping getPartnerMappingByClientID(int customerID, String clientId);

    /**
     * Get list of partner mappings for the customer
     * @param customerID
     * @return
     */
    List<PartnerMapping> getPartnerMappingListByCustomerID(int customerID);

    /**
     * Add or update partner mapping
     * @param partnerMapping
     * @return
     */
    PartnerMapping addOrUpdatePartnerMapping(PartnerMapping partnerMapping);

    /**
     * Delete partner mapping for customer and client id
     * @param customerID
     * @param clientId
     */
    void deletePartnerMappingsByPartnerID(int customerID, String clientId);

    /**
     * Delete all partner mappings
     * @param customerID
     */
    void deleteAllPartnerMappings(int customerID);
}
