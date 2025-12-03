package es.pabloab.zapatilass.services;

import es.pabloab.zapatilass.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatilass.mappers.ZapatillaMapper;
import es.pabloab.zapatilass.models.Zapatilla;
import es.pabloab.zapatilass.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"zapatillas"})
@Service
@RequiredArgsConstructor
@Slf4j
public class ZapatillasServiceImpl implements ZapatillasService {
    private final ZapatillasRepository repository;
    private final ZapatillaMapper mapper;

    @Override
    public List<ZapatillaResponseDto> findAll(String marca, String tipo) {
        if(marca == null || marca.trim().isEmpty() || tipo == null || tipo.trim().isEmpty()) {
            log.info("Buscando todas las zapatillas");
            return mapper.toResponseDtoList(repository.findAll());
        }
        if((marca != null && !marca.isEmpty()) && (tipo != null && !tipo.isEmpty())) {
            log.info("Buscando todas las zapatillas por marca:{}",marca);
            return mapper.toResponseDtoList(repository.findAllByMarca(marca));
        }
        if(marca == null || marca.trim().isEmpty()) {
            log.info("Buscando todas las zapatillas por tipo:{}",tipo);
            return mapper.toResponseDtoList(repository.findAllByTipo(tipo));
        }
        log.info("Buscando zapatillas por marca:{} tipo:{}",marca,tipo);
        return mapper.toResponseDtoList(repository.findAllByMarcaAndTipo(marca,tipo));
    }

    @Override
    public ZapatillaResponseDto findById(long id) {
        return null;
    }

    @Cacheable(key = "#id")
    @Override
    public ZapatillaResponseDto findById(Long id) {
        log.info("Buscando zapatillas por id:{}",id);
        return mapper.toResponseDto(repository.findById(id).get());
    }
    @Cacheable(key = "#id")
    @Override
    public ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException {
        log.info("Buscando zapatilla por uuid {}",uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return mapper.toResponseDto(repository.findByUuid(myUUID));

        } catch (IllegalArgumentException e) {
            throw new ZapatillaBadUuidException(uuid);
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public ZapatillaResponseDto save(ZapatillaCreateDto dto) {
        log.info("Guardando zapatilla:{}",dto);

        Long id = repository.nextId();

        Zapatilla nuevaZapatilla = mapper.toZapatilla(id,dto);

        return mapper.toResponseDto(repository.save(nuevaZapatilla));
    }
    @CachePut(key = "#result.id")
    @Override
    public ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto) {
        log.info("Actualizando zapatilla por id:{}",id);
        var zapatillaActual = repository.findById(id)
                .orElseThrow();
        return null;
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando zapatilla por id:{}",id);
        repository.deleteById(id);
    }
}


