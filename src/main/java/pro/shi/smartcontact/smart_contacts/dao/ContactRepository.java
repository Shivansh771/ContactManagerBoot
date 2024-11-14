package pro.shi.smartcontact.smart_contacts.dao;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pro.shi.smartcontact.smart_contacts.entities.Contact;
import pro.shi.smartcontact.smart_contacts.entities.User;
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // Corrected query
    @Query("from Contact as c where c.user.id=:userId")

    //current-page
    //contact per page=5 for pageable
    public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pePageable);

    //search
    public List<Contact> findByNameContainingAndUser(String name,User user);
}

