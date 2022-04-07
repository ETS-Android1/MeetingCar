package fr.flareden.meetingcar.metier;

public class CommunicationBDD {
    private static CommunicationBDD INSTANCE = null;
    private CommunicationBDD(){

    }
    public static CommunicationBDD getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE  =new CommunicationBDD();
        }
        return INSTANCE;
    }


}
