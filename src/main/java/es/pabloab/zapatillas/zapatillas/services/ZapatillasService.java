package es.pabloab.zapatillas.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaNotFoundException;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);
    ZapatillaResponseDto findById(long id) throws ZapatillaNotFoundException;

    @Cacheable(key = "#id")
    ZapatillaResponseDto findById(Long id) throws ZapatillaNotFoundException;

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;
    ZapatillaResponseDto save(ZapatillaCreateDto dto);
    ZapatillaResponseDto update(Long id,ZapatillaUpdateDto dto) throws ZapatillaNotFoundException;
    void deleteById(Long id);

}
