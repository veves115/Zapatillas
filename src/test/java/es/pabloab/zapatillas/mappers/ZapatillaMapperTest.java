package es.pabloab.zapatillas.mappers;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests de ZapatillaMapper")
class ZapatillaMapperTest {
    private final ZapatillaMapper mapper = new ZapatillaMapper();


    @Test
    void toZapatillaDesdeCreateDtoRellenaCampos(){
        ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air")
                .codigoProducto("NI1234KE")
                .talla(42.5)
                .color("Negro")
                .tipo("Running")
                .precio(120.0)
                .stock(5)
                .build();

        Zapatilla zapatilla = mapper.toZapatilla(10L,dto);

        assertThat(zapatilla.getId()).isEqualTo(10L);
        assertThat(zapatilla.getMarca()).isEqualTo(dto.getMarca());
        assertThat(zapatilla.getModelo()).isEqualTo(dto.getModelo());
        assertThat(zapatilla.getCodigoProducto()).isEqualTo(dto.getCodigoProducto());
        assertThat(zapatilla.getTalla()).isEqualTo(dto.getTalla());
        assertThat(zapatilla.getColor()).isEqualTo(dto.getColor());
        assertThat(zapatilla.getTipo()).isEqualTo(dto.getTipo());
        assertThat(zapatilla.getPrecio()).isEqualTo(dto.getPrecio());
        assertThat(zapatilla.getStock()).isEqualTo(dto.getStock());
        assertThat(zapatilla.getUuid()).isNotNull();
        assertThat(zapatilla.getCreatedAt()).isNotNull();
        assertThat(zapatilla.getUpdatedAt()).isNotNull();
    }
    @Test
    void toZapatillaDesdeUpdateDtoMantieneValoresCuandoSonNull(){
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now().minusHours(2);
        Zapatilla existente = Zapatilla.builder()
                .id(1L)
                .marca("Nike")
                .modelo("Air")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Nero")
                .tipo("Running")
                .precio(100.0)
                .stock(5)
                .uuid(UUID.randomUUID())
                .createdAt(created)
                .updatedAt(updated)
                .build();

        ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder().build();

        Zapatilla resultado = mapper.toZapatilla(dto,existente);

        assertThat(resultado).isEqualTo(existente);
        assertThat(resultado.getUpdatedAt()).isAfter(updated);
    }
}
