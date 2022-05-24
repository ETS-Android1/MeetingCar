package fr.flareden.meetingcar.ui.mail;

import fr.flareden.meetingcar.metier.entity.messagerie.Message;
import fr.flareden.meetingcar.ui.home.IViewModel;

public class MessageViewModel implements IViewModel {
    private Message message;

    public MessageViewModel(Message m) {
        this.message = m;
    }

    public String getContent(){
        return message.getContenu();
    }

    public String getAuthor(){
        return message.getExpediteur().getNom() + (message.getExpediteur().getPrenom() != null ? " " + message.getExpediteur().getPrenom() : "");
    }

    @Override
    public String getSearchString() {
        return "";
    }
}
