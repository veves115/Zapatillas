package es.pabloab.zapatillas.rest.user.services;

import es.pabloab.zapatillas.rest.auth.repositories.AuthUsersRepository;
import es.pabloab.zapatillas.rest.user.dto.UserResponseDto;
import es.pabloab.zapatillas.rest.user.dto.UserUpdateDto;
import es.pabloab.zapatillas.rest.user.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Implementación del servicio de usuarios.
 * 
 * Este servicio maneja la lógica de negocio para gestionar perfiles de usuario.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final AuthUsersRepository userRepository;

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado id=" + id));
        
        return toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        log.info("Actualizando usuario id={} con datos={}", id, dto);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado id=" + id));
        
        // Actualizamos solo los campos permitidos
        if (dto.getNombre() != null) {
            user.setNombre(dto.getNombre());
        }
        if (dto.getApellidos() != null) {
            user.setApellidos(dto.getApellidos());
        }
        if (dto.getEmail() != null) {
            // Verificamos que el email no esté en uso por otro usuario
            userRepository.findByEmail(dto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new IllegalArgumentException("El email ya está en uso");
                        }
                    });
            user.setEmail(dto.getEmail());
        }
        
        // Actualizamos el timestamp
        user.setUpdatedAt(java.time.LocalDateTime.now());
        
        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    /**
     * Convierte un User a UserResponseDto.
     * 
     * @param user El usuario a convertir
     * @return El DTO con la información del usuario
     */
    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deleted(user.getDeleted())
                .build();
    }
}
