package ct.wiremock.demo._global.security;

import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo._global.service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckSSLConfig extends GlobalService {
	@Autowired
	protected Config config;
	
	private String ssl_yml_key = "server.ssl.enabled";
	
	/**
	 * 1: enabled
	 * 0: not enabled
	 * @return
	 */
	public int isSSLEnabled() {
		int enabled = 0;
		if (Boolean.parseBoolean(config.getProperty(ssl_yml_key)) == true || config.getProperty(ssl_yml_key).equals(ssl_yml_key)) {
			enabled = 1;
		}
		
		return enabled;
	}
}
