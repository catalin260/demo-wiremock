package ct.wiremock.demo._global.Utils;

import org.springframework.lang.Nullable;

import java.util.HashMap;

public class Response {
//	public static HashMap<String, Object> returnResponse(Object data, boolean withDataKey) {
//		HashMap<String, Object> resp = new HashMap<>();
//		if (withDataKey) resp.put("data", data); else resp = (HashMap<String, Object>) data;
//		return resp;
//	}
	
	public static HashMap<String, Object> OK(@Nullable Object data) {
		HashMap<String, Object> resp = new HashMap<>();
		resp.put("status", 200);
		resp.put("data", data);
		resp.put("message", "Success");
		
		return resp;
	}
	
	public static HashMap<String, Object> ERROR(@Nullable Object data) {
		HashMap<String, Object> resp = new HashMap<>();
		resp.put("status", 501);
		resp.put("data", data);
		resp.put("message", "Error!");
		
		return resp;
	}
	
	/*public static HashMap<String, Object> returnResponse(int status, Object data, String message) {
		HashMap<String, Object> resp = new HashMap<>();
		resp.put("status", status);
		resp.put("data", data);
		resp.put("message", message);
		
		return resp;
	}*/
}
