package ct.wiremock.demo.service.transactions;

import ct.wiremock.demo._global.enums.TransactionStatus;
import ct.wiremock.demo.abstractClasses.AbstractService;
import ct.wiremock.demo.service.accounts.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionsService extends AbstractService {
	@Autowired
	private AccountsService accountsService;
	
	@PostConstruct
	public void initServiceData() {
		this.DB_key = "TransactionsService_";
		this.mainTblAlias = "t";
	}
	
	@Override
	public int insert(Map<String, Object> params) {
		int last_id = DB.executeGetLastId("TransactionsService_insert", params);
		accountsService.decreaseAccountBallance(new HashMap<String, Object>(){{
			put("account_id", params.get("account_id_src"));
			put("amount", params.get("trans_amount"));
		}});
		
		return last_id;
	}
	
	public void executeQueuedTransactions() {
		List<Map<String, Object>> queued = this.list(new HashMap<String, Object>(){{
			put("trans_status", TransactionStatus.QUEUE.getText());
		}});
		
		queued.forEach((transaction) -> {
			accountsService.increaseAccountBallance(new HashMap<String, Object>(){{
				put("account_id", transaction.get("account_id_dest"));
				put("amount", transaction.get("trans_amount"));
			}});
			
			this.finishTransaction(transaction);
		});
	}
	
	public void finishTransaction(Map<String, Object> transaction) {
		DB.executeUpdate("TransactionsService_finishTransaction", transaction);
	}
}
