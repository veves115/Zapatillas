package es.pabloab.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.zapatillas.repositories.ZapatillasRepository;
import es.pabloab.zapatillas.zapatillas.services.ZapatillasServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
 class ZapatillasServiceImplTest {
    @Mock
    private ZapatillasRepository repository;

    @Spy
    private ZapatillaMapper mapper = new ZapatillaMapper();

    @InjectMocks
    private ZapatillasServiceImpl service;

    private Zapatilla zapatillaBase;

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
    }
    @Test
    void findALLSinFiltrosDevuelveTodos(){
        given(repository.findAll()).willReturn(List.of(zapatillaBase));
        List<ZapatillaResponseDto> resultado = service.findAll(null,null);
        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }
    @Test
    void findAllFiltraPorMarca(){
        given(repository.findAllByMarca()).willReturn(List.of(zapatillaBase));
        List<ZapatillaResponseDto> resultado = service.findAll("Nike",null);
        assertThat(resultado).hasSize(1);
        verify(repository).findAllByMarca("Nike");
    }
    @Test
    void findAllFiltraPorTipo(){
        given(repository.findAllByTipo("Running")).willReturn(List.of(zapatillaBase));
        List<ZapatillaResponseDto> resultado = service.findAll(null,"Running");
        assertThat(resultado).hasSize(1);
        verify(repository).findAllByTipo("Running");
    }
}
