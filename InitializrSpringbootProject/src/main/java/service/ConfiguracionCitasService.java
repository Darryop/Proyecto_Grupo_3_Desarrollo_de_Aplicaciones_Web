package service;

/**
 *
 * @author darry
 */

import model.ConfiguracionCitas;
import repository.ConfiguracionCitasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ConfiguracionCitasService {
    
    @Autowired
    private ConfiguracionCitasRepository configuracionRepository;
    
    public ConfiguracionCitas obtenerConfiguracion() {
        Optional<ConfiguracionCitas> configOpt = configuracionRepository.findById(1L);
        return configOpt.orElseGet(() -> {
            ConfiguracionCitas nuevaConfig = new ConfiguracionCitas();
            return configuracionRepository.save(nuevaConfig);
        });
    }
    
    public ConfiguracionCitas guardarConfiguracion(ConfiguracionCitas configuracion) {
        configuracion.setId(1L); // Siempre usar ID 1
        return configuracionRepository.save(configuracion);
    }
    
    public void actualizarMaxCitasDia(int maxCitas) {
        ConfiguracionCitas config = obtenerConfiguracion();
        config.setMaxCitasDia(maxCitas);
        configuracionRepository.save(config);
    }
    
    public void actualizarHorarios(String apertura, String cierre) {
        ConfiguracionCitas config = obtenerConfiguracion();
        config.setHorarioApertura(apertura);
        config.setHorarioCierre(cierre);
        configuracionRepository.save(config);
    }
}
