package ct.wiremock.demo.abstractClasses;

import ct.wiremock.demo._global.Utils.JsonUtils;
import ct.wiremock.demo._global.Utils.Response;
import ct.wiremock.demo._global.controller.GlobalController;
import ct.wiremock.demo.interfaces.GlobalControllerInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractController extends GlobalController implements GlobalControllerInterface {
	
	protected AbstractService abstractService;
	
	@Override
	public List list(String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		return abstractService.list(map.get("query"));
	}
	
	@Override
	public HashMap<String, Object> save(String body) {
		Map<String, Map<String, Object>> map = JsonUtils.getMapOfMap(body);
		int last_id = abstractService.insert(map.get("query"));
		Map<String, Object> out = new HashMap<>();
		out.put("created", false);
		if (last_id != 0) {
			out.put("created", true);
			out.put("last_id", last_id);
		}
		return Response.OK(out);
	}
}
