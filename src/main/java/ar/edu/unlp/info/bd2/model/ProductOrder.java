package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.types.ObjectId;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_ORDER")
public class ProductOrder implements PersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Product.class)
    private Product product;

    @ManyToOne
    private Order order;

    public ProductOrder() {

    }

    public ProductOrder(Long quantity, Product product, Order order) {
        this.quantity = quantity;
        this.product = product;
        this.order = order;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getQuantity() { return quantity; }

    public void setQuantity(Long quantity) { this.quantity = quantity; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    @Override
    public ObjectId getObjectId() {
        return null;
    }

    @Override
    public void setObjectId(ObjectId objectId) {

    }
}
