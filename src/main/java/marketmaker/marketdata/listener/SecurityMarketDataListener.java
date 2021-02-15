package marketmaker.marketdata.listener;

import marketmaker.marketdata.MarketData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of MarketDataListener to handle received market data for securities and provide reference price of securities
 */
@Component("securityMarketDataListener")
public class SecurityMarketDataListener extends MarketDataListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityMarketDataListener.class);

    private List<ReferencePriceSourceListener> listeners = new CopyOnWriteArrayList<>();
    private Map<Integer, Double> securityReferencePrices = new ConcurrentHashMap<>();

    @Override
    public void subscribe(ReferencePriceSourceListener listener) {
        log.info("Subscribed listener!");
        listeners.add(listener);
    }

    @Override
    public double get(int securityId) {
        if (securityReferencePrices.containsKey(securityId))
        {
            return securityReferencePrices.get(securityId);
        }
        return Double.NaN;
    }

    @Override
    protected void processMarketData(MarketData marketData) {
        // Update cache
        securityReferencePrices.put(marketData.getSecurityId(), marketData.getPrice());

        // Publish Update
        listeners.parallelStream().forEach(l -> {
            log.debug("Callback to listener: " + marketData.getSecurityId()+ " " + marketData.getPrice());
            l.referencePriceChanged(marketData.getSecurityId(), marketData.getPrice());
        });
    }
}
