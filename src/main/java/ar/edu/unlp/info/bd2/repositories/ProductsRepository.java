package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);
}
