package fr.sfr.sumo.xms.srr.alim.repository;

import fr.sfr.sumo.xms.srr.alim.model.SuiviKafkaNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuiviKafkaNotifRepository extends JpaRepository<SuiviKafkaNotification, Long> {
	
	   @Query("SELECT suivi FROM SuiviKafkaNotification suivi "
		        + "WHERE suivi.collecte = :collecte "
		        + "AND suivi.flux = :flux")
	   Optional<SuiviKafkaNotification> findByCollecteFlux(String collecte, String flux);
}
