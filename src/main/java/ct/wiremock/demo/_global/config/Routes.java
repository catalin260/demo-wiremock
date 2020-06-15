package ct.wiremock.demo._global.config;

public class Routes {
	public static final String BASE_WEB =                   "/web";
	public static final String BASE_AUTH =                  BASE_WEB + "/auth";
	public static final String BASE_ACCOUNTS =              BASE_WEB + "/accounts";
	public static final String BASE_TRANSACTIONS =          BASE_WEB + "/transactions";
	
	public static final String BASE_USER =                  BASE_WEB + "/user";
	
	public static final String USER_ME =                    "/me";
	public static final String LOGIN =                      "/login";
	public static final String LIST =                       "/list";
	public static final String GET_BY_ID =                  "/getById";
	public static final String SAVE =                       "/save";
	public static final String DELETE =                     "/delete";
	public static final String UPDATE =                     "/update";
}
