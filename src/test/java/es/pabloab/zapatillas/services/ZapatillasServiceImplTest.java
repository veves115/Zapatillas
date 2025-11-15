package es.pabloab.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.zapatillas.repositories.ZapatillasRepository;
import es.pabloab.zapatillas.zapatillas.services.ZapatillasServiceImpl;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
    }

}
