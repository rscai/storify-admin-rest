package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "product", path="product")
public interface ProductRepository extends CrudRepository<Product, String> {
}
