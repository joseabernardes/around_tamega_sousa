package estg.ipp.pt.aroundtmegaesousa.interfaces;

/**
 * Created by José Bernardes on 14/01/2018.
 */

public interface FirebaseServiceCommunication {
    
    void createProgressNotification();

    void updateProgressNotification(double progress);

    void createResultNotification(boolean result, final String documentID, int resultCode);


}
