package ct.wiremock.demo._global.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtils {
	public static List<String> getSQLBindings(String s) {
		String[] matchesRemove = new String[]{"MI","SS","HH", "mi","ss","hh"};
//		Pattern pattern = Pattern.compile(":[a-zA-Z0-9_]+");
		Pattern pattern = Pattern.compile("[^:](:[a-zA-Z0-9_]+)");
		Matcher matcher = pattern.matcher(s);
		
		List<String> listMatches = new ArrayList<>();
		
		while(matcher.find()) {
			listMatches.add(matcher.group(0).split(":")[1]);
		}
		
		listMatches.removeAll(Arrays.asList(matchesRemove));
		return listMatches;
	}
}
