package repository;

import package.model.Hospital;
import package.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    Optional<Recurso> findByIdAndHospital(Long id, Hospital hospital);
}

