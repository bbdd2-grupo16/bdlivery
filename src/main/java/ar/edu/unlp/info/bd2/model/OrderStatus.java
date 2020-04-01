package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

@Entity
@Table(name="ORDER_STATUS_")
public class OrderStatus{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long order;

    public OrderStatus() {
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