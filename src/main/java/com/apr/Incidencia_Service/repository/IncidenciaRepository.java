package com.apr.Incidencia_Service.repository;

import com.apr.Incidencia_Service.model.EstadoIncidencia;
import com.apr.Incidencia_Service.model.Incidencia;
import com.apr.Incidencia_Service.model.TipoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findBySocioId(Long socioId);
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    List<Incidencia> findByTipo(TipoIncidencia tipo);
}
