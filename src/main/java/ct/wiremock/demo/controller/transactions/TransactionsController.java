package ct.wiremock.demo.controller.transactions;

import ct.wiremock.demo._global.Utils.JsonUtils;
import ct.wiremock.demo._global.Utils.Response;
import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo._global.controller.GlobalController;
import ct.wiremock.demo.service.transactions.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path= Routes.BASE_TRANSACTIONS)
public class TransactionsController extends GlobalController {
	@Autowired
	private TransactionsService transactionsService;
	
	@PostMapping(path = Routes.LIST)
	public @ResponseBody
	List list(@RequestBody String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		return transactionsService.list(map.get("query"));
	}
	
	@PostMapping(path = Routes.SAVE)
	public @ResponseBody
	HashMap<String, Object> save(@RequestBody String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		int last_id = transactionsService.insert(map.get("query"));
		Map<String, Object> out = new HashMap<>();
		out.put("created", false);
		if (last_id != 0) {
			out.put("created", true);
			out.put("last_id", last_id);
		}
		return Response.OK(out);
	}
}
