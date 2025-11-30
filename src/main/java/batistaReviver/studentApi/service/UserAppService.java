package batistaReviver.studentApi.service;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.util.Role;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserAppService {

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
}