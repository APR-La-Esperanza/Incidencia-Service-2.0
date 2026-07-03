package com.apr.Incidencia_Service.controller;

import com.apr.Incidencia_Service.dto.IncidenciaDTO;
import com.apr.Incidencia_Service.dto.IncidenciaResponseDTO;
import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import com.apr.Incidencia_Service.service.IncidenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
@Tag(name = "Incidencias", description = "Endpoints para el reporte y seguimiento de incidencias (baja presión, fugas, cortes de agua).")
@SecurityRequirement(name = "bearerAuth")
public class IncidenciaController {

    private final IncidenciaService service;

    public IncidenciaController(IncidenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar incidencias", description = "Retorna una lista de incidencias. Filtra por socioId, estado (REPORTADA, EN_PROCESO, RESUELTA) o tipo (BAJA_PRESION, FUGA, CORTE, OTRO).")
    @ApiResponse(responseCode = "200", description = "Lista de incidencias obtenida correctamente.")
    public ResponseEntity<List<IncidenciaResponseDTO>> listarTodas(
            @Parameter(description = "ID del socio para filtrar") @RequestParam(required = false) Long socioId,
            @Parameter(description = "Estado de la incidencia para filtrar") @RequestParam(required = false) EstadoIncidencia estado,
            @Parameter(description = "Tipo de incidencia para filtrar") @RequestParam(required = false) TipoIncidencia tipo) {
        if (socioId != null) {
            return ResponseEntity.ok(service.buscarPorSocioId(socioId));
        }
        if (estado != null) {
            return ResponseEntity.ok(service.buscarPorEstado(estado));
        }
        if (tipo != null) {
            return ResponseEntity.ok(service.buscarPorTipo(tipo));
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar incidencia por ID", description = "Obtiene los detalles de una incidencia específica.")
    @ApiResponse(responseCode = "200", description = "Incidencia encontrada.")
    @ApiResponse(responseCode = "404", description = "La incidencia solicitada no existe.")
    public ResponseEntity<IncidenciaResponseDTO> buscarPorId(
            @Parameter(description = "ID único de la incidencia", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Reportar incidencia", description = "Ingresa una nueva incidencia. Valida que el socioId exista en Socio-Service mediante WebClient.")
    @ApiResponse(responseCode = "201", description = "Incidencia reportada con éxito.")
    @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos o socio inválido.")
    public ResponseEntity<IncidenciaResponseDTO> guardar(
            @RequestBody(description = "Datos para el reporte de incidencia", required = true,
                         content = @Content(schema = @Schema(implementation = IncidenciaDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"tipo\": \"FUGA\",\n  \"descripcion\": \"Fuga de agua considerable en la acera frente a mi domicilio.\",\n  \"latitud\": -33.456,\n  \"longitud\": -70.648\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody IncidenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar incidencia", description = "Modifica los datos de una incidencia existente (por ejemplo, cambiar su estado a RESUELTA, lo cual asignará automáticamente la fechaResolucion).")
    @ApiResponse(responseCode = "200", description = "Incidencia actualizada de manera correcta.")
    @ApiResponse(responseCode = "400", description = "Datos provistos incorrectos.")
    @ApiResponse(responseCode = "404", description = "La incidencia indicada no existe.")
    public ResponseEntity<IncidenciaResponseDTO> actualizar(
            @Parameter(description = "ID de la incidencia a actualizar", required = true) @PathVariable Long id,
            @RequestBody(description = "Nuevos datos de la incidencia", required = true,
                         content = @Content(schema = @Schema(implementation = IncidenciaDTO.class),
                                            examples = @ExampleObject(value = "{\n  \"socioId\": 1,\n  \"tipo\": \"FUGA\",\n  \"descripcion\": \"Fuga de agua considerable\",\n  \"estado\": \"RESUELTA\",\n  \"latitud\": -33.456,\n  \"longitud\": -70.648\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody IncidenciaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar incidencia", description = "Borra permanentemente la incidencia del sistema APR.")
    @ApiResponse(responseCode = "204", description = "Incidencia eliminada con éxito.")
    @ApiResponse(responseCode = "404", description = "La incidencia no existe.")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la incidencia a eliminar", required = true) @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
