package ar.edu.unlp.info.bd2.model;

@Entity(name="User")
public class User{

     @Id
     private String id;
     private String username; /*nombre de usuario del usuario*/
     private String email; /*email del usuario*/
     private String password; /*password clave del usuario*/
     private String name; /*nombre y apellido del usuario*/
     private Date dateOfBirth; /*fecha de nacimiento del usuario*/
     @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
     @JoinColumn(name="user_id")
     private Order orders = new ArrayList<>();

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getEmail() {
          return email;
     }

     public void setEmail(String email) {
          this.email = email;
     }

     public String getPassword() {
          return password;
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public String getUsername() {
          return username;
     }

     public void setUsername(String username) {
          this.username = username;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public Date getDateOfBirth() {
          return dateOfBirth;
     }

     public void setDateOfBirth(Date dateOfBirth) {
          this.dateOfBirth = dateOfBirth;
     }

     public Order getOrders() {
          return orders;
     }

     public void setOrders(Order orders) {
          this.orders = orders;
     }

     public User() {
     }

     public User(String email, String password, String username, String name, Date dateOfBirth) {
          this.email = email;
          this.password = password;
          this.username = username;
          this.name = name;
          this.dateOfBirth = dateOfBirth;
     }
}