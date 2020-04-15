package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
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
        List<Product> products = query.getResultList();

        return !products.isEmpty() ? products.get(query.getFirstResult()) : null;
    }

    public User findUserById(Long id){
        String hql = "FROM User where id = :user_id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("user_id", id);
        List<User> users = query.getResultList();

        return !users.isEmpty() ? users.get(query.getFirstResult()) : null;
    }

    public User findUserByUsername(String username){
        String hql = "FROM User where username = :username";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        List<User> users = query.getResultList();

        return !users.isEmpty() ? users.get(query.getFirstResult()) : null;
    }

    public User findUserByEmail(String email){
        String hql = "FROM User where email = :email";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("email", email);
        List<User> users = query.getResultList();

        return !users.isEmpty() ? users.get(query.getFirstResult()) : null;
    }

    public Order findOrderById(Long id){
        String hql = "from Order where id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql).setParameter("id", id);
        List<Order> orders = query.getResultList();

        return !orders.isEmpty() ? orders.get(query.getFirstResult()) : null;

    }

    public void updateOrder(Order o){
        this.sessionFactory.getCurrentSession().saveOrUpdate(o);
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

    /*Obtiene todas las ordenes entregadas por el repartidor con username <code>username</code>
    en los últimos 10 días*/
    public List<Order> findDeliveredOrdersForUser(String username) {

        String hql = "from Order where (dateOfOrder >= :startDate and dateOfOrder <= :endDate) and state = :state";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("username", username);
        query.setParameter("state", "Finish");

        return (List<Order>) query.getResultList();
    }

    /*Obtiene las ordenes que fueron enviadas luego de una hora de realizadas*/
    public List<Order> findSentMoreOneHour(Date startDate, Date endDate){
        String hql = "from Order as o " +
                    "inner join RecordStatus as rs on o.id = rs.order_id " +
                    "where (select date from RecordStatus where rs.state = 'Finish' and rs.order_id = o.id) - " +
                    "(select date from RecordStatus where rs.state = 'Pending' and rs.order_id = o.id) > 1" +
                    "and o.state = :state)";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("state", "Finish");

        return (List<Order>) query.getResultList();
    }

}