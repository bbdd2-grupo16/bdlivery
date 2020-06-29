package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {

  @Query("select o from Order o inner join RecordState s where o.client.username = ?1 and s.status = delivered")
  List<Order> findDeliveredOrdersForUser(String username);

  @Query("select o from Order o where o.client.username = ?1")
  List<Order> findAllOrdersMadeByUser(String username);

}
