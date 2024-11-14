package pro.shi.smartcontact.smart_contacts.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import pro.shi.smartcontact.smart_contacts.dao.ContactRepository;
import pro.shi.smartcontact.smart_contacts.dao.UserRepository;
import pro.shi.smartcontact.smart_contacts.entities.Contact;
import pro.shi.smartcontact.smart_contacts.entities.User;
import pro.shi.smartcontact.smart_contacts.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
   private UserRepository userRepository;

    @Autowired
   private ContactRepository contactRepository;
    //adding commondata to all methods
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);
        System.out.println("User" +user);
        model.addAttribute("user", user);
    }
    @RequestMapping("/index")
    public String Dashboard(Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "User Dashboard");

        // Check if a message exists, add it to the model, and then remove it from the session
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "/normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model,HttpSession session){
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }


    //processing add contact
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
    @RequestParam("profileImage")MultipartFile file,
    HttpSession session, Model model,
    Principal principal)
    {
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }


        try{
        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);


        //processing and uploading file
        if(file.isEmpty()){
            System.out.println("File is empty");
            contact.setImage("contact.png");
        }else{
            //uplaod file
            contact.setImage(file.getOriginalFilename());
            java.io.File saveFile=new ClassPathResource("static/img").getFile();
            Path path= Paths.get(saveFile.getAbsolutePath()+java.io.File.separator +file.getOriginalFilename());
            Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

        }

        contact.setUser(user);
        user.getContacts().add(contact);
        this.userRepository.save(user);
        System.out.println("DATA"+ " "+contact);
        System.out.println("Added to database");

        //message success
        session.setAttribute("message", new Message("Created Successfully", "success"));

       }
        catch(Exception e){
            e.printStackTrace();
            //error
            session.setAttribute("message", new Message("Something went Wrong", "danger"));

        }
        return "normal/add_contact_form";
    }



    //show contacts handler
    //per page 5[n]
    //current page =0[page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal, HttpSession session) {
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            m.addAttribute("message", message);
            // Do not remove the message from the session here
        }
    
        m.addAttribute("title", "Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 6);
        Page<Contact> contact = this.contactRepository.findContactsByUser(user.getId(), pageable);
        m.addAttribute("contacts", contact);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contact.getTotalPages());
    
        return "normal/show_contacts";
    }
    
    @RequestMapping("/{cId}/contact")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal,HttpSession session) {
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
    
        Optional<Contact> contactOp = this.contactRepository.findById(cId);
        
        if (contactOp.isPresent()) {
            Contact contact = contactOp.get();
            
            // Verify that the contact belongs to the logged-in user
            if (user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                return "normal/contact_detail";
            } else {
                return "redirect:/user/show-contacts/0";
            }
        } else {
            return "redirect:/user/show-contacts/0";
        }
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid,Model model,Principal principal,HttpSession session){
       Optional<Contact> contactOptional= this.contactRepository.findById(cid);
       String username=principal.getName();
       User user=this.userRepository.getUserByUserName(username);
       
      Contact contact= contactOptional.get();
      if(contact.getUser().getId()==user.getId()){
      //  contact.setUser(null);
      //this.contactRepository.delete(contact);
      user.getContacts().remove(contact);
      this.userRepository.save(user);
      session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));
      return "redirect:/user/show-contacts/0";
    
    }
        return "redirect:/user/show-contacts/0";
    }
    


    //update form
    @PostMapping("/update/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid,Model m){

        Contact contact=this.contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);
        m.addAttribute("title", "Update Contact");
        return "normal/update_form";
    }

    //update Contact
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal){
        System.out.println("CONTACT"+ contact);
        try{
            //image...


            //old contact details
            Contact old=this.contactRepository.findById(contact.getCid()).get();
            if(!file.isEmpty()){

                //delete
                
                java.io.File deleteFile=new ClassPathResource("static/img").getFile();
                java.io.File file1=new File(deleteFile,old.getImage());
                file1.delete();
                java.io.File saveFile=new ClassPathResource("static/img").getFile();
                Path path= Paths.get(saveFile.getAbsolutePath()+java.io.File.separator +file.getOriginalFilename());
                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            }else{
                contact.setImage(old.getImage());
            }
            User user=this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Updated Successfully", "success"));
        }catch(Exception e){
            e.printStackTrace();
        }

        return "redirect:/user/show-contacts/0";
    }

//profile handler
@GetMapping("/profile")
public String yourProfile(Model model){
    model.addAttribute("title", "Profile Page" );
    return "normal/profile";

}
}
