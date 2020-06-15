package ct.wiremock.demo._global.controller;

import ct.wiremock.demo._global.Utils.JWTUtils;
import ct.wiremock.demo._global.config.Config;
import ct.wiremock.demo._global.config.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalController {
	@Autowired
	protected Environment environment;
	
	@Autowired
	protected Config config;
	
	@Autowired
	protected JWTUtils jwtUtils;
}
