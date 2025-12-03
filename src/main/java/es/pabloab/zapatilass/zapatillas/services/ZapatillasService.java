package es.pabloab.zapatilass.zapatillas.services;

import es.pabloab.zapatilass.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.zapatillas.exceptions.ZapatillaBadUuidException;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);

    ZapatillaResponseDto findById(Long id);

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;
    ZapatillaResponseDto save(ZapatillaCreateDto dto);
    ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto);
    void deleteById(Long id);

}



