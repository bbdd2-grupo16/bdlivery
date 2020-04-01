package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.*;

import javax.persistence.*;
import java.util.List;

public class DBliveryRepository{

    @Autowired
    private SessionFactory sessionFactory;

    public Object save (Object obj) throws Exception{
        this.sessionFactory.getCurrentSession().save(obj);
        return obj;
    }
    public List<Product> findProductByName(String name) {
        String hql = "from Product " + "where name = :product_name ";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("product_name", name);

        return (List<Product>) query.getResultList();

    }

    public User persistUser(User user) {
        this.save(user);
        return user;
    }
}