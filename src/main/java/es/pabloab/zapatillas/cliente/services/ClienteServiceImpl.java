package es.pabloab.zapatillas.cliente.services;

import es.pabloab.zapatillas.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.cliente.dto.ClienteUpdateDto;
import es.pabloab.zapatillas.cliente.mappers.ClienteMapper;
import es.pabloab.zapatillas.cliente.models.Cliente;
import es.pabloab.zapatillas.cliente.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Override
    public Page<ClienteResponseDto> findAll(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toResponseDto);
    }

    @Override
    public ClienteResponseDto findById(Long id) {
        Cliente u = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado id=" + id));
        return mapper.toResponseDto(u);
    }

    @Override
    public ClienteResponseDto save(ClienteCreateDto dto) {
        log.info("Guardando usuario:{}",dto);
        Cliente nuevo = mapper.toUsuario(null, dto);
        Cliente guardado = repository.save(nuevo);
        return mapper.toResponseDto(guardado);
    }

    @Override
    public ClienteResponseDto update(Long id, ClienteUpdateDto dto) {
        log.info("Actualizando usuario id={} datos={}",id,dto);
        Cliente actual = repository.findById(id).orElseThrow(()-> new NoSuchElementException("Usuario no encantrado id={}"+ id));
        Cliente actualizado = mapper.toUsuario(dto,actual);
        Cliente guardado = repository.save(actualizado);
        return mapper.toResponseDto(guardado);
    }

    @Override
    public void deleteById(Long id) {
    repository.findById(id).orElseThrow(()-> new NoSuchElementException("Usuario no encontrado id={}" + id));
    repository.deleteById(id);
    }
}
