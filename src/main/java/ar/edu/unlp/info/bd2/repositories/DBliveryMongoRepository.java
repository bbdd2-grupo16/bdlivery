package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;
import com.mongodb.client.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
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
        FindIterable<Product> result = collection.find(eq("name", Pattern.compile(name)));
        Stream<Product> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(result.iterator(), 0), false);
        return stream.collect(Collectors.toList());
    }

    public List<Order> findOrdersMadeByUser(String username){
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> result = collection.find(eq("client.username", Pattern.compile(username)));
        Stream<Order> stream = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize( result.iterator(), 0), false
        );
        return stream.collect(Collectors.toList());
    }

    // Obtiene los n proovedores que más productos tienen en órdenes que están siendo enviadas
    public List<Supplier> findTopNSuppliersInSentOrders(int n){
        AggregateIterable<Supplier>  result = (AggregateIterable<Supplier>) this.getDb()
            .getCollection("orders", Supplier.class).aggregate(Arrays.asList(
                match(Filters.eq("status.status", "Sent")),
                match(Filters.ne("status.status", "Delivered")),
                unwind("$products"),
                new Document("$group",
                    new Document("_id", "$products.product.supplier")
                        .append("quantity", new Document("$sum", "$products.quantity"))),
                        sort(new Document("quantity", -1)),
                        replaceRoot("$_id"),
                        limit(n)
                    ));

        Stream<Supplier> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(result.iterator(), 0), false);

        return stream.collect(Collectors.toList());
    }

    public List<Order> findPendingOrders() {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> result = collection.find(size("status", 1));
        Stream<Order> stream = StreamSupport.stream(
          Spliterators.spliteratorUnknownSize(result.iterator(), 0), false
        );
        return stream.collect(Collectors.toList());
    }

    public List<Order> findSentOrders() {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> result = collection.find(and(eq("status.status", "Sent"), ne("status.status", "Delivered")));
        Stream<Order> stream = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(result.iterator(), 0),false
        );
        return stream.collect(Collectors.toList());
    }

    public List<Product> findProductsOnePrice() {
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        FindIterable<Product> result = collection.find(size("prices", 1));
        Stream<Product> stream = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(result.iterator(),0), false
        );
        return stream.collect(Collectors.toList());
    }

    public List<Product> findSoldProductsOn(Date day) {
        List<Product> products = new ArrayList<>();
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(eq("status.date", day));
        System.out.println(orders);
        for (Order order : orders) {
            for (ProductOrder prodO : order.getProducts()){
                products.add(prodO.getProduct());
            }
        }
        return products;
    }

    public List<Order> findDeliveredOrdersForUser(String username) {
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> result = collection.find(and(eq("status.status", "Delivered"), eq("client.username", username)));
        Stream<Order> stream = StreamSupport.stream(
          Spliterators.spliteratorUnknownSize(result.iterator(), 0), false
        );
        return stream.collect(Collectors.toList());
    }

    // Obtiene todas las órdenes entregadas entre dos fechas
    public List<Order> findDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        AggregateIterable<Order> result  = (AggregateIterable<Order>) this.getDb()
            .getCollection("orders", Order.class)
            .aggregate(Arrays.asList(
                match(Filters.eq("status.status", "Delivered")),
                match(Filters.gte("status.date", startDate)),
                match(Filters.lte("status.date", endDate))
            ));

        Stream<Order> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(result.iterator(), 0), false);
        return stream.collect(Collectors.toList());
    }

    // Obtiene el producto con más demanda
    public Product findBestSellingProduct() {
        AggregateIterable<Product>  result = (AggregateIterable<Product>) this.getDb()
            .getCollection("orders", Product.class)
            .aggregate(Arrays.asList(
                unwind("$products"),
                new Document("$group",
                    new Document("_id", "$products.product")
                        .append("quantity", new Document("$sum", "$products.quantity") ) ),
                        sort(new Document("quantity",-1)),
                        replaceRoot("$_id"),
                        limit(1)));
        Stream<Product> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(result.iterator(), 0), false);
        return stream.collect(Collectors.toList()).get(0);
    }

    public Product getMaxWeigth() {
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        return collection.find().sort(new Document("weight",-1)).first();
    }

    public List<Order> getOrderNearPlazaMoreno() {
        List<Order> ordersList = new ArrayList<>();
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        FindIterable<Order> orders = collection.find(Filters.nearSphere("position", -34.921236,-57.954571, 0.00006271401156446, 0.0));
        for (Order order : orders) {
            ordersList.add(order);
            System.out.println(order.getPosition());
        }
        return ordersList;
    }
}
