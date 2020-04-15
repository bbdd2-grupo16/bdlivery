package ar.edu.unlp.info.bd2.services;
import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.DBliveryRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


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
    @Transactional
    public Product createProduct(String name, Float price, Float weight, Supplier supplier){
        try {
            return (Product) repository.save(new Product(name, price, weight, supplier));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Transactional
    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
        try {
            return (Product) repository.save(new Product(name, price, weight, supplier, date));
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

    @Transactional
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

    @Transactional
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

    @Transactional
    @Override
    public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
        Optional<Product> optional_product = this.getProductById(id);
        if (!optional_product.isPresent()) {
            throw new DBliveryException("El producto no existe");
        } else {
            Product product = optional_product.get();
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

    @Transactional
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
    @Transactional
    public Order addProduct(Long order, Long quantity, Product product)throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            orderConcrete.addProduct(quantity, product);
            repository.updateOrder(orderConcrete);
            return orderConcrete;
        }else { throw new DBliveryException("La orden no existe"); }
    }

    /**
     * Registra el envío del pedido, registrando al repartidor y cambiando su estado a Send.
     * @param order pedido a ser enviado
     * @param deliveryUser Usuario que entrega el pedido
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido, que el pedido no se encuentre en estado Pending o sí no contiene productos.
     */

    @Transactional
    @Override
    public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canDeliver(orderConcrete.getId())) {
                orderConcrete.setDeliveryUser(deliveryUser);
                orderConcrete.addState("Sent");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Transactional
    @Override
    public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        System.out.println(optional_order.getClass());
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canDeliver(orderConcrete.getId())) {
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

    @Transactional
    @Override
    public Order cancelOrder(Long order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canCancel(orderConcrete.getId())) {
                orderConcrete.addState("Cancelled");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser cancelada"); }
        }
        throw new DBliveryException("La orden no existe");
    }

    @Transactional
    @Override
    public Order cancelOrder(Long order, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canCancel(orderConcrete.getId())) {
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
    @Transactional
    @Override
    public Order finishOrder(Long order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canFinish(orderConcrete.getId())) {
                orderConcrete.addState("Delivered");
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { throw new DBliveryException("La orden no puede ser aprobada"); }
        }else { throw new DBliveryException("La orden no existe"); }
    }

    @Transactional
    @Override
    public Order finishOrder(Long order, Date date) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canFinish(orderConcrete.getId())) {
                orderConcrete.addState("Delivered", date);
                repository.updateOrder(orderConcrete);
                return orderConcrete;
            }else { throw new DBliveryException("La orden no puede ser finalizada"); }
        }else { throw new DBliveryException("La orden no existe"); }
    }

    /**
     * verifica si un pedido se puede cancelar, para lo cual debe estar en estado pending
     * @param order pedido a ser cancelado
     * @return true en caso que pueda ser cancelado false en caso contrario.
     * @throws DBliveryException si no existe el pedido.
     */
    @Override
    public boolean canCancel(Long order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if(orderConcrete.getState() == "Pending"){
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
        Optional<Order> optional_order = this.getOrderById(id);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if(orderConcrete.getState() == "Sent"){
                return true;
            }else {return false;}
        }else{ throw new DBliveryException("La orden no existe"); }
    }

    /**
     * verifica si un pedido puede ser enviado para lo cual debe tener productos y estar en estado pending
     * @param order pedido a ser enviado
     * @return true en caso que pueda ser enviado, false en caso contrario
     * @throws DBliveryException si el pedido no esta en estado pending.
     */
    @Override
    public boolean canDeliver(Long order) throws DBliveryException{
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            System.out.println(orderConcrete.getState());
            if (orderConcrete.getState() == "Pending"){
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
    public String getActualStatus(Long order) {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()) {
            Order orderConcrete = optional_order.get();
            return orderConcrete.getLastStatus().getState();
        }
        return null;
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

    /**
     * Obtiene todas las ordenes realizadas por el usuario con username <code>username</code>
     * @param username
     * @return Una lista de ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getAllOrdersMadeByUser(String username) {
        return null;
    }

    /**
     * Obtiene todos los usuarios que han gastando más de <code>amount</code> en alguna orden en la plataforma
     * @param amount
     * @return una lista de usuarios que satisfagan la condici{on
     */
    @Override
    public List<User> getUsersSpendingMoreThan(Float amount){
        return null;
    }

    /**
     * Obtiene los <code>n</code> proveedores que más productos tienen en ordenes que están siendo enviadas
     * @param n
     * @return una lista con los <code>n</code> proveedores que satisfagan la condición
     */
    @Override
    public List<Supplier> getTopNSuppliersInSentOrders(int n) {
        return null;
    }

    /**
     * Obtiene los 9 productos más costosos
     * @return una lista con los productos que satisfacen la condición
     */
    @Override
    public List<Product> getTop10MoreExpensiveProducts() {
        return null;
    }

    /**
     * Obtiene los 6 usuarios que más cantidad de ordenes han realizado
     * @return una lista con los usuarios que satisfacen la condición
     */
    @Override
    public List<User> getTop6UsersMoreOrders() {
        return null;
    }

    /**
     * Obtiene todas las ordenes canceladas entre dos fechas
     * @param startDate
     * @param endDate
     * @return una lista con las ordenes que satisfagan la condición
     */
    @Override
    public List <Order> getCancelledOrdersInPeriod(Date startDate, Date endDate) {
        return null;
    }

    /**
     * Obtiene el listado de las ordenes pendientes
     */
    @Override
    public List <Order> getPendingOrders() {
        return null;
    }

    /**
     * Obtiene el listado de las ordenes enviadas y no entregadas
     */
    @Override
    public List <Order> getSentOrders() {
        return null;
    }

    /**
     * Obtiene todas las ordenes entregadas entre dos fechas
     * @param startDate
     * @param endDate
     * @return una lista con las ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        return (List<Order>) repository.findDeliveredOrdersInPeriod(startDate, endDate);

    }

    /**
     * Obtiene todas las órdenes entregadas para el cliente con username <code>username</code>
     * @param username
     * @return una lista de ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getDeliveredOrdersForUser(String username) {
        return (List<Order>) repository.findDeliveredOrdersForUser(username);
    }

    /**
     * Obtiene las ordenes que fueron enviadas luego de una hora de realizadas (en realidad, luego de 24hs más tarde)
     * @return una lista de ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getSentMoreOneHour() {
        return null;
    }

    /**
     * Obtiene las ordenes que fueron entregadas el mismo día de realizadas
     * @return una lista de ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getDeliveredOrdersSameDay() {
        return null;
    }

    /**
     * Obtiene los 5 repartidores que menos ordenes tuvieron asignadas (tanto sent como delivered)
     * @return una lista de las ordenes que satisfagan la condición
     */
    @Override
    public List<User> get5LessDeliveryUsers() {
        return null;
    }

    /**
     * Obtiene el producto con más demanda
     * @return el producto que satisfaga la condición
     */
    @Override
    public Product getBestSellingProduct() {
        return null;
    }

    /**
     * Obtiene los productos que no cambiaron su precio
     * @return una lista de productos que satisfagan la condición
     */
    @Override
    public List<Product> getProductsOnePrice() {
        return null;
    }

    /**
     * Obtiene la lista de productos que han aumentado más de un 100% desde su precio inicial
     * @return una lista de productos que satisfagan la condición
     */
    @Override
    public List <Product> getProductIncreaseMoreThan100() {
        return null;
    }

    /**
     * Obtiene el proveedor con el producto de menor valor historico de la plataforma
     * @return el proveedor que cumple la condición
     */
    @Override
    public Supplier getSupplierLessExpensiveProduct() {
        return null;
    }

    /**
     * Obtiene los proveedores que no vendieron productos en un <code>day</code>
     * @param day
     * @return una lista de proveedores que cumplen la condición
     */
    @Override
    public List <Supplier> getSuppliersDoNotSellOn(Date day) {
        return null;
    }

    /**
     * obtiene los productos vendidos en un <code>day</code>
     * @param day
     * @return una lista los productos vendidos
     */
    @Override
    public List <Product> getSoldProductsOn(Date day) {
        return null;
    }

    /**
     * obtiene las ordenes que fueron entregadas en m{as de un día desde que fueron iniciadas(status pending)
     * @return una lista de las ordenes que satisfagan la condición
     */
    @Override
    public List<Order> getOrdersCompleteMorethanOneDay() {
        return null;
    }

    /**
     * obtiene el listado de productos con su precio a una fecha dada
     * @param day
     * @return la lista de cada producto con su precio en la fecha dada
     */
    @Override
    public List<Object[]> getProductsWithPriceAt(Date day) {
        return null;
    }

    /**
     * obtiene la lista de productos que no se han vendido
     * @return la lista de productos que satisfagan la condición
     */
    @Override
    public List<Product> getProductsNotSold(){
        return null;
    }

    /**
     * obtiene la/s orden/es con mayor cantidad de productos ordenados de la fecha dada
     * @day
     * @return una lista con las ordenes que cumplen la condición
     */
    @Override
    public List<Order> getOrderWithMoreQuantityOfProducts(Date day) {
        return null;
    }

}
