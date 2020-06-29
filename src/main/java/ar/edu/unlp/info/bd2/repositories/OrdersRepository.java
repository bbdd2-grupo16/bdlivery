package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.RecordState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {

  List<Order> findByClientUsername(String username);

  List<Order> findByClientUsernameAndStatusStatusEquals(String username, String status);

  @Query("select rs.order from RecordState as rs group by rs.order.id having count(rs.order.id) = 1")
  List<Order> getPendingOrders();

  @Query("select rs.order from RecordState as rs where rs.status='Sent' and rs.order not in (select rs2.order from RecordState as rs2 where rs2.status = 'Delivered' and rs2.order = rs.order)")
  List<Order> getSentOrders();

  List<Order> findByStatusStatusEqualsAndDateOfOrderBetween(String status, Date startDate, Date endDate);

}
