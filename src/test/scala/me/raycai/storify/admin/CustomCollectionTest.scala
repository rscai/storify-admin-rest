package me.raycai.storify.admin

import javax.sql.DataSource

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scalaspring.scalatest.TestContextManagement
import me.raycai.storify.admin.model.{CustomCollection, MetaField}
import me.raycai.storify.admin.model.CustomCollection.SortOrder
import org.hamcrest.CoreMatchers.is
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping
import org.springframework.boot.test.context.{ConfigFileApplicationContextInitializer, SpringBootTest, 
TestConfiguration}
import org.springframework.context.annotation.PropertySource
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get, post, put}
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import scala.collection.JavaConverters._


@RunWith(classOf[SpringRunner])
@WebAppConfiguration
@ContextConfiguration(classes = Array(classOf[Application]),
    initializers = Array(classOf[ConfigFileApplicationContextInitializer]))
@SpringBootTest(properties = Array("logging.level.org.springframework=INFO", "spring.jpa.show-sql=false",
    "spring.jpa.generate-ddl=true",
    "spring.jap.hibernate.ddl-auto=create",
    "spring.datasource.url=jdbc:h2:mem:custom_collection_test;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa", "spring.datasource.password=sa", "spring.datasource.driverClass=org.h2.Driver"))
class CustomCollectionTest extends FeatureSpec with TestContextManagement with GivenWhenThen with BeforeAndAfter {
    @Autowired var context: WebApplicationContext = null
    @Autowired var dataSource: DataSource = null

    protected def mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build

    protected def mapper = new ObjectMapper()
    
    before {
        info("clean all custom collections")
        val connection = dataSource
                .getConnection()
        val stmt = connection
                .createStatement()
        List("CUSTOM_COLLECTION_METAFIELD","custom_collection")
                .foreach { table =>
                    stmt
                            .execute("delete from " + table)
                }
        connection
                .commit();
        connection
                .close

    }


    feature("CustomCollection management") {
        scenario("create new CustomCollection") {
            Given("REST API of CustomCollection")
            mvc
                    .perform(get("/api/profile/custom_collections")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)

            When("create new CustomCollection")

            val customCollection = new CustomCollection()
            customCollection
                    .setId("test000001")
            customCollection
                    .setTitle("testCollection1")
            val bodyHtml = "<p><strong>test</strong></p>"
            customCollection
                    .setBodyHtml(bodyHtml);
            customCollection
                    .setImage("BASE64://assddfdfdfererere=")
            customCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag1")
                                .setValue("tag name 1")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing"),
                        new MetaField()
                                .setKey("tag2")
                                .setValue("tag name 2")
                                .setValueType("string")
                                .setNamespace("collection")
                                .
                                        setDescription("for test")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)
            customCollection
                    .setSortOrder(SortOrder
                            .MANUAL)


            Then("got created customCollection")
            mvc
                    .perform(post("/api/custom_collections")
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.title", is("testCollection1")))
                    .andExpect(jsonPath("$.bodyHtml", is(bodyHtml)))
                    .andExpect(jsonPath("$.image", is("BASE64://assddfdfdfererere=")))
                    .andExpect(jsonPath("$.metafield.length()", is(2)))


            mvc
                    .perform(get("/api/custom_collections/test000001")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)
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
            customCollection
                    .setId(id)
            customCollection
                    .setTitle("testCollection2")
            val bodyHtml = "<p><strong>test</strong></p>"
            customCollection
                    .setBodyHtml(bodyHtml);
            customCollection
                    .setImage("BASE64://assddfdfdfererere=")
            customCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag1")
                                .setValue("tag name 1")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing"),
                        new MetaField()
                                .setKey("tag2")
                                .setValue("tag name 2")
                                .setValueType("string")
                                .setNamespace("collection")
                                .
                                        setDescription("for test")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)
            customCollection
                    .setSortOrder(SortOrder
                            .MANUAL)

            mvc
                    .perform(post("/api/custom_collections")
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())

            When("Update title, bodyHtml, image, and metafield")

            val updateCustomCollection = new CustomCollection()
            updateCustomCollection
                    .setTitle("updatedTitle");
            updateCustomCollection
                    .setBodyHtml("<p>update</p>")
            updateCustomCollection
                    .setImage("http://hostname.com/image.png")
            updateCustomCollection
                    .setSortOrder(SortOrder
                            .ALPHA_ASC)
            updateCustomCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag3")
                                .setValue("tag name 3")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)

