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
    public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException{
//        Product product = repository.getProductById(id);
//        if (!product) {
//            throw new DBliveryException("El producto no existe");
//        } else {
////            Actualizar precio producto
//        }
        return new Product();
    }

    /**
     * Obtiene el usuario por id
     * @param id
     * @return el usuario con el id provisto
     */
    @Override
    public Optional<User> getUserById(Long id){
        Optional<User> user= repository.findUserById(id);
        return  user;
    }

    /**
     * Obtiene el usuario por el email
     * @param email
     * @return el usuario con el email provisto
     */
    @Override
    public Optional<User> getUserByEmail(String email){
        Optional<User> user= repository.findUserByEmail(email);
        return  user;
    }

    /**
     * Obtiene el usuario por el username
     * @param username
     * @return el usuario con el username provisto
     */
    @Override
    public Optional<User> getUserByUsername(String username){
        Optional<User> user= repository.findUserByUsername(username);
        return  user;
    }

    /**
     * Obtiene el producto por id
     * @param id
     * @return el producto con el id provisto
     */
    @Override
    public Optional<Product> getProductById(Long id){
        return Optional.ofNullable(new Product());
//        return repository.findProductById(id);
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
        if (repository.findOrderById(order) != null){
            Order orderConcrete = repository.findOrderById(order);
            orderConcrete.addProduct(quantity, product);
            try {
                return (Order) repository.save(orderConcrete);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
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
        return new Order();
    }

    /**
     * Cancela un pedido
     * @param order id del pedido a cancelar
     * @return el pedido modificado
     * @throws DBliveryException en caso de no existir el pedido o si el pedido no esta en estado pending
     */

    @Override
    public Order cancelOrder(Long order) throws DBliveryException{
        return new Order();
    }
    /**
     * Registra la entrega de un pedido.
     * @param order pedido a finalizar
     * @return el pedido modificado
     * @throws DBliveryException en caso que no exista el pedido o si el mismo no esta en estado Send
     */

    @Override
    public Order finishOrder(Long order) throws DBliveryException{
        return new Order();
    }

    /**
     * verifica si un pedido se puede cancelar, para lo cual debe estar en estado pending
     * @param order pedido a ser cancelado
     * @return true en caso que pueda ser cancelado false en caso contrario.
     * @throws DBliveryException si no existe el pedido.
     */
    @Override
    public boolean canCancel(Long order) throws DBliveryException{
        return true;
    }

    /**
     * verifica si se puede finalizar un pedido
     * @param id del pedido a finalizar
     * @return true en caso que pueda ser finalizado, false en caso contrario
     * @throws DBliveryException en caso de no existir el pedido
     */
    @Override
    public boolean canFinish(Long id) throws DBliveryException{
        return true;
    }

    /**
     * verifica si un pedido puede ser enviado para lo cual debe tener productos y estar en estado pending
     * @param order pedido a ser enviado
     * @return true en caso que pueda ser enviado, false en caso contrario
     * @throws DBliveryException si el pedido no esta en estado pending.
     */
    @Override
    public boolean canDeliver(Long order) throws DBliveryException{
        return true;
    }

    /**
     * Obtiene el estado actual de un pedido.
     * @param order pedido del cual se debe retornar el estado actual
     * @return el estado del pedido actual
     */
    @Override
    public String getActualStatus(Long order){
        return "nada";
//        return repository.findOrderStatusByOrder(order);
    }

    /**
     * Obtiene el listado de productos que su nombre contega el string dado
     * @param name string a buscar
     * @return Lista de productos
     */
    @Override
    public List<Product> getProductByName(String name){
        List<Product> products = repository.findProductByName(name);
        return products;
    }
}
