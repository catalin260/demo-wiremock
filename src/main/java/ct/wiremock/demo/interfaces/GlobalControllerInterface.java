package ct.wiremock.demo.interfaces;

import ct.wiremock.demo._global.config.Routes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

public interface GlobalControllerInterface {
	@PostMapping(path = Routes.LIST)
	@ResponseBody
	Object list(@RequestBody String body);
	
	@PostMapping (path = Routes.SAVE)
	@ResponseBody
	HashMap<String, Object> save(@RequestBody String body);
}
