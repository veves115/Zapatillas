package es.pabloab.zapatilass.services;

import es.pabloab.zapatilass.dto.ZapatillaCreateDto;
import es.pabloab.zapatilass.dto.ZapatillaResponseDto;
import es.pabloab.zapatilass.dto.ZapatillaUpdateDto;
import es.pabloab.zapatilass.exceptions.ZapatillaBadUuidException;

import java.util.List;

public interface ZapatillasService {
    List<ZapatillaResponseDto> findAll(String marca, String tipo);

    ZapatillaResponseDto findById(Long id);

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;
    ZapatillaResponseDto save(ZapatillaCreateDto dto);
    ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto);
    void deleteById(Long id);

}



