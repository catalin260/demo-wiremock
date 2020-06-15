package ct.wiremock.demo._global.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ct.wiremock.demo._global.service.TomcatServerConfigService;

@Configuration
public class TomcatServerConfig extends TomcatServerConfigService {

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = null;
		if (checkSSLConfig.isSSLEnabled() == 0) {
			tomcat = new TomcatServletWebServerFactory();
		} else {
			tomcat = new TomcatServletWebServerFactory() {
				@Override
				protected void postProcessContext(Context context) {
					SecurityConstraint securityConstraint = new SecurityConstraint();
					securityConstraint.setUserConstraint("CONFIDENTIAL");
					SecurityCollection collection = new SecurityCollection();
					collection.addPattern("/*");
					securityConstraint.addCollection(collection);
					context.addConstraint(securityConstraint);
				}
			};
			tomcat.addAdditionalTomcatConnectors(getHttpConnector());
		}
		
		return tomcat;
	}

	private Connector getHttpConnector() {
		String http_port_yml_key = "server.http-port";
		
		int http_port = (config.getProperty(http_port_yml_key) != null
				                 && !config.getProperty(http_port_yml_key).equals("")
				                 && !config.getProperty(http_port_yml_key).equals(http_port_yml_key))
				                ? Integer.parseInt(config.getProperty(http_port_yml_key)) : 10001;

		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(http_port);
		connector.setSecure(false);
		connector.setRedirectPort(Integer.parseInt(config.getProperty("server.port")));
		return connector;
	}
}
