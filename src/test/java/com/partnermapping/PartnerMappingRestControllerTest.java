package com.partnermapping;

import com.partnermapping.config.DataConfig;
import com.partnermapping.config.WebAppConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Igor on 08.11.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =  {DataConfig.class,  WebAppConfig.class})
@WebAppConfiguration
public class PartnerMappingRestControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;
    private final String URL = "http://localhost:8080/customer/";

    @Autowired
    private WebApplicationContext webApplicationContext;

/*
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
*/

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetCustomerById() throws Exception {
        int customerId = 1;
        String url = URL + String.valueOf(customerId);
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(new Integer(1)))
        .andReturn();

        Assert.assertEquals("application/json;charset=UTF-8",
                mvcResult.getResponse().getContentType());
//        .andExpect(content().contentType(contentType))
    }

    @Test
    public void testGetAuthCustomer() throws Exception {

    }

    @Test
    public void testGetPartnerMappingByPartner() throws Exception {

    }

    @Test
    public void testListPartnerMappings() throws Exception {

    }

    @Test
    public void testCreateMapping() throws Exception {

    }

    @Test
    public void testUpdateMapping() throws Exception {

    }

    @Test
    public void testDeleteAllMappings() throws Exception {

    }

    @Test
    public void testDeleteMappingByPartner() throws Exception {

    }
/*
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
*/


}
