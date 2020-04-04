package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT_ORDER")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Product.class)
    private Product product;

    @ManyToOne
    private Long order;

    public ProductOrder(Long quantity, Product product, Long order) {
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

    public Long getOrder() { return order; }

    public void setOrder(Long order) { this.order = order; }
}
