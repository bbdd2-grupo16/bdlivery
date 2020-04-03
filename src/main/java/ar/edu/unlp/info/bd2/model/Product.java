package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; /*nombre del producto a ser creado*/
    private Float price; /*precio actual del producto*/
    private Float weight; /*peso actual del producto*/

    @ManyToOne // Un producto tiene un unico productor
    private Supplier supplier; /*el productor del producto*/

//    private List<Float> prices;

    public Product() {

    }

    public Product(String name, Float price, Float weight, Supplier supplier) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.supplier = supplier;
//        this.prices = new ArrayList<Float>();
    }

    public Long getId() {
        return id;
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

}