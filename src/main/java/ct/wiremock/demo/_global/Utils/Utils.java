package ct.wiremock.demo._global.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
	public static boolean isInteger(String s) {
		return isInteger(s,10);
	}
	
	public static boolean isInteger(String s, int radix) {
		if (s.length() > 1 && s.startsWith("0")) return false;
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}
	
	public static boolean isBoolean(String s) {
		boolean out = false;
		if (s.equals("true") || s.equals("false")) {
			out = true;
		}
		
		return true;
	}
	
	public static String convertObjectToJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String principalJson = null;
		
		if (object != null) {
			try {
				principalJson = mapper.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return principalJson;
	}
	
	public static <T> Set<T> convertListToSet(List<T> list)
	{
		// create a set from the List
		return new HashSet<>(list);
	}
}
