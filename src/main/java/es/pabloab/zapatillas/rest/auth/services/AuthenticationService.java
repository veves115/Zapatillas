package es.pabloab.zapatillas.rest.auth.services;

import es.pabloab.zapatillas.rest.auth.dto.AuthResponseDto;
import es.pabloab.zapatillas.rest.auth.dto.LogingDto;
import es.pabloab.zapatillas.rest.auth.dto.RegisterDto;
import es.pabloab.zapatillas.rest.auth.repositories.AuthUsersRepository;
import es.pabloab.zapatillas.rest.user.models.Role;
import es.pabloab.zapatillas.rest.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterDto registerDto){
        if(authUsersRepository.existsByUsername(registerDto.getUsername())){
            throw new RuntimeException("El username ya está en uso");
        }
        if(authUsersRepository.existsByEmail(registerDto.getEmail())){
            throw new RuntimeException("El email ya está en uso");
        }
        User user = User.builder()
                .nombre(registerDto.getNombre())
                .apellidos(registerDto.getApellidos())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(Set.of(Role.USER))
                .deleted(false)
                .build();

        user = authUsersRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .build();
    }
    public AuthResponseDto login (LogingDto loginDto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        User user = authUsersRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .build();
    }

}
