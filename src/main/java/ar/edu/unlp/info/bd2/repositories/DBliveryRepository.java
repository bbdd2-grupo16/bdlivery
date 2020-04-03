package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
import java.util.List;

public class DBliveryRepository{

    @Autowired
    private SessionFactory sessionFactory;

    /*public Object save (Object obj) throws Exception {
        this.sessionFactory.getCurrentSession().save(obj);
        return obj;
    }*/

    public Object save(Object object) throws Exception{
        System.out.println(this.sessionFactory.getCurrentSession());
        System.out.println(object);
        try {
            this.sessionFactory.getCurrentSession().save(object);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return object;
    }

    public List<Product> findProductByName(String name) {
        String hql = "FROM Product where name = :product_name";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("product_name", name);

        return (List<Product>) query.getResultList();

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
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("email", email);
        return Optional<User> query.uniqueResultOptional();
    }

}