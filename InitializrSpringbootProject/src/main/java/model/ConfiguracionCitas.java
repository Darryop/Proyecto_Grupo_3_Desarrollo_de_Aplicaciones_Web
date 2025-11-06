package model;

/**
 *
 * @author darry
 */

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_citas")
public class ConfiguracionCitas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "max_citas_dia")
    private Integer maxCitasDia;
    
    @Column(name = "horario_apertura")
    private String horarioApertura;
    
    @Column(name = "horario_cierre")
    private String horarioCierre;
    
    @Column(name = "duracion_minima_cita")
    private Integer duracionMinimaCita;
    
    @Column(name = "dias_anticipacion")
    private Integer diasAnticipacion;
    
    @Column(name = "dias_disponibles", columnDefinition = "JSON")
    private String diasDisponibles;
    
    public ConfiguracionCitas() {
        this.maxCitasDia = 10;
        this.horarioApertura = "09:00:00";
        this.horarioCierre = "18:00:00";
        this.duracionMinimaCita = 30;
        this.diasAnticipacion = 7;
        this.diasDisponibles = "[\"LUNES\", \"MARTES\", \"MIÉRCOLES\", \"JUEVES\", \"VIERNES\", \"SÁBADO\"]";
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getMaxCitasDia() { return maxCitasDia; }
    public void setMaxCitasDia(Integer maxCitasDia) { this.maxCitasDia = maxCitasDia; }
    
    public String getHorarioApertura() { return horarioApertura; }
    public void setHorarioApertura(String horarioApertura) { this.horarioApertura = horarioApertura; }
    
    public String getHorarioCierre() { return horarioCierre; }
    public void setHorarioCierre(String horarioCierre) { this.horarioCierre = horarioCierre; }
    
    public Integer getDuracionMinimaCita() { return duracionMinimaCita; }
    public void setDuracionMinimaCita(Integer duracionMinimaCita) { this.duracionMinimaCita = duracionMinimaCita; }
    
    public Integer getDiasAnticipacion() { return diasAnticipacion; }
    public void setDiasAnticipacion(Integer diasAnticipacion) { this.diasAnticipacion = diasAnticipacion; }
    
    public String getDiasDisponibles() { return diasDisponibles; }
    public void setDiasDisponibles(String diasDisponibles) { this.diasDisponibles = diasDisponibles; }
}