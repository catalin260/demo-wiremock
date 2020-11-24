package ct.wiremock.demo.controller.transactions;

import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo.abstractClasses.AbstractController;
import ct.wiremock.demo.service.transactions.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(path= Routes.BASE_TRANSACTIONS)
public class TransactionsController extends AbstractController {
	@Autowired
	private TransactionsService transactionsService;
	
	@PostConstruct
	private void initService() {
		this.abstractService = transactionsService;
	}
}
