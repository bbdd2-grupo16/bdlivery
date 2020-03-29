package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product{

    @Id
    @GeneratedValue;
    private Long id;

    private String name; /*nombre del producto a ser creado*/
    private Float price; /*precio actual del producto*/
    private Float weight; /*peso actual del producto*/

    @ManyToOne; // Un producto tiene un unico productor
    private Supplier supplier; /*el productor del producto*/

    public Product() {

    }

    public Product(String name, Float price, Float weight, Supplier supplier) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.supplier = supplier;
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