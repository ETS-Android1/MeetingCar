package fr.flareden.meetingcar.metier.listener;


import fr.flareden.meetingcar.metier.entity.Image;

public interface IImageReceivingHandler {
    public void receiveImage(Image img);
}
