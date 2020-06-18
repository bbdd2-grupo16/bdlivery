package ar.edu.unlp.info.bd2.repositories;

import ar.edu.unlp.info.bd2.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {

  User findByEmail(String email);

  User findByUsername(String username);

//  @Query("select c from Customer c where c.email = ?1")
//  Customer findByEmail(String email);

}
