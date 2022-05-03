package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.Annonce;
import fr.flareden.meetingcar.metier.entity.Image;

public interface IAnnonceLoaderHandler {
    public void onAnnonceLoad(Annonce a);
    public void onImageAnnonceLoad(Annonce a, Image i);
}
