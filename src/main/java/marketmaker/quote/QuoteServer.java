package marketmaker.quote;

import marketmaker.QuoteCalculationEngine;
import marketmaker.ReferencePriceSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quote Server as the main contact point with client to accept TCP connection.
 * Then, initialize a quote request handling thread for responding quote
 */
@Component
public class QuoteServer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(QuoteServer.class);

    @Value("${serverPort}")
    private int serverPort;

    @Value("${numOfThread}")
    private int numOfThread;

    @Value("${requestServicePeriod}")
    private Long requestServicePeriod;

    @Autowired
    @Qualifier("securityMarketDataListener")
    private ReferencePriceSource securityReferencePriceSource;

    @Autowired
    @Qualifier("fixQuoteCalculateEngine")
    private QuoteCalculationEngine quoteCalculationEngine;

    private ExecutorService executorService;
    private ServerSocket serverSocket;

    @Override
    public void run() {

        executorService = Executors.newFixedThreadPool(numOfThread);

        try {
            try (ServerSocket serverSocket =new ServerSocket(serverPort))
            {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new ClientQuoteRequestHandler(clientSocket, securityReferencePriceSource, quoteCalculationEngine, requestServicePeriod));
                }
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
