package com.apr.Incidencia_Service.service;

import com.apr.Incidencia_Service.dto.IncidenciaDTO;
import com.apr.Incidencia_Service.dto.IncidenciaResponseDTO;
import com.apr.Incidencia_Service.exception.ResourceNotFoundException;
import com.apr.Incidencia_Service.mapper.IncidenciaMapper;
import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.Incidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import com.apr.Incidencia_Service.repository.IncidenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidenciaService {

    private final IncidenciaRepository repository;
    private final WebClient.Builder webClientBuilder;

    public IncidenciaService(IncidenciaRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
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

        Incidencia actualizada = repository.save(incidencia);
        return IncidenciaMapper.toResponseDTO(actualizada);
    }

    public void eliminar(Long id) {
        Incidencia incidencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));
        repository.delete(incidencia);
    }

    private void validarSocioEnSocioService(Long socioId) {
        try {
            Boolean existe = webClientBuilder.build().get()
                    .uri("http://socio-service/socios/" + socioId)
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
