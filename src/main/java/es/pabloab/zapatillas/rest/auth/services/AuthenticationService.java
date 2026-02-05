package es.pabloab.zapatillas.rest.auth.services;

import es.pabloab.zapatillas.rest.auth.dto.AuthResponseDto;
import es.pabloab.zapatillas.rest.auth.dto.LogingDto;
import es.pabloab.zapatillas.rest.auth.dto.RegisterDto;
import es.pabloab.zapatillas.rest.auth.exceptions.AuthDifferentPasswords;
import es.pabloab.zapatillas.rest.auth.exceptions.AuthExistingUsernameOrEmail;
import es.pabloab.zapatillas.rest.auth.repositories.AuthUsersRepository;
import es.pabloab.zapatillas.rest.user.models.Role;
import es.pabloab.zapatillas.rest.user.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(@Valid RegisterDto registerDto){
        log.info("Creanel usuario:{}",registerDto);
        if (!registerDto.getPassword().equals(registerDto.getPasswordComprobacion())){
            log.warn("Intento de registro con contraseñas diferentes para username:{}",registerDto.getUsername());
            throw new AuthDifferentPasswords("Las contraseñas no coinciden");
        }

        // Crea el usuario
        User user = User.builder()
                .nombre(registerDto.getNombre())
                .apellidos(registerDto.getApellidos())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                .deleted(false)
                .build();

        try {
            // Intentar guardar el usuario
            user = authUsersRepository.save(user);
            log.info("Usuario creado exitosamente: {}", user.getUsername());

            // Generar token JWT
            String token = jwtService.generateToken(user);

            // Construir y retornar la respuesta
            return AuthResponseDto.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nombre(user.getNombre())
                    .apellidos(user.getApellidos())
                    .build();

        } catch (DataIntegrityViolationException ex) {
            // Si falla por violación de restricciones únicas (username o email duplicado)
            log.error("Error al crear usuario - Username o email ya existe: {}", registerDto.getUsername());
            throw new AuthExistingUsernameOrEmail(
                    "El usuario con username '" + registerDto.getUsername() +
                            "' o email '" + registerDto.getEmail() + "' ya existe"
            );
        }
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
