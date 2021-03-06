package com.ub.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ub.model.AppUser;
import com.ub.model.Role;
import com.ub.model.UserRole;
import com.ub.repository.RoleRepository;
import com.ub.repository.UserRepository;
import com.ub.repository.UserRoleRepository;
import com.ub.utils.EncrytedPasswordUtils;
import com.ub.utils.WebUtils;

@Controller
public class MainController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
//	@GetMapping(path="/")
//	public RedirectView index() {
//		return new RedirectView("/index.html");
//	}
	
	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
    public String welcomePage(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("message", "This is welcome page!");
        return "welcomePage";
    }
 
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(Model model, Principal principal) {
         
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
         
        return "adminPage";
    }
    
 // Show Register page.
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String viewRegister(Model model) {
    	AppUser appUser = new AppUser();
	
    	model.addAttribute("appUser", appUser);
    
    	return "register";
    }
    
	@PostMapping("/register")
	public String createUser(AppUser appUser) {
		appUser.setId(Long.MAX_VALUE);
		appUser.setPassword(EncrytedPasswordUtils.encrytePassword(appUser.getPassword()));
		AppUser savedUser = null;
		int i = 0;
		while (true) {
			try {
				savedUser = userRepository.save(appUser);
			} catch (Exception e) {
				i++;
				if (i == 5) break;
			}
		}

		Optional<Role> role = roleRepository.findById(2L);
		UserRole userRole = new UserRole();
		userRole.setId(Long.MAX_VALUE);
		userRole.setAppUser(savedUser);
		userRole.setAppRole(role.get());
		userRoleRepository.save(userRole);

		return "loginPage";
	}
  
 
 
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(Model model) {
 
        return "loginPage";
    }
 
    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
    public String logoutSuccessfulPage(Model model) {
        model.addAttribute("title", "Logout");
        return "logoutSuccessfulPage";
    }
 
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String userInfo(Model model, Principal principal) {
 
        // After user login successfully.
        String userName = principal.getName();
 
        System.out.println("User Name: " + userName);
 
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
        String userInfo = WebUtils.toString(loginedUser);
        model.addAttribute("userInfo", userInfo);
 
        return "userInfoPage";
    }
 
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Model model, Principal principal) {
 
        if (principal != null) {
            User loginedUser = (User) ((Authentication) principal).getPrincipal();
 
            String userInfo = WebUtils.toString(loginedUser);
 
            model.addAttribute("userInfo", userInfo);
 
            String message = "Hi " + principal.getName() //
                    + "<br> You do not have permission to access this page!";
            model.addAttribute("message", message);
 
        }
 
        return "403Page";
    }
	
	
}
