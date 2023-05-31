package fr.sfr.sumo.xms.srr.alim.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Getter
@Setter
@Entity
@Table(name = "suivi_kafka_notification")
public class SuiviKafkaNotification {
    @Id
    @Column(name = "kafka_notif_id", nullable = false)
    private long kafkaNotifId;
    @Column(nullable = false)
    private String collecte;
    @Column(nullable = false)
    private String flux;
    @Column(name = "last_update", nullable = false)
    private Date lastUpdate;
    @Column(name = "nb_notif", nullable = false)
    private long nbNotif;
    @Column(name = "nb_ticket_except", nullable = false)
    private long nbTicketReject;
    @Column(name = "nb_file_reject", nullable = false)
    private long nbFileReject;
    @Column(name = "nb_ticket_read", nullable = false)
    private long nbTicketRead;
    @Column(name = "nb_ticket_written", nullable = false)
    private long nbTicketWritten;
    @Column(name = "nb_file_treated", nullable = false)
    private long nbFileTreated;
}