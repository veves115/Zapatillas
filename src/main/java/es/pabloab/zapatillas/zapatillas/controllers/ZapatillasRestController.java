package es.pabloab.zapatillas.zapatillas.controllers;


import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.exceptions.ZapatillaNotFoundException;
import es.pabloab.zapatillas.zapatillas.services.ZapatillasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/zapatillas")
public class ZapatillasRestController {

    private final ZapatillasService service;

    @GetMapping()
    public ResponseEntity<List<ZapatillaResponseDto>> getAll(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false)String tipo){
        log.info("Buscando zapatillas por marca={} tipo={}", marca, tipo);
        return ResponseEntity.ok(service.findAll(marca, tipo));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ZapatillaResponseDto> getById(@PathVariable Long id) throws ZapatillaNotFoundException {
        log.info("Buscando zapatilla por id={}", id);
        return ResponseEntity.ok(service.findById(id));
    }
    @PostMapping
    public ResponseEntity<ZapatillaResponseDto> create(@Valid @RequestBody ZapatillaCreateDto dto){
        log.info("Creando zapatilla={}", dto);
        var saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ZapatillaResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ZapatillaUpdateDto dto) throws ZapatillaNotFoundException {
        log.info("Actualizando zapatilla id={} con datos={}", id, dto);
        return ResponseEntity.ok(service.update(id, dto));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ZapatillaResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody
    ZapatillaUpdateDto dto) throws ZapatillaNotFoundException {
        log.info("Actualizando parcialmente zapatilla id={}",id);
        return ResponseEntity.ok(service.update (id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ZapatillaResponseDto> delete(@PathVariable Long id){
        log.info("Eliminando zapatilla id={}", id);
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult bindingResult = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + bindingResult.getObjectName() +
                "'.Número de errores: " + bindingResult.getErrorCount());
        Map<String,String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errors);
        return problemDetail;
    }
}
