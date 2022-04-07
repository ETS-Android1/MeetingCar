package fr.flareden.meetingcar.metier.entity.client;

import java.util.ArrayList;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String datenaissance;
    private ArrayList<MoyenCommunication> communications;

    public Client(int id, String nom, String prenom, String email, String telephone, String datenaissance, ArrayList<MoyenCommunication> communications) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.datenaissance = datenaissance;
        this.communications = communications;
    }

    public Client(int id, String nom, String prenom, String email, String telephone, String datenaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.datenaissance = datenaissance;
        this.communications = null;
    }

    public void addMoyenCommunication(MoyenCommunication com){
        if(this.communications == null){
            this.communications = new ArrayList<>();
        }
        this.communications.add(com);
    }

    public void removeMoyenCommunication(MoyenCommunication mc) {
        if(this.communications != null){
            this.communications.remove(mc);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(String datenaissance) {
        this.datenaissance = datenaissance;
    }

    public ArrayList<MoyenCommunication> getCommunications() {
        return communications;
    }

}
