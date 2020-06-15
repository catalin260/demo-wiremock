package ct.wiremock.demo.interfaces;

import java.util.List;
import java.util.Map;

public interface GenericServiceInterface {
	Object getById(Map<String, Object> params);
	int insert(Map<String, Object> params);
	void update(Map<String, Object> params);
	int delete(Map<String, Object> params);
	List list(Map<String, Object> filters);
	
}
