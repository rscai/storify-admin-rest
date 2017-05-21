package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.CustomCollection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "custom_collections",path="custom_collections")
public interface CustomCollectionRepository extends CrudRepository<CustomCollection, String> {
}
