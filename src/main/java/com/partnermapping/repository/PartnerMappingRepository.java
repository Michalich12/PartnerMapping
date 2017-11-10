package com.partnermapping.repository;

import com.partnermapping.model.PartnerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Igor on 03.11.2017.
 */
public interface PartnerMappingRepository extends JpaRepository<PartnerMapping, Long> {

    @Query("SELECT a FROM PartnerMapping a WHERE a.customer.id = :customerID")
    List<PartnerMapping> findPartnerMappingByCustomer(@Param("customerID") int customerID);

    @Query("SELECT a FROM PartnerMapping a WHERE a.customer.id = :customerID and a.clientId = :clientId")
    PartnerMapping findPartnerMappingsByPartner(@Param("customerID") int customerID, @Param("clientId") String clientId);
}
