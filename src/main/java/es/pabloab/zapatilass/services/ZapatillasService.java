package es.pabloab.zapatilass.services;

import es.pabloab.zapatilass.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.exceptions.ZapatillaBadUuidException;
import es.pabloab.zapatilass.exceptions.ZapatillaNotFoundException;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);
    ZapatillaResponseDto findById(long id) throws ZapatillaNotFoundException;

    @Cacheable(key = "#id")
    ZapatillaResponseDto findById(Long id) throws ZapatillaNotFoundException;

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;
    ZapatillaResponseDto save(ZapatillaCreateDto dto);
    ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto) throws ZapatillaNotFoundException;
    void deleteById(Long id);

}

