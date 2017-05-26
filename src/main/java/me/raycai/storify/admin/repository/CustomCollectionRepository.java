package me.raycai.storify.admin.repository;

import me.raycai.storify.admin.model.CustomCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "custom_collections", path = "custom_collections")
public interface CustomCollectionRepository extends PagingAndSortingRepository<CustomCollection, String> {
    @RestResource(path = "byPublished", rel = "custom_collections")
    Page findByPublished(@Param("published") boolean published, Pageable p);

    @RestResource(path = "byTitle", rel = "custom_collections")
    Page findByTitle(@Param("title") String title, Pageable p);
}
