package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.Date;

public class RecordState implements PersistentObject {

    @BsonId
    private ObjectId objectId;
    private Date date; /*fecha en que se actualizo el estado*/
    private String status; /*Nombre del estado*/
//    @ManyToOne
    @BsonIgnore
    private Order order;

    public RecordState() {
    }

    public RecordState(String status, Date date) {
        this.date = date;
        this.status = status;
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
//
//    public Order getOrder() {
//        return order;
//    }

}
