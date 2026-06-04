package com.apr.Incidencia_Service.dto;

import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class IncidenciaDTO {

    @NotNull(message = "El ID de socio es obligatorio")
    private Long socioId;

    @NotNull(message = "El tipo de incidencia es obligatorio")
    private TipoIncidencia tipo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private EstadoIncidencia estado;
    private LocalDateTime fechaReporte;
    private LocalDateTime fechaResolucion;

    public IncidenciaDTO() {
    }

    public Long getSocioId() { return socioId; }
    public void setSocioId(Long socioId) { this.socioId = socioId; }
    public TipoIncidencia getTipo() { return tipo; }
    public void setTipo(TipoIncidencia tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoIncidencia getEstado() { return estado; }
    public void setEstado(EstadoIncidencia estado) { this.estado = estado; }
    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime fechaReporte) { this.fechaReporte = fechaReporte; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}
