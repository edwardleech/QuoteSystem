package marketmaker.utility;

import marketmaker.data.Security;
import marketmaker.repository.SecurityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Tool for initialize security in DB
 */
@Component
public class SecuritiesInitializer {

    private static final Logger log = LoggerFactory.getLogger(SecuritiesInitializer.class);

    @Autowired
    private SecurityRepository securityRepository;
    @Value("${securityList}")
    private String securityList;


    @PostConstruct
    public void setupSecurities() throws Exception {

        if (securityList == null || securityList.length() == 0)
        {
            throw new Exception("Failed to create securities.");
        }

        log.info("Creating securities...");
        Arrays.stream(securityList.split(",")).forEach(s -> {
            String[] stockDetail = s.split(":");
            if (stockDetail.length != 3)
            {
                log.error("Invalid stock, failed to create. " + s);
            }
            else {
                try {
                    Security security = new Security(Integer.parseInt(stockDetail[0]), Double.parseDouble(stockDetail[1]), Double.parseDouble(stockDetail[2]));
                    securityRepository.save(security);
                } catch (Exception e) {
                    log.error("Invalid stock, failed to create. " + e.getMessage());
                }

            }
        });
        log.info("Creating securities...OK");
    }
}
