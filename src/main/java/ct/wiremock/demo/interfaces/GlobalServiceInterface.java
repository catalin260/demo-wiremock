package ct.wiremock.demo.interfaces;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

public interface GlobalServiceInterface {
	Object getById(Map<String, Object> params);
	int insert(Map<String, Object> params);
	void update(Map<String, Object> params);
	int delete(Map<String, Object> params);
	List list(Map<String, Object> filters);
	
	@PostConstruct
	void initServiceData();
}
