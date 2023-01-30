package pl.luxan.luxofttaskv4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.luxan.luxofttaskv4.model.ClientData;

import java.util.Optional;

@Repository
public interface ClientDataRepository extends JpaRepository<ClientData, String> {

    Optional<ClientData> findByPrimaryKey(String primaryKey);

}