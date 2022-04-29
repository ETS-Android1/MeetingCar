package fr.flareden.meetingcar.metier.entity.client;

import java.util.ArrayList;

public class Particulier extends Client{
    public Particulier(int id, String nom, String prenom, String email, String telephone, String datenaissance, String adresse,ArrayList<MoyenCommunication> communications) {
        super(id, nom, prenom, email, telephone, datenaissance,adresse, communications);
    }

    public Particulier(int id, String nom, String prenom, String email, String telephone, String datenaissance,String adresse) {
        super(id, nom, prenom, email, telephone, datenaissance, adresse);
    }
}
