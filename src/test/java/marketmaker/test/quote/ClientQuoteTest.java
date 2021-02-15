package marketmaker.test.quote;

import marketmaker.ReferencePriceSource;
import marketmaker.client.TestClient;
import marketmaker.data.Security;
import marketmaker.marketdata.listener.SecurityMarketDataListener;
import marketmaker.marketdata.publisher.InMemoryMarketDataPublisher;
import marketmaker.test.QuoteSystemTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RunWith(SpringRunner.class)
public class ClientQuoteTest extends QuoteSystemTests {

    private static final Logger log = LoggerFactory.getLogger(ClientQuoteTest.class);
    private static Security testSecurity;
    private static TestClient testClient;
    private static TestClient testClientB;
    private static TestClient testClientS;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeAll
    public static void init() throws Exception {
        testSecurity = new Security(123, 0.9d, 0.9d);
        testClient = new TestClient("123 BUY 300");
        testClientB = new TestClient("123 BUY 200");
        testClientS = new TestClient("456 SELL 400");
    }

    @Test
    public void testQuote() throws Exception {
        new Thread(testClient).start();
        long startTime = System.currentTimeMillis();
        log.info("Sleep 90 sec start");
        Thread.sleep(90000);
        log.info("Sleep 90 sec stop");
        try {
            testClient.stopConnection();
        } catch (Exception e)
        {
            log.info("Exception thrown after force close connection");
        }
        log.info("test client connection stopped!");
        assert(testClient.getLastReceivedQuoteTime() < (startTime+60000));
    }

    @Test
    public void testMoreThanOneQuote() throws Exception {
        new Thread(testClientB).start();
        new Thread(testClientS).start();
        long startTime = System.currentTimeMillis();
        log.info("Sleep 90 sec start");
        Thread.sleep(90000);
        log.info("Sleep 90 sec stop");
        try {
            testClientB.stopConnection();
            testClientS.stopConnection();
        } catch (Exception e)
        {
            log.info("Exception thrown after force close connection");
        }
        log.info("test client connection stopped!");
        assert(testClientB.getLastReceivedQuoteTime() < (startTime+60000));
        assert(testClientS.getLastReceivedQuoteTime() < (startTime+60000));
    }

    @Test
    public void testQuoteCalculation() throws Exception {
        ReferencePriceSource priceSource = (SecurityMarketDataListener)applicationContext.getBean("securityMarketDataListener");

        testClientB.startConnection("localhost", 6689);
        testClientB.sendMessage("123 BUY 200");
        BigDecimal referencePrice = BigDecimal.valueOf(priceSource.get(123));
        referencePrice = referencePrice.setScale(2, RoundingMode.HALF_UP);
        BigDecimal quotePrice = new BigDecimal(testClientB.receiveMessage());
        quotePrice = quotePrice.setScale(2, RoundingMode.HALF_UP);
        assert(referencePrice.multiply(new BigDecimal(1.01d)).doubleValue() == quotePrice.doubleValue());
        testClientS.startConnection("localhost", 6689);
        testClientS.sendMessage("456 SELL 300");
        referencePrice = BigDecimal.valueOf(priceSource.get(456));
        referencePrice = referencePrice.setScale(2, RoundingMode.HALF_UP);
        quotePrice = new BigDecimal(testClientS.receiveMessage());
        quotePrice = quotePrice.setScale(2, RoundingMode.HALF_UP);
        assert(referencePrice.multiply(new BigDecimal(0.99d)).doubleValue() == quotePrice.doubleValue());
        testClientB.stopConnection();
        testClientS.stopConnection();
    }
}
