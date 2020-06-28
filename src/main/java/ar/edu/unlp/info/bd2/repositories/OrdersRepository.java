package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {

  @Query("select o from Order o where o.status.status = 'delivered' and o.client.username = ?1")
  List<Order> findByUsername(String username);

}
