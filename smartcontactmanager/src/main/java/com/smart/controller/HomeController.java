package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler for register
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult results,
			@RequestParam(value = "agreement", defaultValue = "false") Boolean agreement, Model model,
			 HttpSession session) {

		try {
			
			if(results.hasErrors())
			{
				System.out.println("YHas errors." + results.toString() );
				model.addAttribute("user", user);
				return "signup";
			}

			if (!agreement) {
				System.out.println("You haven't agreed with Teams and conditions.");
				throw new Exception("You haven't agreed with Teams and conditions.");
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrlStr("default.jpg");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

			System.out.println("agreement" + agreement);
			System.out.println("user" + user);

			User result = this.userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

			return "signup";

		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + ex.getMessage(), "alert-danger"));
			return "signup";
		}

	}
	
	
	@RequestMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "SignIn - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "login";
	}

}
