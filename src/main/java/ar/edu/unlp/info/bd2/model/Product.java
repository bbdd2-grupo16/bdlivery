package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Product implements PersistentObject{

    @BsonId
    private ObjectId objectId;
    private String name; /*nombre del producto a ser creado*/
    private Float price; /*precio actual del producto*/
    private Float weight; /*peso actual del producto*/
    private Date date; /*Fecha en la que se actualiza el precio*/

//    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
//    @JoinColumn(name="product_id")
    @BsonIgnore
    private List<Price> prices; /*Historial de precios*/
//
//    @ManyToOne // Un producto tiene un unico productor
    private Supplier supplier; /*el productor del producto*/

    public Product() {

    }

    public Product(String name, Float price, Float weight, Supplier supplier) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.supplier = supplier;
        this.date = new Date();
        this.prices = new ArrayList<Price>();
        this.prices.add(new Price(price));
    }

    public Product(String name, Float price, Float weight, Supplier supplier, Date date){
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.supplier = supplier;
        this.date = date;
        this.prices = new ArrayList<Price>();
        this.prices.add(new Price(price, date));
    }

    @Override
    public ObjectId getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Price> prices) {
        this.prices = prices;
    }
    public void addPrice(Price price) {
        this.prices.add(price);
        if (this.prices.size() > 1){
            this.prices.get(this.prices.size() - 2).setEndDate(price.getStartDate());
        }
    }

    public Float getPriceAt(Date day) {
        for (Price p : this.getPrices()) {
            if ( (p.getStartDate().before(day)) && (p.getEndDate() != null) && (p.getEndDate().after(day)) ){
                return p.getPrice();
            }
        }
        return this.getPrices().get(this.getPrices().size() - 1).getPrice();
    }

}