package es.pabloab.zapatillas.auth.controllers;

import es.pabloab.zapatillas.auth.dto.AuthResponseDto;
import es.pabloab.zapatillas.auth.dto.LogingDto;
import es.pabloab.zapatillas.auth.dto.RegisterDto;
import es.pabloab.zapatillas.auth.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto>register(@Valid @RequestBody RegisterDto registerDto){
        try{
            AuthResponseDto response = authenticationService.register(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LogingDto logingDto){
        try {
            AuthResponseDto response = authenticationService.login(logingDto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
