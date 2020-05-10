package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Query.*;
import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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

    //Obtiene todas las ordenes realizadas por el usuario con username
    public  List<Order> getAllOrdersMadeByUser(String username){
        String hql ="select o from Order as o inner join User as u on u = o.client "+
                "where u.username = :username ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        return (List<Order>) query.getResultList();
    }
    
    //Obtiene todos los usuarios que han gastando más de amount en alguna orden en la plataforma
    public List<User> getUsersSpendingMoreThan(Float amount){
        String hql = "select distinct(u) from Order as o inner join User as u on u = o.client "+
                "inner join ProductOrder as po on o = po.order "+
                "inner join Product as prod on prod = po.product "+
                "inner join Price as p on prod = p.product "+
                " where (p.endDate is null and o.dateOfOrder >= p.startDate ) "+
                " or (p.endDate is not null and o.dateOfOrder >= p.startDate "+
                " and o.dateOfOrder <= p.endDate) "+
                "group by o "+
                "having sum(p.price * po.quantity) > :amount ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("amount", amount.doubleValue());
        return (List<User>) query.getResultList();
    }

    //Obtiene los n proveedores que más productos tienen en ordenes que están siendo enviadas
    public List<Supplier> getTopNSuppliersInSentOrders(int n) {
        String hql = "select s from ProductOrder as po inner join Product as p on po.product = p.id "+
                "inner join Supplier as s on p.supplier = s.id "+
                "inner join RecordState as rs on rs.order = po.order "+
                "where rs.state='Sent' group by s.id order by count(s) desc ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Supplier>) query.setFirstResult(0).setMaxResults(n).getResultList();
    }


    //Obtiene los 9 productos más costosos
    public List<Product> getTop10MoreExpensiveProducts(){
        String hql = "select p from Product p " +
                "inner join Price as pr on pr.product = p " +
                "where pr.id in ( select max(id) from Price group by product) "+
                "order by pr.price desc";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Product>) query.setFirstResult(0).setMaxResults(9).getResultList();
    }

    //Obtiene los 6 usuarios que más cantidad de ordenes han realizado
    public List<User> getTop6UsersMoreOrders(){
        String hql = "select u, count(o.id) from Order as o inner join User as u on u = o.client "+
                "group by u order by count(o.id) desc";
        List<Object []> query = this.sessionFactory.getCurrentSession().createQuery(hql).setMaxResults(6).list();
        List<User> users = new ArrayList<>();
        for (Object [] row : query) {
            users.add((User) row[0]);
        }

        return (List<User>) users;
    }

    //Obtiene todas las ordenes canceladas entre dos fechas
    public List <Order> getCancelledOrdersInPeriod(Date startDate, Date endDate){
        String hql = "select o from Order as o " +
                "inner join RecordState as rs on rs.order = o " +
                "where (o.dateOfOrder >= :start and o.dateOfOrder <= :end)" +
                "and :state = rs.state";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        query.setParameter("state", "Cancelled");

        return (List<Order>) query.getResultList();
    }

    //Obtiene el listado de las ordenes pendientes
    public List <Order>  getPendingOrders(){
        String hql = "select o from Order as o " +
                "inner join RecordState as rs on rs.order = o " +
                "group by o.id having count(o.id) = 1 ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Order>) query.getResultList();

    }

    //Obtiene el listado de las ordenes enviadas y no entregadas
    public List <Order>  getSentOrders(){
        String hql = "select o from Order as o " +
                "join o.status as os " +
                "where os.state='Sent' and os.order not in "+
                "(select rs1.order from RecordState as rs1 where rs1.state!='Pending' "+
                "and rs1.state!='Sent')";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (List<Order>) query.getResultList();
    }

    /*Obtiene todas las ordenes entregadas entre dos fechas*/
    public List<Order> findDeliveredOrdersInPeriod(Date startDate, Date endDate){
        String hql = "select rs.order from RecordState as rs " +
                "where (rs.order.dateOfOrder >= :startDate and rs.order.dateOfOrder <= :endDate) " +
                "and rs.state = :state";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene todas las órdenes entregadas para el cliente con username <code>username</code>*/
    public List<Order> findDeliveredOrdersForUser(String username) {

        String hql = "select rs.order from RecordState as rs " +
                "where rs.order.client.username = :username " +
                "and rs.state = :state";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron enviadas luego de una hora de realizadas*/
    public List<Order> findSentMoreOneHour(){

        String hql = "from Order as o "+
                    "where (select DATE_FORMAT(date,'%d-%M-%Y') from RecordState as rs where rs.state = 'Sent' and rs.order = o) - " +
                    "(select DATE_FORMAT(date,'%d-%M-%Y') from RecordState rs2 where rs2.state = 'Pending' and rs2.order = o) >= 1"
                    ;

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (List<Order>) query.getResultList();
    }

    //* Obtiene las ordenes que fueron entregadas el mismo día de realizadas
    public List<Order> findDeliveredOrdersSameDay() {
        String hql = "select rs.order from RecordState as rs " +
                "where DATE_FORMAT(rs.date,'%d-%M-%Y') = DATE_FORMAT(rs.order.dateOfOrder,'%d-%M-%Y')" +
                "and rs.state = :state";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    // Obtiene los 5 repartidores que menos ordenes tuvieron asignadas (tanto sent como delivered)
    public List<User> find5LessDeliveryUsers() {
        String hql = "select o.delivery from Order o " +
                "group by o.delivery having count(o.id) > 0 order by count(o.id)";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (List<User>) query.setFirstResult(0).setMaxResults(5).getResultList();
    }

    //  Obtiene el producto con más demanda
    public Product findBestSellingProduct() {
        String hql = "select po.product from ProductOrder as po " +
                "group by po.product order by count(po.quantity) desc";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (Product) query.setFirstResult(0).setMaxResults(1).getSingleResult();
    }

    //  Obtiene los productos que no cambiaron su precio
    public List<Product> findProductsOnePrice() {
        String hql = "select pr.product from Price as pr " +
                "group by pr.product having count(pr.product) = 1 ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        return (List<Product>) query.getResultList();
    }

    //  Obtiene la lista de productos que han aumentado más de un 100% desde su precio inicial
    public List<Product> findProductIncreaseMoreThan100() {
        String hql = "select pr.product from Price as pr " +
                "group by pr.product having max(pr.price) - min(pr.price) >= min(pr.price)";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);

        return (List<Product>) query.getResultList();
    }

    /*Obtiene el proveedor con el producto de menor valor historico de la plataforma*/
    public Supplier findSupplierLessExpensiveProduct() {
        String hql = "select supplier from Supplier as supplier where exists" +
                " (select p.product.supplier, min(p.price)" +
                " from Price as p" +
                " group by p.product.supplier.id)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setFirstResult(1);
        query.setMaxResults(1);
        return (Supplier) query.getSingleResult();
    }

    /*Obtiene los proveedores que no vendieron productos en un day*/
    public List <Supplier> findSupplierDoNotSellOn(Date day){
        String hql = " select supplier from Supplier as supplier" +
                " where supplier not in" +
                " (select distinct po.product.supplier" +
                " from ProductOrder as po" +
                " where po.order.dateOfOrder = DATE_FORMAT(:day, '%Y/%m/%d'))";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        return (List <Supplier>) query.getResultList();
    }

    /*Obtiene los productos vendidos en un day*/
    public List<Product> findSoldProductsOn(Date day){
        String hql = "select distinct po.product" +
                " from ProductOrder as po" +
                " where po.order.dateOfOrder = DATE_FORMAT(:day, '%Y/%m/%d')";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        return (List<Product>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron entregadas en mas de un dia desde que fueron iniciadas*/
    public List<Order> findOrdersCompleteMoreThanOneDay(){
        String hql = "select rs.order" +
                " from RecordState as rs" +
                " where rs.state = :state" +
                " and DATE_FORMAT(rs.date,'%Y/%m/%d') >= DATE_FORMAT(adddate(rs.order.dateOfOrder, 1),'%Y/%m/%d')";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Delivered");
        return (List<Order>) query.getResultList();
    }

    /*Obtiene el listado de productos con su precio en una fecha dada*/
    public List <Object[]> findProductsWithPriceAt(Date day) {

        String hql = "select price.product, price.price" +
                " from Price as price" +
                " where (DATE_FORMAT(price.startDate ,'%Y/%m/%d') <= DATE_FORMAT(:day,'%Y/%m/%d') " +
                    "and DATE_FORMAT(:day,'%Y/%m/%d')  <= DATE_FORMAT(price.endDate,'%Y/%m/%d')) " +
                    "or (DATE_FORMAT(price.startDate,'%Y/%m/%d') <= DATE_FORMAT(:day,'%Y/%m/%d') " +
                    "and price.endDate is null)";

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
        String hql = "select po.order from ProductOrder as po" +
                " where po.order.dateOfOrder = :day" +
                " group by po.order" +
                " having max( sum(po.quantity) )";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("day", day);
        return (List<Order>) query.getResultList();
    }
}