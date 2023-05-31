package fr.sfr.sumo.xms.srr.alim.repository;

import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface SuiviAlimentationRepository extends JpaRepository<SuiviAlimentation, Long> {

    @Query("SELECT suiviAlimentation FROM SuiviAlimentation suiviAlimentation "
        + "WHERE suiviAlimentation.chksFich = :chksFich "
        + "AND suiviAlimentation.etat != 'ANNL' "
        + "OR suiviAlimentation.etat IS NULL")
    List<SuiviAlimentation> findByChksFichWhereEtatIsNotAnnl(String chksFich);

    @Query("SELECT suiviAlimentation FROM SuiviAlimentation suiviAlimentation "
            + "WHERE suiviAlimentation.nomFich = :nomFich "
            + "AND suiviAlimentation.etat != 'ANNL' "
            + "OR suiviAlimentation.etat IS NULL")
    List<SuiviAlimentation> findByNomFichWhereEtatIsNotAnnl(String nomFich);
    Optional<SuiviAlimentation> findByIdTrtmAndNomFich(long idTrtm, String nomFich);
    Optional<SuiviAlimentation> findByIdTrtm(long idTrtm);
    
    @Transactional
    @Modifying
    @Query(value = "update suivi_alimentation s set nomb_lign_chrg = nomb_lign_chrg+1 where s.id_trtm = :idtrtm", nativeQuery = true)
    void incSuiviAlientation( @Param("idtrtm") long idtrtm);
    @Transactional
    @Modifying
    @Query(value = "update suivi_alimentation s set statut_sync = 'R' where s.id_trtm = :idtrtm", nativeQuery = true)
    void updatSuiviAlimSyncElastic( @Param("idtrtm") long idtrtm);
}