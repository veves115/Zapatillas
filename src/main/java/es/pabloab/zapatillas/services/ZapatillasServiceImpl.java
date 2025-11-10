package es.pabloab.zapatillas.services;

import es.pabloab.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.exceptions.ZapatillaNotFoundException;
import es.pabloab.zapatillas.mappers.ZapatillaMapper;
import es.pabloab.zapatillas.repositories.ZapatillasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = {"zapatillas"})
@Service
@RequiredArgsConstructor
@Slf4j
public class ZapatillasServiceImpl implements ZapatillasService {
    private final ZapatillasRepository repository;
    private final ZapatillaMapper mapper;

    @Override
    public List<ZapatillaResponseDto> findAll(String marca,String tipo) {
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
    @Cacheable(key = "#id")
    @Override
    public ZapatillaResponseDto findById(long id) {
        log.info("Buscando la zapatilla por id {}",id);

        return mapper.toResponseDto(repository.findById(id).orElseThrow());
    }

    @Override
    public ZapatillaResponseDto findById(Long id) {
        return null;
    }

    @Override
    public ZapatillaResponseDto findByUuid(String uuid) {
        return null;
    }

    @Override
    public ZapatillaResponseDto save(ZapatillaCreateDto dto) {
        return null;
    }

    @Override
    public ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
