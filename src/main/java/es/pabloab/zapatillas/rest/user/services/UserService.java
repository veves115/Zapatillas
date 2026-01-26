package es.pabloab.zapatillas.rest.user.services;

import es.pabloab.zapatillas.rest.user.dto.UserResponseDto;
import es.pabloab.zapatillas.rest.user.dto.UserUpdateDto;

/**
 * Interfaz del servicio para gestionar usuarios.
 * 
 * Este servicio maneja operaciones relacionadas con el perfil de usuario.
 */
public interface UserService {
    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id El ID del usuario
     * @return El DTO con la información del usuario
     */
    UserResponseDto findById(Long id);

    /**
     * Actualiza la información de un usuario.
     * 
     * @param id El ID del usuario a actualizar
     * @param dto Los datos a actualizar
     * @return El DTO con la información actualizada del usuario
     */
    UserResponseDto update(Long id, UserUpdateDto dto);
}
