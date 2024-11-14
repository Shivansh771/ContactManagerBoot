package pro.shi.smartcontact.smart_contacts.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pro.shi.smartcontact.smart_contacts.dao.ContactRepository;
import pro.shi.smartcontact.smart_contacts.dao.UserRepository;
import pro.shi.smartcontact.smart_contacts.entities.Contact;
import pro.shi.smartcontact.smart_contacts.entities.User;

@RestController
public class SearchController {
    //search handler
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
        User user=this.userRepository.getUserByUserName(principal.getName());

        List<Contact> contact=this.contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contact);
        

    }
}
