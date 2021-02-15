package marketmaker.marketdata.publisher;

import marketmaker.marketdata.MarketData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Base class of market data publisher. Any subclass need to implement getMarketData method for generating
 * market data. Market data is published as ApplicationEvent.
 */
@Component
public abstract class MarketDataPublisher implements Runnable, ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    protected void publishMarketData(MarketData marketData)
    {
        publisher.publishEvent(marketData);
    }

    protected abstract MarketData getMarketData();

    @Override
    public void run() {
        while (true)
        {
            MarketData marketData = getMarketData();
            if (marketData != null)
                publishMarketData(marketData);
            else
                break;
        }
    }
}
