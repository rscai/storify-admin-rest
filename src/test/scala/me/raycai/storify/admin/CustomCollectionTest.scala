package me.raycai.storify.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scalaspring.scalatest.TestContextManagement
import me.raycai.storify.admin.model.{CustomCollection, MetaField}
import me.raycai.storify.admin.model.CustomCollection.SortOrder
import org.hamcrest.CoreMatchers.is
import org.junit.runner.RunWith
import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{get, post}
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import scala.collection.JavaConverters._

@RunWith(classOf[SpringRunner])
@WebAppConfiguration
@ContextConfiguration(classes = Array(classOf[Application]),
  initializers = Array(classOf[ConfigFileApplicationContextInitializer]))
class CustomCollectionTest extends FeatureSpec with TestContextManagement with GivenWhenThen {
  @Autowired var context: WebApplicationContext = null

  protected def mvc = MockMvcBuilders.webAppContextSetup(context).build


  feature("CustomCollection management") {
    scenario("create new CustomCollection") {
      Given("REST API of CustomCollection")
      mvc.perform(get("/api/profile/custom_collections").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk)

      When("create new CustomCollection")

      val customCollection = new CustomCollection()
      customCollection.setId("test000001")
      customCollection.setTitle("testCollection1")
      val bodyHtml = "<p><strong>test<strong><p>"
      customCollection.setBodyHtml(bodyHtml);
      customCollection.setImage("BASE64://assddfdfdfererere=")
      customCollection.setMetafield(List(
        new MetaField().setKey("tag1").setValue("tag name 1").setValueType("string").setNamespace("global")
          .setDescription("nothing"),
        new MetaField().setKey("tag2").setValue("tag name 2").setValueType("string").setNamespace("collection").
          setDescription("for test")
      ).asInstanceOf[List[MetaField]].asJava)
      customCollection.setSortOrder(SortOrder.MANUAL)

      val mapper = new ObjectMapper();

      Then("got created customCollection")
      mvc.perform(post("/api/custom_collections").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(customCollection)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.title", is("testCollection1")));
    }
  }
}
