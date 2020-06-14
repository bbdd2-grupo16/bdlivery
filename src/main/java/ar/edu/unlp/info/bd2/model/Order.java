package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Order extends Product implements PersistentObject {

    @BsonId
    private ObjectId objectId;

    private Date dateOfOrder; /*timestamp de la fecha en que fue realizado el pedido*/

    private String address; /*dirección en la cual se debe entregar el pedido*/

    private Float coordX; /*coordenada X de la dirección*/

    private Float coordY; /*coordenada Y de la dirección*/

    /**
     *  una orden solo pertenece a un usuario y un usuario puede tener varias ordenes
     */
    private User client; /*cliente que realizó el pedido*/

    private User delivery; /*User que realizó la entrega*/

    private List<RecordState> status; /*Listado de estados por los que pasó la orden*/

    private List<ProductOrder> products; /*Listado de productos que posee la orden*/

    private Point position;

    public Order() {
    }



    public Order(Date date, String address, Float coordX, Float coordY, User user) {
        this.dateOfOrder = date;
        this.address = address;
        this.coordX = coordX;
        this.coordY = coordY;
        this.client = user;
        this.products = new ArrayList<ProductOrder>();
        this.status = new ArrayList<RecordState>();
        this.status.add(new RecordState("Pending", date));
        Position pos = new Position(coordX, coordY);
        this.position = new Point(pos);
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getCoordX() {
        return coordX;
    }

    public void setCoordX(Float coordX) {
        this.coordX = coordX;
    }

    public Float getCoordY() {
        return coordY;
    }

    public void setCoordY(Float coordY) {
        this.coordY = coordY;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<ProductOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrder> products) {
        this.products = products;
    }

    public Order addProduct(ProductOrder new_product) {
        this.products.add(new_product);
        return this;
    }

    public void setDeliveryUser(User delivery) {
        this.delivery = delivery;
    }

    public User getDeliveryUser() {
        return delivery;
    }

    public List<RecordState> getStatus() {
        return this.status;
    }

    public void setStatus(List<RecordState> status) {
        this.status = status;
    }

    public void addState(String state) {
        this.status.add(new RecordState(state, new Date()));
    }

    public void addState(String state, Date date) {
        this.status.add(new RecordState(state, date));
    }

    public RecordState getLastStatus() {
        return this.getStatus().get(this.getStatus().size() - 1);
    }

    public Float getAmount() {
        Float amount = Float.valueOf("0");
        for ( ProductOrder po : this.products ) {
            amount = Float.sum(amount, po.getProduct().getPriceAt(this.dateOfOrder) * po.getQuantity());
        }
        return amount;
    }
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}