package service;

import package.exception.ApiRequestException;
import package.model.Hospital;
import package.model.Intercambio;
import package.model.Recurso;
import package.model.dto.MediaRecursosDTO;
import package.model.dto.RelatorioDTO;
import package.model.enumeration.TipoRecurso;
import package.repository.HospitalRepository;
import package.repository.IntercambioRepository;
import package.repository.RecursoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private HospitalRepository hospitalRepository;
    private IntercambioRepository intercambioRepository;
    private RecursoRepository recursoRepository;

    public RelatorioService(HospitalRepository hospitalRepository, IntercambioRepository intercambioRepository, RecursoRepository recursoRepository) {
        this.hospitalRepository = hospitalRepository;
        this.intercambioRepository = intercambioRepository;
        this.recursoRepository = recursoRepository;
    }

    public RelatorioDTO gerarRelatorio() {
        List<Hospital> hospitais = hospitalRepository.findAll();

        int numeroDeHospitais = hospitais.size();

        if (numeroDeHospitais == 0) {
            throw new ApiRequestException("Não há hospitais no banco de dados");
        }

        long contagemHospitaisSublotados = hospitais.stream()
                .filter(hospital -> hospital.getOcupacao() < 90).count();
        long contagemHospitaisSuperlotados = numeroDeHospitais - contagemHospitaisSublotados;

        double porcentagemHospitaisSublotados = (contagemHospitaisSublotados * 100) / numeroDeHospitais;
        double porcentagemHospitaisSuperlotados = (contagemHospitaisSuperlotados * 100) / numeroDeHospitais;

        Hospital hospitalSublotadoMaisTempo = hospitais.stream()
                .filter(hospital -> hospital.getOcupacao() <= 90)
                .min(Comparator.comparing(Hospital::getDataAttOcupacao))
                .orElse(null);
        Hospital hospitalSuperlotadoMaisTempo = hospitais.stream()
                .filter(hospital -> hospital.getOcupacao() > 90)
                .min(Comparator.comparing(Hospital::getDataAttOcupacao))
                .orElse(null);

        List<Recurso> recursos = recursoRepository.findAll();

        Map<TipoRecurso, Long> frequenciaMap = recursos.stream()
                .collect(Collectors.groupingBy(Recurso::getTipoRecurso, Collectors.counting()));

        List<Intercambio> intercambios = intercambioRepository.findAll();

        List<MediaRecursosDTO> mediaRecursos = frequenciaMap.entrySet().stream()
                .map(entry -> new MediaRecursosDTO(entry.getKey(), entry.getValue().doubleValue() / numeroDeHospitais))
                .collect(Collectors.toList());

        return new RelatorioDTO(porcentagemHospitaisSuperlotados, porcentagemHospitaisSublotados,
                hospitalSuperlotadoMaisTempo, hospitalSublotadoMaisTempo,
                mediaRecursos, intercambios);
    }
}

