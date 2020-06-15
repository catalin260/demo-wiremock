package ct.wiremock.demo._global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySources({
		@PropertySource(value = "classpath:sqls.web.yml", factory = YamlPropertySourceFactory.class),
		@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class),
		@PropertySource(value = "classpath:global.yml", factory = YamlPropertySourceFactory.class),
		@PropertySource(value = "classpath:schedule.yml", factory = YamlPropertySourceFactory.class)
})
public class Config {
	
	@Autowired
	private Environment env;
	
	public String getProperty(String pPropertyKey) {
		String property = env.getProperty(pPropertyKey);
		return (property == null) ? pPropertyKey : property;
	}
}
