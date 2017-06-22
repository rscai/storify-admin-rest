package me.raycai.storify.admin.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="product_variant")
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="_id")
    private long id;
    
    @Column
    private String barcode;
    @Column
    private Date createdAt;
    @Column(name="fulfillment_service")
    private String fulfillmentService;
    @Column
    private int grams;
    @Column(name="image_id")
    private String imageId;
    @Column(name="inventory_management")
    private String inventoryManagement;
    @Column(name="inventory_policy")
    private String inventoryPolicy;
    
    @Column(name="inventory_quantity")
    private int inventoryQuantity;
    @Column(name="old_inventory_quantity")
    private int oldInventoryQuantity;
    @Column(name="inventory_quantity_adjustment")
    private int inventoryQuantityAdjustment;
    @ElementCollection
    @CollectionTable(name = "product_variant_metafield",joinColumns=@JoinColumn(name="collection_id"))
    private List<MetaField> metafield;
    @Column
    private int position;
    @Column
    private String price;
    //@Column(insertable = false,updatable=false)
    //private String productId;
    @Column
    private boolean requiresShipping;
    @Column
    private String sku;
    @Column
    private boolean taxable;
    @Column
    private String title;
    @Column
    private Date updatedAt;
    @Column
    private float weight;
    @Column
    private String weightUnit;

    public long getId() {
        return id;
    }

    public ProductVariant setId(long id) {
        this.id = id;
        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public ProductVariant setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ProductVariant setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getFulfillmentService() {
        return fulfillmentService;
    }

    public ProductVariant setFulfillmentService(String fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
        return this;
    }

    public int getGrams() {
        return grams;
    }

    public ProductVariant setGrams(int grams) {
        this.grams = grams;
        return this;
    }

    public String getImageId() {
        return imageId;
    }

    public ProductVariant setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public String getInventoryManagement() {
        return inventoryManagement;
    }

    public ProductVariant setInventoryManagement(String inventoryManagement) {
        this.inventoryManagement = inventoryManagement;
        return this;
    }

    public String getInventoryPolicy() {
        return inventoryPolicy;
    }

    public ProductVariant setInventoryPolicy(String inventoryPolicy) {
        this.inventoryPolicy = inventoryPolicy;
        return this;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public ProductVariant setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
        return this;
    }

    public int getOldInventoryQuantity() {
        return oldInventoryQuantity;
    }

    public ProductVariant setOldInventoryQuantity(int oldInventoryQuantity) {
        this.oldInventoryQuantity = oldInventoryQuantity;
        return this;
    }

    public int getInventoryQuantityAdjustment() {
        return inventoryQuantityAdjustment;
    }

    public ProductVariant setInventoryQuantityAdjustment(int inventoryQuantityAdjustment) {
        this.inventoryQuantityAdjustment = inventoryQuantityAdjustment;
        return this;
    }

    public List<MetaField> getMetafield() {
        return metafield;
    }

    public ProductVariant setMetafield(List<MetaField> metafield) {
        this.metafield = metafield;
        return this;
    }
    
    public int getPosition() {
        return position;
    }

    public ProductVariant setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public ProductVariant setPrice(String price) {
        this.price = price;
        return this;
    }

    public boolean isRequiresShipping() {
        return requiresShipping;
    }

    public ProductVariant setRequiresShipping(boolean requiresShipping) {
        this.requiresShipping = requiresShipping;
        return this;
    }

    public String getSku() {
        return sku;
    }

    public ProductVariant setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public ProductVariant setTaxable(boolean taxable) {
        this.taxable = taxable;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ProductVariant setTitle(String title) {
        this.title = title;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public ProductVariant setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public float getWeight() {
        return weight;
    }

    public ProductVariant setWeight(float weight) {
        this.weight = weight;
        return this;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public ProductVariant setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
        return this;
    }
}
