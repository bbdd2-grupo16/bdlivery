package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.RecordState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {

  @Query("select rs.order from RecordState as rs where rs.order.client.username = ?1 and rs.status = 'Delivered'")
  List<Order> getDeliveredOrdersForUser(String username);

  @Query("select o from Order o where o.client.username = ?1")
  List<Order> getAllOrdersMadeByUser(String username);

  @Query("select rs.order from RecordState as rs group by rs.order.id having count(rs.order.id) = 1")
  List<Order> getPendingOrders();

  @Query("select rs.order from RecordState as rs where rs.status='Sent' and rs.order not in (select rs2.order from RecordState as rs2 where rs2.status = 'Delivered' and rs2.order = rs.order)")
  List<Order> getSentOrders();
}
