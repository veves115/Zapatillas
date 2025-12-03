package es.pabloab.zapatilass.services;

import es.pabloab.zapatilass.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatilass.mappers.ZapatillaMapper;
import es.pabloab.zapatilass.models.Zapatilla;
import es.pabloab.zapatilass.repositories.ZapatillasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ZapatillasService")
class ZapatillasServiceImplTest {
    @Mock
    private ZapatillasRepository repository;

    @Spy
    private ZapatillaMapper mapper = new ZapatillaMapper();

    @InjectMocks
    private ZapatillasServiceImpl service;

    private Zapatilla zapatillaBase;
    private ZapatillaCreateDto createDto;
    private ZapatillaUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        zapatillaBase = Zapatilla.builder()
                .id(1L)
                .marca("Nike")
                .modelo("Air Max 90")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Blanco")
                .tipo("Running")
                .precio(129.99)
                .stock(10)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .uuid(UUID.randomUUID())
                .build();

        createDto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air Max 90")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Blanco")
                .tipo("Running")
                .precio(129.99)
                .stock(10)
                .build();

        updateDto = ZapatillaUpdateDto.builder()
                .precio(149.99)
                .stock(20)
                .build();
    }
    @Nested
    @DisplayName("findall()")
    class findAllTests{
        @Test
        @DisplayName("Sin filtros debe llamar a repository.findAll()")
        void findALLSinFiltrosDevuelveTodos(){
            given(repository.findAll()).willReturn(List.of(zapatillaBase));
            List<ZapatillaResponseDto> resultado = service.findAll(null,null);
            assertThat(resultado).hasSize(1);
            verify(repository).findAll();
        }
        @Test
        @DisplayName("Con marca debe llamar a repository.findAllByMarca()")
        void findAllFiltraPorMarca(){
            given(repository.findAllByMarca("Nike")).willReturn(List.of(zapatillaBase));
            List<ZapatillaResponseDto> resultado = service.findAll("Nike",null);
            assertThat(resultado).hasSize(1);
            verify(repository).findAllByMarca("Nike");
        }
        @Test
        @DisplayName("Con tipo debe llamar a repository.findAllByTipo()")
        void findAllFiltraPorTipo(){
            given(repository.findAllByTipo("Running")).willReturn(List.of(zapatillaBase));
            List<ZapatillaResponseDto> resultado = service.findAll(null,"Running");
            assertThat(resultado).hasSize(1);
            verify(repository).findAllByTipo("Running");
        }
        @Test
        @DisplayName("Con marca y tipo debe llamar a repository.findAllByMarcaAndTipo()")
        void findAllFiltraPorMarcaYTipo(){
            given(repository.findAllByMarcaAndTipo("Nike", "Running")).willReturn(List.of(zapatillaBase));

            List<ZapatillaResponseDto> resultado = service.findAll("Nike","Running");

            assertThat(resultado).hasSize(1);
            verify(repository).findAllByMarcaAndTipo("Nike", "Running");
        }
        @Test
        @DisplayName("Debe mapear correctamente a DTOs")
        void findAllMapeaCorrectamente(){
            given(repository.findAll()).willReturn(List.of(zapatillaBase));
            List<ZapatillaResponseDto> resultado = service.findAll(null,null);
            verify(mapper).toResponseDtoList(anyList());

            ZapatillaResponseDto dto = resultado.get(0);
            assertThat(dto.getId()).isEqualTo(zapatillaBase.getId());
            assertThat(dto.getMarca()).isEqualTo(zapatillaBase.getMarca());
        }
    }
    @Nested
    @DisplayName("findById()")
    class findByIdTests{

        @Test
        @DisplayName("Debe devolver DTO cuando la zapatilla existe")
        void findByIdZapatillaExiste(){
            given(repository.findById(1L)).willReturn(Optional.of(zapatillaBase));

            ZapatillaResponseDto resultado = service.findById(1L);
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getMarca()).isEqualTo("Nike");

            verify(repository).findById(1L);
            verify(mapper).toResponseDto(any(Zapatilla.class));
        }
        @Test
        @DisplayName("Debe lanzar una excepcion cuando no existe")
        void findByIdZapatillaNoExiste(){
            given(repository.findById(10000L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(10000L)).isInstanceOf(NoSuchElementException.class);
            verify(repository).findById(10000L);
        }
    }
    @Nested
    @DisplayName("findByUuid()")
    class findByUuidTests{
        @Test
        @DisplayName("Debe devolver DTO cuando UUID válido no existe")
        void findByUuidValidoYExiste() throws ZapatillaBadUuidException {
            String uuidStr = zapatillaBase.getUuid().toString();
            given(repository.findByUuid(any(UUID.class))).willReturn(zapatillaBase);

            ZapatillaResponseDto resultado = service.findByUuid(uuidStr);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getUuid()).isEqualTo(zapatillaBase.getUuid());

            verify(repository).findByUuid(any(UUID.class));
        }
        @Test
        @DisplayName("Debe lanzar ZapatillaBadUuidException cuando sea un UUID inválido")
        void findByUuidInvalido(){
            String uuidInvalido = "abc-12231-fsv";
            assertThatThrownBy(() -> service.findByUuid(uuidInvalido)).isInstanceOf(ZapatillaBadUuidException.class)
                    .hasMessageContaining(uuidInvalido);
            verify(repository,never()).findByUuid(any(UUID.class));
        }
        @Test
        @DisplayName("Debe devolver null cuando el UUID sea válido pero no exista")
        void findByUuidValidoPeroNoExiste() throws ZapatillaBadUuidException {
            String uuid = UUID.randomUUID().toString();
            given(repository.findByUuid(any(UUID.class))).willReturn(null);
            ZapatillaResponseDto resultado = service.findByUuid(uuid);
            assertThat(resultado).isNull();
        }
        @Nested
        @DisplayName("save()")
        class SaveTests{

            @Test
            @DisplayName("Debe crear zapatilla con ID del repository")
            void saveZapatillaNueva(){
                given(repository.nextId()).willReturn(1L);
                given(repository.save(any(Zapatilla.class))).willAnswer(invocation -> invocation.getArgument(0));

                ZapatillaResponseDto resultado = service.save(createDto);

                assertThat(resultado).isNotNull();
                assertThat(resultado.getId()).isEqualTo(1L);
                assertThat(resultado.getMarca()).isEqualTo(createDto.getMarca());

                verify(repository).nextId();
                verify(repository).save(any(Zapatilla.class));
            }
            @Test
            @DisplayName("Debe generar UUID automáticamente")
            void saveGeneraUuid(){
                given(repository.nextId()).willReturn(1L);
                given(repository.save(any(Zapatilla.class))).willAnswer(invocation -> invocation.getArgument(0));

                ZapatillaResponseDto resultado = service.save(createDto);
                assertThat(resultado.getUuid()).isNotNull();
                assertThat(resultado.getCreatedAt()).isNotNull();
                assertThat(resultado.getUpdatedAt()).isNotNull();
            }
        }
        @Nested
        @DisplayName("update()")
        class UpdateTests{

            @Test
            @DisplayName("Debe actualizar zapatilla existente")
            void updateZapatillaExiste(){
                given(repository.findById(1L)).willReturn(Optional.of(zapatillaBase));
                given(repository.save(any(Zapatilla.class))).willAnswer(invocation -> invocation.getArgument(0));

                ZapatillaResponseDto resultado = service.update(1L,updateDto);

                assertThat(resultado).isNotNull();
                assertThat(resultado.getPrecio()).isEqualTo(149.99);
                assertThat(resultado.getStock()).isEqualTo(20);

                verify(repository).findById(1L);
                verify(repository).save(any(Zapatilla.class));
            }
            @Test
            @DisplayName("Debe lanzar excepción si no existe")
            void updateZapatillaNoExiste(){
                given(repository.findById(9999L)).willReturn(Optional.empty());
                assertThatThrownBy(() -> service.update(9999L,updateDto)).isInstanceOf(NoSuchElementException.class);
                verify(repository,never()).save(any(Zapatilla.class));
            }
        }
        @Nested
        @DisplayName("deleteById()")
        class DeleteByIdTests{

            @Test
            @DisplayName("Debe eliminar zapatilla")
            void deleteByIdZapatillaExiste(){
                doNothing().when(repository).deleteById(1L);
                service.deleteById(1L);
                verify(repository).deleteById(1L);
            }
        }
    }

}

