package ar.edu.unlp.info.bd2.model;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class User implements PersistentObject{

     @BsonId
     private ObjectId objectId;
     private String username; /*nombre de usuario del usuario*/
     private String email; /*email del usuario*/
     private String password; /*password clave del usuario*/
     private String name; /*nombre y apellido del usuario*/
     private Date dateOfBirth; /*fecha de nacimiento del usuario*/

     //@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
     //@JoinColumn(name="user_id")
     @BsonIgnore
     private List<Order> orders;

     public User() { //jpa only
     }

     public User(String email, String password, String username, String name, Date dateOfBirth) {
          this.email = email;
          this.password = password;
          this.username = username;
          this.name = name;
          this.dateOfBirth = dateOfBirth;
//          this.orders = new ArrayList<Order>();
     }

     @Override
     public ObjectId getObjectId() {
          return objectId;
     }

     @Override
     public void setObjectId(ObjectId objectId) {
          this.objectId = objectId;
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
//
//     public List<Order> getOrders() {
//          return orders;
//     }

//     public void setOrders(Order orders) {
//          this.orders = orders;
//     }

}