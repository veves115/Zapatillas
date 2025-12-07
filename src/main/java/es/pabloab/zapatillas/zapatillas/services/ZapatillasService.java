package es.pabloab.zapatillas.zapatillas.services;

import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaBadUuidException;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);

    ZapatillaResponseDto findById(Long id);

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;

    ZapatillaResponseDto save(ZapatillaCreateDto dto);

    ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto);

    void deleteById(Long id);

}



