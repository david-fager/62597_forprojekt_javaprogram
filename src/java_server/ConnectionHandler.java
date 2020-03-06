package java_server;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.soap.Brugeradmin;
import java_common.IConnectionHandler;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("NonAsciiCharacters")
@WebService(endpointInterface = "java_common.IConnectionHandler")
public class ConnectionHandler implements IConnectionHandler {

    private Galgelogik galgelogik;
    private HashMap<Integer, String> connections = new HashMap<>();
    private DateFormat df = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");

    public void serverStatus() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Server is running");

                if (connections.size() > 0) {
                    System.out.println("Active connections are:");
                    for (int clientid : connections.keySet()) {
                        System.out.println("Client#: " + clientid + "\t\tUsername: " + connections.get(clientid));
                    }
                } else {
                    System.out.println("No active connections.");
                }
                System.out.println();
            }
        }, 30*60*1000, 30*60*1000);
    }

    @Override
    public boolean login(int clientID, String username, String password) {

        try {
            URL url = new URL("http://javabog.dk:9901/brugeradmin?wsdl");
            QName qname = new QName("http://soap.transport.brugerautorisation/", "BrugeradminImplService");
            Service service = Service.create(url, qname);
            Brugeradmin ba = service.getPort(Brugeradmin.class);

            Bruger bruger = ba.hentBruger(username, password);
            if (bruger.brugernavn.equals(username) && bruger.adgangskode.equals(password)) {
                System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Client#" + clientID + " successfully logged in as '" + username + "'.");
                connections.put(clientID, username);
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Client#" + clientID + " failed to login.");
            return false;
        }
        return false;
    }

    @Override
    public void startGame(int clientID, int i) throws Exception {
        if(i == 1){
            galgelogik = new Galgelogik(1);
        } else if(i == 2){
            galgelogik = new Galgelogik(2);
        }

        galgelogik.nulstil();
        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Client#" + clientID + " started at game.");
    }

    @Override
    public boolean isGameOver() {
        return galgelogik.erSpilletSlut();
    }

    @Override
    public boolean guessLetter(String letter) {
        if (letter.isEmpty()) {
            return false;
        }
        if (letter.length() > 1) {
            return false;
        }
        galgelogik.gætBogstav(letter);
        return true;
    }

    @Override
    public String getVisibleWord() {
        return galgelogik.getSynligtOrd();
    }

    @Override
    public ArrayList<String> getUsedLetters() {
        return galgelogik.getBrugteBogstaver();
    }

    @Override
    public String getWord() {
        return galgelogik.getOrdet();
    }

    @Override
    public int informConnect() {
        boolean usedID;
        int randomID;
        do {
            usedID = false;
            randomID = (int) (Math.random() * 100);
            if (connections.containsKey(randomID)) {
                usedID = true;
            }
        } while (usedID);
        connections.put(randomID, "unknown");
        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Client#" + randomID + " connected and received an ID.");
        return randomID;
    }

    @Override
    public void informDisconnect(int clientID) {
        connections.remove(clientID);
        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Client#" + clientID + " disconnected.");
    }

}
