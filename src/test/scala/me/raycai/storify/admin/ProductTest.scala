package me.raycai.storify.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scalaspring.scalatest.TestContextManagement
import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.{ConfigFileApplicationContextInitializer, SpringBootTest}
import org.springframework.http.MediaType
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{get, post}
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.hamcrest.CoreMatchers.is
import me.raycai.storify.admin.model.Product
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.web.{AnnotationConfigWebContextLoader, WebAppConfiguration}
import org.springframework.web.context.WebApplicationContext;

@RunWith(classOf[SpringRunner])
@WebAppConfiguration
@ContextConfiguration(classes = Array(classOf[Application]),
  initializers = Array(classOf[ConfigFileApplicationContextInitializer]))
class ProductTest extends FeatureSpec with TestContextManagement with GivenWhenThen {
  @Autowired var context:WebApplicationContext = null
  protected def mvc = MockMvcBuilders.webAppContextSetup(context).build


  //new TestContextManager(this.getClass()).prepareTestInstance(this)
  

  feature("Admin Login") {
    scenario("Correct username and password") {
      
      val product = new Product();
      product.setId("test000001");
      product.setName("test1");

      val mapper = new ObjectMapper();

      mvc.perform(post("/api/product").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(product)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.name", is("test1")));
    }
  }
}
