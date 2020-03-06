package java_common;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;

@SuppressWarnings("NonAsciiCharacters")
@WebService
public interface IConnectionHandler {

    @WebMethod
    boolean login(int clientID, String username, String password);

    @WebMethod
    void startGame(int clientID, int i) throws Exception;

    @WebMethod
    boolean isGameOver();

    @WebMethod
    boolean guessLetter(String letter);

    @WebMethod
    String getVisibleWord();

    @WebMethod
    ArrayList<String> getUsedLetters();

    @WebMethod
    String getWord();

    @WebMethod
    int informConnect();

    @WebMethod
    void informDisconnect(int clientID);

}
