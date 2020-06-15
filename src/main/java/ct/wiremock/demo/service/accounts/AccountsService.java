package ct.wiremock.demo.service.accounts;

import ct.wiremock.demo.abstractClasses.AbstractService;
import ct.wiremock.demo.interfaces.GenericServiceInterface;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountsService extends AbstractService implements GenericServiceInterface {
	public AccountsService() {
		this.DB_key = "AccountsService_";
		this.mainTblAlias = "a";
	}
	
	@Override
	public Object getById(Map<String, Object> params) {
		return null;
	}
	
	@Override
	public int delete(Map<String, Object> params) {
		return 0;
	}
	
	public void increaseAccountBallance(Map<String, Object> params) {
		DB.executeUpdate("AccountsService_increaseAccountBallance", params);
	}
	
	public void decreaseAccountBallance(Map<String, Object> params) {
		DB.executeUpdate("AccountsService_decreaseAccountBallance", params);
	}
}
