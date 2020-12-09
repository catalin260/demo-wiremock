package ct.wiremock.demo.service.user;

import ct.wiremock.demo._global.Utils.JWTUtils;
import ct.wiremock.demo._global.Utils.crypto.CryptoEnum;
import ct.wiremock.demo._global.Utils.crypto.CryptoUtil;
import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo.abstractClasses.AbstractService;
import ct.wiremock.demo.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends AbstractService {
	
	@Autowired
	protected JWTUtils jwtUtils;
	
	@Autowired
	protected AuthService authService;
	
	@Autowired
	protected Config config;
	
	@PostConstruct
	public void initServiceData() {
		this.DB_key = "UserService_";
		this.mainTblAlias = "";
	}
	
	public ResponseEntity<Principal> get() {
		return ResponseEntity.ok(authService.getPrincipal());
	}
	
	public void updateUserPassword(String user_password, String user_username) {
		if (user_password != null && !user_password.equals("")) {
			String enc = CryptoUtil.encrypt(CryptoEnum.AES256, user_password);
			DB.executeUpdate("UserService_updateMobilePin", new HashMap<String, Object>(){{
				put("user_username", user_username);
				put("user_password", enc);
			}});
		}
	}
	
	public String returnUserPasswordDecrypted(String username) {
		Map<String, Object> result = DB.getRow("UserService_returnPasswordDecrypted", new HashMap<String, Object>(){{
			put("user_username", username);
		}});
		
		return CryptoUtil.decrypt(CryptoEnum.AES256, String.valueOf(result.get("user_password")));
	}
	
	private boolean checkIfUserExists(String username) {
		return (this.getByCriteria("user_username", username).size() > 0);
	}
	
	public int insert(Map<String, Object> params) {
		int last_id = 0;
		if (!checkIfUserExists((String) params.get("username"))) {
			params.put("password", CryptoUtil.encrypt(CryptoEnum.AES256, (String) params.get("password")));
			last_id = DB.executeGetLastId("UserService_insertUser", params);
		}
		
		return last_id;
	}
	
	public Map<String, Object> getByCriteria(String criteria, Object criteriaValue) {
		List<Map<String, Object>> users = this.list(new HashMap<String, Object>(){{
			put(criteria, criteriaValue);
		}});
		
		Map<String, Object> out = new HashMap<>();
		if (!users.isEmpty()) {
			out = users.get(0);
		}
		
		return out;
	}
	
}
