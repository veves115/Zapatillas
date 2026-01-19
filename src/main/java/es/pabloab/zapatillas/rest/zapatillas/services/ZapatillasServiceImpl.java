package es.pabloab.zapatillas.rest.zapatillas.services;

import es.pabloab.zapatillas.rest.zapatillas.controllers.websocket.ZapatillasWebSocketController;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.websocket.ZapatillaNotificacion;
import es.pabloab.zapatillas.rest.zapatillas.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatillas.rest.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.rest.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.rest.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZapatillasServiceImpl implements ZapatillasService {

    private final ZapatillasRepository repository;
    private final ZapatillaMapper mapper;
    private final ZapatillasWebSocketController webSocketController;  // ← NUEVO

    @Override
    public Page<ZapatillaResponseDto> findAll(String marca, String tipo, Pageable pageable) {
        Page<Zapatilla> zapatillasPage;

        if (marca != null && tipo != null) {
            zapatillasPage = repository.findAllByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(marca, tipo, pageable);
        } else if (marca != null) {
            zapatillasPage = repository.findAllByMarcaContainingIgnoreCase(marca, pageable);
        } else if (tipo != null) {
            zapatillasPage = repository.findAllByTipoContainingIgnoreCase(tipo, pageable);
        } else {
            zapatillasPage = repository.findAll(pageable);
        }

        return zapatillasPage.map(mapper::toResponseDto);
    }

    @Override
    public ZapatillaResponseDto findById(Long id) {
        Zapatilla zapatilla = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        return mapper.toResponseDto(zapatilla);
    }

    @Override
    public ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException {
        try {
            UUID u = UUID.fromString(uuid);
            Zapatilla zapatilla = repository.findByUuid(u)
                    .orElseThrow(() -> new NoSuchElementException());
            return mapper.toResponseDto(zapatilla);
        } catch (IllegalArgumentException e) {
            throw new ZapatillaBadUuidException(uuid);
        }
    }

    @Override
    public ZapatillaResponseDto save(ZapatillaCreateDto dto) {
        log.info("Guardando zapatilla: {}", dto);

        Zapatilla nueva = mapper.toZapatilla(null, dto);
        Zapatilla guardada = repository.save(nueva);
        ZapatillaResponseDto response = mapper.toResponseDto(guardada);

        // ← NUEVO: Enviar notificación WebSocket
        ZapatillaNotificacion notificacion = ZapatillaNotificacion.crear(
                ZapatillaNotificacion.TipoNotificacion.CREATED,
                guardada.getId(),
                "Nueva zapatilla: " + guardada.getMarca() + " " + guardada.getModelo(),
                response
        );
        webSocketController.enviarNotificacion(notificacion);

        return response;
    }

    @Override
    public ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto) {
        log.info("Actualizando zapatilla por id: {}", id);

        Zapatilla actual = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        Zapatilla actualizada = mapper.toZapatilla(dto, actual);
        Zapatilla guardada = repository.save(actualizada);
        ZapatillaResponseDto response = mapper.toResponseDto(guardada);

        // ← NUEVO: Notificar actualización
        webSocketController.notificarCambioStock(guardada.getId(), guardada.getStock());
        webSocketController.notificarStockBajo(guardada.getId(), guardada.getStock());

        return response;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Borrando zapatilla por id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        repository.deleteById(id);

        // ← NUEVO: Notificar eliminación
        ZapatillaNotificacion notificacion = ZapatillaNotificacion.crear(
                ZapatillaNotificacion.TipoNotificacion.DELETED,
                id,
                "Zapatilla eliminada",
                null
        );
        webSocketController.enviarNotificacion(notificacion);
    }

    @Override
    public <Z> Page<ZapatillaResponseDto> findAll(Optional<Z> empty, Optional<Z> empty1, Optional<Z> empty2, Pageable pageable) {
        return ;
    }
}