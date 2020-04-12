package ar.edu.unlp.info.bd2.services;
import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.DBliveryRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DBliveryServiceImpl implements DBliveryService{

    private DBliveryRepository repository;

    public DBliveryServiceImpl(DBliveryRepository repository) {this.repository = repository;}

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
            return (Product) repository.save(new Product(name, price, weight, supplier));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
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
            return (Supplier) repository.save(new Supplier(name, cuil, address, coordX, coordY));
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
            User user = new User(email, password, username, name, dateOfBirth);
            System.out.println(user.getName());
            return (User) repository.save(user);
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
    public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
        Product product = repository.findProductById(id);
        if (product == null) {
            throw new DBliveryException("El producto no existe");
        } else {
        //  Actualizar precio producto
            product.setPrice(price);
            product.addPrice(new Price(price, startDate));
            try{
                return (Product) repository.save(product);
            }catch (DBliveryException e){
                System.out.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Obtiene el usuario por id
     * @param id
     * @return el usuario con el id provisto
     */
    @Override
    public Optional<User> getUserById(Long id){
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
    public Optional<Product> getProductById(Long id){
        return Optional.ofNullable(repository.findProductById(id));
    }

    /**
     * Obtiene el pedido por id
     * @param id
     * @return el pedido con el id provisto
     */
    @Override
    public Optional<Order> getOrderById(Long id){
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
            return (Order) repository.save(new Order(dateOfOrder, address, coordX, coordY, client));
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
    public Order addProduct(Long order, Long quantity, Product product)throws DBliveryException{
        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            orderConcrete.addProduct(quantity, product);
            repository.updateOrder(orderConcrete);
            return orderConcrete;
        }else { throw new DBliveryException("The order don't exist"); }
    }

    /**
     * Registra el envío del pedido, registrando al repartidor y cambiando su estado a Send.
     * @param order pedido a ser enviado
     * @param deliveryUser Usuario que entrega el pedido
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido, que el pedido no se encuentre en estado Pending o sí no contiene productos.
     */

    @Override
    public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException{

        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            if (this.canDeliver(orderConcrete.getId())) {
                orderConcrete.setDeliveryUser(deliveryUser);
                orderConcrete.addState("Delivered");
                return orderConcrete;
            }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Override
    public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
        return null;
    }

    /**
     * Cancela un pedido
     * @param order id del pedido a cancelar
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido o si el pedido no esta en estado pending
     */

    @Override
    public Order cancelOrder(Long order) throws DBliveryException{

        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            if (this.canCancel(orderConcrete.getId())) {
                orderConcrete.addState("Cancelled");
                return orderConcrete;
            }else { new DBliveryException("The order can't be cancelled"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Override
    public Order cancelOrder(Long order, Date date) throws DBliveryException {
        return null;
    }

    /**
     * Registra la entrega de un pedido.
     * @param order pedido a finalizar
     * @return el pedido modificado
     * @throws DBliveryException en caso que no exista el pedido o si el mismo no esta en estado Send
     */

    @Override
    public Order finishOrder(Long order) throws DBliveryException{

        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            if (this.canFinish(orderConcrete.getId())) {
                orderConcrete.addState("Delivered");
                return orderConcrete;
            }else { throw new DBliveryException("The order can't be finished"); }
        }else { throw new DBliveryException("The order don't exist"); }
    }

    @Override
    public Order finishOrder(Long order, Date date) throws DBliveryException {
        return null;
    }

    /**
     * verifica si un pedido se puede cancelar, para lo cual debe estar en estado pending
     * @param order pedido a ser cancelado
     * @return true en caso que pueda ser cancelado false en caso contrario.
     * @throws DBliveryException si no existe el pedido.
     */
    @Override
    public boolean canCancel(Long order) throws DBliveryException{
        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            if(orderConcrete.getLastStatus() == "Pending"){
                return true;
            }else {return false;}
        }else{
            throw new DBliveryException("La orden no existe");
        }
    }

    /**
     * verifica si se puede finalizar un pedido
     * @param id del pedido a finalizar
     * @return true en caso que pueda ser finalizado, false en caso contrario
     * @throws DBliveryException en caso de no existir el pedido
     */
    @Override
    public boolean canFinish(Long id) throws DBliveryException{
        Order orderConcrete = repository.findOrderById(id);
        if (orderConcrete != null){
            if(orderConcrete.getLastStatus() == "Delivered"){
                return true;
            }else {return false;}
        }else{ throw new DBliveryException("The order don't exist"); }
    }

    /**
     * verifica si un pedido puede ser enviado para lo cual debe tener productos y estar en estado pending
     * @param order pedido a ser enviado
     * @return true en caso que pueda ser enviado, false en caso contrario
     * @throws DBliveryException si el pedido no esta en estado pending.
     */
    @Override
    public boolean canDeliver(Long order) throws DBliveryException{
        Order orderConcrete = repository.findOrderById(order);
        if (orderConcrete != null){
            if((orderConcrete.getLastStatus() == "Pending") && (orderConcrete.getProducts().size() != 0)){
                return true;
            }else {return false;}
        }else{
            throw new DBliveryException("La orden no existe");
        }
    }

    /**
     * Obtiene el estado actual de un pedido.
     * @param order pedido del cual se debe retornar el estado actual
     * @return el estado del pedido actual
     */
    @Override
    public String getActualStatus(Long order){
        Order orderConcrete = repository.findOrderById(order);
        return orderConcrete.getLastStatus();
    }

    /**
     * Obtiene el listado de productos que su nombre contega el string dado
     * @param name string a buscar
     * @return Lista de productos
     */
    @Override
    public List<Product> getProductByName(String name){
        return (List<Product>) repository.findProductByName(name);
    }

    @Override
    public List<Order> getAllOrdersMadeByUser(String username) {
        return null;
    }

    @Override
    public List<User> getUsersSpendingMoreThan(Float amount) {
        return null;
    }

    @Override
    public List<Supplier> getTopNSuppliersInSentOrders(int n) {
        return null;
    }

    @Override
    public List<Product> getTop10MoreExpensiveProducts() {
        return null;
    }

    @Override
    public List<User> getTop6UsersMoreOrders() {
        return null;
    }

    @Override
    public List<Order> getCancelledOrdersInPeriod(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<Order> getPendingOrders() {
        return null;
    }

    @Override
    public List<Order> getSentOrders() {
        return null;
    }

    @Override
    public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<Order> getDeliveredOrdersForUser(String username) {
        return null;
    }

    @Override
    public List<Order> getSentMoreOneHour() {
        return null;
    }

    @Override
    public List<Order> getDeliveredOrdersSameDay() {
        return null;
    }

    @Override
    public List<User> get5LessDeliveryUsers() {
        return null;
    }

    @Override
    public Product getBestSellingProduct() {
        return null;
    }

    @Override
    public List<Product> getProductsOnePrice() {
        return null;
    }

    @Override
    public List<Product> getProductIncreaseMoreThan100() {
        return null;
    }

    @Override
    public Supplier getSupplierLessExpensiveProduct() {
        return null;
    }

    @Override
    public List<Supplier> getSuppliersDoNotSellOn(Date day) {
        return null;
    }

    @Override
    public List<Product> getSoldProductsOn(Date day) {
        return null;
    }

    @Override
    public List<Order> getOrdersCompleteMorethanOneDay() {
        return null;
    }

    @Override
    public List<Object[]> getProductsWithPriceAt(Date day) {
        return null;
    }

    @Override
    public List<Product> getProductsNotSold() {
        return null;
    }

    @Override
    public List<Order> getOrderWithMoreQuantityOfProducts(Date day) {
        return null;
    }
}
