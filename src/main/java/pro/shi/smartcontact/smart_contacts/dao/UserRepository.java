package pro.shi.smartcontact.smart_contacts.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pro.shi.smartcontact.smart_contacts.entities.User;

public interface UserRepository extends JpaRepository<User,Integer>{

    @Query("select u from User u where u.email=:email")
    public User getUserByUserName(@Param("email")String email);

}
