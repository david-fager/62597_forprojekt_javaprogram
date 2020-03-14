package java_server.rmi;

import brugerautorisation.data.Bruger;
import java_common.rmi.IConnectionHandlerRMI;
import java_server.soap.ConnectionHandlerSOAP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ConnectionHandlerRMI extends UnicastRemoteObject implements IConnectionHandlerRMI {
    private ConnectionHandlerSOAP chsoap;

    public ConnectionHandlerRMI(ConnectionHandlerSOAP chsoap) throws RemoteException {
        this.chsoap = chsoap;
    }

    @Override
    public boolean idRecognized(int sesID) throws RemoteException {
        return chsoap.idRecognized(sesID);
    }

    @Override
    public boolean login(int sesID, String username, String password) {
        return chsoap.login(sesID, username, password);
    }

    @Override
    public boolean startGame(int sesID, int i) throws Exception {
        return chsoap.startGame(sesID, i);
    }

    @Override
    public boolean isGameOver(int sesID) {
        return chsoap.isGameOver(sesID);
    }

    @Override
    public boolean guessLetter(int sesID, String letter) {
        return chsoap.guessLetter(sesID, letter);
    }

    @Override
    public String getVisibleWord(int sesID) {
        return chsoap.getVisibleWord(sesID);
    }

    @Override
    public ArrayList<String> getUsedLetters(int sesID) {
        return chsoap.getUsedLetters(sesID);
    }

    @Override
    public int numberWrongGuesses(int sesID) {
        return chsoap.numberWrongGuesses(sesID);
    }

    @Override
    public String getWord(int sesID) {
        return chsoap.getWord(sesID);
    }

    @Override
    public int informConnect() {
        return chsoap.informConnect();
    }

    @Override
    public boolean informDisconnect(int sesID) {
        return chsoap.informDisconnect(sesID);
    }

    @Override
    public boolean didPlayerWin(int sesID) {
        return chsoap.didPlayerWin(sesID);
    }

    @Override
    public Bruger getFullUser(int sesID) {
        return chsoap.getFullUser(sesID);
    }

    @Override
    public Bruger changePassword(int sesID, String oldPassword, String newPassword) {
        return chsoap.changePassword(sesID, oldPassword, newPassword);
    }

    @Override
    public boolean forgotPassword(String username, String message) {
        return chsoap.forgotPassword(username, message);
    }

    @Override
    public Bruger getPublicUser(int sesID) {
        return chsoap.getPublicUser(sesID);
    }

}
