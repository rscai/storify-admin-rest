package me.raycai.storify.admin

import javax.sql.DataSource

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.github.scalaspring.scalatest.TestContextManagement
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FeatureSpec, GivenWhenThen}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.{ConfigFileApplicationContextInitializer, SpringBootTest}
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.{delete, get, post, put, patch}
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.{jsonPath, status}
import org.hamcrest.CoreMatchers.{is, notNullValue, nullValue}
import me.raycai.storify.admin.model.{Product, ProductImage, ProductVariant}
import org.springframework.http.MediaType

import scala.collection.JavaConverters._

@RunWith(classOf[SpringRunner])
@WebAppConfiguration
@ContextConfiguration(classes = Array(classOf[Application]),
    initializers = Array(classOf[ConfigFileApplicationContextInitializer]))
@SpringBootTest(properties = Array("logging.level.org.springframework=INFO", "spring.jpa.show-sql=false",
    "spring.datasource.url=jdbc:h2:~/test.h2;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa", "spring.datasource.password=sa", "spring.datasource.driverClass=org.h2.Driver",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"))
class ProductTest extends FeatureSpec with TestContextManagement with GivenWhenThen with BeforeAndAfter {
    @Autowired var context: WebApplicationContext = null
    @Autowired var dataSource: DataSource = null

    protected def mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build

    protected def mapper = new ObjectMapper()
            .configure(DeserializationFeature
                    .FAIL_ON_UNKNOWN_PROPERTIES, false)

    val API_BASE = "/api"
    val RES_PRODUCTS = "products"


    before {
        info("clean all product")
        val connection = dataSource
                .getConnection()
        val stmt = connection
                .createStatement()
        List("product_variant_metafield", "product_variant", "product_image", "product")
                .foreach { table =>
                    stmt
                            .execute("delete from " + table)
                }
        connection
                .commit();
        connection
                .close
    }

    feature("Create new product") {
        scenario("Create a new, but unpublished product") {
            Given("new product content")
            val newOne = new Product()
                    .setTitle("Burton Custom Freestyle 151")
                    .setBodyHtml("<strong>Good snowboard!</strong>")
                    .setVendor("Burton")
                    .setProductType("Snowboard")
                    .setPublished(false)

            When("post")
            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(newOne)))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andReturn()
                    .getResponse
                    .getContentAsString()
            val newProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            And("get product by new id")
            Then("found new one")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + newProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andExpect(jsonPath("$.title", is("Burton Custom Freestyle 151")))
                    .andExpect(jsonPath("$.vendor", is("Burton")))
                    .andExpect(jsonPath("$.productType", is("Snowboard")))
                    .andExpect(jsonPath("$.createdAt", notNullValue()))
                    .andExpect(jsonPath("$.updatedAt", notNullValue()))
                    .andExpect(jsonPath("$.published", is(false)))
                    .andExpect(jsonPath("$.publishedAt", nullValue))
                    .andExpect(jsonPath("$.templateSuffix", nullValue))
                    .andExpect(jsonPath("$.publishedScope", is("global")))
                    //.andExpect(jsonPath("$.tags", is("")))
                    .andExpect(jsonPath("$.variants.length()", is(1)))
                    .andExpect(jsonPath("$.variants[0].price", is("0.00")))
                    .andExpect(jsonPath("$.variants[0].grams", is(0)))
                    .andExpect(jsonPath("$.images.length()", is(0)))

        }
        scenario("Create a new product with the default variant and a product image") {
            Given("New product content")
            val newOne = new Product()
                    .setTitle("Burton Custom Freestyle 151")
                    .setBodyHtml("<strong>Good snowboard!</strong>")
                    .setVendor("Burton")
                    .setProductType("Snowboard")
                    .setImages(List(new ProductImage()
                            .setSrc("http://example.com/rails_logo.gif"))
                            .asJava)
            When("post")
            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(newOne)))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andReturn()
                    .getResponse
                    .getContentAsString()
            val newProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            And("get new product by id")
            Then("get new product with images")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + newProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.variants.length()", is(1)))
                    .andExpect(jsonPath("$.images" +
                            ".length()", is(1)))
                    .andExpect(jsonPath("$.images[0].src", is("http://example.com/rails_logo.gif")))
        }
        scenario("Trying to create a product without a title will return an error") {
            Given("new product content")
            val newOne = new Product()
                    .setBodyHtml("A mystery")
            When("post")
            Then("get error")
            mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsBytes(newOne)))
                    .andExpect(status
                            .is4xxClientError()
                    )
                    .andDo(print)
                    .andExpect(jsonPath("$.errors.length()", is
                    (1)))
                    .andExpect(jsonPath("$.errors[0].entity", is("Product")))
                    .andExpect(jsonPath("$.errors[0].property", is("title")))
        }
        scenario("Create a product with multiple product variants") {
            Given("new product content")
            val newOne = new Product()
                    .setTitle("Burton Custom Freestyle 151")
                    .setBodyHtml("<strong>Good snowboard!</strong>")
                    .setVendor("Burton")
                    .setProductType("Snowboard")
                    .setVariants(List(new ProductVariant()
                            .setTitle("First")
                            .setPrice("10.00")
                            .setSku("123"), new
                                    ProductVariant()
                            .setTitle("Second")
                            .setPrice("20.00")
                            .setSku("234"))
                            .asJava)
            When("post")
            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(newOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn()
                    .getResponse
                    .getContentAsString
            val newProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            And("get product by new product id")
            Then("get new product with multiple variants")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + newProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.variants.length()", is(2)))
                    .andExpect(jsonPath("$.variants[0].title", is("First")))
                    .andExpect(jsonPath("$.variants[0].price", is("10.00")))
                    .andExpect(jsonPath("$.variants[1].title", is("Second")))
                    .andExpect(jsonPath("$.variants[1].price", is("20.00")))
        }
    }


    feature("Update product") {


        scenario("Show a hidden product by changing the published attribute to true") {
            Given("a existed product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true))
                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            When("update published to true")
            mvc
                    .perform(patch(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .contentType
                            ("application/merge-patch+json")
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content("{\"published\":true}"))
                    .andExpect(status
                            .is2xxSuccessful())
            Then("Only update published")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.published", is(true)))
                    .andExpect(jsonPath("$.title", is
                    ("IPod Nano - 8GB")))
                    .andExpect(jsonPath("$.vendor", is("Apple")))
                    .andExpect(jsonPath("$.variants.length()", is(1)))
        }
        scenario("Update a product's title") {
            Given("a existed product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true))
                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId

            When("update title")
            mvc
                    .perform(patch(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .contentType("application/merge-patch+json")
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content("{\"title\":\"New product title\"}"))
                    .andExpect(status
                            .is2xxSuccessful())
            Then("Only update title")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.published", is(false)))
                    .andExpect(jsonPath("$.title", is
                    ("New product title")))
                    .andExpect(jsonPath("$.vendor", is("Apple")))
                    .andExpect(jsonPath("$.variants.length()", is(1)))
        }
        scenario("Update a product, reordering product image") {

            Given("a existed product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true))
                            .asJava)
                    .setImages(List(new ProductImage()
                            .setSrc("http://hpimges.blob.core.chinacloudapi" +
                                    ".cn/coverstory/watermark_prayercard_zh-cn13472871640_1920x1080.jpg")
                            .setPosition
                            (0),
                        new
                                        ProductImage()
                                .setSrc("http://hpimges.blob.core.chinacloudapi" +
                                        ".cn/coverstory/watermark_etretatsunrise_zh-cn10891175350_1920x1080.jpg")
                                .setPosition(1))
                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andDo(print)
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            val existedImage1Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getImages()
                    .get(0)
                    .getId
            val existedImage2Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getImages()
                    .get(1)
                    .getId


            When("reorder product image")
            val updateJson =
                """{
                "images" : [ {
      "id" : %d,
      "position" : 1
    } ,{
     "id" : %d,
     "position" : 0
   } ]
            }"""
                        .format(existedImage1Id, existedImage2Id)
            mvc
                    .perform(patch(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .contentType("application/merge-patch+json")
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(updateJson))
                    .andDo(print)
                    .andExpect(status
                            .is2xxSuccessful())
            Then("Only reorder image")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)
                    .andExpect(jsonPath("$.images[0].src", is("http://hpimges.blob.core.chinacloudapi" +
                            ".cn/coverstory/watermark_etretatsunrise_zh-cn10891175350_1920x1080.jpg")))
                    .andExpect(jsonPath("$" +
                            ".images[1].src", is("http://hpimges.blob.core.chinacloudapi" +
                            ".cn/coverstory/watermark_prayercard_zh-cn13472871640_1920x1080.jpg")))
        }

        scenario("Update a product, adding a new product image") {
            Given("a existed product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true))
                            .asJava)
                    .setImages(List(new ProductImage()
                            .setSrc("http://hpimges.blob.core.chinacloudapi" +
                                    ".cn/coverstory/watermark_prayercard_zh-cn13472871640_1920x1080.jpg"), new
                                    ProductImage()
                            .setSrc("http://hpimges.blob.core.chinacloudapi" +
                                    ".cn/coverstory/watermark_etretatsunrise_zh-cn10891175350_1920x1080.jpg"))
                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            val existedImage1Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getImages()
                    .get(0)
                    .getId
            val existedImage2Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getImages()
                    .get(1)
                    .getId
            When("add a new product image")
            val updateJson =
                """{
                "images":[
                {
                   "id":%d
                },
                {
                   "id":%d
                },
                {
                   "src":"http://nonexist.com/xx.png"
                }
                ]
            }"""
                        .format(existedImage1Id, existedImage2Id)
            Then("add new one image")
            mvc
                    .perform(patch(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .contentType("application/merge-patch+json")
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.images.length()", is(3)))
                    .andExpect(jsonPath("$" +
                            ".images[2].src", is("http://nonexist.com/xx.png")))
        }

        scenario("Update a product, reordering the product variants") {
            Given("a existed product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true)
                            .setPosition(0),
                        new ProductVariant()
                                .setTitle("Yellow")
                                .setPrice("99.00")
                                .setSku("IPOD2008YELLOW")
                                .setGrams(567)
                                .setInventoryPolicy("continue")
                                .setFulfillmentService("manual")
                                .setInventoryManagement
                                ("shopify")
                                .setTaxable(true)
                                .setBarcode("1234_yellow")
                                .setInventoryQuantity(9)
                                .setWeight(1.25F)
                                .setWeightUnit("lb")
                                .setOldInventoryQuantity(9)
                                .setRequiresShipping(true)
                                .setPosition(1))

                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId
            val existedVariant1Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getVariants()
                    .get(0)
                    .getId
            val existedVariant2Id = mapper
                    .readValue(responseJson, classOf[Product])
                    .getVariants()
                    .get(1)
                    .getId

            When("reorder variants")
            val updateJson =
                """{
                "variants":[
                {
                   "id":%d,
                   "position":1
                },
                {
                   "id":%d,
                   "position":0
                }
                ]
            }"""
                        .format(existedVariant1Id, existedVariant2Id)

            mvc
                    .perform(patch(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .contentType("application/merge-patch+json")
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status
                            .is2xxSuccessful())

            Then("reordered variants")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andExpect(jsonPath("$.variants.length()", is(2)))
                    .andExpect(jsonPath("$" +
                            ".variants[0].title", is("Yellow")))
                    .andExpect(jsonPath("$.variants[1].title", is("Pink")))
        }

        scenario("Hide a published product by changing the published attribute to false") {
            pending
        }

        scenario("Update a product, clearing product images") {
            pending
        }

        scenario("Update a product and one of its variants") {
            pending
        }

        scenario("Add a metafield to an existing product") {
            pending
        }
    }

    feature("Delete product") {
        scenario("Delete a product along with all its variants and images") {
            Given("existed one product")
            val existedOne = new Product()
                    .setTitle("IPod Nano - 8GB")
                    .setBodyHtml("<p>It's the small iPod with one " +
                            "very big idea: Video. Now the world's most popular music player, available in 4GB and " +
                            "8GB " +
                            "models, lets you enjoy TV shows, movies, video podcasts, and more. The larger, brighter " +
                            "display " +
                            "means amazing picture quality. In six eye-catching colors, iPod nano is stunning all " +
                            "around. And" +
                            " with models starting at just $149, little speaks volumes.</p>")
                    .setVendor("Apple")
                    .setProductType("Cult Products")
                    .setPublishedScope("web")
                    .setVariants(List(new ProductVariant()
                            .setTitle("Pink")
                            .setPrice("199.00")
                            .setSku("IPOD2008PINK")
                            .setGrams(567)
                            .setInventoryPolicy
                            ("continue")
                            .setFulfillmentService("manual")
                            .setInventoryManagement("shopify")
                            .setTaxable(true)
                            .setBarcode("1234_pink")
                            .setInventoryQuantity(10)
                            .setWeight(1.25F)
                            .setWeightUnit("lb")
                            .setOldInventoryQuantity(10)
                            .setRequiresShipping(true)
                            .setPosition(0),
                        new ProductVariant()
                                .setTitle("Yellow")
                                .setPrice("99.00")
                                .setSku("IPOD2008YELLOW")
                                .setGrams(567)
                                .setInventoryPolicy("continue")
                                .setFulfillmentService("manual")
                                .setInventoryManagement
                                ("shopify")
                                .setTaxable(true)
                                .setBarcode("1234_yellow")
                                .setInventoryQuantity(9)
                                .setWeight(1.25F)
                                .setWeightUnit("lb")
                                .setOldInventoryQuantity(9)
                                .setRequiresShipping(true)
                                .setPosition(1))

                            .asJava)
                    .setPublished(false)

            val responseJson = mvc
                    .perform(post(API_BASE + "/" + RES_PRODUCTS)
                            .contentType(MediaType
                                    .APPLICATION_JSON)
                            .accept(MediaType
                                    .APPLICATION_JSON)
                            .content(mapper
                                    .writeValueAsString(existedOne)))
                    .andExpect(status
                            .is2xxSuccessful())
                    .andReturn
                    .getResponse
                    .getContentAsString
            val existedProductId = mapper
                    .readValue(responseJson, classOf[Product])
                    .getId

            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .is2xxSuccessful)

            When("Delete product")
            mvc
                    .perform(delete(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId))
                    .andExpect(status
                            .is2xxSuccessful)

            Then("delete existed product")
            mvc
                    .perform(get(API_BASE + "/" + RES_PRODUCTS + "/" + existedProductId)
                            .accept(MediaType
                                    .APPLICATION_JSON))
                    .andExpect(status
                            .isNotFound)
        }
    }
}
