package com.partnermapping.util;

import com.partnermapping.model.Customer;
import com.partnermapping.model.PartnerMapping;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Igor on 06.11.2017.
 */
public class RestClientTest {
    private static final Logger logger = LoggerFactory.getLogger(RestClientTest.class);

    private final String TRUSTED_PARTNER_CLIENT = "app-client";
    private final String TRUSTED_PARTNER_PWD = "12345";

    private final String APPLICATION_CONTEXT = "/";
    private final String REST_SERVICE_URL = "http://localhost:8080" + APPLICATION_CONTEXT + "/customer/";
    private final String AUTH_SERVER_URL = "http://localhost:8080" + APPLICATION_CONTEXT + "/oauth/token";
    private final String QPM_ACCESS_TOKEN = "?access_token=";

    public RestClientTest() {
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials= TRUSTED_PARTNER_CLIENT + ":" + TRUSTED_PARTNER_PWD;
//                "app-client:12345";
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }


    private AccessToken authorizeCustomer(Customer customer){
        AccessToken token = null;
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());

            String urlTemplate = AUTH_SERVER_URL + "?grant_type=password&username=%s&password=%s";
            String url = String.format(urlTemplate, customer.getLogin(), customer.getPass());

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
            token = null;

            if(map!=null){
                token = new AccessToken();
                token.setAccessToken((String) map.get("access_token"));
                logger.info("Access token=" + token.toString());
            }else{
                logger.warn("There is no customer");
            }
        } catch (RestClientException e) {
            logger.warn("authorizeCustomer exception", e);
        }
        return token;
    }


    private Customer testGetCustomerById(int customerId, AccessToken accessToken) {
        logger.info( "Test by CustomerID =" + customerId);

        Customer customer = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "{customerId}";
            HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders());
            customer = restTemplate.getForObject(encodeUrlWithAccessToken(url, accessToken), Customer.class, customerId);
            logger.info("print customer="+customer.toString());
        } catch (RestClientException e) {
            logger.warn("testGetCustomerById exception",e);
        }
        return customer;
    }

    private Customer testGetAuthCustomer(AccessToken accessToken) {
        logger.info( "Test Get Auth Customer");

        Customer customer = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "@me";
            HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders());
            customer = restTemplate.getForObject(encodeUrlWithAccessToken(url, accessToken), Customer.class);
            if(customer != null) {
                logger.info("print customer=" + customer.toString());
            }
        } catch (RestClientException e) {
            logger.warn("testGetAuthCustomer exception", e);
        }
        return  customer;
    }

    private void testGetPartnerMappingByPartnerID(int customerId, String clientId, AccessToken accessToken) {
        logger.info("Test Get partner mapping by customerId={} and clientId={}", customerId, clientId);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "{customerId}/mappings/partner/{clientID}/";
            HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders());
            ResponseEntity<PartnerMapping> pmEntity =
                    restTemplate.exchange(encodeUrlWithAccessToken(url, accessToken), HttpMethod.GET, requestEntity, PartnerMapping.class,
                    customerId, clientId);
            PartnerMapping pm = pmEntity.getBody();
            logger.info("print Partner Mapping="+pm.toString());
        } catch (RestClientException e) {
            logger.warn("testGetPartnerMappingByPartnerID exception", e);
        }
    }

    private PartnerMapping createNewPartnerMapping() {
        PartnerMapping pm = new PartnerMapping();
        pm.setAccountId("Id125872");
        pm.setClientId("APPVC25478");
        pm.setFullName("Ivan Ivanovich");
        pm.setAvatar("https://upload.wikimedia.org/wikipedia/en/b/b0/Avatar-Teaser-Poster.jpg");

        return pm;
    }

    private PartnerMapping testAddMapping(Customer customer, AccessToken accessToken) {
        logger.info("Test Add Mapping for customer=" + customer.toString());

        PartnerMapping newPartnerMapping = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "/mappings/";
            newPartnerMapping = createNewPartnerMapping();
            newPartnerMapping.setCustomer(customer);
            logger.info("Partner Mapping to create={}", newPartnerMapping.toString());

            HttpEntity<PartnerMapping> requestEntity = new HttpEntity<>(newPartnerMapping, getHeaders());
            URI uri = restTemplate.postForLocation(encodeUrlWithAccessToken(url, accessToken), requestEntity);
            logger.info("Created Mapping Location: " + REST_SERVICE_URL + uri.getPath());
        } catch (RestClientException e) {
            logger.warn("testAddMapping exception", e);
        }
        return newPartnerMapping;
    }

    private PartnerMapping testUpdateMapping(PartnerMapping updateMapping, AccessToken accessToken) {
        logger.info("Test Update Maping ");

        PartnerMapping partnerMapping = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "/mappings/";

            updateMapping.setFullName("Иванов");
            updateMapping.setAccountId("id1111111");

            HttpEntity<Object> request = new HttpEntity<Object>(updateMapping, getHeaders());
            ResponseEntity<PartnerMapping> response  = restTemplate.exchange(encodeUrlWithAccessToken(url, accessToken), HttpMethod.PUT, request, PartnerMapping.class);
            partnerMapping = response.getBody();
            logger.info("Updated  mapping=" +partnerMapping.toString());
        } catch (RestClientException e) {
            logger.warn("testUpdateMapping exception", e);
        }

        return partnerMapping;
    }

    private void testListMappings(int customerId, AccessToken accessToken) {
        logger.info("Test all customer list mapping for customerId = " + customerId);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "{customerId}/mappings/";

            List<LinkedHashMap<String, Object>> partnerMappingMap = restTemplate.getForObject(encodeUrlWithAccessToken(url, accessToken), List.class, customerId);
            if(partnerMappingMap != null) {
                for (LinkedHashMap<String, Object> map  : partnerMappingMap) {
                    LinkedHashMap<String, Object> customerMap  = (LinkedHashMap<String, Object>) map.get("customer");
                    logger.info("print Partner Mapping: clientId:" +  map.get("clientId") +
                                ", Mapping FullName: " + map.get("fullName") +
                                ", accountId: " + map.get("accountId") +
                                ", Customer login: " + customerMap.get("login") +
                                ", Customer full name: " + customerMap.get("fullName") +
                                ", Customer balance: " + customerMap.get("balance") +
                                ", Customer status: " + ((Boolean) customerMap.get("status") ? "Active" : "Blocked") +
                                ", avatar: " + map.get("avatar")
                    );
                }
            }
        } catch (RestClientException e) {
            logger.warn("testListMappings exception",e);
        }
    }

    private void testDeleteMapping(int customerId, String clientId, AccessToken accessToken ) {
        logger.info(" Test delete all mappings for customerId={} and clientId={}", customerId, clientId);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "{customerId}/mappings/partner/{clientID}/";

            restTemplate.delete(encodeUrlWithAccessToken(url, accessToken), customerId, clientId);
        } catch (RestClientException e) {
            logger.warn("testDeleteMapping exception", e);
        }
    }

    private void testDeleteAllMappings(int customerId, AccessToken accessToken) {
        logger.info(" Test delete all mappings for customer Id ="+ customerId);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = REST_SERVICE_URL + "{customerId}/mappings/";
            restTemplate.delete(encodeUrlWithAccessToken(url, accessToken), customerId);
        } catch (RestClientException e) {
            logger.warn("testDeleteAllMappings exception", e);
        }
    }

    /**
     *
     * @param url
     * @param accessToken
     * @return
     */
    private String encodeUrlWithAccessToken(String url, AccessToken accessToken) {
        return url + QPM_ACCESS_TOKEN + accessToken.getAccessToken();
    }

    public static void main(String[] args) {
        RestClientTest rc = new RestClientTest();
        int customerId = 1;
        String login = "Ivanov";
        String pass = "12345";
        Customer currentCustomer = new Customer(customerId, login, pass);

        String clientId = "APPVC25478";
        AccessToken accessToken = rc.authorizeCustomer(currentCustomer);

        rc.testGetPartnerMappingByPartnerID(customerId, clientId, accessToken);
        logger.info("");
        rc.testDeleteMapping(customerId, clientId, accessToken);
        logger.info("");
        rc.testListMappings(customerId, accessToken);
        logger.info("");

        Customer customer = rc.testGetCustomerById(customerId, accessToken);
        rc.testGetAuthCustomer(accessToken);
        logger.info("");
        rc.testListMappings(customerId, accessToken);
        logger.info("");

        rc.testDeleteAllMappings(customerId, accessToken);
        rc.testListMappings(customerId, accessToken);
        logger.info("");

        PartnerMapping newPM = rc.testAddMapping(customer, accessToken);
        logger.info("");
        rc.testListMappings(customerId, accessToken);
        logger.info("");

        rc.testUpdateMapping(newPM, accessToken);
        logger.info("");

        int customerId2 = 2;
        rc.testListMappings(customerId2, accessToken);
        String login2 = "Petrov";
        String pwd2 = "123";
        Customer customer2 = new Customer(customerId2, login2, pwd2);
        AccessToken accessToken2 = rc.authorizeCustomer(customer2);
        rc.testGetAuthCustomer(accessToken2);
        rc.testListMappings(customerId2, accessToken2);

    }

    private class AccessToken {
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public String toString() {
            return "AccessToken{" +
                    "accessToken='" + accessToken + '\'' +
                    '}';
        }
    }
}
