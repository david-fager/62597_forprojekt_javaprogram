package java_server;

import javax.xml.ws.Endpoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RunServer {
    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] - ");

        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Starting server");
        ConnectionHandler ch = new ConnectionHandler();
        ch.serverStatus();
        Endpoint.publish("http://[::]:9920/hangman", ch);
        System.out.println(df.format(Calendar.getInstance().getTimeInMillis()) + "Server started");
    }
}