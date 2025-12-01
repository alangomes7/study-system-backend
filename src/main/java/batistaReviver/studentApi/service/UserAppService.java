package batistaReviver.studentApi.service;

import batistaReviver.studentApi.exception.EntityNotFoundException;
import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.util.Role;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserAppService implements UserDetailsService {

  private final UserAppRepository userAppRepository;
  private final PasswordEncoder passwordEncoder;

  public UserApp createUserApp(UserApp userApp) {
    if (userAppRepository.findByEmail(userApp.getEmail()).isPresent()) {
      throw new RuntimeException("User already registered!");
    }

    userApp.setPassword(passwordEncoder.encode(userApp.getPassword()));

    if (userApp.getRole() == null) {
      userApp.setRole(Role.USER);
    }

    return userAppRepository.save(userApp);
  }

  public List<UserApp> fetchAllUserApps() {
    return userAppRepository.findAll();
  }

  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    UserApp userApp =
            userAppRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    return new User(userApp.getEmail(), userApp.getPassword(), Collections.emptyList());
  }
}