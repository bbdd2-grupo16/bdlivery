package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;
import com.mongodb.BasicDBObject;
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

    public Supplier findSupplierById(ObjectId id){
        MongoCollection<Supplier> collection = this.getDb().getCollection("suppliers", Supplier.class);
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
//
//        Bson productsCollection = project(Projections.fields(Projections.excludeId(),
//            Projections.include("products")));
//
//        this.getDb().getCollection("orders", Order.class).aggregate(Arrays.asList(
//            match(and(eq("status.status", "Sent"), ne("status.status", "Delivered"))),
//            out("sent_orders"))).toCollection();
//
//        this.getDb().getCollection("sent_orders", Order.class).aggregate(Arrays.asList(productsCollection,
//                out("products_in_sent_orders"))).toCollection();
//
//        MongoCollection<Document> products = this.getDb().getCollection("products_in_sent_orders");
//
//        Bson productsOrdersCollection = project(Projections.fields(Projections.excludeId(),
//                Projections.computed("products.product", "products.quantity")));
//
//        this.getDb().getCollection("products_in_sent_orders", Order.class).aggregate(Arrays.asList(productsCollection,
//                out("productOrder_in_sent_orders"))).toCollection();
//
//        Bson suppliersCollection = project(Projections.fields(
//            Projections.include("product"), Projections.computed("", 1)));
//
////        products.aggregate(Arrays.asList(suppliersCollection,
//        products.aggregate(Arrays.asList(productsOrdersCollection,
//            Aggregates.group("$supplier", Accumulators.sum("quantity", 1)),
//            out("suppliers_in_sent_orders"))).toCollection();
//
//        String map = "function() { emit( this, this.quantity); }";
//        String reduce = "function(key, values) { return Array.sum(values); }";
//        MapReduceIterable mapReduceIterable =  this.getDb().getCollection("productOrder_in_sent_orders").mapReduce(map, reduce);
//        FindIterable<Document> findIterable = this.getDb().getCollection("suppliers_in_sent_orders").find().limit(n);
//
//        for (Object doc: mapReduceIterable) {
//            System.out.println(doc);
////            Product product = (Product) doc.get("product");
////            System.out.println(product);
//
//        }

        this.getDb().getCollection("orders", Order.class).aggregate(Arrays.asList(
                match(and(eq("status.status", "Sent"))),
                out("sent_orders"))).toCollection();

        Bson productsProjection = project(Projections.excludeId());

        MongoCollection<Order> collection = this.getDb().getCollection("sent_orders", Order.class);
        collection.aggregate(Arrays.asList(productsProjection, unwind("$products"),
                group("$products.product.supplier._id",  Accumulators.sum("quantity", 1)),
                sort(Sorts.descending("quantity")),
                limit(n),
                out("suppliers_in_sent_orders"))).toCollection();

        FindIterable<Document> suppliers = this.getDb().getCollection("suppliers_in_sent_orders").find();
        List<Supplier> suppliersList = new ArrayList<>();
        for (Document doc: suppliers) {
            ObjectId supplierId = (ObjectId) doc.get("_id");
            Supplier supplier = this.findSupplierById(supplierId);
            suppliersList.add(supplier);
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
//        Bson productsProject = project(Projections.fields(Projections.excludeId(),
//                Projections.include("products.product")));
//        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
//        collection.aggregate(Arrays.asList(productsProject, unwind("$products"),
//                group("$products.product",  Accumulators.sum("quantity", 1)),
//                sort(Sorts.descending("quantity")),
//                limit(1),
//                out("bestSellingProduct"))).toCollection();
//        System.out.println(this.getDb().getCollection("bestSellingProduct").find().first().get("_id"));
//        return order.getProducts().get(0).getProduct();

        //        Bson productsCollection = project(
//                Projections.include("products.product"));
//        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
//        collection.aggregate(Arrays.asList(productsCollection, unwind("$products"),
//                group("$products.product._id",  Accumulators.sum("quantity", 1)),
//                sort(Sorts.descending("quantity")),
//                limit(1),
//                out("bestSellingProduct_aux")), Product.class).toCollection();
//
//        Bson productsCollectionAux = project(Projections.exclude("_id", "quantity"));
//        this.getDb().getCollection("bestSellingProduct_aux").aggregate(
//                Arrays.asList(productsCollectionAux,
////                unwind("$product"),
//                out("bestSellingProduct"))).toCollection();
////        this.getDb().getCollection("bestSellingProduct_aux", Product.class).find();
//        System.out.println(this.getDb().getCollection("bestSellingProduct").find().first());
////        this.getDb().getCollection("bestSellingProduct", Product.class).find().first()
//
//        MongoCollection<Order> orderCollection = this.getDb().getCollection("orders", Order.class);
//        BasicDBObject filter = new BasicDBObject("$unwind", "$products");
//        BasicDBObject projections = new BasicDBObject();
//        projections.append("_id", "$product._id");
//        projections.append("name", "$product.name");
//        projections.append("price", "$product.price");
//        projections.append("prices", "$product.prices");
//        projections.append("supplier", "$product.supplier");
//        projections.append("weight", "$product.weight");
//
//        BasicDBObject project = new BasicDBObject("$project",
//                new BasicDBObject( "product", 1)
////                        .append( "_id", "$product._id" )
//                        .append( "name", "$product.name" ));
        Bson productsProject = project(Projections.excludeId());
        MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
        collection.aggregate(Arrays.asList(productsProject,
                unwind("$products"),
                group("$products.product._id",  Accumulators.sum("quantity", 1)),
                sort(Sorts.descending("quantity")),
                limit(1),
                out("bestSellingProduct")), Product.class).toCollection();
        ObjectId productId = (ObjectId) this.getDb().getCollection("bestSellingProduct").find().first().get("_id");
        Product product = this.findProductById(productId);
        return product;
    }

    public Product getMaxWeigth() {
        MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
        return collection.find().sort(new Document("weight",-1)).first();
    }

    public List<Order> getOrderNearPlazaMoreno() {
        MongoCollection<Order> collection = this.getDb().getCollection("Order", Order.class);
        FindIterable<Order> orders = collection.find();
        List<Order> ordersList = new ArrayList<>();
        return ordersList;
    }
}
