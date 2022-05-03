package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.client.Client;

public interface IClientLoadingHandler {

    public void onClientLoad(Client c, boolean self);

}
