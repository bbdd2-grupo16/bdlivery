package ar.edu.unlp.info.bd2.services;
import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.services.*;
import ar.edu.unlp.info.bd2.repositories.DBliveryMongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
//import org.springframework.transaction.annotation.Transactional;


public class DBliveryServiceImpl implements DBliveryService{

    private DBliveryMongoRepository repository;

    public DBliveryServiceImpl(DBliveryMongoRepository repository) {this.repository = repository;}

    /**
     *  Crea y devuelve un nuevo Producto.
     * @param name nombre del producto a ser creado
     * @param price precio actual del producto
     * @param weight peso actual del producto
     * @param supplier el productor del producto
     * @return el producto creado
     */

    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier){
        try {
            return (Product) repository.save(new Product(name, price, weight, supplier), "products", Product.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
        try {
            return (Product) repository.save(new Product(name, price, weight, supplier, date), "products", Product.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Crea y retorna un nuevo Productor
     * @param name nombre del productor
     * @param cuil cuil del productor
     * @param address dirección del productor
     * @param coordX  coordenada X de la dirección del productor
     * @param coordY coordeada Y de la dirección del produtor
     * @return el productor creado
     */

    @Override
    public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY){
        try {
            return (Supplier) repository.save(new Supplier(name, cuil, address, coordX, coordY), "suppliers", Supplier.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Crea y retorna un Usuario
     * @param email email del usuario
     * @param password clave del usuario
     * @param username nombre de usuario del usuario
     * @param name nombre y apellido del usuario
     * @param dateOfBirth fecha de nacimiento del usuario
     * @return el usuario creado
     */

    @Override
    public User createUser(String email, String password, String username, String name, Date dateOfBirth){
        try {
            return (User) repository.save(new User(email, password, username, name, dateOfBirth), "users", User.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza el precio del producto manteniendo el historial de cambios de precio del mismo.
     * @param id id del producto
     * @param price nuevo precio del producto
     * @param startDate fecha de inicio del nuevo precio
     * @return el producto modificado
     * @throws DBliveryException en caso de que no exista el producto para el id dado
     */

    @Override
    public Product updateProductPrice(ObjectId id, Float price, Date startDate) throws DBliveryException {
        Optional<Product> optional_product = this.getProductById(id);
        if (!optional_product.isPresent()) {
            throw new DBliveryException("El producto no existe");
        } else {
            Product product = optional_product.get();
        //  Actualizar precio producto
            product.setPrice(price);
            Price price1 = new Price(price, startDate);
            product.addPrice(price1);
            try {
                return (Product) repository.updateProduct(product);
            }catch (Exception e){
                throw new DBliveryException("El producto no pudo actualizarse");
            }
        }
    }

    /**
     * Obtiene el usuario por id
     * @param id
     * @return el usuario con el id provisto
     */
    @Override
    public Optional<User> getUserById(ObjectId id){
        return Optional.ofNullable(repository.findUserById(id));
    }

    /**
     * Obtiene el usuario por el email
     * @param email
     * @return el usuario con el email provisto
     */
    @Override
    public Optional<User> getUserByEmail(String email){
      return Optional.ofNullable(repository.findUserByEmail(email));
    }

    /**
     * Obtiene el usuario por el username
     * @param username
     * @return el usuario con el username provisto
     */
    @Override
    public Optional<User> getUserByUsername(String username){
      return Optional.ofNullable(repository.findUserByUsername(username));
    }

    /**
     * Obtiene el producto por id
     * @param id
     * @return el producto con el id provisto
     */
    @Override
    public Optional<Product> getProductById(ObjectId id){
        return Optional.ofNullable(repository.findProductById(id));
    }

    /**
     * Obtiene el pedido por id
     * @param id
     * @return el pedido con el id provisto
     */
    @Override
    public Optional<Order> getOrderById(ObjectId id){
      return Optional.ofNullable(repository.findOrderById(id));
    }

    /**
     * Crea y retorna un nuevo pedido
     * @param dateOfOrder timestamp de la fecha en que fue realizado el pedido
     * @param address dirección en la cual se debe entregar el pedido
     * @param coordX coordenada X de la dirección
     * @param coordY coordenada Y de la dirección
     * @param client cliente que realizó el pedido
     * @return el nuevo pedido
     */
    @Override
    public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client){
        try {
            return (Order) repository.save(new Order(dateOfOrder, address, coordX, coordY, client), "orders", Order.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * agrega un producto al pedido
     * @param order pedido al cual se le agrega el producto
     * @param quantity cantidad de producto a agregar
     * @param product producto a agregar
     * @return el pedido con el nuevo producto
     * @throws DBliveryException en caso de no existir el pedido
     */
    @Override
    public Order addProduct(ObjectId order, Long quantity, Product product)throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            orderConcrete.addProduct(new ProductOrder(quantity, product, orderConcrete));
            repository.updateOrder(orderConcrete);
            repository.saveAssociation(orderConcrete, product, "product_order");
            return orderConcrete;
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * Registra el envío del pedido, registrando al repartidor y cambiando su estado a Send.
     * @param order pedido a ser enviado
     * @param deliveryUser Usuario que entrega el pedido
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido, que el pedido no se encuentre en estado Pending o sí no contiene productos.
     */
    @Override
    public Order deliverOrder(ObjectId order, User deliveryUser) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canDeliver(orderConcrete.getObjectId())) {
                orderConcrete.setDeliveryUser(deliveryUser);
                orderConcrete.addState("Sent");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Override
    public Order deliverOrder(ObjectId order, User deliveryUser, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        System.out.println(optional_order.getClass());
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canDeliver(orderConcrete.getObjectId())) {
                orderConcrete.setDeliveryUser(deliveryUser);
                orderConcrete.addState("Sent", date);
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { throw new DBliveryException("La orden no puede ser enviada"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * Cancela un pedido
     * @param order id del pedido a cancelar
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido o si el pedido no esta en estado pending
     */
    @Override
    public Order cancelOrder(ObjectId order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canCancel(orderConcrete.getObjectId())) {
                orderConcrete.addState("Cancelled");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser cancelada"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Override
    public Order cancelOrder(ObjectId order, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canCancel(orderConcrete.getObjectId())) {
                orderConcrete.addState("Cancelled", date);
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser cancelada"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * Registra la entrega de un pedido.
     * @param order pedido a finalizar
     * @return el pedido modificado
     * @throws DBliveryException en caso que no exista el pedido o si el mismo no esta en estado Send
     */
    @Override
    public Order finishOrder(ObjectId order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canFinish(orderConcrete.getObjectId())) {
                orderConcrete.addState("Delivered");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { throw new DBliveryException("La orden no puede ser aprobada"); }
        }
        throw new DBliveryException("La orden no existe");

    }

    @Override
    public Order finishOrder(ObjectId order, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canFinish(orderConcrete.getObjectId())) {
                orderConcrete.addState("Delivered", date);
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { throw new DBliveryException("La orden no puede ser finalizada"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * verifica si un pedido se puede cancelar, para lo cual debe estar en estado pending
     * @param order pedido a ser cancelado
     * @return true en caso que pueda ser cancelado false en caso contrario.
     * @throws DBliveryException si no existe el pedido.
     */
    @Override
    public boolean canCancel(ObjectId order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if(this.getActualStatus(orderConcrete.getObjectId()).getStatus().equals("Pending")){
                return true;
            }else {return false;}
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * verifica si se puede finalizar un pedido
     * @param id del pedido a finalizar
     * @return true en caso que pueda ser finalizado, false en caso contrario
     * @throws DBliveryException en caso de no existir el pedido
     */
    @Override
    public boolean canFinish(ObjectId id) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(id);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if(this.getActualStatus(orderConcrete.getObjectId()).getStatus().equals("Sent")){
                return true;
            }else {return false;}
        }
        throw new DBliveryException("La orden no existe");
    }

    /**
     * verifica si un pedido puede ser enviado para lo cual debe tener productos y estar en estado pending
     * @param order pedido a ser enviado
     * @return true en caso que pueda ser enviado, false en caso contrario
     * @throws DBliveryException si el pedido no esta en estado pending.
     */
    @Override
    public boolean canDeliver(ObjectId order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.getActualStatus(orderConcrete.getObjectId()).getStatus().equals("Pending")){
                if (orderConcrete.getProducts().size() > 0) {
                    return true;
                }
            }
        }else{
            throw new DBliveryException("La orden no existe");
        }
        return false;
    }

    /**
     * Obtiene el estado actual de un pedido.
     * @param order pedido del cual se debe retornar el estado actual
     * @return el estado del pedido actual
     */
    @Override
    public RecordState getActualStatus(ObjectId order){
        Order orderConcrete = repository.findOrderById(order);
        return orderConcrete.getLastStatus();
    }

    /**
     * Obtiene el listado de productos que su nombre contega el string dado
     * @param name string a buscar
     * @return Lista de productos
     */
    @Override
    public List<Product> getProductsByName(String name){
        return (List<Product>) repository.findProductsByName(name);
    }

    @Override
    public List<Order> getAllOrdersMadeByUser(String username) throws DBliveryException {
        List<Order> orders = repository.findOrdersMadeByUser(username);
        if (orders.size() != 0) {
            return orders;
        }
        throw new DBliveryException("El usuario no tiene ordenes");
    }

    @Override
    // Obtiene los n proovedores que más productos tienen en órdenes que están siendo enviadas
    public List<Supplier> getTopNSuppliersInSentOrders(int n) {
        return repository.findTopNSuppliersInSentOrders(n);
    }

    @Override
    public List<Order> getPendingOrders() {
        return repository.findPendingOrders();
    }

    @Override
    public List<Order> getSentOrders() {
        return repository.findSentOrders();
    }

    @Override
    // Obtiene todas las órdenes entregadas entre dos fechas
    public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        return repository.findDeliveredOrdersInPeriod(startDate, endDate);
    }

    @Override
    public List<Order> getDeliveredOrdersForUser(String username) {
        return repository.findDeliveredOrdersForUser(username);
    }

    @Override
    // Obtiene el producto con más demanda
    public Product getBestSellingProduct() {
        return repository.findBestSellingProduct();
    }

    @Override
    public List<Product> getProductsOnePrice() {
        return repository.findProductsOnePrice();
    }

    @Override
    //Obtiene los productos vendidos en un day
    public List<Product> getSoldProductsOn(Date day) {
        return repository.findSoldProductsOn(day);
    }

    @Override
    //Obtiene el producto más pesado
    public Product getMaxWeigth() {
        return repository.getMaxWeigth();
    }

    @Override
    public List<Order> getOrderNearPlazaMoreno() {
        return repository.getOrderNearPlazaMoreno();
    }
}
