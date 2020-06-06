package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.repositories.DBliveryMongoRepository;

import java.util.Date;
import java.util.List;

public class DBliveryStatisticsServiceImpl implements DBliveryStatisticsService{

  private DBliveryMongoRepository repository;

  public DBliveryStatisticsServiceImpl(DBliveryMongoRepository repository) {this.repository = repository;}

  @Override
  public List<Order> getAllOrdersMadeByUser(String username) throws DBliveryException {
    List<Order> orders = repository.findOrdersMadeByUser(username);
    if (orders.size() != 0) {
      return orders;
    }
    throw new DBliveryException("El usuario no tiene ordenes");
  }

  @Override
  public List<Supplier> getTopNSuppliersInSentOrders(int n) {
    return null;
  }

  @Override
  public List<Order> getPendingOrders() {
    return null;
  }

  @Override
  public List<Order> getSentOrders() {
    return null;
  }

  @Override
  public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
    return null;
  }

  @Override
  public List<Order> getDeliveredOrdersForUser(String username) {
    return null;
  }

  @Override
  public Product getBestSellingProduct() {
    return null;
  }

  @Override
  public List<Product> getProductsOnePrice() {
    return null;
  }

  @Override
  public List<Product> getSoldProductsOn(Date day) {
    return null;
  }

  @Override
  public Product getMaxWeigth() {
    return null;
  }

  @Override
  public List<Order> getOrderNearPlazaMoreno() {
    return null;
  }
}
