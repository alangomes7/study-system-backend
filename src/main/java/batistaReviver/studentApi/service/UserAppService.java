package batistaReviver.studentApi.service;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.util.Role;
import batistaReviver.studentApi.util.UserAppInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserAppService {

  private final UserAppRepository userAppRepository;
  private final PasswordEncoder passwordEncoder;

  public UserAppInfo createUserApp(UserApp userApp) {
    try {
      UserApp userAppRegistered =
          userAppRepository
              .findByEmail(userApp.getEmail())
              .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
      return new UserAppInfo(false, true, "User already registered!");
    } catch (UsernameNotFoundException e) {
      userApp.setPassword(passwordEncoder.encode(userApp.getPassword()));
      userApp.setRole(Role.USER);
      userAppRepository.save(userApp);
      return new UserAppInfo(true, false, "User registered!");
    }
  }

  public List<UserApp> recuperarUsuarios() {
    return userAppRepository.findAll();
  }
}
