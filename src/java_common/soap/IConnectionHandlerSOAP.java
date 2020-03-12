package java_common.soap;

import brugerautorisation.data.Bruger;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;

@SuppressWarnings("NonAsciiCharacters")
@WebService
public interface IConnectionHandlerSOAP {

    @WebMethod
    boolean login(int sesID, String username, String password);

    @WebMethod
    boolean startGame(int sesID, int i) throws Exception;

    @WebMethod
    boolean isGameOver(int sesID);

    @WebMethod
    boolean guessLetter(int sesID, String letter);

    @WebMethod
    String getVisibleWord(int sesID);

    @WebMethod
    ArrayList<String> getUsedLetters(int sesID);

    @WebMethod
    String getWord(int sesID);

    @WebMethod
    int informConnect();

    @WebMethod
    boolean informDisconnect(int sesID);

    @WebMethod
    boolean didPlayerWin(int sesID);

    @WebMethod
    Bruger getFullUser(int sesID, String password);

    @WebMethod
    Bruger changePassword(int sesID, String oldPassword, String newPassword);

    @WebMethod
    boolean forgotPassword(String username, String message);

    @WebMethod
    Bruger getPublicUser(int sesID);

}
