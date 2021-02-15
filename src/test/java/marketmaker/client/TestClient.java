package marketmaker.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TestClient.class);
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private long lastReceivedQuoteTime;
    private String request;

    public static void main(String [] args) {
        TestClient testClient = new TestClient("123 BUY 300");
        try {
            testClient.startConnection("localhost", 6689);
            testClient.sendMessage("123 BUY 300");
            log.info("Sent message: 123 BUY 300");
            while (true)
            {
                log.info("Waiting for quote..");
                log.info("Quote received: "+testClient.receiveMessage());
            }
        } catch (IOException ioe)
        {
            log.error(ioe.getMessage(), ioe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            try {
                testClient.stopConnection();
            } catch (IOException ioe)
            {
                log.error(ioe.getMessage(), ioe);
            }
        }
    }

    public TestClient(String request)
    {
        this.request = request;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public String receiveMessage() throws IOException {
        String quote = in.readLine();
        lastReceivedQuoteTime = System.currentTimeMillis();
        return quote;
    }

    public void stopConnection() throws IOException {
        clientSocket.close();
        in.close();
        out.close();
    }

    public long getLastReceivedQuoteTime() {
        return lastReceivedQuoteTime;
    }

    @Override
    public void run() {
        try {
            startConnection("localhost", 6689);
            sendMessage(request);
            while (true)
            {
                log.info("Quote received: "+receiveMessage());
            }
        } catch (IOException ioe)
        {
            log.error(ioe.getMessage(), ioe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            log.info("Stop receive quote!");
            try {
                stopConnection();
            } catch (IOException ioe)
            {
                log.error(ioe.getMessage(), ioe);
            }
        }
    }
}
