package me.raycai.storify.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.raycai.storify.admin.model.Product;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCreateTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;


    @Before

    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }


    @Test
    public void testCreate() throws Exception {
        final Product product = new Product();
        product.setId("test000001");
        product.setName("test1");

        ObjectMapper mapper = new ObjectMapper();
        
        mvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is("test1")));

    }
}
