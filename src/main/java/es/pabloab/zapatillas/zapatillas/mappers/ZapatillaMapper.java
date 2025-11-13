package es.pabloab.zapatillas.zapatillas.mappers;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ZapatillaMapper {
    public Zapatilla toZapatilla(Long id,ZapatillaCreateDto dto) {
        return Zapatilla.builder()
                .id(id)
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .codigoProducto(dto.getCodigoProducto())
                .talla(dto.getTalla())
                .color(dto.getColor())
                .tipo(dto.getTipo())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    public Zapatilla toZapatilla(ZapatillaUpdateDto dto, Zapatilla zapatilla) {
        return Zapatilla.builder()
                .id(zapatilla.getId())
                .marca(zapatilla.getMarca()) // No se actualiza
                .modelo(zapatilla.getModelo()) // No se actualiza
                .codigoProducto(dto.getCodigoProducto() != null ?
                        dto.getCodigoProducto() : zapatilla.getCodigoProducto())
                .talla(dto.getTalla() != null ?
                        dto.getTalla() : zapatilla.getTalla())
                .color(dto.getColor() != null ?
                        dto.getColor() : zapatilla.getColor())
                .tipo(dto.getTipo() != null ?
                        dto.getTipo() : zapatilla.getTipo())
                .precio(dto.getPrecio() != null ?
                        dto.getPrecio() : zapatilla.getPrecio())
                .createdAt(zapatilla.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(zapatilla.getUuid())
                .build();
    }
    public ZapatillaResponseDto toResponseDto(Zapatilla zapatilla) {
        return ZapatillaResponseDto.builder()
                .id(zapatilla.getId())
                .marca(zapatilla.getMarca())
                .modelo(zapatilla.getModelo())
                .codigoProducto(zapatilla.getCodigoProducto())
                .talla(zapatilla.getTalla())
                .color(zapatilla.getColor())
                .tipo(zapatilla.getTipo())
                .precio(zapatilla.getPrecio())
                .stock(zapatilla.getStock())
                .createdAt(zapatilla.getCreatedAt())
                .updatedAt(zapatilla.getUpdatedAt())
                .uuid(zapatilla.getUuid())
                .build();
    }
    public List<ZapatillaResponseDto> toResponseDtoList(List<Zapatilla> zapatillas) {
        return zapatillas.stream()
                .map(this::toResponseDto)
                .toList();
    }

}


