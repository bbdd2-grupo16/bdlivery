package ar.edu.unlp.info.bd2.repositories;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.ProductOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductOrderRepository extends CrudRepository<ProductOrder, Long> {

    @Nullable
    @Query("SELECT DISTINCT po.product FROM ProductOrder AS po WHERE po.order.dateOfOrder = :day")
    List<Product> getSoldProductsOn(@Param("day") Date day);
}
