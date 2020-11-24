package ct.wiremock.demo.service.auth;

import ct.wiremock.demo._global.Utils.JWTUtils;
import ct.wiremock.demo._global.Utils.JsonUtils;
import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo._global.enums.AuthMode;
import ct.wiremock.demo._global.service.GlobalService;
import ct.wiremock.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService extends GlobalService {
	@Autowired
	protected Config config;
	
	@Autowired
	protected JWTUtils jwtUtils;
	
	@Autowired
	private UserService userService;
	
	public String generatedJwt = "";
	
	public Object returnAuthDetails(Map<String, Object> bodyMap) {
		String username = "";
		String password = "";
		Object authDetails = returnUnauthorized();
		
		AuthMode authMode = AuthMode.MODE_UNAUTHORIZED;
		
		if (bodyMap != null && bodyMap.size() > 0) {
			String auth_mode = (String) bodyMap.get("auth_mode");
			username = String.valueOf(bodyMap.get("username"));
			password = String.valueOf(bodyMap.get("password"));
			
			if (auth_mode != null && auth_mode.toUpperCase().equals(AuthMode.MODE_SUPERADMIN.getText())) {
				if (username.equals(config.getProperty("superadmin_user")) && password.equals(config.getProperty("superadmin_pass"))) {
					//autentificare ca superadmin
					authMode = AuthMode.MODE_SUPERADMIN;
				}
			} else {
				String user_password = userService.returnUserPasswordDecrypted(username);
				if (user_password != null && user_password.equals(password)) {
					authMode = AuthMode.MODE_NORMAL;
				}
			}
		}
		
		if (!username.equals("") && !password.equals("") && authMode != AuthMode.MODE_UNAUTHORIZED) {
			authDetails = buildAuthDetails(authMode, username);
		}
		
		if (authDetails instanceof HashMap) {
			String decoded = jwtUtils.decodeJWT(generatedJwt);
			Map<Object, Object> map = JsonUtils.getMap(decoded);
			((HashMap) authDetails).put("user", map);
		}
		
		return authDetails;
	}
	
	private HashMap<String, Object> buildAuthDetails(AuthMode authMode, String username) {
		HashMap<String, Object> authDetails = new HashMap<>();
		generatedJwt = jwtUtils.generateJWT(username, authMode, null);
		authDetails.put("accessToken", generatedJwt);
		return authDetails;
	}
	
	private ResponseEntity returnUnauthorized() {
		return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
	}
	
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	public Principal getPrincipal() {
		Object authentication = getAuthentication();
		Principal principal = (Principal) authentication;
		return principal;
	}
}
