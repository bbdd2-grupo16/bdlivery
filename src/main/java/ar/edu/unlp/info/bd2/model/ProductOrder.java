package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;


public class ProductOrder implements PersistentObject {

    @BsonId
    private ObjectId objectId;
    private Long quantity;

//    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Product.class)
//    private Product product;

//    @ManyToOne
//    private Order order;

    public ProductOrder() {

    }

    public ProductOrder(Long quantity, Product product, Order order) {
        this.quantity = quantity;
//        this.product = product;
//        this.order = order;
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public Long getQuantity() { return quantity; }

    public void setQuantity(Long quantity) { this.quantity = quantity; }
//
//    public Product getProduct() { return product; }
//
//    public void setProduct(Product product) { this.product = product; }
//
//    public Order getOrder() { return order; }
//
//    public void setOrder(Order order) { this.order = order; }

}
