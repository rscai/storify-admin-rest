package me.raycai.storify.admin.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="_id")
    private long id;
    @Column
    private String src;
    @Column
    private int position;
    
    //private List<String> variantIds;
    
    @Column
    private Date createdAt;
    
    @Column
    private Date updatedAt;
    
    //@Column(insertable = false,updatable=false)
    //private String productId;


    public long getId() {
        return id;
    }

    public ProductImage setId(long id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public ProductImage setSrc(String src) {
        this.src = src;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public ProductImage setPosition(int position) {
        this.position = position;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ProductImage setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public ProductImage setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}
