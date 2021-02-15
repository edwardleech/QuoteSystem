package marketmaker.marketdata.listener;

import marketmaker.ReferencePriceSource;
import marketmaker.marketdata.MarketData;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Base class for Subscribe to Market Data. Any subclass need to implement processMarketData method for
 * handling the received market data
 * Implement ReferencePriceSource to provide current reference price of security
 */
@Component
public abstract class MarketDataListener implements ApplicationListener<MarketData>, ReferencePriceSource {

    protected abstract void processMarketData(MarketData marketData);

    @Async
    public void onApplicationEvent(MarketData marketData)
    {
        processMarketData(marketData);
    }
}
