package es.pabloab.zapatillas.cliente.services;


import es.pabloab.zapatillas.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.cliente.dto.ClienteUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    Page<ClienteResponseDto> findAll(Pageable pageable);
    ClienteResponseDto findById(Long id);
    ClienteResponseDto save(ClienteCreateDto dto);
    ClienteResponseDto update(Long id, ClienteUpdateDto dto);
    void deleteById(Long id);
}
