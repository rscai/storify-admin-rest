package me.raycai.storify.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "_id")
    private long id;
    
    @Column
    @NotNull
    private String title;
    
    @Lob
    @Column(name="body_html")
    private String bodyHtml;
    @Column
    private String handle;
    @Column
    private Date createdAt;

    @OneToMany(cascade=CascadeType.ALL)
    @OrderColumn(name="position")
    @JoinColumn(name = "product_id")
    private List<ProductImage> images;
    
    //private Map<String,String> options;
    @Column(name="product_type")
    private String productType;
    
    @Column
    private boolean published;
    @Column
    private Date publishedAt;
    @Column
    private String publishedScope;
    @Column
    private String tags;
    @Column
    private String templateSuffix;
    @Column(name="metafields_global_title_tag")
    private String metafieldsGlobalTitleTag;
    @Column(name="metafields_global_description_tag")
    private String metafieldsGlobalDescriptionTag;
    @Column
    private Date updatedAt;
    @OneToMany(cascade=CascadeType.ALL)
    @OrderColumn(name="position")
    @JoinColumn(name="product_id")
    private List<ProductVariant> variants;
    
    @Column
    private String vendor;

    public long getId() {
        return id;
    }

    public Product setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Product setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public Product setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
        return this;
    }

    public String getHandle() {
        return handle;
    }

    public Product setHandle(String handle) {
        this.handle = handle;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Product setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public Product setImages(List<ProductImage> images) {
        this.images = images;
        return this;
    }

    public String getProductType() {
        return productType;
    }

    public Product setProductType(String productType) {
        this.productType = productType;
        return this;
    }

    public boolean isPublished() {
        return published;
    }

    public Product setPublished(boolean published) {
        this.published = published;
        return this;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public Product setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public String getPublishedScope() {
        return publishedScope;
    }

    public Product setPublishedScope(String publishedScope) {
        this.publishedScope = publishedScope;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public Product setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public Product setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
        return this;
    }

    public String getMetafieldsGlobalTitleTag() {
        return metafieldsGlobalTitleTag;
    }

    public Product setMetafieldsGlobalTitleTag(String metafieldsGlobalTitleTag) {
        this.metafieldsGlobalTitleTag = metafieldsGlobalTitleTag;
        return this;
    }

    public String getMetafieldsGlobalDescriptionTag() {
        return metafieldsGlobalDescriptionTag;
    }

    public Product setMetafieldsGlobalDescriptionTag(String metafieldsGlobalDescriptionTag) {
        this.metafieldsGlobalDescriptionTag = metafieldsGlobalDescriptionTag;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Product setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public Product setVariants(List<ProductVariant> variants) {
        this.variants = variants;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public Product setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }
}
