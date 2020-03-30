package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

import java.util.Date;
import ar.edu.unlp.info.bd2.services.DBliveryException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

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

    /**
     * Actualiza el precio del producto manteniendo el historial de cambios de precio del mismo.
     * @param id id del producto
     * @param price nuevo precio del producto
     * @param startDate fecha de inicio del nuevo precio
     * @return el producto modificado
     * @throws DBliveryException en caso de que no exista el producto para el id dado
     */
    public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException{
        return new Product();
    }

    /**
     * Obtiene el producto por id
     * @param id
     * @return el producto con el id provisto
     */
    public Product getProductById(Long id){
        return null;
    }

}