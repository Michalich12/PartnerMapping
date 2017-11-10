package com.partnermapping.service.impl;

import com.partnermapping.model.PartnerMapping;
import com.partnermapping.repository.PartnerMappingRepository;
import com.partnermapping.service.PartnerMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PartnerMappingServiceImpl implements PartnerMappingService {
    private static final Logger logger = LoggerFactory.getLogger(PartnerMappingServiceImpl.class);

    @Autowired
    private PartnerMappingRepository partnerMappingRepository;

    @Override
    public List<PartnerMapping> getPartnerMappingListByCustomerID(int customerID) {
        return partnerMappingRepository.findPartnerMappingByCustomer(customerID);
    }

    @Override
    public PartnerMapping getPartnerMappingByClientID(int customerID, String clientId) {
        return partnerMappingRepository.findPartnerMappingsByPartner(customerID, clientId);
    }

    @Override
    public PartnerMapping addOrUpdatePartnerMapping(PartnerMapping partnerMapping) {
        return partnerMappingRepository.saveAndFlush(partnerMapping);
    }

    @Override
    public void deletePartnerMappingsByPartnerID(int customerID, String clientId) {
        PartnerMapping partnerMapping = partnerMappingRepository.findPartnerMappingsByPartner(customerID, clientId);
        if(partnerMapping != null ) {
            partnerMappingRepository.delete(partnerMapping);
        }
    }

    @Override
    public void deleteAllPartnerMappings(int customerID) {
        List<PartnerMapping> listToDelete = partnerMappingRepository.findPartnerMappingByCustomer(customerID);
        if(listToDelete != null && !listToDelete.isEmpty()) {
            partnerMappingRepository.deleteInBatch(listToDelete);
        }
    }
}
