package repository;

import package.model.TransacaoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<TransacaoRecurso, Long> {
}

