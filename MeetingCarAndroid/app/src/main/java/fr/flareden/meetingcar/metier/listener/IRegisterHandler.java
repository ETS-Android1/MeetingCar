package fr.flareden.meetingcar.metier.listener;

public interface IRegisterHandler {
    public static enum State {
        OK,
        EMAIL_ALREADY_ON_USE,
        SERVER_ERROR
    }

    public void onRegisterClient(State state, String email, String password);
}
