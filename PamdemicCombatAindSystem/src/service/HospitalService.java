package service;

import package.exception.ApiRequestException;
import package.model.Hospital;
import package.model.Recurso;
import package.model.dto.HospitalDTO;
import package.model.dto.PercentualOcupacaoDTO;
import package.repository.HospitalRepository;
import package.repository.RecursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalService {

    private HospitalRepository repository;
    private RecursoRepository recursoRepository;

    public HospitalService(HospitalRepository repository, RecursoRepository recursoRepository) {
        this.repository = repository;
        this.recursoRepository = recursoRepository;
    }

    public List<Hospital> listar() {
        return repository.findAll();
    }

    public Hospital buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Não foi possível encontrar o hospital com ID: " + id));
    }

    @Transactional
    public Hospital salvar(HospitalDTO dto) {
        Hospital hospital = new Hospital(dto.getNome(), dto.getCnpj(), dto.getEndereco(),
                dto.getLatitude(), dto.getLongitude(), dto.getOcupacao(), LocalDateTime.now());

        List<Recurso> recursos = dto.getRecursos().stream()
                .map(recurso -> new Recurso(recurso.getNome(),
                        recurso.getTipoRecurso(),
                        hospital))
                .collect(Collectors.toList());

        hospital.setRecursos(recursos);

        repository.save(hospital);

        recursoRepository.saveAll(recursos);

        return hospital;
    }

    @Transactional
    public Hospital atualizarOcupacao(Long id, PercentualOcupacaoDTO percentualOcupacaoDTO) {
        Hospital hospital = repository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Não foi possível encontrar o hospital com ID:" + id));

        hospital.setOcupacao(percentualOcupacaoDTO.getOcupacao());
        hospital.setDataAttOcupacao(LocalDateTime.now());

        return hospital;
    }
}

