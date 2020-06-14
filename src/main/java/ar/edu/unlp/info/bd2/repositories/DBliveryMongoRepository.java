package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
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

//        Bson productsCollection = project(Projections.fields(Projections.excludeId(),
//            Projections.include("products")));

//        this.getDb().getCollection("orders", Order.class).aggregate(Arrays.asList(
//            match(and(eq("status.status", "Sent"), ne("status.status", "Delivered"))),
//            out("sent_orders"))).toCollection();

//        this.getDb().getCollection("sent_orders", Order.class).aggregate(Arrays.asList(productsCollection,
//                out("products_in_sent_orders"))).toCollection();

//        MongoCollection<Document> products = this.getDb().getCollection("products_in_sent_orders");

//        Bson productsOrdersCollection = project(Projections.fields(Projections.excludeId(),
//                Projections.computed("products.product", "products.quantity")));

//        this.getDb().getCollection("products_in_sent_orders", Order.class).aggregate(Arrays.asList(productsCollection,
//                out("productOrder_in_sent_orders"))).toCollection();

//        Bson suppliersCollection = project(Projections.fields(
//            Projections.include("product"), Projections.computed("", 1)));

//        products.aggregate(Arrays.asList(suppliersCollection,
//        products.aggregate(Arrays.asList(productsOrdersCollection,
//            Aggregates.group("$supplier", Accumulators.sum("quantity", 1)),
//            out("suppliers_in_sent_orders"))).toCollection();
//
//        String map = "function() { emit( this, this.quantity); }";
//        String reduce = "function(key, values) { return Array.sum(values); }";
//        MapReduceIterable mapReduceIterable =  this.getDb().getCollection("productOrder_in_sent_orders").mapReduce(map, reduce);
//                FindIterable<Document> findIterable = this.getDb().getCollection("suppliers_in_sent_orders").find().limit(n);

        //            Product product = (Product) doc.get("product");
        //            System.out.println(product);
        ////            suppliersList.add(product.getSupplier());
//        for (Object doc: mapReduceIterable) {
//            System.out.println(doc);
//        }

        this.getDb().getCollection("orders", Order.class).aggregate(Arrays.asList(
                match(and(eq("status.status", "Sent"))),
                out("sent_orders"))).toCollection();

        Bson productsCollection = project(Projections.fields(Projections.excludeId(),
                Projections.include("products.product.supplier")));

        MongoCollection<Order> collection = this.getDb().getCollection("sent_orders", Order.class);
        collection.aggregate(Arrays.asList(productsCollection, unwind("$products"),
                group("$products.product.supplier",  Accumulators.sum("quantity", 1)),
                sort(Sorts.descending("quantity")),
                limit(n),
                out("suppliers_in_sent_orders"))).toCollection();

        FindIterable<Document> suppliers = this.getDb().getCollection("suppliers_in_sent_orders").find();
        List<Supplier> suppliersList = new ArrayList<>();
        for (Document doc: suppliers) {
            System.out.println(doc.values());
        }
        return suppliersList;
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
        Bson productsCollection = project(Projections.fields(Projections.excludeId(),
                Projections.include("products.product")));
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        collection.aggregate(Arrays.asList(productsCollection, unwind("$products"),
                group("$products.product",  Accumulators.sum("quantity", 1)),
                sort(Sorts.descending("quantity")),
                limit(1),
                out("bestSellingProduct"))).toCollection();

//        Order order = this.getDb().getCollection("bestSellingProduct", Order.class).find().first();
//        Product product = (Product) this.getDb().getCollection("bestSellingProduct").find().first().get("_id");
        System.out.println(this.getDb().getCollection("bestSellingProduct").find().first().get("_id"));
//        return order.getProducts().get(0).getProduct();


        return new Product();
    }

    public Product getMaxWeigth() {
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        return collection.find().sort(new Document("weight",-1)).first();
    }

    public List<Order> getOrderNearPlazaMoreno() {
        List<Order> ordersList = new ArrayList<>();
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        Point refPoint = new Point(new Position(-34.921236,-57.954571));
        //faltaria agregar position como un campo para poder filtrar
        for (Order order : collection.find(Filters.near("position", refPoint, 400.0, 0.0)))
        {
            ordersList.add(order);
        }
        return ordersList;
    }
}
