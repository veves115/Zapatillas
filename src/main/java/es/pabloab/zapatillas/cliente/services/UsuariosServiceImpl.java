package es.pabloab.zapatillas.cliente.services;

import es.pabloab.zapatillas.cliente.dto.UsuarioCreateDto;
import es.pabloab.zapatillas.cliente.dto.UsuarioResponseDto;
import es.pabloab.zapatillas.cliente.dto.UsuarioUpdateDto;
import es.pabloab.zapatillas.cliente.mappers.UsuarioMapper;
import es.pabloab.zapatillas.cliente.models.Usuario;
import es.pabloab.zapatillas.cliente.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuariosServiceImpl implements UsuariosService {
    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    @Override
    public Page<UsuarioResponseDto> findAll(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toResponseDto);
    }

    @Override
    public UsuarioResponseDto findById(Long id) {
        Usuario u = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado id=" + id));
        return mapper.toResponseDto(u);
    }

    @Override
    public UsuarioResponseDto save(UsuarioCreateDto dto) {
        log.info("Guardando usuario:{}",dto);
        Usuario nuevo = mapper.toUsuario(null, dto);
        Usuario guardado = repository.save(nuevo);
        return mapper.toResponseDto(guardado);
    }

    @Override
    public UsuarioResponseDto update(Long id, UsuarioUpdateDto dto) {
        log.info("Actualizando usuario id={} datos={}",id,dto);
        Usuario actual = repository.findById(id).orElseThrow(()-> new NoSuchElementException("Usuario no encantrado id={}"+ id));
        Usuario actualizado = mapper.toUsuario(dto,actual);
        Usuario guardado = repository.save(actualizado);
        return mapper.toResponseDto(guardado);
    }

    @Override
    public void deleteById(Long id) {
    repository.findById(id).orElseThrow(()-> new NoSuchElementException("Usuario no encontrado id={}" + id));
    repository.deleteById(id);
    }
}
