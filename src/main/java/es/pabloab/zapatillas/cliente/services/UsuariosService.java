package es.pabloab.zapatillas.cliente.services;


import es.pabloab.zapatillas.cliente.dto.UsuarioCreateDto;
import es.pabloab.zapatillas.cliente.dto.UsuarioResponseDto;
import es.pabloab.zapatillas.cliente.dto.UsuarioUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuariosService {
    Page<UsuarioResponseDto> findAll(Pageable pageable);
    UsuarioResponseDto findById(Long id);
    UsuarioResponseDto save(UsuarioCreateDto dto);
    UsuarioResponseDto update(Long id, UsuarioUpdateDto dto);
    void deleteById(Long id);
}
