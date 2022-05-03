package fr.flareden.meetingcar.metier.listener;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.Annonce;

public interface IListAnnonceLoaderHandler {
    public void onListAnnonceLoad(ArrayList<Annonce> liste);
}
