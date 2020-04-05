package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="PRICE")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float price; /* @param precio del producto*/
    private Date startDate; /* @param inicio de precio en vigencia*/
    private Date endDate; /* @param fin de precio en vigencia*/

    @ManyToOne // un precio solo pertenece a un producto y un producto puede tener varios precios
    private Product product; /*cliente que realizó el pedido*/

    public Price() {
    }

    public Price(Float price) {
        this.price = price;
        this.startDate = new Date();
        this.endDate = null;
    }

    public Price(Float price, Date startDate) {
        this.price = price;
        this.startDate = startDate;
        this.endDate = null;
    }

    public Long getId() {
        return id;
    }
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Product getProduct() {
        return product;
    }

}