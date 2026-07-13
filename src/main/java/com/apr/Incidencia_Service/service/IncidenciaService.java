package com.apr.Incidencia_Service.service;

import com.apr.Incidencia_Service.dto.IncidenciaDTO;
import com.apr.Incidencia_Service.dto.IncidenciaResponseDTO;
import com.apr.Incidencia_Service.exception.ResourceNotFoundException;
import com.apr.Incidencia_Service.mapper.IncidenciaMapper;
import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.Incidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import com.apr.Incidencia_Service.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidenciaService {

    private final IncidenciaRepository repository;
    private final WebClient webClient;

    public IncidenciaService(IncidenciaRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<IncidenciaResponseDTO> listarTodas() {
        return repository.findAll()
                .stream()
                .map(IncidenciaMapper::toResponseDTO)
                .toList();
    }

    public IncidenciaResponseDTO buscarPorId(Long id) {
        Incidencia incidencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));
        return IncidenciaMapper.toResponseDTO(incidencia);
    }

    public List<IncidenciaResponseDTO> buscarPorSocioId(Long socioId) {
        return repository.findBySocioId(socioId)
                .stream()
                .map(IncidenciaMapper::toResponseDTO)
                .toList();
    }

    public List<IncidenciaResponseDTO> buscarPorEstado(EstadoIncidencia estado) {
        return repository.findByEstado(estado)
                .stream()
                .map(IncidenciaMapper::toResponseDTO)
                .toList();
    }

    public List<IncidenciaResponseDTO> buscarPorTipo(TipoIncidencia tipo) {
        return repository.findByTipo(tipo)
                .stream()
                .map(IncidenciaMapper::toResponseDTO)
                .toList();
    }

    public IncidenciaResponseDTO guardar(IncidenciaDTO dto) {
        // Validar que el socioId exista en Socio-Service
        validarSocioEnSocioService(dto.getSocioId());

        Incidencia incidencia = IncidenciaMapper.toEntity(dto);
        Incidencia guardada = repository.save(incidencia);
        return IncidenciaMapper.toResponseDTO(guardada);
    }

    public IncidenciaResponseDTO actualizar(Long id, IncidenciaDTO dto) {
        Incidencia incidencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));

        if (!incidencia.getSocioId().equals(dto.getSocioId())) {
            validarSocioEnSocioService(dto.getSocioId());
        }

        incidencia.setSocioId(dto.getSocioId());
        incidencia.setTipo(dto.getTipo());
        incidencia.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            incidencia.setEstado(dto.getEstado());
            // Si pasa a RESUELTA, guardar la fecha de resolucion
            if (dto.getEstado() == EstadoIncidencia.RESUELTA && incidencia.getFechaResolucion() == null) {
                incidencia.setFechaResolucion(LocalDateTime.now());
            }
        }
        if (dto.getFechaReporte() != null) incidencia.setFechaReporte(dto.getFechaReporte());
        if (dto.getFechaResolucion() != null) incidencia.setFechaResolucion(dto.getFechaResolucion());
        incidencia.setLatitud(dto.getLatitud());
        incidencia.setLongitud(dto.getLongitud());
        incidencia.setOperadorId(dto.getOperadorId());
 
        Incidencia actualizada = repository.save(incidencia);
        return IncidenciaMapper.toResponseDTO(actualizada);
    }

    public java.util.Map<String, Object> notificarIncidencia(Long id) {
        Incidencia incidencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));

        java.util.Map<String, Object> notificacion = new java.util.HashMap<>();
        notificacion.put("tipo", "NOTIFICACION_INCIDENCIA");
        notificacion.put("socioId", incidencia.getSocioId());
        notificacion.put("incidenciaId", incidencia.getId());
        notificacion.put("tipoIncidencia", incidencia.getTipo());
        notificacion.put("estado", incidencia.getEstado());
        notificacion.put("latitud", incidencia.getLatitud());
        notificacion.put("longitud", incidencia.getLongitud());
        notificacion.put("operadorId", incidencia.getOperadorId());
        notificacion.put("mensaje", String.format(
                "Socio %d: Su reporte de tipo %s ha sido recibido. Se ha asignado al operador %d para la inspección en las coordenadas [%s, %s].",
                incidencia.getSocioId(),
                incidencia.getTipo(),
                incidencia.getOperadorId() != null ? incidencia.getOperadorId() : 0,
                incidencia.getLatitud() != null ? incidencia.getLatitud().toString() : "sin latitud",
                incidencia.getLongitud() != null ? incidencia.getLongitud().toString() : "sin longitud"));
        notificacion.put("simulado", true);
        return notificacion;
    }

    public void eliminar(Long id) {
        Incidencia incidencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));
        repository.delete(incidencia);
    }

    private void validarSocioEnSocioService(Long socioId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/socios/" + socioId)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorReturn(false)
                    .block();

            if (existe == null || !existe) {
                throw new IllegalArgumentException("El Socio con ID " + socioId + " no existe.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al comunicarse con Socio-Service: " + e.getMessage());
        }
    }
}
