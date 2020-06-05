package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Date;

public class Price implements PersistentObject {

    @BsonId
    private ObjectId objectId;
    private Float price; /* @param precio del producto*/
    private Date startDate; /* @param inicio de precio en vigencia*/
    private Date endDate; /* @param fin de precio en vigencia*/

//    @ManyToOne // un precio solo pertenece a un producto y un producto puede tener varios precios
    private Product product; /*Producto al cual pertenece el precio*/

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

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
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