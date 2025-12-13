package es.pabloab.zapatillas.usuarios.services;


import es.pabloab.zapatillas.usuarios.dto.UsuarioCreateDto;
import es.pabloab.zapatillas.usuarios.dto.UsuarioResponseDto;
import es.pabloab.zapatillas.usuarios.dto.UsuarioUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuariosService {
    Page<UsuarioResponseDto> findAll(Pageable pageable);
    UsuarioResponseDto findById(Long id);
    UsuarioResponseDto save(UsuarioCreateDto dto);
    UsuarioResponseDto update(Long id, UsuarioUpdateDto dto);
    void deleteById(Long id);
}
