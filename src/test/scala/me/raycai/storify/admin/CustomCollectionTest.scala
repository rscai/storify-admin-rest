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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{get, post, put, delete}
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

  protected def mapper = new ObjectMapper()


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
      val bodyHtml = "<p><strong>test</strong></p>"
      customCollection.setBodyHtml(bodyHtml);
      customCollection.setImage("BASE64://assddfdfdfererere=")
      customCollection.setMetafield(List(
        new MetaField().setKey("tag1").setValue("tag name 1").setValueType("string").setNamespace("global")
          .setDescription("nothing"),
        new MetaField().setKey("tag2").setValue("tag name 2").setValueType("string").setNamespace("collection").
          setDescription("for test")
      ).asInstanceOf[List[MetaField]].asJava)
      customCollection.setSortOrder(SortOrder.MANUAL)


      Then("got created customCollection")
      mvc.perform(post("/api/custom_collections").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(customCollection)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.title", is("testCollection1")))
        .andExpect(jsonPath("$.bodyHtml", is(bodyHtml)))
        .andExpect(jsonPath("$.image", is("BASE64://assddfdfdfererere=")))
        .andExpect(jsonPath("$.metafield.length()", is(2)))


      mvc.perform(get("/api/custom_collections/test000001").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.title", is("testCollection1")))
        .andExpect(jsonPath("$.bodyHtml", is(bodyHtml)))
        .andExpect(jsonPath("$.image", is("BASE64://assddfdfdfererere=")))
        .andExpect(jsonPath("$.metafield.length()", is(2)))
        .andExpect(jsonPath("$.metafield[0].key", is("tag1")))
        .andExpect(jsonPath("$.metafield[0].value", is("tag name 1")))
        .andExpect(jsonPath("$.metafield[0].valueType", is("string")))
        .andExpect(jsonPath("$.metafield[0].namespace", is("global")))
    }

    scenario("update existed customCollection") {
      Given("a customCollection")
      val id: String = "test000002"
      val customCollection = new CustomCollection()
      customCollection.setId(id)
      customCollection.setTitle("testCollection2")
      val bodyHtml = "<p><strong>test</strong></p>"
      customCollection.setBodyHtml(bodyHtml);
      customCollection.setImage("BASE64://assddfdfdfererere=")
      customCollection.setMetafield(List(
        new MetaField().setKey("tag1").setValue("tag name 1").setValueType("string").setNamespace("global")
          .setDescription("nothing"),
        new MetaField().setKey("tag2").setValue("tag name 2").setValueType("string").setNamespace("collection").
          setDescription("for test")
      ).asInstanceOf[List[MetaField]].asJava)
      customCollection.setSortOrder(SortOrder.MANUAL)

      mvc.perform(post("/api/custom_collections").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(customCollection)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())

      When("Update title, bodyHtml, image, and metafield")

      val updateCustomCollection = new CustomCollection()
      updateCustomCollection.setTitle("updatedTitle");
      updateCustomCollection.setBodyHtml("<p>update</p>")
      updateCustomCollection.setImage("http://hostname.com/image.png")
      updateCustomCollection.setSortOrder(SortOrder.ALPHA_ASC)
      updateCustomCollection.setMetafield(List(
        new MetaField().setKey("tag3").setValue("tag name 3").setValueType("string").setNamespace("global")
          .setDescription("nothing")
      ).asInstanceOf[List[MetaField]].asJava)

      mvc.perform(put("/api/custom_collections/" + id).contentType(MediaType.APPLICATION_JSON).accept(MediaType
        .APPLICATION_JSON).content(mapper.writeValueAsString(updateCustomCollection)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())

      Then("title, bodyHtml, image and metafield has been updated")
      mvc.perform(get("/api/custom_collections/" + id).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.title", is("updatedTitle")))
        .andExpect(jsonPath("$.bodyHtml", is("<p>update</p>")))
        .andExpect(jsonPath("$.image", is("http://hostname.com/image.png")))
        .andExpect(jsonPath("$.metafield.length()", is(1)))
        .andExpect(jsonPath("$.metafield[0].key", is("tag3")))
        .andExpect(jsonPath("$.metafield[0].value", is("tag name 3")))
        .andExpect(jsonPath("$.metafield[0].valueType", is("string")))
        .andExpect(jsonPath("$.metafield[0].namespace", is("global")))
    }

    scenario("publish customCollection") {
      Given("a unpublished customCollection")
      val id: String = "test000003"
      val customCollection = new CustomCollection()
      customCollection.setId(id)
      customCollection.setTitle("unpublished")
      val bodyHtml = "<p><strong>test</strong></p>"
      customCollection.setBodyHtml(bodyHtml);
      customCollection.setImage("BASE64://assddfdfdfererere=")
      customCollection.setMetafield(List(
        new MetaField().setKey("tag1").setValue("tag name 1").setValueType("string").setNamespace("global")
          .setDescription("nothing"),
        new MetaField().setKey("tag2").setValue("tag name 2").setValueType("string").setNamespace("collection").
          setDescription("for test")
      ).asInstanceOf[List[MetaField]].asJava)
      customCollection.setSortOrder(SortOrder.MANUAL)
      customCollection.setPublished(false)

      mvc.perform(post("/api/custom_collections").contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(customCollection)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
      mvc.perform(get("/api/custom_collections/" + id).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk)
        .andExpect(jsonPath("$.published", is(false)))

      When("publish")
      customCollection.setPublished(true)
      mvc.perform(put("/api/custom_collections/" + id).contentType(MediaType.APPLICATION_JSON).accept(MediaType
        .APPLICATION_JSON).content(mapper.writeValueAsString(customCollection)))
        .andDo(print())
        .andExpect(status.is2xxSuccessful())

      Then("change published status to true")
      mvc.perform(get("/api/custom_collections/" + id).accept(MediaType.APPLICATION_JSON)).andDo(print)
        .andExpect(status.isOk)
        .andExpect(jsonPath("$.published", is(true)))


    }
  }
}
