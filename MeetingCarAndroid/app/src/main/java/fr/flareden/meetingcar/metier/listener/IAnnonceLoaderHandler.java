package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.Annonce;

public interface IAnnonceLoaderHandler {
    public void onAnnonceLoad(Annonce a);
}
