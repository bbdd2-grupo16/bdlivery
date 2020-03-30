package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ORDERSTATUS")

public class OrderStatus{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long order;

    public OrderStatus() {
    }

}