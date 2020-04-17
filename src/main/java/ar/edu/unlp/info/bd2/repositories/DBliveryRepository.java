package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
        String hql = "from Order where (dateOfOrder >= :startDate and dateOfOrder <= :endDate) and state = :state";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("state", "Sent");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene todas las órdenes entregadas para el cliente con username <code>username</code>
    en los últimos 10 días*/
    public List<Order> findDeliveredOrdersForUser(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date startDate = calendar.getTime();

        String hql = "from Order as o inner join User as u on u.id = o.client " +
                "where u.username = :username and " +
                "(dateOfOrder >= :startDate) and state = :state";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        query.setParameter("startDate", startDate);
//        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron enviadas luego de una hora de realizadas*/
    public List<Order> findSentMoreOneHour(){
        String hql = "from Order as o " +
                    "inner join RecordStatus as rs on o.id = rs.order_id " +
                    "where (select date from RecordStatus where rs.state = 'Delivered' and rs.order_id = o.id) - " +
                    "(select date from RecordStatus where rs.state = 'Pending' and rs.order_id = o.id) > 1" +
                    "and o.state = :state)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    //* Obtiene las ordenes que fueron entregadas el mismo día de realizadas
    public List<Order> findDeliveredOrdersSameDay() {
        String hql = "from Order as o " +
                "inner join RecordStatus as rs on o.id = rs.order_id " +
                "where (select date from RecordStatus where rs.state = 'Delivered' and rs.order_id = o.id) = " +
                "(select date from RecordStatus where rs.state = 'Pending' and rs.order_id = o.id)" +
                "and o.state = :state)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Delivered");

        return (List<Order>) query.getResultList();
    }

    // Obtiene los 5 repartidores que menos ordenes tuvieron asignadas (tanto sent como delivered)
    public List<User> find5LessDeliveryUsers() {
        String hql = "select u, count(o.id) " +
                "from User u, Order o where o.delivery = u.id " +
                "and o.state = :sent or o.state = :delivered "+
                "group by o.id order by count(o.id)";

        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("delivered", "Delivered");
        query.setParameter("sent", "Sent");

        return (List<User>) query.setMaxResults(5).getResultList();
    }

}