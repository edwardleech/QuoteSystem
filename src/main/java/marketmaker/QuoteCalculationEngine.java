package marketmaker;

/**
* Interface to price a product.
*/
public interface QuoteCalculationEngine {
    /**
     * Computes the price for a product.
     *
     * @param securityId     security identifier
     * @param way            the direction of the trade
     * @param size           the size of the trade
     * @param referencePrice the reference price of the security
    # @return the quote price
     */
    double computeQuote(int securityId, int way, int size, double referencePrice);
}
