package marketmaker.marketdata;

import org.springframework.context.ApplicationEvent;

/**
 * Market Data of security. It is as a ApplicationEvent publish to market data subscriber
 */
public class MarketData extends ApplicationEvent implements Comparable<MarketData> {

    private long time;
    private int securityId;
    private double price;

    public MarketData(Object source, long time, int securityId, double price)
    {
        super(source);
        this.time = time;
        this.securityId = securityId;
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public int getSecurityId() {
        return securityId;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString()
    {
        return String.format("%d change to %.2f", securityId, price);
    }

    @Override
    public int compareTo(MarketData o) {
        if (this.time > o.getTime())
        {
            return 1;
        } else if (this.time < o.getTime())
        {
            return -1;
        }
        return 0;
    }
}
