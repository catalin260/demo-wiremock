package ct.wiremock.demo._global.Utils;

import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo._global.enums.AuthMode;
import ct.wiremock.demo._global.enums.Roles;
import ct.wiremock.demo.service.auth.AuthService;
import ct.wiremock.demo.service.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

@Service
public class JWTUtils {
	
	@Autowired
	private Config config;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;
	
	private ResourceLoader resourceLoader;
	
	@Autowired
	public JWTUtils(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	private String[] keysToRemoveFromReceivedToken = new String[]{
			"iss",
			"jti",
			"aud",
			"exp",
			"iat",
			"nbf",
			"nbf",
			"_target"
	};
	
	public String getAccessToken() {
		String access_token = null;
		String principalJson = getPrincipalJson();
		
		if (principalJson != null && !principalJson.equals("")) {
			Map<String, Object> principalMap = JsonUtils.getMap(principalJson);
			Map<String, Object> principalDetails = (Map<String, Object>) principalMap.get("details");
			access_token = (principalDetails.get("tokenValue") != null) ? String.valueOf(principalDetails.get("tokenValue")) : null;
		}
		
		return access_token;
	}
	
	private String getPrincipalJson() {
		return Utils.convertObjectToJson(authService.getPrincipal());
	}
	
	private String _decode(String access_token) {
		String body = null;
		String[] split_string = access_token.split("\\.");
		
		if (split_string.length == 3) {
			String base64EncodedHeader = split_string[0];
			String base64EncodedBody = split_string[1];
			String base64EncodedSignature = "";
			if (split_string.length == 3) base64EncodedSignature = split_string[2];
			
			Base64 base64Url = new Base64(true);
			String header = new String(base64Url.decode(base64EncodedHeader));
			body = new String(base64Url.decode(base64EncodedBody));
			
			if (!JsonUtils.isJsonValid(body)) {
				body = null;
				//@TODO: de sters linia urmatoare dupa ce termin testarea
				body = header;
			}
		}
		
		if (body == null) {
			logErrorJWT(access_token);
		}
		
		return body;
	}
	
	private void logErrorJWT(String access_token) {
		String errorMessage = "Received access_token from [" + config.getProperty("security.oauth2.client.access-token-uri") + "] is INVALID. Check for signature\n\n";
		errorMessage += "Received access_token: \n";
		errorMessage += access_token + "\n\n";
		System.out.println(errorMessage);
	}
	
	public Map<String, Object> getJWTDecodedMap(String access_token) {
		String decodedJwtJson = decodeJWT(access_token);
		Map<String, Object> decodedJwtMap = JsonUtils.getMap(decodedJwtJson);
		
		return decodedJwtMap;
	}
	
	public Map<String, Object> getJWTDecodedMap() {
		String decodedJwtJson = decodeJWT(this.getAccessToken());
		Map<String, Object> decodedJwtMap = JsonUtils.getMap(decodedJwtJson);
		
		return decodedJwtMap;
	}
	
	public String getCurrentUserId() {
		return String.valueOf(getJWTDecodedMap().get("user_id"));
	}
	
	public String decodeJWT(){
		String access_token = getAccessToken();
		String _decodedJwt = null;
		if (access_token != null) {
			_decodedJwt = _decode(access_token);
		}
		return _decodedJwt;
	}
	
	public String decodeJWT(String jwtToken){
		return _decode(jwtToken);
	}
	
	private Key _getSigningCertificate() {
		String keyStorePassword = config.getProperty("security.jwt.key-store-password");
		
		KeyStore ks  = getKeyStoreInstance();
		InputStream inputStream = getKeyStoreInputStream();
		
		try {
			ks.load(inputStream, keyStorePassword.toCharArray());
		} catch (IOException | NoSuchAlgorithmException | CertificateException e) {
			e.printStackTrace();
		}
		
		return _getSigningCertificate(ks, keyStorePassword);
	}
	
	private Key _getSigningCertificate(KeyStore ks, String keyStorePassword) {
		Key key = null;
		String keyPairAlias = config.getProperty("security.jwt.key-pair-alias");
		
		try {
			key = ks.getKey(keyPairAlias, keyStorePassword.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	public String generateJWT(@Nullable String username, @Nullable AuthMode authMode, @Nullable Map<String, Object> decodedJWT) {
		DateTime currentDateTime = new DateTime();
		Date expirationDate = currentDateTime.plusHours(getValidHours()).toDate();
		
		String jti = RandomStringUtils.randomAlphanumeric(55);
		Map<String, Object> claims;
		
		if (decodedJWT == null) {
			claims = getClaims(username, authMode);
		} else {
			claims = getClaims(decodedJWT);
		}
		
		return Jwts.builder()
				.setClaims(claims)
				.setExpiration(expirationDate)
				.setIssuedAt(currentDateTime.toDate())
				.setId(jti)
				.signWith(SignatureAlgorithm.RS256, _getSigningCertificate())
				.compact();
	}
	
	private Map<String, Object> getClaims(Map<String, Object> decodedJWT) {
		String clientId = config.getProperty("security.oauth2.client.client-id");
		Map<String, Object> claims = new HashMap<>();
		claims.put("client_id", clientId);
		
		if (decodedJWT != null && decodedJWT.size() > 0) {
			for (Map.Entry<String, Object> entry : decodedJWT.entrySet()) {
				claims.put(entry.getKey(), entry.getValue());
			}
			
			for (String key : keysToRemoveFromReceivedToken) {
				claims.remove(key);
			}
		}
		
		return claims;
	}
	
	private Map<String, Object> getClaims(String username, AuthMode authMode) {
		String clientId = config.getProperty("security.oauth2.client.client-id");
		Map<String, Object> claims = new HashMap<>();
		claims.put("client_id", clientId);
		
		if (authMode != null) {
			switch (authMode) {
				case MODE_SUPERADMIN:
					appendClaimValuesByUid(null, claims);
					break;
				case MODE_NORMAL:
					appendClaimValuesByUid(username, claims);
					break;
			}
		}
		return claims;
	}
	
	private void appendClaimValuesByUid(String username, Map<String, Object> claims) {
		String otherScopes = config.getProperty("security.oauth2.client.other-scopes");
		otherScopes += "," + config.getProperty("security.oauth2.client.scope");
		List<String> clientScope = new ArrayList<>();
		
		claims.put("authorities", new String[]{Roles.ROLE_SUPERADMIN.getText()});
		claims.put("username", config.getProperty("superadmin_user"));
		clientScope.add(config.getProperty("superadmin_role"));
		claims.put(config.getProperty("security.oauth2.client.scope"), clientScope);
		claims.put("scope", otherScopes.split(","));
		claims.put("firstname", config.getProperty("speradmin_firstname"));
		claims.put("lastname", config.getProperty("superadmin_lastname"));
		claims.put("mail", config.getProperty("superadmin_mail"));
		
		if (username != null) {
			Map<String, Object> userValues = userService.getByCriteria("user_username", username);
			claims.put("authorities", new String[]{Roles.ROLE_USER.getText()});
			claims.put("username", userValues.get("username"));
			claims.put("user_id", userValues.get("user_id"));
			claims.put("firstname", userValues.get("firstname"));
			claims.put("lastname", userValues.get("lastname"));
			claims.put("mail", userValues.get("mail"));
			if (userValues.get("ir_nume") != null) {
				clientScope.clear();
				clientScope.add(String.valueOf(userValues.get("ir_nume")));
				claims.put(config.getProperty("security.oauth2.client.scope"), clientScope);
			}
		}
	}
	
	private int getValidHours() {
		String _valid_hours = config.getProperty("security.jwt.valid-hours");
		int valid_hours = 8;
		if (_valid_hours != null && !_valid_hours.equals("security.jwt.valid-hours")) {
			valid_hours = Integer.parseInt(_valid_hours);
		}
		
		return valid_hours;
	}
	
	private KeyStore getKeyStoreInstance() {
		KeyStore ks  = null;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		
		return ks;
	}
	
	private Resource getKeyStoreFile() {
		Resource resourceFile;
//		File file = null;
//		file = ResourceUtils.getFile(config.getProperty("security.jwt.key-store"));
		resourceFile = resourceLoader.getResource(config.getProperty("security.jwt.key-store"));
		
		return resourceFile;
	}
	
	private InputStream getKeyStoreInputStream() {
		InputStream inputStream = null;
		try {
//			inputStream = new FileInputStream(getKeyStoreFile());
			inputStream = getKeyStoreFile().getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return inputStream;
	}
	
	private Key getSigningCertificate(KeyStore ks, String keyStorePassword) {
		Key key = null;
		String keyPairAlias = config.getProperty("security.jwt.key-pair-alias");
		
		try {
			key = ks.getKey(keyPairAlias, keyStorePassword.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		
		return key;
	}
}
