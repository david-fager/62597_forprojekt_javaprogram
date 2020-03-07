package java_server;

import java_server.rmi.ConnectionHandlerRMI;
import java_server.soap.ConnectionHandlerSOAP;

import javax.xml.ws.Endpoint;
import java.rmi.Naming;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class RunServer {
    private static DateFormat df = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");
    private static ConnectionHandlerSOAP chsoap;
    private static ConnectionHandlerRMI chrmi;

    public static void main(String[] args) throws Exception {
        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Starting server");

        // SOAP for the javaprogram terminal client
        chsoap = new ConnectionHandlerSOAP();
        Endpoint.publish("http://[::]:9920/hangman_remote", chsoap);

        // RMI server for the javalin webserver
        java.rmi.registry.LocateRegistry.createRegistry(8920); // start rmiregistry i server-JVM
        chrmi = new ConnectionHandlerRMI();
        Naming.rebind("rmi://localhost:8920/hangman_local", chrmi);

        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Server started");

        serverStatus();
    }

    public static void serverStatus() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "SERVER STATUS: RUNNING");
                chsoap.printAllSessions();
            }
        }, 30*60*1000, 30*60*1000);
    }

}