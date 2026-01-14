package es.pabloab.zapatillas.rest.zapatillas.services;

import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.rest.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.rest.zapatillas.exceptions.ZapatillaBadUuidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ZapatillasService {
    Page<ZapatillaResponseDto> findAll(String marca, String tipo, Pageable pageable);

    ZapatillaResponseDto findById(Long id);

    ZapatillaResponseDto findByUuid(String uuid) throws ZapatillaBadUuidException;

    ZapatillaResponseDto save(ZapatillaCreateDto dto);

    ZapatillaResponseDto update(Long id, ZapatillaUpdateDto dto);

    void deleteById(Long id);

    <Z> Page<ZapatillaResponseDto> findAll(Optional<Z> empty, Optional<Z> empty1, Optional<Z> empty2, Pageable pageable);
}



