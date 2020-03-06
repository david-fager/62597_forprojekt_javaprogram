package java_client;

import java_common.IConnectionHandler;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// Class is set up to connect with SOAP
@SuppressWarnings("NonAsciiCharacters")
public class RunClient {

    private int clientID;
    private Scanner scanner;
    private IConnectionHandler server;

    public static void main(String[] args) throws Exception {
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
                url = new URL("http://localhost:9920/hangman?wsdl"); // Local testing
            } else if (response.equals("fjern")) {
                url = new URL("http://s185120@dist.saluton.dk:9920/hangman?wsdl"); // dist.saluton.dk testing
            }

            System.out.print("Forbinder ... ");
            QName qname = new QName("http://java_server/", "ConnectionHandlerService");
            Service service = Service.create(url, qname);
            server = service.getPort(IConnectionHandler.class);
            clientID = server.informConnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("SUCCES");
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                server.informDisconnect(clientID);
            }
        }, "Shutdown-thread"));
    }

    private void awaitDecision() throws Exception {

        String input;
        while (true) {
            System.out.println();
            System.out.println("#########################################");
            System.out.println("#                                       #");
            System.out.println("#   VELKOMMEN TIL GALGELEG TERMINALEN   #");
            System.out.println("#                                       #");
            System.out.println("#            SKRIV KOMMANDO             #");
            System.out.println("#                > SPIL                 #");
            System.out.println("#                > LUK                  #");
            System.out.println("#                                       #");
            System.out.println("#########################################");
            System.out.println("> ");
            input = scanner.nextLine().toLowerCase();
            if (input.equals("luk")) {
                break;
            }
            if (input.equals("spil")) {
                System.out.println();
                System.out.println("#########################################");
                System.out.println("#                                       #");
                System.out.println("#            VÆLG ORDKATALOG            #");
                System.out.println("#                > DR                   #");
                System.out.println("#                > LOKAL                #");
                System.out.println("#                                       #");
                System.out.println("#########################################");
                System.out.println("> ");
                input = scanner.nextLine().toLowerCase();
                if(input.equals("dr")){
                    gameLoop(1);
                }else if(input.equals("lokal")) {
                    gameLoop(2);
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
            System.out.println("Brugernavn: ");
            username = scanner.nextLine();
            System.out.println("Kodeord: ");
            success = server.login(clientID, username, scanner.nextLine());
            if (success) {
                System.out.println("Login succesfuldt");
                break;
            } else {
                System.out.println("Forkert brugernavn eller adgangskode");
                System.out.println();
            }
        } while (!success);
    }

    private void gameLoop(int j ) throws Exception {
        server.startGame(clientID, j);

        while (!server.isGameOver()) {
            System.out.println("ORD DER SKAL GÆTTES: " + server.getVisibleWord());
            System.out.print("BRUGTE BOGSTAVER: ");
            //i == 1 for DR ord i == 2 for lokale ord

            ArrayList<String> letters = server.getUsedLetters();

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
            server.guessLetter(scanner.nextLine());
            System.out.println();
        }

        System.out.println("Ordet var: " + server.getWord());
        System.out.println("Spillet er slut");
    }

}
