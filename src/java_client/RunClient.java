package java_client;

import java_common.soap.IConnectionHandlerSOAP;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// Class is set up to connect with SOAP
@SuppressWarnings("NonAsciiCharacters")
public class RunClient {

    private int sessionID;
    private Scanner scanner;
    private IConnectionHandlerSOAP server;

    public static void main(String[] args) {
        RunClient runClient = new RunClient();
        runClient.startConnection();
        runClient.shutdownHook();
        runClient.login();
        runClient.awaitDecision();
    }

    private void startConnection() {
        scanner = new Scanner(System.in);
        URL url = null;

        try {
            System.out.println("Forbindes der med lokal- eller fjernforbindelse?");
            System.out.println("lokal/fjern");
            String response = scanner.nextLine().toLowerCase();
            if (response.equals("lokal")) {
                url = new URL("http://localhost:58008/hangman_remote?wsdl"); // Local testing
            } else if (response.equals("fjern")) {
                url = new URL("http://s185120@dist.saluton.dk:58008/hangman_remote?wsdl"); // dist.saluton.dk testing
            }

            System.out.print("Forbinder ... ");
            QName qname = new QName("http://soap.java_server/", "ConnectionHandlerSOAPService");
            Service service = Service.create(url, qname);
            server = service.getPort(IConnectionHandlerSOAP.class);

            sessionID = server.informConnect();
            if (sessionID < 1) {
                System.out.println("SERVER FEJL - IKKE FLERE ID");
                System.exit(-1);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("SUCCES");
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.informDisconnect(sessionID);
        }, "Shutdown-thread"));
    }

    private void awaitDecision() {
        int page = 0; // 0=menu, 1=game, 2=account
        String input = "";
        while (true) {
            if (page == 0) {
                System.out.println();
                System.out.println("#########################################");
                System.out.println("#                                       #");
                System.out.println("#   VELKOMMEN TIL GALGELEG TERMINALEN   #");
                System.out.println("#                                       #");
                System.out.println("#             SKRIV KOMMANDO            #");
                System.out.println("#             > SPIL                    #");
                System.out.println("#             > KONTO                   #");
                System.out.println("#             > LUK                     #");
                System.out.println("#                                       #");
                System.out.println("#########################################");
                System.out.print("> ");
                input = scanner.nextLine().toLowerCase();

                if (input.equals("spil")) {
                    page = 1;
                } else if (input.equals("konto")) {
                    page = 2;
                } else if (input.equals("luk")) {
                    break;
                }
            } else if (page == 1) {
                System.out.println();
                System.out.println("#########################################");
                System.out.println("#                                       #");
                System.out.println("#            STARTER NYT SPIL           #");
                System.out.println("#                                       #");
                System.out.println("#            VÆLG ORDKATALOG            #");
                System.out.println("#            > DR                       #");
                System.out.println("#            > STANDARD                 #");
                System.out.println("#            > TILBAGE                  #");
                System.out.println("#                                       #");
                System.out.println("#########################################");
                System.out.print("> ");
                input = scanner.nextLine().toLowerCase();

                try {
                    if (input.equals("dr")) {
                        System.out.println("Henter ord fra dr.dk");
                        gameLoop(1);
                        page = 0;
                    } else if (input.equals("standard")) {
                        gameLoop(2);
                        page = 0;
                    } else if (input.equals("tilbage")) {
                        page = 0;
                    }
                } catch (Exception e) {
                    System.out.println("Server fejl - kunne ikke starte spillet");
                }

            } else if (page == 2) {
                System.out.println();
                System.out.println("#########################################");
                System.out.println("#                                       #");
                System.out.println("#           ADMINISTRER KONTO           #");
                System.out.println("#                                       #");
                System.out.println("#            VÆLG HANDLING              #");
                System.out.println("#            > HENT BRUGER              #");
                System.out.println("#            > SKIFT KODE               #");
                System.out.println("#            > GLEMT KODE               #");
                System.out.println("#            > OFF. BRUGER              #");
                System.out.println("#            > TILBAGE                  #");
                System.out.println("#                                       #");
                System.out.println("#########################################");
                System.out.print("> ");
                input = scanner.nextLine().toLowerCase();

                if (input.equals("tilbage")) {
                    page = 0;
                } else {
                    if (input.equals("hent bruger")) {
                        System.out.print("Kodeord: ");
                        String password = scanner.nextLine();

                        System.out.println("Modtog:");
                        System.out.println(server.getFullUser(sessionID, password).toString());
                    } else if (input.equals("skift kode")) {

                        System.out.print("Gamle kodeord: ");
                        String oldPassword = scanner.nextLine();
                        System.out.print("Nye kodeord: ");
                        String newPassword = scanner.nextLine();

                        System.out.println("Modtog:");
                        System.out.println(server.changePassword(sessionID, oldPassword, newPassword).toString());
                    } else if (input.equals("glemt kode")) {

                        System.out.println("Angiv en valgfri besked:");
                        String message = scanner.nextLine();

                        server.forgotPassword(sessionID, message);
                        System.out.println("Informationer om glemt adgangskode er tilsendt din e-mail.");
                    } else if (input.equals("off. bruger")) {

                        System.out.println("Modtog:");
                        System.out.println(server.getPublicUser(sessionID).toString());
                    }

                    System.out.println("TRYK ENTER FOR AT FORTSÆTTE");
                    scanner.nextLine();
                }
            }
        }
        scanner.close();
    }

    private void login() {
        System.out.println();
        System.out.println("Du bedes logge ind via dist.saluton.dk");
        String username;
        boolean success;

        do {
            System.out.print("Brugernavn: ");
            username = scanner.nextLine();
            System.out.print("Kodeord: ");
            success = server.login(sessionID, username, scanner.nextLine());
            if (success) {
                System.out.println("Login succesfuldt");
                break;
            } else {
                System.out.println("Forkert brugernavn eller adgangskode");
                System.out.println();
            }
        } while (!success);
    }

    private void gameLoop(int j) throws Exception {
        server.startGame(sessionID, j);

        while (!server.isGameOver(sessionID)) {
            System.out.println("ORD DER SKAL GÆTTES: " + server.getVisibleWord(sessionID));
            System.out.print("BRUGTE BOGSTAVER: ");
            //i == 1 for DR ord i == 2 for lokale ord

            ArrayList<String> letters = server.getUsedLetters(sessionID);

            for (int i = 0; i < letters.size(); i++) {
                if (i == 0) {
                    System.out.print(letters.get(i));
                } else {
                    System.out.print(", " + letters.get(i));
                }
            }
            System.out.println();

            // When the user gives input
            System.out.println("Skriv bogstavet du vil gætte på");
            server.guessLetter(sessionID, scanner.nextLine());
            System.out.println();
        }

        System.out.println("Ordet var: " + server.getWord(sessionID));
        if (server.didPlayerWin(sessionID)) {
            System.out.println("Spillet er slut - du vandt!");
        } else {
            System.out.println("Spillet er slut - du tabte!");
        }
    }

}
