package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ORDER")
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    private Date dateOfOrder; /*timestamp de la fecha en que fue realizado el pedido*/
    
    private String address; /*dirección en la cual se debe entregar el pedido*/
    
    private Float coordX; /*coordenada X de la dirección*/
    
    private Float coordY; /*coordenada Y de la dirección*/
    
    @ManyToOne // una orden solo pertenece a un usuario y un usuario puede tener varias ordenes
    private User client; /*cliente que realizó el pedido*/

    private String state;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="order_id")
    private List<ProductOrder> products;


    public Order() {
    }

    public Order(Date date, String address, Float coordX, Float coordY, User user) {
    	this.dateOfOrder = date;
    	this.address = address;
    	this.coordX = coordX;
    	this.coordY = coordY;
    	this.client = user;
        this.state = "Pending";
        this.products = new ArrayList<>();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Date getDateOfOrder() { return dateOfOrder; }

    public void setDateOfOrder(Date dateOfOrder) { this.dateOfOrder = dateOfOrder; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public Float getCoordX() { return coordX; }

    public void setCoordX(Float coordX) { this.coordX = coordX; }

    public Float getCoordY() { return coordY; }

    public void setCoordY(Float coordY) { this.coordY = coordY; }

    public User getClient() { return client; }

    public void setClient(User client) { this.client = client; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public List<ProductOrder> getProducts() { return products; }

    public void setProducts(List<ProductOrder> products) { this.products = products; }
}