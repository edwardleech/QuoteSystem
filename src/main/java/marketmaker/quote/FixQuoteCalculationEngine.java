package marketmaker.quote;

import marketmaker.QuoteCalculationEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of QuoteCalculationEngine.
 * A fix profit quote calculation that always mark up a fix percentage for profit
 */
@Component("fixQuoteCalculateEngine")
public class FixQuoteCalculationEngine implements QuoteCalculationEngine {

    @Value("${profitRate}")
    private double profitRate;

    @Override
    public double computeQuote(int securityId, int way, int size, double referencePrice) {

        if (way ==1)
        {
            return referencePrice * (1d + profitRate);
        }
        else
        {
            return referencePrice * (1d - profitRate);
        }
    }
}
