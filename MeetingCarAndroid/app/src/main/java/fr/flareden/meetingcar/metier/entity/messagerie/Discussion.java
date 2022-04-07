package fr.flareden.meetingcar.metier.entity.messagerie;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.client.Client;

public class Discussion {
    private int id;
    private Client expediteur;
    private String expediteurEmail;

    private Client destinataire;
    private Annonce annonce;
    private ArrayList<Message> messages;

    public Discussion(int id, Client expediteur, String expediteurEmail, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.id = id;
        this.expediteur = expediteur;
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
    }

    public Discussion(Client expediteur, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.expediteur = expediteur;
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
    }

    public Discussion(String expediteurEmail, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
    }

    public Discussion(Client expediteur, Client destinataire, Annonce annonce) {
        this.expediteur = expediteur;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = null;
    }

    public Discussion(String expediteurEmail, Client destinataire, Annonce annonce) {
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = null;
    }

    public void addMessage(Message m){
        m.setDiscussion(this);
        if(this.messages == null){
            this.messages = new ArrayList<>();
        }
        this.messages.add(m);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Client expediteur) {
        this.expediteur = expediteur;
    }

    public String getExpediteurEmail() {
        return expediteurEmail;
    }

    public void setExpediteurEmail(String expediteurEmail) {
        this.expediteurEmail = expediteurEmail;
    }

    public Client getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Client destinataire) {
        this.destinataire = destinataire;
    }

    public Annonce getAnnonce() {
        return annonce;
    }

    public void setAnnonce(Annonce annonce) {
        this.annonce = annonce;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
