package id.my.rizkiyuwanda.userapp;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserAppService implements UserDetailsService {

    @Autowired
    private UserAppRepository userAppRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<UserApp> userApp = userAppRepository.findById(id);
        if (userApp.isPresent() == false) {
            throw new UsernameNotFoundException("No user app present with username: " + id);
        } else {
            return new org.springframework.security.core.userdetails.User(userApp.get().getId(), userApp.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +userApp.get().getRole())));//Harus ditambahkan ROLE_ sesuai aturan spring
        }
    }
}
