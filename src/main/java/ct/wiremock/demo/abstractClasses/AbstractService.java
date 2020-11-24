package ct.wiremock.demo.abstractClasses;

import ct.wiremock.demo._global.service.GlobalService;
import ct.wiremock.demo.interfaces.GlobalServiceInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractService extends GlobalService implements GlobalServiceInterface {
	public String DB_key = "whatever";
	public String mainTblAlias = "";
	
	public List<Map<String, Object>> list(Map<String, Object> filters) {
		Map<String, Object> _filters = new HashMap<>();
		String tbl_alias = (!mainTblAlias.isEmpty()) ? mainTblAlias + "." : "";
		if (filters != null) {
			for (Map.Entry<String, Object> entry : filters.entrySet()) {
				if (!String.valueOf(entry.getValue()).equals("")) {
					switch (entry.getKey()) {
						default: _filters.put(tbl_alias + entry.getKey(), filters.get(entry.getKey())); break;
					}
				}
			}
		}
		
		return DB.search(DB_key + "list", _filters);
	}
	
	public int insert(Map<String, Object> params) {
		return DB.executeGetLastId(DB_key + "insert", params);
	}
	
	public void update(Map<String, Object> params) {
		DB.executeUpdate(DB_key + "update", params);
	}
	
	@Override
	public Object getById(Map<String, Object> params) {
		return null;
	}
	
	@Override
	public int delete(Map<String, Object> params) {
		return 0;
	}
}
