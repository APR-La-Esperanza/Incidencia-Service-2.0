package com.apr.Incidencia_Service.mapper;

import com.apr.Incidencia_Service.dto.IncidenciaDTO;
import com.apr.Incidencia_Service.dto.IncidenciaResponseDTO;
import com.apr.Incidencia_Service.model.Incidencia;

public class IncidenciaMapper {

    public static Incidencia toEntity(IncidenciaDTO dto) {
        if (dto == null) return null;
        Incidencia incidencia = new Incidencia();
        incidencia.setSocioId(dto.getSocioId());
        incidencia.setTipo(dto.getTipo());
        incidencia.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) incidencia.setEstado(dto.getEstado());
        if (dto.getFechaReporte() != null) incidencia.setFechaReporte(dto.getFechaReporte());
        if (dto.getFechaResolucion() != null) incidencia.setFechaResolucion(dto.getFechaResolucion());
        incidencia.setLatitud(dto.getLatitud());
        incidencia.setLongitud(dto.getLongitud());
        incidencia.setOperadorId(dto.getOperadorId());
        return incidencia;
    }

    public static IncidenciaResponseDTO toResponseDTO(Incidencia incidencia) {
        if (incidencia == null) return null;
        IncidenciaResponseDTO dto = new IncidenciaResponseDTO();
        dto.setId(incidencia.getId());
        dto.setSocioId(incidencia.getSocioId());
        dto.setTipo(incidencia.getTipo());
        dto.setDescripcion(incidencia.getDescripcion());
        dto.setEstado(incidencia.getEstado());
        dto.setFechaReporte(incidencia.getFechaReporte());
        dto.setFechaResolucion(incidencia.getFechaResolucion());
        dto.setLatitud(incidencia.getLatitud());
        dto.setLongitud(incidencia.getLongitud());
        dto.setOperadorId(incidencia.getOperadorId());
        return dto;
    }
}
