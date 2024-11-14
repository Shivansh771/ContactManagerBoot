package pro.shi.smartcontact.smart_contacts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pro.shi.smartcontact.smart_contacts.dao.UserRepository;
import pro.shi.smartcontact.smart_contacts.entities.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetching user by email
        User user = userRepository.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        // Returning a CustomUserDetails object
        return new CustomUserDetails(user);
    }
}
