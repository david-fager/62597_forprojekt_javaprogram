package java_server.soap;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.soap.Brugeradmin;
import java_common.soap.IConnectionHandlerSOAP;
import java_server.Galgelogik;
import java_server.Session;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("NonAsciiCharacters")
@WebService(endpointInterface = "java_common.soap.IConnectionHandlerSOAP")
public class ConnectionHandlerSOAP implements IConnectionHandlerSOAP {

    private final int MAX_SESSIONS = 1000000;
    private Brugeradmin ba;
    private HashMap<Integer, Session> sessions = new HashMap<>();
    private DateFormat df = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");

    @Override
    public boolean login(int sesID, String username, String password) {

        try {
            URL url = new URL("http://javabog.dk:9901/brugeradmin?wsdl");
            QName qname = new QName("http://soap.transport.brugerautorisation/", "BrugeradminImplService");
            Service service = Service.create(url, qname);
            ba = service.getPort(Brugeradmin.class);

            Bruger bruger = ba.hentBruger(username, password);
            if (bruger.brugernavn.equals(username) && bruger.adgangskode.equals(password)) {
                System.out.println(getTime() + "Session#" + sesID + " successfully logged in as '" + username + "'.");
                sessions.get(sesID).setUsername(username);
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e2) {
            System.out.println(getTime() + "Session#" + sesID + " failed to login.");
            return false;
        }
        return false;
    }

    @Override
    public boolean startGame(int sesID, int i) throws Exception {
        sessions.get(sesID).setGalgelogik(new Galgelogik(i));
        sessions.get(sesID).getGalgelogik().nulstil();
        System.out.println(getTime() + "Session#" + sesID + " started at game.");
        return true;
    }

    @Override
    public boolean isGameOver(int sesID) {
        return sessions.get(sesID).getGalgelogik().erSpilletSlut();
    }

    @Override
    public boolean guessLetter(int sesID, String letter) {
        if (letter.isEmpty()) {
            return false;
        }
        if (letter.length() > 1) {
            return false;
        }
        sessions.get(sesID).getGalgelogik().gætBogstav(letter);
        return true;
    }

    @Override
    public String getVisibleWord(int sesID) {
        return sessions.get(sesID).getGalgelogik().getSynligtOrd();
    }

    @Override
    public ArrayList<String> getUsedLetters(int sesID) {
        return sessions.get(sesID).getGalgelogik().getBrugteBogstaver();
    }

    @Override
    public String getWord(int sesID) {
        return sessions.get(sesID).getGalgelogik().getOrdet();
    }

    @Override
    public int informConnect() {
        Session newSession = new Session();
        boolean inUse;
        int randomSessionID, safetyStepCounter = 0;
        do {
            if (safetyStepCounter++ > MAX_SESSIONS) {
                System.out.println(getTime() + "SERVER ERROR - NO MORE AVAILABLE IDS - MUST RE-LAUNCH PROGRAM");
                System.out.println(getTime() + "SERVER ERROR - NO MORE AVAILABLE IDS - MUST RE-LAUNCH PROGRAM");
                System.out.println(getTime() + "SERVER ERROR - NO MORE AVAILABLE IDS - MUST RE-LAUNCH PROGRAM");
                return -1;
            }
            inUse = false;
            randomSessionID = (int) (Math.random() * MAX_SESSIONS + 1);

            if (sessions.get(randomSessionID) != null) {
                inUse = true;
            }
        } while (inUse);
        sessions.put(randomSessionID, newSession);
        newSession.setId(randomSessionID);

        System.out.println(getTime() + "Session#" + newSession.getId() + " connected and received an ID.");
        return newSession.getId();
    }

    @Override
    public boolean informDisconnect(int sesID) {
        System.out.println(getTime() + "Session#" + sesID + " disconnected.");
        return true;
    }

    @Override
    public boolean didPlayerWin(int sesID) {
        boolean won = sessions.get(sesID).getGalgelogik().erSpilletVundet();
        if (won) {
            System.out.println(getTime() + "Session#" + sesID + " won a game.");
        } else {
            System.out.println(getTime() + "Session#" + sesID + " lost a game.");
        }
        return won;
    }

    @Override
    public Bruger getFullUser(int sesID, String password) {
        return ba.hentBruger(sessions.get(sesID).getUsername(), password);
    }

    @Override
    public Bruger changePassword(int sesID, String oldPassword, String newPassword) {
        return ba.ændrAdgangskode(sessions.get(sesID).getUsername(), oldPassword, newPassword);
    }

    @Override
    public boolean forgotPassword(int sesID, String message) {
        ba.sendGlemtAdgangskodeEmail(sessions.get(sesID).getUsername(), message);
        return true;
    }

    @Override
    public Bruger getPublicUser(int sesID) {
        return ba.hentBrugerOffentligt(sessions.get(sesID).getUsername());
    }

    public void printAllSessions() {
        if (sessions.size() > 0) {
            System.out.println("All sessions:");
            for (int sesID : sessions.keySet()) {
                System.out.println(sessions.get(sesID).toString());
            }
        } else {
            System.out.println("No sessions yet");
        }
        System.out.println();
    }

    private String getTime() {
        return df.format(Calendar.getInstance().getTimeInMillis());
    }

}
