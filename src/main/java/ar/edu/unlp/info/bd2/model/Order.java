package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ORDER_")
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

    @ManyToOne
    private User delivery;

    //private String state;

    @ElementCollection
    private List<String> status;

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
        this.products = new ArrayList<ProductOrder>();
        this.status = new ArrayList<String>();
        this.status.add("Pending");
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

    public List<ProductOrder> getProducts() { return products; }

    public void setProducts(List<ProductOrder> products) { this.products = products; }

    public Order addProduct(Long quantity, Product product) {
        ProductOrder new_product = new ProductOrder(quantity, product,this);
        this.products.add(new_product);
        return this;
    }

    public void setDeliveryUser(User delivery) {
        this.delivery = delivery;
    }

    public User getDeliveryUser() {
        return delivery;
    }

    public List<String> getStatus() {
        return this.status;
    }

    public void addState(String state) {
        this.status.add(state);
    }

    public String getLastStatus(){
        return this.getStatus().get(this.getStatus().size()-1);
    }
}