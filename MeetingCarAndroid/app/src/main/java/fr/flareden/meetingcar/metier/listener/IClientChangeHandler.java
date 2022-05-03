package fr.flareden.meetingcar.metier.listener;

import fr.flareden.meetingcar.metier.entity.client.Client;

public interface IClientChangeHandler {
    public void onClientChange(Client c);
}
