package com.partnermapping.controller;

import com.partnermapping.common.ErrorData;
import com.partnermapping.model.Customer;
import com.partnermapping.model.PartnerMapping;
import com.partnermapping.service.CustomerService;
import com.partnermapping.service.PartnerMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Partner Mapping Rest Controller
 */
@RestController
@RequestMapping("customer")
public class PartnerMappingRestController {
    private static final Logger logger = LoggerFactory.getLogger(PartnerMappingRestController.class);

    private final PartnerMappingService partnerMappingService;

    private final CustomerService customerService;

    @Autowired
    PartnerMappingRestController(PartnerMappingService partnerMappingService, CustomerService customerService) {
        this.partnerMappingService = partnerMappingService;
        this.customerService = customerService;
    }

    @ExceptionHandler({PartnerMappingNotFoundException.class , CustomerNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void exceptionHandlerMapping() {
    }


    @ExceptionHandler(CustomerAccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void exceptionForbidenAccess() {
    }

    @ExceptionHandler(Exception.class)
    public ErrorData exceptionHandlerMapping(HttpServletRequest request, Exception exception) {
        ErrorData errorData = new ErrorData();
        errorData.setMessage(exception.getLocalizedMessage());
        errorData.setUrl(request.getRequestURL().append("/error").toString());

        logger.warn("Error occured", exception);
        return errorData;
    }

    private Customer validateCustomer(int customerID) throws CustomerAccessForbiddenException, CustomerNotFoundException {
        Customer customer = customerService.getCustomer(customerID);

        if (customer == null) {
            throw new CustomerNotFoundException(customerID);
        }

        String customerLogin = customer.getLogin();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth !=null) {
            String authLoginName = auth.getName();

            if (!customerLogin.equals(authLoginName)) {
                throw new CustomerAccessForbiddenException(customer.getLogin());
            }
        }

        return customer;
    }

    @GetMapping("{customerID}")
    public ResponseEntity<Customer> getCustomerByID(@PathVariable("customerID") Integer customerID) {
        logger.info("Get customer with customerID {}", customerID);
        Customer customer = validateCustomer(customerID);

        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }


    @GetMapping("@me")
    public ResponseEntity<Customer> getAuthCustomer() {
        logger.info("Get auth customer ");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            String loginName = auth.getName();

            Customer customer = customerService.getCustomerByLogin(loginName);
            if (customer == null) {
                throw new CustomerNotFoundException(loginName);
            }

            return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        } else {
            return new ResponseEntity<Customer>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("{customerID}/mappings/partner/{clientId}")
    public ResponseEntity<PartnerMapping> getPartnerMappingByPartner(@PathVariable("customerID") Integer customerID,
                                                                     @PathVariable("clientId") String clientId) {
        logger.info("Get partner mapping  for customerId {} and clientId {}", customerID, clientId);

        validateCustomer(customerID);

        PartnerMapping partnerMapping = partnerMappingService.getPartnerMappingByClientID(customerID, clientId);
        if (partnerMapping == null) {
            throw new PartnerMappingNotFoundException(customerID, clientId);
        }

        return new ResponseEntity<PartnerMapping>(partnerMapping, HttpStatus.OK);
    }

    @GetMapping("{customerId}/mappings/")
    public ResponseEntity<List<PartnerMapping>> listPartnerMappings(@PathVariable("customerId") Integer customerId) {
        logger.info("Get list partner mappings for customerId {}", customerId);

        validateCustomer(customerId);

        List<PartnerMapping> list = partnerMappingService.getPartnerMappingListByCustomerID(customerId);
        if (list.isEmpty()) {
            logger.info("there is no partner mappings for customerId {}", customerId);
            return new ResponseEntity<List<PartnerMapping>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<PartnerMapping>>(list, HttpStatus.OK);
    }

    @PostMapping("/mappings/")
    public ResponseEntity<Void> createMapping(@RequestBody PartnerMapping partnerMapping, UriComponentsBuilder ucBuilder) {
        logger.info("Create new mapping for customer {}", partnerMapping.getCustomer());

        int customerID = partnerMapping.getCustomer().getId();

        validateCustomer(customerID);

        String clientId = partnerMapping.getClientId();
        if (partnerMappingService.getPartnerMappingByClientID(customerID,clientId) != null) {
            logger.info("Partner mapping already exist {}", partnerMapping.toString());
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }

        partnerMappingService.addOrUpdatePartnerMapping(partnerMapping);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/{customerID}/mappings/partner/{clientId}").
                buildAndExpand(customerID, clientId).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/mappings/")
    public ResponseEntity<PartnerMapping> updateMapping(@RequestBody PartnerMapping partnerMapping) {
        logger.info("Updating partner mapping {}", partnerMapping.toString());

        int customerID = partnerMapping.getCustomer().getId();

        validateCustomer(customerID);

        String clientId = partnerMapping.getClientId();

        PartnerMapping updatedMapping = partnerMappingService.getPartnerMappingByClientID(
                customerID,
                clientId);

        if (updatedMapping == null) {
            throw new PartnerMappingNotFoundException(customerID, clientId);
        }

        updatedMapping.setFullName(partnerMapping.getFullName());
        updatedMapping.setClientId(clientId);
        updatedMapping.setAvatar(partnerMapping.getAvatar());
        updatedMapping.setAccountId(partnerMapping.getAccountId());

        partnerMappingService.addOrUpdatePartnerMapping(updatedMapping);
        return new ResponseEntity<PartnerMapping>(updatedMapping, HttpStatus.OK);
    }

    @DeleteMapping("{customerId}/mappings/")
    public ResponseEntity<Void> deleteAllMappings(@PathVariable("customerId") Integer customerId) {
        logger.info("Delete all mappings for customerId {}", customerId);
        validateCustomer(customerId);

        partnerMappingService.deleteAllPartnerMappings(customerId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{customerId}/mappings/partner/{clientId}/")
    public ResponseEntity<Void> deleteMappingByPartner(@PathVariable("customerId") Integer customerId,
                                                       @PathVariable("clientId") String clientId) {
        logger.info("Delete mapping for customerId {} and clientId {}", customerId, clientId);

        validateCustomer(customerId);

        PartnerMapping partnerMapping = partnerMappingService.getPartnerMappingByClientID(customerId, clientId);
        if (partnerMapping == null) {
            throw new PartnerMappingNotFoundException(customerId, clientId);
        }

        partnerMappingService.deletePartnerMappingsByPartnerID(customerId, clientId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class CustomerNotFoundException extends RuntimeException {
        CustomerNotFoundException(int customerID) {
            super("Couldn't find customer with customerID " + customerID );
            logger.info("Customer with customerID {} is not found", customerID);
        }

        CustomerNotFoundException(String loginName) {
            super("Couldn't find customer with login " + loginName );
            logger.info("Customer with login name {} is not found", loginName);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class PartnerMappingNotFoundException extends RuntimeException {

        PartnerMappingNotFoundException(int customerID, String clientId) {
            super("Couldn't find mapping with client id " + clientId);
            logger.info("Partner mapping  for customerId {} and clientId {} is not found", customerID, clientId);
        }

        PartnerMappingNotFoundException(int customerID) {
            super("Couldn't find mapping for customer id " + customerID);
            logger.info("there is no partner mappings for customerId {}", customerID);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    class CustomerAccessForbiddenException extends RuntimeException {
        public CustomerAccessForbiddenException(String login) {
            super("Access forbidden for customer with login " + login);
            logger.info("Access forbidden for customer with login " + login);
        }
    }
}
