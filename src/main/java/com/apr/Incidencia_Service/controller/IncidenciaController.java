package com.apr.Incidencia_Service.controller;

import com.apr.Incidencia_Service.dto.IncidenciaDTO;
import com.apr.Incidencia_Service.dto.IncidenciaResponseDTO;
import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import com.apr.Incidencia_Service.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService service;

    public IncidenciaController(IncidenciaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IncidenciaResponseDTO>> listarTodas(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) EstadoIncidencia estado,
            @RequestParam(required = false) TipoIncidencia tipo) {
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
    public ResponseEntity<IncidenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IncidenciaResponseDTO> guardar(@Valid @RequestBody IncidenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidenciaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody IncidenciaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
