package batistaReviver.studentApi.service;

import batistaReviver.studentApi.exception.EntityNotFoundException;
import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserAppDetailsService implements UserDetailsService {

  private final UserAppRepository userAppRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    UserApp userApp =
        userAppRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    return new User(userApp.getEmail(), userApp.getPassword(), Collections.emptyList());
  }
}
