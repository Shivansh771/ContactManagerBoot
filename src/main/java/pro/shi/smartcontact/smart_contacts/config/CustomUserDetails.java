package pro.shi.smartcontact.smart_contacts.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import pro.shi.smartcontact.smart_contacts.entities.User;

public class CustomUserDetails implements UserDetails{
    public CustomUserDetails(User user) {
        this.user = user;
    }

    private User user;

    


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }
    @Override
    public boolean isEnabled(){
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority simpleGrantedAuthority= new SimpleGrantedAuthority(user.getRole());
    return List.of(simpleGrantedAuthority);
    }
}
