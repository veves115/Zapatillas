package es.pabloab.zapatillas.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.controllers.websocket.ZapatillasWebSocketController;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.dto.websocket.ZapatillaNotificacion;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatillas.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.zapatillas.models.Zapatilla;
import es.pabloab.zapatillas.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZapatillasServiceImpl implements ZapatillasService {

    private final ZapatillasRepository repository;
    private final ZapatillaMapper mapper;
    private final ZapatillasWebSocketController webSocketController;  // ← NUEVO

    @Override
    public List<ZapatillaResponseDto> findAll(String marca, String tipo) {
        return List.of();
    }

    @Override
    public ZapatillaResponseDto findById(Long id) {
        return null;
    }

    @Override
    public ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException {
        return null;
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
}