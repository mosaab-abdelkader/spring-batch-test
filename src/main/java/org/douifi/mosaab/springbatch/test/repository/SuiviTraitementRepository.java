package fr.sfr.sumo.xms.srr.alim.repository;

import fr.sfr.sumo.xms.srr.alim.model.SuiviTraitement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuiviTraitementRepository extends JpaRepository<SuiviTraitement, Long> {

    @Query("SELECT suiviTraitement FROM SuiviTraitement suiviTraitement "
        + "WHERE suiviTraitement.idTrtm = :idTrtm "
        + "AND suiviTraitement.nomEtape = :nomEtape")
    Optional<SuiviTraitement> findByIdTrtmAndNomEtape(long idTrtm, String nomEtape);
}
