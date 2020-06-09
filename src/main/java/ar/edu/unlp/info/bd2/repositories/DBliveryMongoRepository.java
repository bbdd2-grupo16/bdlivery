package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;
import com.mongodb.client.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

public class DBliveryMongoRepository {

    @Autowired private MongoClient client;


    public void saveAssociation(PersistentObject source, PersistentObject destination, String associationName) {
        Association association = new Association(source.getObjectId(), destination.getObjectId());
        this.getDb()
            .getCollection(associationName, Association.class)
            .insertOne(association);
    }

    public MongoDatabase getDb() {
    return this.client.getDatabase("bd2_grupo" + this.getGroupNumber());
    }

    private Integer getGroupNumber() {
        return 16;
    }

    public <T extends PersistentObject> List<T> getAssociatedObjects(
        PersistentObject source, Class<T> objectClass, String association, String destCollection) {
        AggregateIterable<T> iterable =
        this.getDb()
            .getCollection(association, objectClass)
            .aggregate(
                Arrays.asList(
                    match(eq("source", source.getObjectId())),
                    lookup(destCollection, "destination", "_id", "_matches"),
                    unwind("$_matches"),
                    replaceRoot("$_matches")));
        Stream<T> stream =
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
        return stream.collect(Collectors.toList());
    }

    public <T extends PersistentObject> List<T> getObjectsAssociatedWith(
        ObjectId objectId, Class<T> objectClass, String association, String destCollection) {
        AggregateIterable<T> iterable =
            this.getDb()
                .getCollection(association, objectClass)
                .aggregate(
                    Arrays.asList(
                        match(eq("destination", objectId)),
                        lookup(destCollection, "source", "_id", "_matches"),
                        unwind("$_matches"),
                        replaceRoot("$_matches")));
        Stream<T> stream =
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
        return stream.collect(Collectors.toList());
    }

    public Object save(Object object, String collection, Class className){
        MongoCollection<Object> objectMongoCollection = this.getDb().getCollection(collection, className);
        objectMongoCollection.insertOne(object);
        return object;
    }

    public Product updateProduct(Product product){
        MongoCollection<Product> productMongoCollection = this.getDb().getCollection("products", Product.class);
        try {
            productMongoCollection.replaceOne(eq("_id", product.getObjectId()), product);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return product;
    }

    public Order updateOrder(Order order){
        MongoCollection<Order> orderMongoCollection = this.getDb().getCollection("orders", Order.class);
        try {
            orderMongoCollection.replaceOne(eq("_id", order.getObjectId()), order);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return order;
    }

    public User findUserById(ObjectId id){
        MongoCollection<User> collection = this.getDb().getCollection("users",User.class);
        return collection.find(eq("_id", id)).first();
    }

    public User findUserByUsername(String username){
        MongoCollection<User> collection = this.getDb().getCollection("users", User.class);
        return collection.find(eq("username", username)).first();
    }

    public User findUserByEmail(String email){
        MongoCollection<User> collection = this.getDb().getCollection("users", User.class);
        return collection.find(eq("email", email)).first();
    }

    public Order findOrderById(ObjectId id){
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        return collection.find(eq("_id", id)).first();
    }

    public Product findProductById(ObjectId id){
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        return collection.find(eq("_id", id)).first();
    }

    public List<Product> findProductsByName(String name){
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        FindIterable<Product> products = collection.find(eq("name", Pattern.compile(name)));
        List<Product> productsList = new ArrayList<>();
        for (Product product : products){
            productsList.add(product);
        }
        return productsList;
    }

    public List<Order> findOrdersMadeByUser(String username){
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(eq("client.username", Pattern.compile(username)));
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
        }
        return ordersList;
    }

    // Obtiene los n proovedores que más productos tienen en órdenes que están siendo enviadas
    public List<Supplier> findTopNSuppliersInSentOrders(int n){
//        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
//        FindIterable<Supplier> suppliers = collection.find(eq("status.status", "Sent"), ne("status.status", "Delivered"));
//        List<Supplier> suppliersList = new ArrayList<>();
//        for (Supplier supplier : suppliers) {
//            suppliersList.add(supplier);
//        }
//        return suppliersList;
        return (new ArrayList<Supplier>());
    }

    public List<Order> findPendingOrders() {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(size("status", 1));
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
        }
        return ordersList;
    }

    public List<Order> findSentOrders() {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(and(eq("status.status", "Sent"), ne("status.status", "Delivered")));
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
        }
        return ordersList;
    }

    public List<Product> findProductsOnePrice() {
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        FindIterable<Product> products = collection.find(size("prices", 1));
        List<Product> productsList = new ArrayList<>();
        for (Product product : products) {
            productsList.add(product);
        }
        return productsList;
    }

    public List<Product> findSoldProductsOn(Date day) {
        return null;
    }

    public List<Order> findDeliveredOrdersForUser(String username) {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(and(eq("status.status", "Delivered"), eq("client.username", username)));
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
        }
        return ordersList;
    }

    // Obtiene todas las órdenes entregadas entre dos fechas
    public List<Order> findDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(and(eq("status.status", "Delivered"), gte("dateOfOrder", startDate), lte("dateOfOrder", endDate)));
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
        }
        return ordersList;
    }

    // Obtiene el producto con más demanda
    public Product findBestSellingProduct() {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.aggregate(
                
        );
        List<Order> ordersList = new ArrayList<>();
        for (Order order : orders) {
            ordersList.add(order);
            System.out.println(order.getProducts());
        }

//        List<Product> products = collection.find();
        return new Product();
    }
}
