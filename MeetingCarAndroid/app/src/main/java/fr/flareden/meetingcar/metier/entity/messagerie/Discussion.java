package fr.flareden.meetingcar.metier.entity.messagerie;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.entity.client.Client;

public class Discussion {
    private int id;
    private String expediteurEmail;
    private Client expediteur;
    private Client destinataire;
    private Annonce annonce;
    private ArrayList<Message> messages;

    public Discussion(int id, Client expediteur, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.id = id;
        this.destinataire = destinataire;
        this.expediteur = expediteur;
        this.annonce = annonce;
        this.messages = messages;
    }

    public Discussion(int id, String expediteurEmail, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.id = id;
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
    }


    public Discussion(Client expediteur, Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.id = -1;
        this.expediteur = expediteur;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
    }

    public Discussion(String expediteurEmail,Client destinataire, Annonce annonce, ArrayList<Message> messages) {
        this.id = -1;
        this.expediteurEmail = expediteurEmail;
        this.destinataire = destinataire;
        this.annonce = annonce;
        this.messages = messages;
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

    public Client getDestinataire() {
        return destinataire;
    }


    public void setDestinataire(Client destinataire) {
        this.destinataire = destinataire;
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

    public static Discussion fromJsonObject(JSONObject json) throws JSONException {

        //int id, String title, String desc, float prix, Client vendeur, ArrayList<Image> photos, boolean disponible, Client acheteur, ArrayList<Visite> visites, boolean location, boolean renforcer{
        Annonce annonce = new Annonce(json.getInt("annonce"), json.getString("annonce_title"));
        int idExpediteur = json.optInt("expediteur", -1);
        if(idExpediteur >= 0){
            Client expediteur = new Client(idExpediteur, json.getString("exped_nom"), json.optString("exped_prenom", ""), new Image(json.optInt("exped_photo", -1)));
            Client destinataire = new Client(json.getInt("destinataire"), json.getString("dest_nom"), json.optString("dest_prenom", ""), new Image(json.optInt("dest_photo", -1)));
            return new Discussion(
                    json.getInt("id"),
                    expediteur,
                    destinataire,
                    annonce,
                    new ArrayList<Message>()
            );
        } else {
            String mailExpediteur = json.getString("mail_expediteur");
            Client destinataire = new Client(json.getInt("destinataire"), json.getString("dest_nom"), json.optString("dest_prenom", ""), new Image(json.optInt("dest_photo", -1)));

            return new Discussion(
                    json.getInt("id"),
                    mailExpediteur,
                    destinataire,
                    annonce,
                    new ArrayList<Message>()
            );
        }
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", this.id);
        result.put("id_expediteur", (this.expediteur != null ? this.expediteur.getId() : -1));
        result.put("mail_expediteur", this.expediteurEmail);
        result.put("id_destinataire",this.destinataire.getId());
        result.put("id_annonce", this.annonce.getId());
        return result;
    }
}
