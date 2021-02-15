package marketmaker.marketdata.publisher;

import marketmaker.data.Security;
import marketmaker.marketdata.MarketData;
import marketmaker.repository.SecurityRepository;
import marketmaker.utility.MarketDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Subclass of MarketDataPublisher. This InMemoryMarketDataPublisher will load all the market data and then
 * publish market data based on the timestamp of market data
 */
@Component
@DependsOn("securitiesInitializer")
public class InMemoryMarketDataPublisher extends MarketDataPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMarketDataPublisher.class);

    private long currentTickerTime = 0L;
    private LinkedList<MarketData> marketDataList;
    private boolean marketDataGenerated = false;
    private int numberOfUpdate = 1;

    @Autowired
    private SecurityRepository securityRepository;
    @Value("${marketDataList}")
    private String initMarketDataList;
    @Value("${marketDataDuration}")
    private String duration;

    @PostConstruct
    public void initialize()
    {
        log.info("Start generating market data....");
        if (initMarketDataList == null || initMarketDataList.length() == 0)
        {
            log.error("No market data list is found. Market data is not generated.");
            marketDataList = new LinkedList<>();
            return;
        }

        if (duration == null || duration.length() == 0)
        {
            log.error("No market data generation duration is defined. Market data not generated.");
            marketDataList = new LinkedList<>();
            return;
        }

        String[] securityIdList = initMarketDataList.split(",");
        List<Security> securities = new ArrayList<>();
        List<Double> initPrice = new ArrayList<>();

        for (String s : securityIdList)
        {
            String[] securityDetails = s.split(":");
            List<Security> securityResult = securityRepository.findBySecurityId(Integer.parseInt(securityDetails[0]));
            if (securityResult.size() == 0)
            {
                log.error("Failed to generate market data for " + securityDetails[0]);
            }
            else
            {
                securities.add(securityResult.get(0));
                initPrice.add(Double.parseDouble(securityDetails[1]));
            }
        }
        Security[] securityArr = new Security[securities.size()];
        securityArr = securities.toArray(securityArr);
        Double[] priceArr = new Double[securities.size()];
        priceArr = initPrice.toArray(priceArr);

        List<MarketData> tickers = MarketDataGenerator.generateMarketData(this, securityArr, priceArr, Long.parseLong(duration));
        Collections.sort(tickers);
        marketDataList = new LinkedList<>(tickers);
        log.info(String.format("Generate %d market data!", tickers.size()));
    }

    @Override
    protected MarketData getMarketData() {

        if (marketDataList.size() == 0)
            return null;

        MarketData marketData = marketDataList.removeFirst();
        if (currentTickerTime == 0L || marketData.getTime() == currentTickerTime)
        {
            if (currentTickerTime == 0L)
                currentTickerTime = marketData.getTime();

            log.debug(marketData.toString());
            return marketData;
        }
        else
        {
            try {
                Thread.sleep(marketData.getTime() - currentTickerTime);
                currentTickerTime = marketData.getTime();
            } catch (Exception e)
            {
                log.error(e.getMessage());
            } finally {
                log.debug(marketData.toString());
                return marketData;
            }
        }
    }
}

