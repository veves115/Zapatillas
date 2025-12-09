package es.pabloab.zapatillas.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.zapatillas.repositories.ZapatillasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import es.pabloab.zapatillas.zapatillas.controllers.websocket.ZapatillasWebSocketController;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("Unit tests for ZapatillasServiceImpl (basic)")
class ZapatillasServiceImplTest {

    @Mock
    private ZapatillasRepository repository;
    @Mock
    private ZapatillaMapper mapper;
    @Mock
    private ZapatillasWebSocketController webSocketController;

    private ZapatillasServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ZapatillasServiceImpl(repository, mapper, webSocketController);
    }

    @Test
    void saveDelegatesToRepositoryAndSendsNotification() {
        ZapatillaCreateDto dto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Test")
                .codigoProducto("C123")
                .talla(42.0)
                .color("Negro")
                .tipo("Running")
                .precio(99.0)
                .stock(5)
                .build();

        Zapatilla zapatilla = Zapatilla.builder()
                .id(1L)
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

        given(mapper.toZapatilla(null, dto)).willReturn(zapatilla);
        given(repository.save(zapatilla)).willReturn(zapatilla);
        given(mapper.toResponseDto(zapatilla)).willReturn(ZapatillaResponseDto.builder().id(1L).marca("Nike").build());

        ZapatillaResponseDto result = service.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        ArgumentCaptor<Zapatilla> captor = ArgumentCaptor.forClass(Zapatilla.class);
        verify(repository).save(captor.capture());
        Zapatilla saved = captor.getValue();
        assertThat(saved.getMarca()).isEqualTo("Nike");

        verify(webSocketController).enviarNotificacion(any());
    }

}


