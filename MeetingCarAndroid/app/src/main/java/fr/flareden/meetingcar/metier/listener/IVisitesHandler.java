package fr.flareden.meetingcar.metier.listener;

import java.util.ArrayList;

import fr.flareden.meetingcar.metier.entity.Visite;

public interface IVisitesHandler {
    public void onVisitesReceive(ArrayList<Visite> visites);
}
