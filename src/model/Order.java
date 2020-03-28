import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;

@Entity
public class Order{
	@Id
	@GeneratedValue;
	private Long id;
	
    private Date dateOfOrder; /*timestamp de la fecha en que fue realizado el pedido*/
    
    private String address; /*dirección en la cual se debe entregar el pedido*/
    
    private Float coordX; /*coordenada X de la dirección*/
    
    private Float coordY; /*coordenada Y de la dirección*/
    
    @ManyToOne;// una orden solo pertenece a un usuario y un usuario puede tener varias ordenes
    private User client; /*cliente que realizó el pedido*/
    
    Order(){ //required jpa
    }
    
    public createOrder(Date date, String address, Float coordX, Float coordY, User user) {
    	this.dateOfOrder = date;
    	this.address = address;
    	this.coordX = coordX;
    	this.coordY = coordY;
    	this.client = user;
    }

}