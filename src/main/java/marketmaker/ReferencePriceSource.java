package marketmaker;

/**
 * Source for reference prices.
 */
public interface ReferencePriceSource {
    /**
     * Subscribe to changes to refernce prices.
     *
     * @param listener callback interface for changes
     */
    void subscribe(ReferencePriceSourceListener listener);

    /**
     * Returns the last price.
     * return the price or Nan if there is no active subscription
     */
    double get(int securityId);

    /**
     * Callback interface for {@link ReferencePriceSource}
     */
    public interface ReferencePriceSourceListener {

        /**
         * Called when a price has changed.
         *
         * @param securityId security identifier
         * @param price      reference price
         */
        void referencePriceChanged(int securityId, double price);
    }
}
