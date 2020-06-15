package ct.wiremock.demo._global.Utils;

import com.google.gson.Gson;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
	private static final Gson gson = new Gson();
	
	public static List getList(String JSON_SOURCE){
		List result;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			result = mapper.readValue(JSON_SOURCE, List.class);
			
		} catch (IOException e) {
			result = new ArrayList();
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static Map getMap(String JSON_SOURCE){
		Map result;
		try {
			result = new ObjectMapper().readValue(JSON_SOURCE, HashMap.class);
		} catch (IOException | NullPointerException ex) {
			result = new HashMap<>();
			System.out.println(ex.getMessage());
		}
		return result;
	}
	
	public static Map<String, Map<String, Object>> getMapOfMap(String JSON_SOURCE){
		Map result;
		try {
			result = new ObjectMapper().readValue(JSON_SOURCE, HashMap.class);
		} catch (IOException ex) {
			result = new HashMap<>();
			System.out.println(ex.getMessage());
		}
		return result;
	}
	
	public static boolean isJsonValid(String JSON_SOURCE) {
		boolean isValid = false;
		try {
			gson.fromJson(JSON_SOURCE, Object.class);
			isValid = true;
		} catch(com.google.gson.JsonSyntaxException ex) {
		
		}
		
		return isValid;
	}
}
