package ct.wiremock.demo.controller.user;

import ct.wiremock.demo._global.config.Routes;
import ct.wiremock.demo.abstractClasses.AbstractController;
import ct.wiremock.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(path= Routes.BASE_USER)
public class UserController extends AbstractController {
	
	@Autowired
	protected UserService userService;
	
	@GetMapping(path = Routes.USER_ME)
	public ResponseEntity get() {
		return userService.get();
	}
	
	@PostConstruct
	private void initService() {
		this.abstractService = userService;
	}
}
