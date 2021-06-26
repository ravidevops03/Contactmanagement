package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {

		model.addAttribute("title", "Dashboard - Smart Contact Manager");

		String userName = principal.getName();

		User userDetails = userRepository.getUserByUserName(userName);

		model.addAttribute("user", userDetails);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "Dashboard - Smart Contact Manager");

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());

		return "normal/add-contact-from";
	}

	// handler for add contact
	@RequestMapping(value = "/process-contact", method = RequestMethod.POST)
	public String addContact(@Valid @ModelAttribute("contact") Contact contact, Model model, HttpSession session,
			Principal principal, @RequestParam("profileImage") MultipartFile file) {

		try {

			String userName = principal.getName();

			User userDetails = userRepository.getUserByUserName(userName);

			if (file.isEmpty()) {

				System.out.println("Image is not uploaded!!!");
				contact.setImage("contact.png");

			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded!!!");
			}

			contact.setUser(userDetails);
			userDetails.getContacts().add(contact);
			this.userRepository.save(userDetails);
			System.out.println("userDetails" + userDetails);
			model.addAttribute("contact", new Contact());
			session.setAttribute("message", new Message("Successfully Contact added !!", "alert-success"));
			System.out.println("contact" + contact);

			return "normal/add-contact-from";

		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went wrong!!" + ex.getMessage(), "alert-danger"));
			return "normal/add-contact-from";
		}

	}

	// show contact details
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "View Contact - Smart Contact Manager");
		String userName = principal.getName();

		User userDetails = userRepository.getUserByUserName(userName);

//		currentPage-Page
//		Contact per page - 5 

		Pageable pageable = PageRequest.of(page, 10);

		Page<Contact> contacts = this.contactRepository.findContactByUser(userDetails.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";

	}

	// showing particular contact details

	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		model.addAttribute("title", "Contact Detials - Smart Contact Manager");
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);

		Contact contact = contactOptional.get();

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";

	}

	// showing particular contact details

	@RequestMapping("/delete/{cId}")
	public String deleteContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal,
			HttpSession session) {

		Contact contact = this.contactRepository.findById(cId).get();

		contact.setUser(null);
		this.contactRepository.delete(contact);
		System.out.print("contact deleted!!!!!");

		session.setAttribute("message", new Message("Contact deleted successfully !!", "alert-success"));

		return "redirect:/user/show_contacts/0";

	}

	@PostMapping("/update-contact/{cId}")
	public String updateContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal,
			HttpSession session) {

		model.addAttribute("title", "Update Contact Detials - Smart Contact Manager");
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);

		Contact contact = contactOptional.get();
		System.out.println("contact cid **:" + contact.getcId());
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		return "normal/update_detail";

	}

	// handler for add contact
	@RequestMapping(value = "/process-contact-update", method = RequestMethod.POST)
	public String updateContact(@ModelAttribute("contact") Contact contact, Model model, HttpSession session,
			Principal principal, @RequestParam("profileImage") MultipartFile file) {

		try {

			String userName = principal.getName();

			System.out.println("contact userName" + userName);
			System.out.println("contact cid:" + contact.getcId());
			System.out.println("contact email:" + contact.getEmail());
			System.out.println("contact name:" + contact.getName());
			System.out.println("contact sec name:" + contact.getSecondName());
			System.out.println("contact Phone:" + contact.getPhone());
			System.out.println("contact Work:" + contact.getWork());

			Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

//				Delete Old Photo

//				Update New Photo

				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

				System.out.println("Image is uploaded!!!");

			} else {
				contact.setImage(oldContactDetails.getImage());
			}

			User user = userRepository.getUserByUserName(userName);
			contact.setUser(user);
			this.contactRepository.save(contact);

			session.setAttribute("message", new Message("Successfully Contact updated !!", "alert-success"));
//			System.out.println("contact" + contact);

			return "redirect:/user/" + contact.getcId() + "/contact";

		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went wrong!!" + ex.getMessage(), "alert-danger"));
			return "redirect:/user/" + contact.getcId() + "/contact";
		}

	}
	
	
	// your Profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Your Profile - Smart Contact Manager");
	
		return "normal/profile";
		
	}

}
