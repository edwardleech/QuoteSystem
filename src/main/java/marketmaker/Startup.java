package marketmaker;

import marketmaker.marketdata.publisher.InMemoryMarketDataPublisher;
import marketmaker.marketdata.publisher.MarketDataPublisher;
import marketmaker.quote.QuoteServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Main class to start up the market data publisher and quote server
 */
@SpringBootApplication
public class Startup {

    private static final Logger log = LoggerFactory.getLogger(Startup.class);

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Startup.class);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner run(TaskExecutor executor) {
        return (args) -> {
            log.info("Starting Quote Server...");
            QuoteServer quoteServer = applicationContext.getBean(QuoteServer.class);
            executor.execute(quoteServer);
            log.info("Starting Quote Server...OK");
            log.info("Starting market data publisher...");
            MarketDataPublisher marketDataPublisher = applicationContext.getBean(InMemoryMarketDataPublisher.class);
            executor.execute(marketDataPublisher);
            log.info("Starting market data publisher...OK");
        };
    }

}
