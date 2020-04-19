package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Query.*;
import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DBliveryRepository{

    @Autowired
    private SessionFactory sessionFactory;

    public Object save(Object object) throws Exception{
        try {
            this.sessionFactory.getCurrentSession().save(object);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return object;
    }


    public Order updateOrder(Order o){
        try {
            this.sessionFactory.getCurrentSession().saveOrUpdate(o);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return o;
    }

    public List<Product> findProductByName(String name) {
        String hql = "from Product where name like concat('%', :product_name, '%')";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("product_name", name);

        return (List<Product>) query.getResultList();
    }

    public Product findProductById(Long id){
        String hql = "from Product " + "where id = :product_id ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("product_id", id);

        return (Product) query.getSingleResult();
    }

    public User findUserById(Long id){
        String hql = "FROM User where id = :user_id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("user_id", id);

        return (User) query.getSingleResult();
    }

    public User findUserByUsername(String username){
        String hql = "FROM User where username = :username";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);

        return (User) query.getSingleResult();
    }

    public User findUserByEmail(String email){
        String hql = "FROM User where email = :email";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("email", email);

        return (User) query.getSingleResult();
    }

    public Order findOrderById(Long id){
        String hql = "from Order where id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql).setParameter("id", id);

        return (Order) query.getSingleResult();
    }

    /*Obtiene todas las ordenes entregadas entre dos fechas*/
    public List<Order> findDeliveredOrdersInPeriod(Date startDate, Date endDate){
        String hql = "from Order as o " +
                "inner join RecordState as rs on rs.order = o " +
                "where (o.dateOfOrder >= :startDate and o.dateOfOrder <= :endDate)" +
//                "and :state = (select state, max(date) from RecordState group by rs.order)";
                "and :state = (select rs.state from RecordState order by rs.date desc 1)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("state", "Sent");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene todas las órdenes entregadas para el cliente con username <code>username</code>
    en los últimos 10 días*/
    public List<Order> findDeliveredOrdersForUser(String username) {

        String hql = "from Order as o inner join User as u on u.id = o.client " +
                "inner join RecordState as rs on o = rs.order " +
                "where u.username = :username " +
                "and rs.state = :state";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron enviadas luego de una hora de realizadas*/
    public List<Order> findSentMoreOneHour(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DAY_OF_MONTH, -10);
//        Date startDate = calendar.getTime();

        String hql = "from Order as o "+
                    "where (select DATE_FORMAT(date,'%d-%M-%Y') from RecordState as rs where rs.state = 'Delivered' and rs.order = o) - " +
                    "(select DATE_FORMAT(date,'%d-%M-%Y') from RecordState rs2 where rs2.state = 'Pending' and rs2.order = o) >= 1"
                    ;
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
//        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    //* Obtiene las ordenes que fueron entregadas el mismo día de realizadas
    public List<Order> findDeliveredOrdersSameDay() {
        String hql = "from Order as o " +
                "where (select DATE_FORMAT(date,'%d-%M-%Y') from RecordState as rs " +
                        "where rs.state = 'Delivered' and rs.order = o) = " +
                        "(select DATE_FORMAT(date,'%d-%M-%Y') from RecordState as rs2 " +
                        "where rs2.state = 'Pending' and rs2.order = o)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (List<Order>) query.getResultList();
    }

    // Obtiene los 5 repartidores que menos ordenes tuvieron asignadas (tanto sent como delivered)
    public List<User> find5LessDeliveryUsers() {
        String hql = "select u, count(o.id) " +
                "from User u, Order o where o.delivery.id = u.id " +
                "and o.state = :sent or o.state = :delivered "+
                "group by o.id order by count(o.id)";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("delivered", "Delivered");
        query.setParameter("sent", "Sent");

        return (List<User>) query.setMaxResults(5).getResultList();
    }

    public List<Product> findProductsIncreaseMoreThan100(){
        String hql = "from Product as product left join product.prices as price order by price.startDate ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Product>) query.getResultList();
    }

    /*Mari*/
    /*Obtiene el proveedor con el producto de menor valor historico de la plataforma*/
    public Supplier findSupplierLessExpensiveProduct() {
        String hql = "select supplier from Supplier as supplier where exists" +
                " (select supplier, min(price.price)" +
                " from Supplier as supplier" +
                " inner join Product as product on product.supplier = supplier" +
                " inner join Price as price on price.product = product" +
                " group by supplier.id)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setFirstResult(1);
        query.setMaxResults(1);
        return (Supplier) query.getSingleResult();
    }

    /*Obtiene los proveedores que no vendieron productos en un day*/
    public List <Supplier> findSupplierDoNotSellOn(Date day){
        String hql = " select supplier" +
                " from RecordState as recordState" +
                " inner join Order as order on order = recordState.order" +
                " inner join ProductOrder as productOrder on order = productOrder.order" +
                " left join Product as product on productOrder.product = product" +
                " inner join Supplier as supplier on product.supplier = supplier" +
                " where order.dateOfOrder = :day" +
                " and productOrder.product = null";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        return (List <Supplier>) query.getResultList();
    }

    /*Obtiene los productos vendidos en un day*/
    public List<Product> findSoldProductsOn(Date day){
        String hql = "select distinct product.id" +
                " from Product as product" +
                " inner join ProductOrder as productOrder on product = productOrder.product" +
                " inner join Order as order on order = productOrder.order" +
                " inner join RecordState as recordState on order = recordState.order" +
                " where recordState.state = :state or recordState.state = :stateDelivered" +
                " and order.dateOfOrder = :day";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Sent");
        query.setParameter("stateDelivered", "Delivered");
        query.setParameter("day", day);

        return (List<Product>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron entregadas en mas de un dia desde que fueron iniciadas*/
    public List<Order> findOrdersCompleteMoreThanOneDay(){
        String hql = "select order" +
                " from Order as order" +
                " inner join RecordState as recordState on order = recordState.order" +
                " where recordState.state = :state" +
                " and recordState.date > order.dateOfOrder";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Delivered");
        return (List<Order>) query.getResultList();
    }

    /*Obtiene el listado de productos con su precio en una fecha dada*/
    public List <Object[]> findProductsWithPriceAt(Date day){
        String hql = "select product, price" +
                " from Price as price" +
                " inner join Product as product on price.product = product" +
                " where :day BETWEEN price.startDate AND price.endDate";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        return (List <Object[]>) query.getResultList();
    }

    /*Obtiene la lista de productos que no se han vendido*/
    public List<Product> findProductsNotSold(){
        String hql = "select product from Product as product" +
                " left join ProductOrder as productOrder on product = productOrder.product" +
                " where productOrder.product = null";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Product>) query.getResultList();
    }

    /*Obtiene la/s orden/es con mayor cantidad de productos ordenados de la fecha dada*/
    public List<Order> findOrderWithMoreQuantityOfProducts(Date day){
        String hql = "select order from Order as order where exists" +
                " (select order, max(po.quantity) as max" +
                " from Order as o" +
                " inner join ProductOrder as po on po.order = o" +
                " where o.dateOfOrder = :day" +
                " group by o.id)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        query.setFirstResult(1);
        query.setMaxResults(1);
        return (List<Order>) query.getResultList();
    }
}