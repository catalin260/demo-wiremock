package ct.wiremock.demo.cron;

import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo.service.transactions.TransactionsService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@EnableAutoConfiguration
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TransactionsCron {
	@Autowired
	TransactionsService transactionsService;
	
	@Autowired
	private Config config;
	
	@Scheduled(cron = "${transactions.cronjobExecuteQueuedTransactions.value}")
	public void cronjobExecuteQueuedTransactions() {
		transactionsService.executeQueuedTransactions();
	}
}
