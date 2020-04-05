package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
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

    public Optional<User> findUserById(Long id){
        Optional<User> user = this.sessionFactory.getCurrentSession().createQuery("FROM User where id = :user_id")
                .setParameter("user_id", id).uniqueResultOptional();
        return user;
    }

    public Optional<User> findUserByUsername(String username){
        Optional<User> user = this.sessionFactory.getCurrentSession().createQuery("FROM User where username = :username")
                .setParameter("username", username).uniqueResultOptional();
        return user;
    }

    public Optional<User> findUserByEmail(String email){
        String hql = "FROM User where email = :email";
        return (Optional<User>) this.sessionFactory.getCurrentSession().createQuery(hql).setParameter("email", email).uniqueResultOptional();
    }

    public Order findOrderById(Long id){
        String hql = "FROM ORDER WHERE id = :id";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql).setParameter("id", id);
        List<Order> orders = query.getResultList();

        return !orders.isEmpty() ? orders.get(query.getFirstResult()) : null;

    }

}