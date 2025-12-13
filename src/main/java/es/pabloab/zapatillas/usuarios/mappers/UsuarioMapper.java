package es.pabloab.zapatillas.usuarios.mappers;

import es.pabloab.zapatillas.usuarios.dto.UsuarioCreateDto;
import es.pabloab.zapatillas.usuarios.dto.UsuarioResponseDto;
import es.pabloab.zapatillas.usuarios.dto.UsuarioUpdateDto;
import es.pabloab.zapatillas.usuarios.models.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UsuarioMapper {
    public Usuario toUsuario(Long id, UsuarioCreateDto dto) {
        return Usuario.builder()
                .id(id)
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public Usuario toUsuario(UsuarioUpdateDto dto, Usuario usuario){
        return Usuario.builder()
                .id(usuario.getId())
                .nombre(dto.getNombre()!= null ? dto.getNombre() : usuario.getNombre())
                .email(dto.getEmail()!= null ? dto.getNombre() : usuario.getEmail())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public UsuarioResponseDto toResponseDto(Usuario usuario){
        return UsuarioResponseDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }
}
