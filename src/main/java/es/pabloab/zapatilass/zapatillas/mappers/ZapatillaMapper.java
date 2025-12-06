package es.pabloab.zapatilass.zapatillas.mappers;


import es.pabloab.zapatilass.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZapatillaMapper {
    public Zapatilla toZapatilla(ZapatillaCreateDto dto) {
        return Zapatilla.builder()
                .marca(dto.getMarca())
                .modelo(dto.getModelo())
                .codigoProducto(dto.getCodigoProducto())
                .talla(dto.getTalla())
                .color(dto.getColor())
                .tipo(dto.getTipo())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                // El UUID, createdAt y updatedAt se establecerán en el servicio
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
                .updatedAt(zapatilla.getUpdatedAt()) // Se actualizará en el servicio
                .uuid(zapatilla.getUuid())
                .stock(dto.getStock() != null ? dto.getStock() : zapatilla.getStock())
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



