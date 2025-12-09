package es.pabloab.zapatillas.zapatillas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaCreateDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaResponseDto;
import es.pabloab.zapatillas.zapatillas.dto.ZapatillaUpdateDto;
import es.pabloab.zapatillas.zapatillas.services.ZapatillasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ZapatillasRestController.class)
@DisplayName("Tests de ZapatillasRestController")
class ZapatillasRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ZapatillasService service;

    private ZapatillaResponseDto responseDto;
    private ZapatillaCreateDto createDto;
    private ZapatillaUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        responseDto = ZapatillaResponseDto.builder()
                .id(1L)
                .marca("Nike")
                .modelo("Air Max 90")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Blanco")
                .tipo("Running")
                .precio(129.99)
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        createDto = ZapatillaCreateDto.builder()
                .marca("Nike")
                .modelo("Air Max 90")
                .codigoProducto("NI1234KE")
                .talla(42.0)
                .color("Blanco")
                .tipo("Running")
                .precio(129.99)
                .stock(10)
                .build();

        updateDto = ZapatillaUpdateDto.builder()
                .precio(149.99)
                .stock(20)
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/zapatillas")
    class GetAllTests {

        @Test
        @DisplayName("Debe devolver 200 OK y lista de zapatillas")
        void getAllDevuelveLista() throws Exception {
            given(service.findAll(null, null)).willReturn(List.of(responseDto));

            mockMvc.perform(
                            get("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].marca", is("Nike")))
                    .andExpect(jsonPath("$[0].precio", is(129.99)));

            verify(service).findAll(null, null);
        }

        @Test
        @DisplayName("Con marca debe pasar el parámetro al service")
        void getAllConMarca() throws Exception {
            given(service.findAll("Nike", null)).willReturn(List.of(responseDto));

            mockMvc.perform(
                            get("/api/v1/zapatillas")
                                    .param("marca", "Nike")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
            verify(service).findAll("Nike", null);
        }

        @Test
        @DisplayName("Con tipo debe pasar el parámetro al service")
        void getAllConTipo() throws Exception {
            given(service.findAll(null, "Running")).willReturn(List.of(responseDto));
            mockMvc.perform(
                            get("/api/v1/zapatillas")
                                    .param("tipo", "Running")
                    )
                    .andExpect(status().isOk());
            verify(service).findAll(null, "Running");
        }

        @Test
        @DisplayName("Con marca y tipo debe pasar ambos parámetros")
        void getAllConMarcaAndTipo() throws Exception {
            given(service.findAll("Nike", "Running")).willReturn(List.of(responseDto));
            mockMvc.perform(
                            get("/api/v1/zapatillas")
                                    .param("marca", "Nike")
                                    .param("tipo", "Running")
                    )
                    .andExpect(status().isOk());
            verify(service).findAll("Nike", "Running");
        }

        @Test
        @DisplayName("Lista vacía debe devolver 200 OK con array vacío")
        void getAllListaVaca() throws Exception {
            given(service.findAll(null, null)).willReturn(List.of());
            mockMvc.perform(
                            get("/api/v1/zapatillas")
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(content().json("[]"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/zapatillas/{id}")
    class GetByIdTests {

        @Test
        @DisplayName("Debe devolver 200 OK y la zapatilla")
        void getByIdZapatilla() throws Exception {
            given(service.findById(1L)).willReturn(responseDto);
            mockMvc.perform(
                            get("/api/v1/zapatillas/{id}", 1L)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.marca", is("Nike")))
                    .andExpect(jsonPath("$.precio", is(129.99)))
                    .andExpect(jsonPath("$.modelo", is("Air Max 90")));

            verify(service).findById(1L);
        }

        @Test
        @DisplayName("Debe devolver 404 cuando no existe")
        void getByIdZapatillaNotFound() throws Exception {
            given(service.findById(999L)).willThrow(new NoSuchElementException());
            mockMvc.perform(
                            get("/api/v1/zapatillas/{id}", 999L)
                    )
                    .andExpect(status().isNotFound());
            verify(service).findById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/zapatillas")
    class PostTests {

        @Test
        @DisplayName("Debe devolver 201 Created con la zapatilla creada")
        void postZapatillaCreada() throws Exception {
            given(service.save(any(ZapatillaCreateDto.class))).willReturn(responseDto);
            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createDto))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.marca", is("Nike")));
            verify(service).save(any(ZapatillaCreateDto.class));
        }

        @Test
        @DisplayName("Debe devolver 400 cuando marca está vacía")
        void postMarcaVacia() throws Exception {
            ZapatillaCreateDto dtoInvalido = ZapatillaCreateDto.builder()
                    .marca("")
                    .modelo("Air Max")
                    .codigoProducto("NI12345KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(10)
                    .build();

            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores.marca", notNullValue()));
            verify(service, never()).save(any());
        }

        @Test
        @DisplayName("Debe devolver 400 cuando precio es negativo")
        void postPrecioNegativo() throws Exception {
            ZapatillaCreateDto dtoInvalido = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(-129.99)
                    .stock(10)
                    .build();

            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores.precio", notNullValue()));
        }

        @Test
        @DisplayName("Debe devolver 400 cuando stock es negativo")
        void postStockNegativo() throws Exception {
            ZapatillaCreateDto dtoInvalido = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(129.99)
                    .stock(-10)
                    .build();

            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores.stock", notNullValue()));
        }

        @Test
        @DisplayName("Debe devolver 400 cuando tipo es inválido")
        void postTipoInvalido() throws Exception {
            ZapatillaCreateDto dtoInvalido = ZapatillaCreateDto.builder()
                    .marca("Nike")
                    .modelo("Air Max")
                    .codigoProducto("NI1234KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Natacion")
                    .precio(129.99)
                    .stock(10)
                    .build();

            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores.tipo", notNullValue()));
        }

        @Test
        @DisplayName("Debe devolver 400 con múltiples errores de validación")
        void postMultiplesErrores() throws Exception {
            ZapatillaCreateDto dtoInvalido = ZapatillaCreateDto.builder()
                    .marca("")
                    .modelo("")
                    .talla(10.0)
                    .precio(-50.0)
                    .stock(-10)
                    .build();

            mockMvc.perform(
                            post("/api/v1/zapatillas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores", aMapWithSize(greaterThanOrEqualTo(3))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/zapatillas/{id}")
    class putTests {

        @Test
        @DisplayName("Debe devolver 200 OK con la zapatilla actualizada")
        void putZapatillaExiste() throws Exception {
            ZapatillaResponseDto actualizado = ZapatillaResponseDto.builder()
                    .id(1L)
                    .marca("Nike")
                    .modelo("Air Max 90")
                    .codigoProducto("NI12345KE")
                    .talla(42.0)
                    .color("Blanco")
                    .tipo("Running")
                    .precio(149.99)
                    .stock(20)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .uuid(UUID.randomUUID())
                    .build();

            given(service.update(eq(1L), any(ZapatillaUpdateDto.class))).willReturn(actualizado);
            mockMvc.perform(
                            put("/api/v1/zapatillas/{id}", 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.precio", is(149.99)))
                    .andExpect(jsonPath("$.stock", is(20)));

            verify(service).update(eq(1L), any(ZapatillaUpdateDto.class));
        }

        @Test
        @DisplayName("Debe devolver 404 cuando no existe")
        void putZapatillaNoExiste() throws Exception {
            given(service.update(eq(999L), any(ZapatillaUpdateDto.class))).willThrow(new NoSuchElementException());
            mockMvc.perform(
                            put("/api/v1/zapatillas/{id}", 999L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateDto))
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debe devolver 400 con datos inválidos")
        void putDatosInvalidos() throws Exception {
            ZapatillaUpdateDto dtoInvalido = ZapatillaUpdateDto.builder()
                    .precio(-100.0)
                    .build();

            mockMvc.perform(
                            put("/api/v1/zapatillas/{id}", 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(dtoInvalido))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errores.precio", notNullValue()));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/zapatillas/{id}")
    class patchTests {
        @Test
        @DisplayName("Debe actualizar parcialmente y devolver 200 OK")
        void updateParcialZapatillaExiste() throws Exception {
            ZapatillaUpdateDto parcial = ZapatillaUpdateDto.builder()
                    .precio(99.99)
                    .build();

            given(service.update(eq(1L), any(ZapatillaUpdateDto.class))).willReturn(responseDto);

            mockMvc.perform(
                            patch("/api/v1/zapatillas/{id}", 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(parcial))
                    )
                    .andExpect(status().isOk());
            verify(service).update(eq(1L), any(ZapatillaUpdateDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/zapatillas/{id}")
    class DeleteTests {
        @Test
        @DisplayName("Debe devolver 204 No Content")
        void deleteZapatillaExiste() throws Exception {
            willDoNothing().given(service).deleteById(1L);
            mockMvc.perform(
                            delete("/api/v1/zapatillas/{id}", 1L)
                    )
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
            verify(service).deleteById(1L);
        }
    }
}
