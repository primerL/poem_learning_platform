package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Integer> {
}
