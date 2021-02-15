package marketmaker.utility;

import marketmaker.data.Security;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Tool for generate price change for stock based on Discrete Time Geometric Brownian Motion
 */
public class DiscreteTimeGeometricBrownianMotion {

    private static NormalDistribution n = new NormalDistribution();

    public static double getPriceChange(Security security, double currentPrice, double seconds)
    {
        double priceChange = -currentPrice - 1d;
        while (priceChange < -currentPrice)
        {
            priceChange = ((security.getExpectedReturn() * (seconds / 7257600d))
                    + (security.getAnnualizedStandardDeviation() * n.sample() * Math.sqrt(seconds / 7257600d)))
                    * currentPrice;
        }
        return priceChange;
    }
}
