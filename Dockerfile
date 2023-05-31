FROM registry.pic.services.prod/rhel7/rhel-sfr-jdk:17u3

RUN useradd -ms /bin/bash webadmin

# USER permet de définir le user (ou le UID) qui démarrera le container
USER webadmin

# ADD copie des fichier dans l'image
ADD target/mab-sumo-xms-srr-alim-0.1-SNAPSHOT.jar mab-sumo-xms-srr-alim-0.1-SNAPSHOT.jar

# EXPOSE définit le port exposé par le container.
# Ce port doit correspondre au port exposé par votre application
# Le port par défaut des modules Gitlab est le 8080
# EXPOSE 8080

#ENTRYPOINT définit la commande qui sera exécutée au démarrage au container

ENTRYPOINT ["java", "-jar" ,  "mab-sumo-xms-srr-alim-0.1-SNAPSHOT.jar"]
