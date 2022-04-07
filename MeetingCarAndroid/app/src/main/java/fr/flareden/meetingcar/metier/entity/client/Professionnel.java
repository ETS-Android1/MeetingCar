package fr.flareden.meetingcar.metier.entity.client;

import java.util.ArrayList;

public class Professionnel extends Client{
    private boolean abonner;

    public Professionnel(int id, String nom, String prenom, String email, String telephone, String datenaissance, ArrayList<MoyenCommunication> communications, boolean abonner) {
        super(id, nom, prenom, email, telephone, datenaissance, communications);
        this.abonner = abonner;
    }

    public Professionnel(int id, String nom, String prenom, String email, String telephone, String datenaissance, boolean abonner) {
        super(id, nom, prenom, email, telephone, datenaissance);
        this.abonner = abonner;
    }

    public boolean isAbonner() {
        return abonner;
    }

    public void setAbonner(boolean abonner) {
        this.abonner = abonner;
    }
}
