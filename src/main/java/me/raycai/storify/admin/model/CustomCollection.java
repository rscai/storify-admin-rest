package me.raycai.storify.admin.model;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "custom_collection")
public class CustomCollection {
    public enum SortOrder {
        ALPHA_ASC("alpha-asc"),
        ALPHA_DESC("alpha-desc"),
        BEST_SELLING("best-selling"),
        CREATED("created"),
        CREATED_DESC("created-desc"),
        MANUAL("manual"),
        PRICE_ASC("price-asc"),
        PRICE_DESC("price-desc");

        private final String text;

        SortOrder(String text) {
            this.text = text;
        }
        @JsonValue
        public String value() {
            return text;
        }

    }

    @Id
    @Column(name = "_id")
    private String id;

    @Column
    private String title;

    @Column(name = "body_html")
    private String bodyHtml;

    @Column
    private String handle;

    @Column
    private String image;

    @ElementCollection
    @CollectionTable(name = "custom_collection_metafield",joinColumns=@JoinColumn(name="collection_id"))
    private List<MetaField> metafield;

    @Column
    private boolean published;

    @Column
    private Date publishedAt;

    @Column
    private String publishedScope;

    @Column
    private SortOrder sortOrder;

    @Column
    private String templateSuffix;

    @Column
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<MetaField> getMetafield() {
        return metafield;
    }

    public void setMetafield(List<MetaField> metafield) {
        this.metafield = metafield;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getPublishedScope() {
        return publishedScope;
    }

    public void setPublishedScope(String publishedScope) {
        this.publishedScope = publishedScope;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