            mvc
                    .perform(put("/api/custom_collections/" + id)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(updateCustomCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())

            Then("title, bodyHtml, image and metafield has been updated")
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)
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
            customCollection
                    .setId(id)
            customCollection
                    .setTitle("unpublished")
            val bodyHtml = "<p><strong>test</strong></p>"
            customCollection
                    .setBodyHtml(bodyHtml);
            customCollection
                    .setImage("BASE64://assddfdfdfererere=")
            customCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag1")
                                .setValue("tag name 1")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing"),
                        new MetaField()
                                .setKey("tag2")
                                .setValue("tag name 2")
                                .setValueType("string")
                                .setNamespace("collection")
                                .
                                        setDescription("for test")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)
            customCollection
                    .setSortOrder(SortOrder
                            .MANUAL)
            customCollection
                    .setPublished(false)

            mvc
                    .perform(post("/api/custom_collections")
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)
                    .andExpect(jsonPath("$.published", is(false)))

            When("publish")
            customCollection
                    .setPublished(true)
            mvc
                    .perform(put("/api/custom_collections/" + id)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status
                            .is2xxSuccessful())

            Then("change published status to true")
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andDo(print)
                    .andExpect(status
                            .isOk)
                    .andExpect(jsonPath("$.published", is(true)))
        }
        scenario("unpublish custom collection") {
            Given("a published custom collection")
            val id: String = "test000004"
            val customCollection = new CustomCollection()
            customCollection
                    .setId(id)
            customCollection
                    .setTitle("published")
            val bodyHtml = "<p><strong>test</strong></p>"
            customCollection
                    .setBodyHtml(bodyHtml);
            customCollection
                    .setImage("BASE64://assddfdfdfererere=")
            customCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag1")
                                .setValue("tag name 1")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing"),
                        new MetaField()
                                .setKey("tag2")
                                .setValue("tag name 2")
                                .setValueType("string")
                                .setNamespace("collection")
                                .
                                        setDescription("for test")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)
            customCollection
                    .setSortOrder(SortOrder
                            .MANUAL)
            customCollection
                    .setPublished(true)

            mvc
                    .perform(post("/api/custom_collections")
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)
                    .andExpect(jsonPath("$.published", is(true)))

            When("unpublish")
            customCollection
                    .setPublished(false)
            mvc
                    .perform(put("/api/custom_collections/" + id)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status
                            .is2xxSuccessful())

            Then("change published status to true")
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andDo(print)
                    .andExpect(status
                            .isOk)
                    .andExpect(jsonPath("$.published", is(false)))
        }
        scenario("remove unused custom collection") {
            Given("a custom collection")
            val id: String = "test000005"
            val customCollection = new CustomCollection()
            customCollection
                    .setId(id)
            customCollection
                    .setTitle("published")
            val bodyHtml = "<p><strong>test</strong></p>"
            customCollection
                    .setBodyHtml(bodyHtml);
            customCollection
                    .setImage("BASE64://assddfdfdfererere=")
            customCollection
                    .setMetafield(List(
                        new MetaField()
                                .setKey("tag1")
                                .setValue("tag name 1")
                                .setValueType("string")
                                .setNamespace("global")
                                .setDescription("nothing"),
                        new MetaField()
                                .setKey("tag2")
                                .setValue("tag name 2")
                                .setValueType("string")
                                .setNamespace("collection")
                                .
                                        setDescription("for test")
                    )
                            .asInstanceOf[List[MetaField]]
                            .asJava)
            customCollection
                    .setSortOrder(SortOrder
                            .MANUAL)
            customCollection
                    .setPublished(true)

            mvc
                    .perform(post("/api/custom_collections")
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(customCollection)))
                    .andExpect(status()
                            .is2xxSuccessful())
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .isOk)

            When("remove custom collection")
            mvc
                    .perform(delete("/api/custom_collections/" + id))
                    .andDo(print())
                    .andExpect(status()
                            .is2xxSuccessful())

            Then("the custom collection has been removed")
            mvc
                    .perform(get("/api/custom_collections/" + id)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status()
                            .isNotFound)
        }

        scenario("fetch custom collection by page") {
            Given("53 custom collections")
            List
                    .range(1, 54)
                    .foreach {
                        index: Int =>
                            val id: String = "fetch_" + index
                            val title: String = "fetch_" + index

                            val customCollection = new CustomCollection()
                            customCollection
                                    .setId(id)
                            customCollection
                                    .setTitle(title)
                            val bodyHtml = "<p><strong>test</strong></p>"
                            customCollection
                                    .setBodyHtml(bodyHtml);
                            customCollection
                                    .setImage("BASE64://assddfdfdfererere=")
                            customCollection
                                    .setMetafield(List(
                                        new MetaField()
                                                .setKey("tag1")
                                                .setValue("tag name 1")
                                                .setValueType("string")
                                                .setNamespace("global")
                                                .setDescription("nothing"),
                                        new MetaField()
                                                .setKey("tag2")
                                                .setValue("tag name 2")
                                                .setValueType("string")
                                                .setNamespace("collection")
                                                .
                                                        setDescription("for test")
                                    )
                                            .asInstanceOf[List[MetaField]]
                                            .asJava)
                            customCollection
                                    .setSortOrder(SortOrder
                                            .MANUAL)
                            customCollection
                                    .setPublished(true)

                            mvc
                                    .perform(post("/api/custom_collections")
                                            .contentType(MediaType
                                                    .APPLICATION_JSON)
                                            .content(mapper
                                                    .writeValueAsString(customCollection)))

                                    .andExpect(status()
                                            .is2xxSuccessful())
                            mvc
                                    .perform(get("/api/custom_collections/" + id)
                                            .accept(MediaType
                                                    .APPLICATION_JSON))

                                    .andExpect(status()
                                            .isOk)

                    }

            When("fetch second page in page size 10")

            Then("get 10 custom collections")
            mvc
                    .perform(get("/api/custom_collections/")
                            .param("page", "2")
                            .param("size", "10")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status()
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$._embedded.custom_collections.length()", is(10)))


        }

        scenario("filter custom collection by publish status") {
            Given("2 published and 3 unpublished custom collections")
            List(true, true, false, false, false)
                    .zipWithIndex
                    .foreach {
                        x: (Boolean, Int) =>
                            val id: String = "pub_" + x
                                    ._2
                            val title: String = "pub_" + x
                                    ._2
                            val customCollection = new CustomCollection()
                            customCollection
                                    .setId(id)
                            customCollection
                                    .setTitle(title)
                            val bodyHtml = "<p><strong>test</strong></p>"
                            customCollection
                                    .setBodyHtml(bodyHtml);
                            customCollection
                                    .setImage("BASE64://assddfdfdfererere=")
                            customCollection
                                    .setMetafield(List(
                                        new MetaField()
                                                .setKey("tag1")
                                                .setValue("tag name 1")
                                                .setValueType("string")
                                                .setNamespace("global")
                                                .setDescription("nothing"),
                                        new MetaField()
                                                .setKey("tag2")
                                                .setValue("tag name 2")
                                                .setValueType("string")
                                                .setNamespace("collection")
                                                .
                                                        setDescription("for test")
                                    )
                                            .asInstanceOf[List[MetaField]]
                                            .asJava)
                            customCollection
                                    .setSortOrder(SortOrder
                                            .MANUAL)
                            customCollection
                                    .setPublished(x
                                            ._1)

                            mvc
                                    .perform(post("/api/custom_collections")
                                            .contentType(MediaType
                                                    .APPLICATION_JSON)
                                            .content(mapper
                                                    .writeValueAsString(customCollection)))

                                    .andExpect(status()
                                            .is2xxSuccessful())
                            mvc
                                    .perform(get("/api/custom_collections/" + id)
                                            .accept(MediaType
                                                    .APPLICATION_JSON))

                                    .andExpect(status()
                                            .isOk)

                    }

            When("filter published custom collections")
            Then("got 2")
            mvc
                    .perform(get("/api/custom_collections/search/byPublished")
                            .param("published", "true")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andExpect(jsonPath("$._embedded.custom_collections.length()",
                        is(2)))

            When("filter unpublished custom collections")
            Then("got 3")
            mvc
                    .perform(get("/api/custom_collections/search/byPublished")
                            .param("published", "false")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andExpect(jsonPath("$._embedded.custom_collections.length()",
                        is(3)))
        }

        scenario("fetch custom collections by title") {
            Given("10 custom collections")
            List
                    .range(1, 11)
                    .foreach {
                        index: Int =>
                            val id: String = "fetch_" + index
                            val title: String = "fetch_" + index

                            val customCollection = new CustomCollection()
                            customCollection
                                    .setId(id)
                            customCollection
                                    .setTitle(title)
                            val bodyHtml = "<p><strong>test</strong></p>"
                            customCollection
                                    .setBodyHtml(bodyHtml);
                            customCollection
                                    .setImage("BASE64://assddfdfdfererere=")
                            customCollection
                                    .setMetafield(List(
                                        new MetaField()
                                                .setKey("tag1")
                                                .setValue("tag name 1")
                                                .setValueType("string")
                                                .setNamespace("global")
                                                .setDescription("nothing"),
                                        new MetaField()
                                                .setKey("tag2")
                                                .setValue("tag name 2")
                                                .setValueType("string")
                                                .setNamespace("collection")
                                                .
                                                        setDescription("for test")
                                    )
                                            .asInstanceOf[List[MetaField]]
                                            .asJava)
                            customCollection
                                    .setSortOrder(SortOrder
                                            .MANUAL)
                            customCollection
                                    .setPublished(true)

                            mvc
                                    .perform(post("/api/custom_collections")
                                            .contentType(MediaType
                                                    .APPLICATION_JSON)
                                            .content(mapper
                                                    .writeValueAsString(customCollection)))
                                    .andExpect(status()
                                            .is2xxSuccessful())
                            mvc
                                    .perform(get("/api/custom_collections/" + id)
                                            .accept(MediaType
                                                    .APPLICATION_JSON))
                                    .andExpect(status()
                                            .isOk)

                    }

            When("search custom collections by title")
            Then("get 1 matched")
            mvc
                    .perform(get("/api/custom_collections/search/byTitle")
                            .param("title", "fetch_2")
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andExpect(jsonPath("$._embedded.custom_collections.length()", is(1)))
                    .andExpect(jsonPath("$._embedded.custom_collections[0].title", is("fetch_2")))
        }

    }
}
