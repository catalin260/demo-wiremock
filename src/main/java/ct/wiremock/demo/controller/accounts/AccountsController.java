package ct.wiremock.demo.controller.accounts;

import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo.abstractClasses.AbstractController;
import ct.wiremock.demo.service.accounts.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(path= Routes.BASE_ACCOUNTS)
public class AccountsController extends AbstractController {
	@Autowired
	private AccountsService accountsService;
	
	@PostConstruct
	private void initService() {
		this.abstractService = accountsService;
	}
}
