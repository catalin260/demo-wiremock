package ct.wiremock.demo.service.accounts;

import ct.wiremock.demo.abstractClasses.AbstractService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class AccountsService extends AbstractService {
	
	@PostConstruct
	public void initServiceData() {
		this.DB_key = "AccountsService_";
		this.mainTblAlias = "a";
	}
	
	public void increaseAccountBallance(Map<String, Object> params) {
		DB.executeUpdate("AccountsService_increaseAccountBallance", params);
	}
	
	public void decreaseAccountBallance(Map<String, Object> params) {
		DB.executeUpdate("AccountsService_decreaseAccountBallance", params);
	}
}
