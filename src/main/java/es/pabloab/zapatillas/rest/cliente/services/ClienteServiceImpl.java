package es.pabloab.zapatillas.rest.cliente.services;

import es.pabloab.zapatillas.config.SecurityUtils;
import es.pabloab.zapatillas.rest.auth.repositories.AuthUsersRepository;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteUpdateDto;
import es.pabloab.zapatillas.rest.cliente.mappers.ClienteMapper;
import es.pabloab.zapatillas.rest.cliente.models.Cliente;
import es.pabloab.zapatillas.rest.cliente.repositories.ClienteRepository;
import es.pabloab.zapatillas.rest.user.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Implementación del servicio de clientes.
 * 
 * IMPORTANTE: Este servicio maneja la lógica de negocio relacionada con clientes.
 * La seguridad de acceso se controla en el controlador, pero aquí verificamos
 * la propiedad de los recursos cuando es necesario.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final AuthUsersRepository userRepository;

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

    /**
     * Verifica si un cliente pertenece a un usuario específico.
     * 
     * Lógica:
     * 1. Buscamos el cliente por ID
     * 2. Buscamos el usuario por ID
     * 3. Verificamos si el cliente está asociado al usuario (relación OneToOne)
     * 
     * @param clienteId El ID del cliente
     * @param userId El ID del usuario
     * @return true si el cliente pertenece al usuario, false en caso contrario
     */
    @Override
    public boolean belongsToUser(Long clienteId, Long userId) {
        Cliente cliente = repository.findById(clienteId)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado id=" + clienteId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado id=" + userId));
        
        // Verificamos si el cliente está asociado al usuario
        // La relación es OneToOne: User -> Cliente
        return user.getCliente() != null && user.getCliente().getId().equals(clienteId);
    }
}
