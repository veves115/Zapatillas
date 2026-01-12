package es.pabloab.zapatillas.rest.cliente.controllers;

import es.pabloab.zapatillas.rest.cliente.dto.ClienteCreateDto;
import es.pabloab.zapatillas.rest.cliente.dto.ClienteResponseDto;
import es.pabloab.zapatillas.rest.cliente.services.ClienteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/usuarios")
@Slf4j
public class ClientesRestController {
    private final ClienteService service;

    public ClientesRestController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDto>> getAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PostMapping
    public ResponseEntity<ClienteResponseDto> create(@Valid @RequestBody ClienteCreateDto dto){
        var saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult bindingResult = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto=" + bindingResult.getObjectName() + "Número de errores: " +
                bindingResult.getErrorCount());
        Map<String,String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });
        problemDetail.setProperty("errores",errors);
        return problemDetail;
    }
}
