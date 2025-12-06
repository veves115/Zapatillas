package es.pabloab.zapatilass.zapatillas.services;

import es.pabloab.zapatilass.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.zapatillas.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatilass.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatilass.zapatillas.models.Zapatilla;
import es.pabloab.zapatilass.zapatillas.models.ZapatillaChangeEvent;
import es.pabloab.zapatilass.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"zapatillas"})
@Service
@RequiredArgsConstructor
@Slf4j
public class ZapatillasServiceImpl implements ZapatillasService {
    private final ZapatillasRepository repository;
    private final ZapatillaMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ZapatillaResponseDto> findAll(String marca, String tipo) {
        boolean sinMarca = (marca == null || marca.trim().isEmpty());
        boolean sinTipo = (tipo == null || tipo.trim().isEmpty());

        if (sinMarca && sinTipo) {
            log.info("Buscando todas las zapatillas");
            return mapper.toResponseDtoList(repository.findAll());
        }
        if (!sinMarca && sinTipo) {
            log.info("Buscando todas las zapatillas por marca: {}", marca);
            return mapper.toResponseDtoList(repository.findAllByMarca(marca));
        }
        if (sinMarca) {
            log.info("Buscando todas las zapatillas por tipo: {}", tipo);
            return mapper.toResponseDtoList(repository.findAllByTipo(tipo));
        }
        log.info("Buscando zapatillas por marca: {} y tipo: {}", marca, tipo);
        return mapper.toResponseDtoList(repository.findAllByMarcaAndTipo(marca, tipo));
    }

    @Cacheable(key = "#id")
    @Override
    public ZapatillaResponseDto findById(Long id) {
        log.info("Buscando zapatillas por id: {}", id);
        return mapper.toResponseDto(
                repository.findById(id).orElseThrow()
        );
    }
    @Cacheable(key = "#uuid")
    @Override
    public ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException {
        log.info("Buscando zapatilla por uuid {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            var zapatilla = repository.findByUuid(myUUID);
            if (zapatilla.isEmpty()) {
                return null;
            }
            return mapper.toResponseDto(zapatilla.get());
        } catch (IllegalArgumentException e) {
            throw new ZapatillaBadUuidException(uuid);
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public ZapatillaResponseDto save(ZapatillaCreateDto dto) {
        log.info("Guardando zapatilla: {}", dto);

        Zapatilla nuevaZapatilla = mapper.toZapatilla(dto);
        
        // Establecer campos automáticos
        if (nuevaZapatilla.getUuid() == null) {
            nuevaZapatilla.setUuid(UUID.randomUUID());
        }
        if (nuevaZapatilla.getCreatedAt() == null) {
            nuevaZapatilla.setCreatedAt(LocalDateTime.now());
        }
        nuevaZapatilla.setUpdatedAt(LocalDateTime.now());

        Zapatilla guardada = repository.save(nuevaZapatilla);
        
        // Publicar evento de creación
        eventPublisher.publishEvent(new ZapatillaChangeEvent(guardada, ZapatillaChangeEvent.ChangeType.CREATE));

        return mapper.toResponseDto(guardada);
    }
    @CachePut(key = "#result.id")
    @Override
    public ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto) {
        log.info("Actualizando zapatilla por id: {}", id);
        Zapatilla zapatillaActual = repository.findById(id)
                .orElseThrow();

        Zapatilla zapatillaActualizada = mapper.toZapatilla(dto, zapatillaActual);
        
        // Actualizar fecha de modificación
        zapatillaActualizada.setUpdatedAt(LocalDateTime.now());

        Zapatilla guardada = repository.save(zapatillaActualizada);
        
        // Publicar evento de actualización
        eventPublisher.publishEvent(new ZapatillaChangeEvent(guardada, ZapatillaChangeEvent.ChangeType.UPDATE));

        return mapper.toResponseDto(guardada);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando zapatilla por id:{}", id);
        
        // Obtener la zapatilla antes de eliminarla para publicar el evento
        Zapatilla zapatilla = repository.findById(id)
                .orElseThrow();
        
        repository.deleteById(id);
        
        // Publicar evento de eliminación
        eventPublisher.publishEvent(new ZapatillaChangeEvent(zapatilla, ZapatillaChangeEvent.ChangeType.DELETE));
    }
}


