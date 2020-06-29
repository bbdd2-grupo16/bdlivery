package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Long> {

    List<Product> findByNameContaining(@Param("name") String name);

    @Nullable
    Product findFirstByOrderByWeightDesc();

    @Nullable
    @Query("SELECT p FROM Product p GROUP BY p HAVING size(p.prices) = 1")
    List<Product> getProductsOnePrice();
}
