package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.CustomCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "custom_collections", path = "custom_collections")
public interface CustomCollectionRepository extends PagingAndSortingRepository<CustomCollection, String> {
    Page findByPublished(@Param("published") boolean published, Pageable p);
}
