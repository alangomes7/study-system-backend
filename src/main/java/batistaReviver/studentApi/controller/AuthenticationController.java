package batistaReviver.studentApi.controller;

import batistaReviver.studentApi.model.UserApp;
import batistaReviver.studentApi.repository.UserAppRepository;
import batistaReviver.studentApi.service.JwtService;
import batistaReviver.studentApi.util.TokenResponse;
import batistaReviver.studentApi.util.UserAppLogin;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserAppRepository userAppRepository;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
      @Valid @RequestBody UserAppLogin userAppLogin, HttpServletResponse response) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userAppLogin.getEmail(), userAppLogin.getPassword()));

    UserApp userApp = userAppRepository.findByEmail(userAppLogin.getEmail()).orElseThrow();

    String accessToken = jwtService.generateAccessToken(userApp);

    return new ResponseEntity<>(
        new TokenResponse(
            accessToken, userApp.getId(), userApp.getName(), userApp.getRole().name()),
        HttpStatus.OK);
  }
}
