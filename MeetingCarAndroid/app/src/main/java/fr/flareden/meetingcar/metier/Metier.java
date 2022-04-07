package fr.flareden.meetingcar.metier;

import fr.flareden.meetingcar.metier.entity.client.Client;
import fr.flareden.meetingcar.metier.entity.messagerie.Messagerie;

public class Metier {
    Client utilisateur;
    Messagerie messagerie;
    Connection connection;
    public Metier(){
        this.utilisateur = null;
        this.connection = new Connection();
    }

    public void connection(String username, String pass){
       this.utilisateur = this.connection.connect(username,pass);
        if(utilisateur != null){
            messagerie = new Messagerie();
            messagerie.queryDiscussion();
        }
    }



}
