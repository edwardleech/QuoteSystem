package marketmaker.utility;

import marketmaker.data.Security;
import marketmaker.marketdata.MarketData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tool for generating market data for security
 */
public class MarketDataGenerator {

    public static List<MarketData> generateMarketData (Object source, Security[] securityList, Double[] initialPrice, long duration)
    {
        List<MarketData> marketDataList = new ArrayList<>();
        Random random = new Random();

        for (int x = 0; x < securityList.length; x++)
        {
            Security security = securityList[x];
            double price = initialPrice[x];
            long time = System.currentTimeMillis();
            long endTime = time + duration;

            while (time < endTime)
            {
                marketDataList.add(new MarketData(source, time, security.getSecurityId(), price));
                double timeChange = (random.nextDouble() * (2d - 0.5d)) + 0.5d;
                time += (timeChange * 1000d);
                price += round(DiscreteTimeGeometricBrownianMotion.getPriceChange(security, price, timeChange), 2);
            }
        }

        return marketDataList;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        try {
            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e)
        {
            return 0d;
        }
    }
}
