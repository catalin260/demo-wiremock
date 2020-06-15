package ct.wiremock.demo.controller.user;

import ct.wiremock.demo._global.Utils.JsonUtils;
import ct.wiremock.demo._global.Utils.Response;
import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo._global.controller.GlobalController;
import ct.wiremock.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path= Routes.BASE_USER)
public class UserController extends GlobalController {
	
	@Autowired
	protected UserService userService;
	
	@GetMapping(path = Routes.USER_ME)
	public ResponseEntity get() {
		return userService.get();
	}
	
	@PostMapping(path = Routes.LIST)
	public @ResponseBody
	List list(@RequestBody String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		return userService.list(map.get("query"));
	}
	
	@PostMapping(path = Routes.SAVE)
	public @ResponseBody
	HashMap<String, Object> save(@RequestBody String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		int last_id = userService.insert(map.get("query"));
		Map<String, Object> out = new HashMap<>();
		out.put("created", false);
		if (last_id != 0) {
			out.put("created", true);
			out.put("last_id", last_id);
		}
		return Response.OK(out);
	}
}
