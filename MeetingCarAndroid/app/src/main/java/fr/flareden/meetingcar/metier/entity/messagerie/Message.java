package fr.flareden.meetingcar.metier.entity.messagerie;

import org.json.JSONException;
import org.json.JSONObject;

import fr.flareden.meetingcar.metier.entity.Image;
import fr.flareden.meetingcar.metier.entity.client.Client;

public class Message {
    private int id;
    private String contenu;
    private Image image; //Verify how Android work
    private Client expediteur;
    private Discussion discussion;
    private String horodatage;

    public Message(int id, String contenu, Image image, Client expediteur, Discussion discussion, String horodatage) {
        this.id = id;
        this.contenu = contenu;
        this.image = image;
        this.expediteur = expediteur;
        this.discussion = discussion;
        this.horodatage = horodatage;
    }

    public Message(String contenu, Image image, Client expediteur, String horodatage) {
        this.contenu = contenu;
        this.image = image;
        this.expediteur = expediteur;
        this.discussion = discussion;
        this.horodatage = horodatage;
    }
    public Message(String contenu, Client expediteur, String horodatage) {
        this.contenu = contenu;
        this.expediteur = expediteur;
        this.discussion = discussion;
        this.horodatage = horodatage;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Client getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Client expediteur) {
        this.expediteur = expediteur;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public String getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(String horodatage) {
        this.horodatage = horodatage;
    }



    public static Message fromJsonObject(JSONObject json, Discussion d) throws JSONException {
        //    public Message(int id, String contenu, String image, Client expediteur, Discussion discussion, String horodatage) {
        if(d.getId() == json.getInt("id_discussion")){
            return new Message(
                    json.getInt("id"),
                    json.getString("contenu"),
                    new Image(json.optInt("id_image", -1)),
                    new Client(json.getInt("id_expediteur"), json.getString("exped_nom"), json.getString("exped_prenom"), new Image(json.optInt("exped_image",-1))),
                    d,
                    json.getString("horodatage")
                );
        }
        return null;

    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("id", this.id);
        result.put("contenu", this.contenu);
        result.put("id_image", (this.image != null ? this.image.getId() : -1));
        result.put("id_expediteur",this.expediteur.getId());
        result.put("id_discussion", this.discussion.getId());
        result.put("horodatage", this.horodatage);
        return result;
    }
}
