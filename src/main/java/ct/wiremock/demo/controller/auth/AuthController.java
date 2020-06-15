package ct.wiremock.demo.controller.auth;

import ct.wiremock.demo._global.Utils.JsonUtils;
import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo._global.controller.GlobalController;
import ct.wiremock.demo.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path= Routes.BASE_AUTH)
public class AuthController extends GlobalController {
	
	@Autowired
	protected AuthService authService;
	
	@PostMapping(path = Routes.LOGIN)
	public @ResponseBody
	Object login(@RequestBody String body) {
		Map<String, Object> bodyMap = new HashMap<>();
		if (body != null && !body.equals("")) {
			bodyMap = JsonUtils.getMap(body);
		}
		
		Object authDetails = authService.returnAuthDetails(bodyMap);
		return authDetails;
	}
}
