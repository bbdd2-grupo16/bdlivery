package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

@Entity
@Table(name="STATUS")
public class Status{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long order;

    public Status() {
    }

    public Long getId() {
        return id;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

}