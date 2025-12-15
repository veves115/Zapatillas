package es.pabloab.zapatillas.cliente.mappers;

import es.pabloab.zapatillas.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.cliente.dto.ClienteUpdateDto;
import es.pabloab.zapatillas.cliente.models.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClienteMapper {
    public Cliente toUsuario(Long id, ClienteCreateDto dto) {
        return Cliente.builder()
                .id(id)
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public Cliente toUsuario(ClienteUpdateDto dto, Cliente cliente){
        return Cliente.builder()
                .id(cliente.getId())
                .nombre(dto.getNombre()!= null ? dto.getNombre() : cliente.getNombre())
                .email(dto.getEmail()!= null ? dto.getNombre() : cliente.getEmail())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public ClienteResponseDto toResponseDto(Cliente cliente){
        return ClienteResponseDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .build();
    }
}
