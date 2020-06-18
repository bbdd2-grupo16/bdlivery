package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends CrudRepository<Product, Long> {

    List<Product> findByNameContaining(@Param("name") String name);

    @Query("SELECT coalesce(max(p.weight), 0) FROM Product p")
//    @Query("SELECT p FROM Product p GROUP BY p HAVING max(p.weight)")
    Product getMaxWeight();

    @Query("SELECT p FROM Product p GROUP BY p HAVING count (p.prices) = 1")
    List<Product> getProductsOnePrice();
}
