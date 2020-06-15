package ct.wiremock.demo._global.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class SuccessAuthenticationHandler implements AuthenticationSuccessHandler {
	public SuccessAuthenticationHandler(){
	
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                    HttpServletResponse response, Authentication auth) throws IOException, ServletException {
		HttpSession session = request.getSession();
		User user =   (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String redirect = "";
		if(user != null){
			session.setAttribute("username", user.getUsername());
			if(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
						|| user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")))
				redirect = "ro/ct.wiremock.demo/wiremock/admin/";
			else if(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_YOUR_ROLE")))
				redirect = "yourrole/";
		}
		if(redirect.isEmpty())
			redirect = "signin";
		
		response.sendRedirect(redirect);
	}
	
}
