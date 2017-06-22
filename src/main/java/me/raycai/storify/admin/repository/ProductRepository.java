package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.validation.annotation.Validated;

@Validated
@RepositoryRestResource
public interface ProductRepository extends CrudRepository<Product, Long> {
}
