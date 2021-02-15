package marketmaker.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity class for Security
 */
@Entity
public class Security {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private int securityId;
    private double expectedReturn;
    private double annualizedStandardDeviation;


    protected Security() {}

    public Security(int securityId, double expectedReturn, double annualizedStandardDeviation) {
        this.securityId = securityId;
        this.expectedReturn = expectedReturn;
        this.annualizedStandardDeviation = annualizedStandardDeviation;
    }

    @Override
    public String toString() {
        return String.format(
                "Security[id=%d, securityId='%d']",
                id, securityId);
    }

    public Long getId() {
        return id;
    }

    public int getSecurityId() {
        return securityId;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getAnnualizedStandardDeviation() {
        return annualizedStandardDeviation;
    }

}