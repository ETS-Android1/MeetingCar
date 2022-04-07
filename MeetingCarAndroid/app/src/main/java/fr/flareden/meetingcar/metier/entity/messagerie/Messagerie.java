package fr.flareden.meetingcar.metier.entity.messagerie;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.client.Client;

public class Messagerie {
    private ArrayList<Discussion> listeDiscussion = null;

    public Messagerie() {
    }

    public Messagerie(ArrayList<Discussion> listeDiscussion) {
        this.listeDiscussion = listeDiscussion;
    }

    public void queryDiscussion(){
        //TODO
    }

    public Discussion getDiscussion(int id){
        Discussion d = null;
        if(this.listeDiscussion!=null) {
            for (int i = 0, max = listeDiscussion.size(); i < max && d == null; i++) {
                if (listeDiscussion.get(i).getId() == id) {
                    d = listeDiscussion.get(i);
                }
            }
        }
        return d;
    }

    public void addDiscussion(Discussion d){
        if(this.listeDiscussion == null){
            this.listeDiscussion = new ArrayList<>();
        }
        listeDiscussion.add(d);
    }

    public void removeDiscussion(Discussion d){
        if(this.listeDiscussion!=null){
            this.listeDiscussion.remove(d);
        }
    }

    public ArrayList<Discussion> getListeDiscussion() {
        return listeDiscussion;
    }

    public void setListeDiscussion(ArrayList<Discussion> listeDiscussion) {
        this.listeDiscussion = listeDiscussion;
    }
}
