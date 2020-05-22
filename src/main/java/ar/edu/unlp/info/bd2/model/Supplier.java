package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.types.ObjectId;

import javax.persistence.*;

@Entity
@Table(name="SUPPLIER")
public class Supplier implements PersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; /* @param name nombre del productor*/

    private String cuil; /* @param cuil cuil del productor*/

    private String address; /* @param address dirección del productor*/

    private Float coordX; /* @param coordX  coordenada X de la dirección del productor*/

    private Float coordY; /* @param coordY coordeada Y de la dirección del produtor*/

    public Supplier() {
    }

    public Supplier(String name, String cuil, String address, Float coordX, Float coordY) {
        this.name = name;
        this.cuil = cuil;
        this.address = address;
        this.coordX = coordX;
        this.coordY = coordY;
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

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
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

    @Override
    public ObjectId getObjectId() {
        return null;
    }

    @Override
    public void setObjectId(ObjectId objectId) {

    }
}