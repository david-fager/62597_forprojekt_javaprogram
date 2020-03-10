package java_server.rmi;

import brugerautorisation.data.Bruger;
import java_common.rmi.IConnectionHandlerRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ConnectionHandlerRMI extends UnicastRemoteObject implements IConnectionHandlerRMI {

    public ConnectionHandlerRMI() throws RemoteException {
    }

    @Override
    public boolean login(int sesID, String username, String password) {
        return false;
    }

    @Override
    public void startGame(int sesID, int i) throws Exception {

    }

    @Override
    public boolean isGameOver(int sesID) {
        return false;
    }

    @Override
    public boolean guessLetter(int sesID, String letter) {
        return false;
    }

    @Override
    public String getVisibleWord(int sesID) {
        return null;
    }

    @Override
    public ArrayList<String> getUsedLetters(int sesID) {
        return null;
    }

    @Override
    public String getWord(int sesID) {
        return "hejmeddigjeghedderkaj";
    }

    @Override
    public int informConnect() {
        return 0;
    }

    @Override
    public void informDisconnect(int clientID) {

    }

    @Override
    public boolean didPlayerWin(int sesID) {
        return false;
    }

    @Override
    public Bruger getFullUser(int sesID, String password) {
        return null;
    }

    @Override
    public Bruger changePassword(int sesID, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public void forgotPassword(int sesID, String message) {

    }

    @Override
    public Bruger getPublicUser(int sesID) {
        return null;
    }

}
