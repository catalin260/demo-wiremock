package ct.wiremock.demo._global.service;

import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo._global.security.CheckSSLConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TomcatServerConfigService {
	@Autowired
	protected CheckSSLConfig checkSSLConfig;
	
	@Autowired
	protected Config config;
}
