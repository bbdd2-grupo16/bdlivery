package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SpringDataDBliveryService implements DBliveryService{

    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private SuppliersRepository  suppliersRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
        try {
            return productsRepository.save(new Product(name, price, weight, supplier));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
        try {
            return productsRepository.save(new Product(name, price, weight, supplier, date));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
        try {
            return suppliersRepository.save(new Supplier(name, cuil, address, coordX, coordY));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
        try {
            return usersRepository.save(new User(email, password, username, name, dateOfBirth));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
        Optional<Product> optional_product = productsRepository.findById(id);
        if (!optional_product.isPresent()) {
            throw new DBliveryException("El producto no existe");
        } else {
            Product product = optional_product.get();
            //  Actualizar precio producto
            product.setPrice(price);
            product.addPrice(new Price(price, startDate));
            try{
                return productsRepository.save(product);
            } catch (Exception e) {
                throw new DBliveryException("El precio no pudo actualizarse");
            }
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return usersRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersRepository.findByEmail(email));
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(usersRepository.findByUsername(username));
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return ordersRepository.findById(id);
    }

    @Override
    public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
        try {
            return ordersRepository.save(new Order(dateOfOrder, address, coordX, coordY, client));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
        Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            orderConcrete.addProduct(quantity, product);
            ordersRepository.save(orderConcrete);
            return orderConcrete;
        }
        throw new DBliveryException("La orden no existe");
    }

    @Override
    public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
        return null;
    }

    @Override
    public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
        /*Optional<Order> optional_order = this.getOrderById(order);
        if (optional_order.isPresent()){
            Order orderConcrete = optional_order.get();
            if (this.canDeliver(orderConcrete.getId())) {
                orderConcrete.setDeliveryUser(deliveryUser);
                orderConcrete.addState("Sent");
                ordersRepository.save(orderConcrete);
                return orderConcrete;
            }else { new DBliveryException("La orden no puede ser"); }
        }
        throw new DBliveryException("La orden no existe");*/
        return null;
    }

    @Override
    public Order cancelOrder(Long order) throws DBliveryException {
        return null;
    }

    @Override
    public Order cancelOrder(Long order, Date date) throws DBliveryException {
        return null;
    }

    @Override
    public Order finishOrder(Long order) throws DBliveryException {
        return null;
    }

    @Override
    public Order finishOrder(Long order, Date date) throws DBliveryException {
        return null;
    }

    @Override
    public boolean canCancel(Long order) throws DBliveryException {
        return false;
    }

    @Override
    public boolean canFinish(Long id) throws DBliveryException {
        return false;
    }

    @Override
    public boolean canDeliver(Long order) throws DBliveryException {
        return false;
    }

    @Override
    public RecordState getActualStatus(Long order) {
        return new RecordState();
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productsRepository.findByNameContaining(name);
    }

    @Override
    public Product getMaxWeight() {
        return productsRepository.findFirstByOrderByWeightDesc();
    }

    @Override
    public List<Order> getAllOrdersMadeByUser(String username) {
        return ordersRepository.findAllOrdersMadeByUser(username);
    }

    @Override
    public List<Order> getPendingOrders() {
        return ordersRepository.findPendingOrders();
    }

    @Override
    public List<Order> getSentOrders() {
        return ordersRepository.findSentOrders();
    }

    @Override
    public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public List<Order> getDeliveredOrdersForUser(String username) {
        return ordersRepository.findDeliveredOrdersForUser(username);
    }

    @Override
    public List<Product> getProductsOnePrice() {
        return productsRepository.getProductsOnePrice();
    }

    @Override
    public List<Product> getSoldProductsOn(Date day) {
        return productOrderRepository.getSoldProductsOn(day);
    }
}
