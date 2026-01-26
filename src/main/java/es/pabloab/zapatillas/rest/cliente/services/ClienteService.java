package es.pabloab.zapatillas.rest.cliente.services;


import es.pabloab.zapatillas.rest.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    Page<ClienteResponseDto> findAll(Pageable pageable);
    ClienteResponseDto findById(Long id);
    ClienteResponseDto save(ClienteCreateDto dto);
    ClienteResponseDto update(Long id, ClienteUpdateDto dto);
    void deleteById(Long id);
    
    /**
     * Verifica si un cliente pertenece al usuario autenticado actual.
     * Útil para controlar el acceso: un usuario solo puede modificar su propio cliente.
     * 
     * @param clienteId El ID del cliente a verificar
     * @param userId El ID del usuario autenticado
     * @return true si el cliente pertenece al usuario, false en caso contrario
     */
    boolean belongsToUser(Long clienteId, Long userId);
}
