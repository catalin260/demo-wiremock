package ct.wiremock.demo._global.service;

import ct.wiremock.demo._global.config.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalService {
	@Autowired
	protected DB DB;
	
}
