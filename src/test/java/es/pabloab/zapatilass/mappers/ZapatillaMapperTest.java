package es.pabloab.zapatilass.mappers;

import es.pabloab.zapatilass.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.models.Zapatilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
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

            Zapatilla resultado = mapper.toZapatilla(dto,existente);
            assertThat(resultado.getPrecio()).isEqualTo(150.0);
            assertThat(resultado.getStock()).isEqualTo(5);
            assertThat(resultado.getTalla()).isEqualTo(42.0);
            assertThat(resultado.getColor()).isEqualTo("Negro");
            assertThat(resultado.getTipo()).isEqualTo("Running");
            assertThat(resultado.getCodigoProducto()).isEqualTo("NI1234KE");
        }
        @Test
        @DisplayName("No se debe actualizar marca ni modelo(campos protegidos)")
        void toZapatilla_noActualizaMarcaNiModelo(){
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
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder()
                    .precio(200.00)
                    .build();

            Zapatilla resultado = mapper.toZapatilla(dto,existente);
            assertThat(resultado.getMarca()).isEqualTo("Nike");
            assertThat(resultado.getModelo()).isEqualTo("Air");
        }
        @Test
        @DisplayName("Debe mantener createdAt pero actualizar UpdatedAt")
        void toZapatilla_mantieneCreatedAtActualizaUpdatedAt() throws InterruptedException {
            LocalDateTime createdOriginal = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedOriginal = LocalDateTime.now().minusHours(2);

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
                    .createdAt(createdOriginal)
                    .updatedAt(updatedOriginal)
                    .build();

            ZapatillaUpdateDto dto = ZapatillaUpdateDto.builder()
                    .precio(150.00)
                    .build();

            Thread.sleep(100);
            Zapatilla resultado = mapper.toZapatilla(dto,existente);
            assertThat(resultado.getCreatedAt()).isEqualTo(createdOriginal);
            assertThat(resultado.getUpdatedAt()).isAfter(updatedOriginal);
        }
    }
    @Nested
    @DisplayName("toResponseDto(Zapatilla)")
    class ModeltoResponseDtoTest{
        @Test
        @DisplayName("Debe mapear todos los campos del modelo al Dto")
        void toResponseDto_mapeaTodosCampos(){
            Zapatilla zapatilla = Zapatilla.builder()
                    .id(1L)
                    .marca("Nike")
                    .modelo("Air")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Negro")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .uuid(UUID.randomUUID())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            ZapatillaResponseDto dto = mapper.toResponseDto(zapatilla);
            assertThat(dto.getId()).isEqualTo(zapatilla.getId());
            assertThat(dto.getMarca()).isEqualTo(zapatilla.getMarca());
            assertThat(dto.getModelo()).isEqualTo(zapatilla.getModelo());
            assertThat(dto.getCodigoProducto()).isEqualTo(zapatilla.getCodigoProducto());
            assertThat(dto.getTalla()).isEqualTo(zapatilla.getTalla());
            assertThat(dto.getColor()).isEqualTo(zapatilla.getColor());
            assertThat(dto.getTipo()).isEqualTo(zapatilla.getTipo());
            assertThat(dto.getPrecio()).isEqualTo(zapatilla.getPrecio());
            assertThat(dto.getStock()).isEqualTo(zapatilla.getStock());
            assertThat(dto.getUuid()).isEqualTo(zapatilla.getUuid());
            assertThat(dto.getCreatedAt()).isEqualTo(zapatilla.getCreatedAt());
            assertThat(dto.getUpdatedAt()).isEqualTo(zapatilla.getUpdatedAt());
        }
    }
    @Nested
    @DisplayName("toResponseDto(List<Zapatilla>)")
    class ListtoListDtoTests{

        @Test
        @DisplayName("Lista vacía debe devolver lista vacía")
        void toResponseDtoList_listaVacia(){
            List<Zapatilla> lista = List.of();
            List<ZapatillaResponseDto> resultado = mapper.toResponseDtoList(lista);
            assertThat(resultado).isEmpty();
        }
        @Test
        @DisplayName("Debe mapear todos los elementos de la lista")
        void toResponseDtoList_mapeaTodasLasZapatillas(){
            Zapatilla z1 = crearZapatilla(1L,"Nike");
            Zapatilla z2 = crearZapatilla(2L, "Adidas");
            Zapatilla z3 = crearZapatilla(3L, "Puma");

            List<Zapatilla> lista = List.of(z1, z2, z3);
            List<ZapatillaResponseDto> resultado = mapper.toResponseDtoList(lista);
            assertThat(resultado).hasSize(3);
            assertThat(resultado.get(0).getId()).isEqualTo(z1.getId());
            assertThat(resultado.get(0).getMarca()).isEqualTo(z1.getMarca());
            assertThat(resultado.get(0).getModelo()).isEqualTo(z1.getModelo());
        }
        @Test
        @DisplayName("El orden de la lista debe mantenerse")
        void toResponseDtoList_mantieneOrden(){
            List<Zapatilla> lista = List.of(
                    crearZapatilla(3L,"Puma"),
                    crearZapatilla(1L,"Nike"),
                    crearZapatilla(2l,"Adidas")
            );
            List<ZapatillaResponseDto> resultado = mapper.toResponseDtoList(lista);
            assertThat(resultado).extracting(ZapatillaResponseDto::getId).containsExactly(3L,1L,2L);
        }
        //Método para crear zapatillas de prueba
        private Zapatilla crearZapatilla(Long id, String marca){
            return Zapatilla.builder()
                    .id(id)
                    .marca(marca)
                    .modelo("Modelo" + id)
                    .codigoProducto("Producto" + id)
                    .tipo("Tipo" + id)
                    .precio(100.0)
                    .stock(10)
                    .uuid(UUID.randomUUID())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }
}

