package fr.flareden.meetingcar.metier.entity.messagerie;

import fr.flareden.meetingcar.metier.entity.client.Client;

public class Message {
    private int id;
    private String contenu;
    private String image; //Verify how Android work
    private Client expediteur;
    private Discussion discussion;
    private String horodatage;

    public Message(int id, String contenu, String image, Client expediteur, Discussion discussion, String horodatage) {
        this.id = id;
        this.contenu = contenu;
        this.image = image;
        this.expediteur = expediteur;
        this.discussion = discussion;
        this.horodatage = horodatage;
    }

    public Message(String contenu, String image, Client expediteur, String horodatage) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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
}
