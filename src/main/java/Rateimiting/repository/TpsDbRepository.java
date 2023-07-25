package Rateimiting.repository;

import Rateimiting.model.TpsDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TpsDbRepository extends JpaRepository<TpsDb, Long> {
    TpsDb findByUsernameAndPath(String username, String path);
}
