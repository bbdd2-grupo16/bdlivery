package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;


@Entity
public class Supplier{

    @Id
    @GeneratedValue;
    private Long id;

    private String name; /* @param name nombre del productor*/

    private String cuil; /* @param cuil cuil del productor*/

    private String address; /* @param address dirección del productor*/

    private Float coordX; /* @param coordX  coordenada X de la dirección del productor*/

    private Float coordY; /* @param coordY coordeada Y de la dirección del produtor*/

    public Supplier(String name, String cuil, String address, Float coordX, Float coordY) {
        this.name = name;
        this.cuil = cuil;
        this.address = address;
        this.coordX = coordX;
        this.coordY = coordY;
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
}