package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.Product;
import me.raycai.storify.admin.model.ProductVariant;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

@Component
@RepositoryEventHandler(Product.class)
public class ProductEventHandler {
    @HandleBeforeCreate
    public void beforeCreate(final Product product){
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        if(StringUtils.isEmpty(product.getPublishedScope())){
            product.setPublishedScope("global");
        }
        // default 1 variant
        if(CollectionUtils.isEmpty(product.getVariants())){
            product.setVariants(Arrays.asList(
                    new ProductVariant().setTitle("Default Title").setPrice("0.00").setSku("").setGrams(0)
                            .setInventoryPolicy("deny").setFulfillmentService("manual").setCreatedAt(new Date())
                            .setUpdatedAt(new Date()).setTaxable(true).setInventoryQuantity(1).setWeight(0.0F)
                            .setWeightUnit("lb").setOldInventoryQuantity(1).setRequiresShipping(true)
            ));
        }
        
        // set order of images
        /*
        if(!CollectionUtils.isEmpty(product.getImages())){
            for(int i=0;i<product.getImages().size();i++){
                product.getImages().get(i).setPosition(i+1);
                product.getImages().get(i).setCreatedAt(new Date());
                product.getImages().get(i).setUpdatedAt(new Date());
            }
        }*/
    }
    @HandleBeforeSave
    public void beforeSave(final Product product){
        product.setUpdatedAt(new Date());
        // set order of images
        /*
        if(!CollectionUtils.isEmpty(product.getImages())){
            for(int i=0;i<product.getImages().size();i++){
                product.getImages().get(i).setPosition(i+1);
                product.getImages().get(i).setUpdatedAt(new Date());

            }
        }*/
    }
}
