package fr.flareden.meetingcar.ui.mail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;

import fr.flareden.meetingcar.metier.Metier;
import fr.flareden.meetingcar.metier.entity.messagerie.Discussion;
import fr.flareden.meetingcar.ui.home.IViewModel;

public class MailViewModel implements IViewModel {

    //
    private Discussion discussion;

    public MailViewModel(Discussion d) {
        this.discussion = d;
    }

    public String getTitle(){
        return this.discussion.getAnnonce().getTitle();
    }

    public String getContactName(){
        String nom, prenom;
        if(this.discussion.getExpediteur() == Metier.getINSTANCE().getUtilisateur()){
            nom = this.discussion.getDestinataire().getNom();
            prenom = this.discussion.getDestinataire().getPrenom();
        } else {
            nom = this.discussion.getExpediteur().getNom();
            prenom = this.discussion.getExpediteur().getPrenom();
        }
        return nom + (prenom != null ? " " + prenom : "");
    }

    @Override
    public String getSearchString() {
        return null;
    }

    public int getId() {
        return this.discussion.getId();
    }

    public Serializable getDiscussion() {
        return discussion;
    }
}