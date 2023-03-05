package id.my.rizkiyuwanda.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import id.my.rizkiyuwanda.userapp.UserApp;
import id.my.rizkiyuwanda.userapp.UserAppRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    @Autowired
    private UserAppRepository userAppRepository;
    @Autowired
    private AuthenticationContext authenticationContext;


    public Optional<UserApp> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userAppRepository.findById(userDetails.getUsername()).get());
    }

    public void logout() {
        authenticationContext.logout();
    }

}
