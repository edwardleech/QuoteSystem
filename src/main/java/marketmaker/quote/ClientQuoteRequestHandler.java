package marketmaker.quote;

import marketmaker.QuoteCalculationEngine;
import marketmaker.ReferencePriceSource;
import marketmaker.data.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ReferencePriceSourceListener and handling class for any quote request received from Client
 * Also, push quote request to client when there is updated of reference price within request service period
 */
public class ClientQuoteRequestHandler implements Runnable, ReferencePriceSource.ReferencePriceSourceListener {

    private static final Logger log = LoggerFactory.getLogger(ClientQuoteRequestHandler.class);

    private Socket clientSocket;

    private Map<Integer, Request> requests;

    private PrintWriter out;

    private long requestServicePeriod;

    private QuoteCalculationEngine quoteCalculationEngine;

    private ReferencePriceSource securityReferencePriceSource;

    public ClientQuoteRequestHandler (Socket clientSocket, ReferencePriceSource referencePriceSource,
                                      QuoteCalculationEngine quoteCalculationEngine, long requestServicePeriod)
    {
        this.clientSocket = clientSocket;
        this.securityReferencePriceSource = referencePriceSource;
        this.quoteCalculationEngine = quoteCalculationEngine;
        this.requestServicePeriod = requestServicePeriod;
        requests = new HashMap<>();
        log.info("Client connected at " + clientSocket.getPort());
    }

    @Override
    public void run() {

        log.info("Start handling request from port " + clientSocket.getPort());
        String request = null;
        try {
            securityReferencePriceSource.subscribe(this);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter tempOut = new PrintWriter(clientSocket.getOutputStream(), true)) {
                out = tempOut;
                log.info("Waiting for quote request...");
                while ((request = br.readLine()) != null)
                {
                    log.info("Received request: " + request);
                    String[] requestDetails = request.split(" ");
                    int securityId = Integer.parseInt(requestDetails[0]);
                    Request newRequest = new Request(securityId,
                            requestDetails[1].equals("BUY") ? 1 : 2,
                            Integer.parseInt(requestDetails[2]),
                            System.currentTimeMillis());
                    requests.put(securityId, newRequest);
                    double quote = quoteCalculationEngine.computeQuote(securityId,
                            newRequest.getWay(), newRequest.getSize(), securityReferencePriceSource.get(securityId));
                    if (quote != Double.NaN) {
                        out.println(quote);
                    }
                }
                log.info("Stopped ClientQuoteRequestHanlder at port " + clientSocket.getPort());
            }
        } catch (IOException ioe)
        {
            log.error(ioe.getMessage(), ioe);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void referencePriceChanged(int securityId, double price) {
        log.debug("Callback received: " + securityId + " " + price);
        if (requests.containsKey(securityId) &&
                System.currentTimeMillis() - requests.get(securityId).getRequestStartTime() <= requestServicePeriod)
        {
            Request request = requests.get(securityId);
            log.debug("Send update quote!");
            out.println(quoteCalculationEngine.computeQuote(securityId
                    , request.getWay(), request.getSize(), price));
        }
    }
}
