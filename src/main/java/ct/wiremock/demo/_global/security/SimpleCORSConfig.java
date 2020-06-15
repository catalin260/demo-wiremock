package ct.wiremock.demo._global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class SimpleCORSConfig {
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		String port = "4201";
		
		return new WebMvcConfigurer() {
			
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/").setViewName("forward:/res/admin/angular/index.html");
				registry.addViewController("/web").setViewName("forward:/res/admin/angular/index.html");
			}
			
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(
								"http://localhost:"             + port
						)
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}
