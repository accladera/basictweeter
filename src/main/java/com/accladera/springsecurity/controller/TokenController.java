package com.accladera.springsecurity.controller;

import com.accladera.springsecurity.controller.dto.LoginRequest;
import com.accladera.springsecurity.controller.dto.LoginResponse;
import com.accladera.springsecurity.entities.Role;
import com.accladera.springsecurity.repository.UserRepository;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class TokenController {
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public TokenController(JwtEncoder jwtEncoder, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository){
        this.jwtEncoder =jwtEncoder;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
       var user = userRepository.findByUsername(loginRequest.username());
       if (user.isEmpty()|| !user.get().isLoginCorrect(loginRequest,this.bCryptPasswordEncoder)) {
           throw  new BadCredentialsException("user or password is invalid.");
       }


       var now = Instant.now();
       var expiresIn= 300L;
       var scope = user.get()
               .getRoles()
               .stream().map(Role::getName)
               .collect(Collectors.joining(" "));

       var claims = JwtClaimsSet.builder()
               .issuer("mybackend")
               .subject(user.get().getUserId().toString())
               .issuedAt(now)
               .expiresAt(now.plusSeconds(expiresIn))
               .claim("scope",scope)
               .build();
       var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
       return ResponseEntity.ok(new LoginResponse(jwtValue,expiresIn));



    }

}
