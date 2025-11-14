package es.pabloab.zapatillas.mappers;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests de ZapatillaMapper")
class ZapatillaMapperTest {
    private final ZapatillaMapper mapper = new ZapatillaMapper();

    @Nested
    @DisplayName("toZapatilla(Long,CreateDto)")
    class CreateDtoToModelTests{
    @Test
    @DisplayName("Debe mapear todos los campos del dto al modelo")
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
    @DisplayName("UUID generado debe ser único")
        void toZapatilla_generaUuidUnicos(){
        ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Negro")
                .tipo("Running")
                .precio(100.0)
                .stock(10)
                .build();

        Zapatilla z1 = mapper.toZapatilla(1L, dto);
        Zapatilla z2 = mapper.toZapatilla(2L, dto);

        assertThat(z1.getUuid()).isNotEqualTo(z2.getUuid());
    }
    @Test
    @DisplayName("Las fechas createdAt y updatedAt deben ser recientes")
        void toZapatilla_generaFechasRecientes(){
        LocalDateTime antes = LocalDateTime.now();

        ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Negro")
                .tipo("Running")
                .precio(100.0)
                .stock(10)
                .build();

        Zapatilla zapatilla = mapper.toZapatilla(1L, dto);

        LocalDateTime despues = LocalDateTime.now();

        assertThat(zapatilla.getCreatedAt()).isAfterOrEqualTo(antes).isBeforeOrEqualTo(despues);
    }
}

    @Nested
    @DisplayName("toZapatilla,UpdateDto,Zapatilla")
    class UpdateDtoToModelTests{

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
                .color("Negro")
                .tipo("Running")
                .precio(100.0)
                .stock(5)
                .uuid(UUID.randomUUID())
                .createdAt(created)
                .updatedAt(updated)
                .build();

        ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder().build();

        Zapatilla resultado = mapper.toZapatilla(dto,existente);

        assertThat(resultado.getId()).isEqualTo(existente.getId());
        assertThat(resultado.getMarca()).isEqualTo(existente.getMarca());
        assertThat(resultado.getModelo()).isEqualTo(existente.getModelo());
        assertThat(resultado.getCodigoProducto()).isEqualTo(existente.getCodigoProducto());
        assertThat(resultado.getTalla()).isEqualTo(existente.getTalla());
        assertThat(resultado.getColor()).isEqualTo(existente.getColor());
        assertThat(resultado.getTipo()).isEqualTo(existente.getTipo());
        assertThat(resultado.getPrecio()).isEqualTo(existente.getPrecio());
        assertThat(resultado.getStock()).isEqualTo(existente.getStock());
        assertThat(resultado.getUuid()).isEqualTo(existente.getUuid());

        assertThat(resultado.getCreatedAt()).isEqualTo(created);
        assertThat(resultado.getUpdatedAt()).isAfter(updated);
    }
    @Test
    @DisplayName("Debe actualizar solo los campos no nulos del UpdateDto")
        void toZapatilla_actualizaSoloNuevosValores(){
        Zapatilla existente = Zapatilla.builder()
                .id(1L)
                .marca("Nike")
                .modelo("Air")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Negro")
                .tipo("Running")
                .precio(100.0)
                .stock(10)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder()
                .precio(150.0)
                .stock(5)
                .build();
    }
    }
}
