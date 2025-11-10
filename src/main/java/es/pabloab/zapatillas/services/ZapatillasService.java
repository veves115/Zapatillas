package es.pabloab.zapatillas.services;

import es.pabloab.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.models.Zapatilla;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);
    ZapatillaResponseDto findById(long id);

    @Cacheable(key = "#id")
    ZapatillaResponseDto findById(Long id);

    ZapatillaResponseDto findByUuid(String uuid);
    ZapatillaResponseDto save(ZapatillaCreateDto dto);
    ZapatillaResponseDto update(Long id,ZapatillaUpdateDto dto);
    void deleteById(Long id);

}
