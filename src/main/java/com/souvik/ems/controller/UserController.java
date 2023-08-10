package com.souvik.ems.controller;

import com.souvik.ems.dto.UserDTO;
import com.souvik.ems.model.User;
import com.souvik.ems.dto.SearchQuery;
import com.souvik.ems.model.Role;
import com.souvik.ems.service.RoleService;
import com.souvik.ems.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@AllArgsConstructor
@Controller
public class UserController {

    private static final int PAGE_SIZE = 10;

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        return getAllUser(model, 1, "firstName", "asc");
    }

    @GetMapping("/{pageNo}")
    public String getAllUser(Model model,
                             @PathVariable(value = "pageNo") int pageNo,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortOrder") String sortOrder) {

        Page<User> page = userService.getAllUser(pageNo, PAGE_SIZE, sortField, sortOrder);
        List<User> users = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalRows", page.getTotalElements());
        model.addAttribute("users", users);
        model.addAttribute("search", new SearchQuery());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");
        model.addAttribute("username", userService.getLoggedInUserUsername());

        return "index";
    }

    @GetMapping("/add")
    public String showUserPage(Model model) {
        model.addAttribute("heading", "Add New User");
        model.addAttribute("user", new UserDTO());
        model.addAttribute("username", userService.getLoggedInUserUsername());
        return "user";
    }

    @GetMapping("/search")
    public String search(Model model, @ModelAttribute(name = "search") SearchQuery searchQuery) {
        if (searchQuery.getFirstName().equals("") && searchQuery.getLastName().equals("") && searchQuery.getEmail().equals(""))
            return "redirect:/";
        return search(model, 1, searchQuery.getFirstName(), searchQuery.getLastName(), searchQuery.getEmail(), "firstName", "asc");
    }

    @GetMapping("/search/{pageNo}")
    public String search(Model model,
                         @PathVariable("pageNo") Integer pageNo,
                         @RequestParam(name = "firstName") String firstName,
                         @RequestParam(name = "lastName") String lastName,
                         @RequestParam(name = "email") String email,
                         @RequestParam(name = "sortField") String sortField,
                         @RequestParam(name = "sortOrder") String sortOrder) {

        SearchQuery searchObject = new SearchQuery();
        searchObject.setFirstName(firstName);
        searchObject.setLastName(lastName);
        searchObject.setEmail(email);

        Page<User> page = userService.search(firstName, lastName, email, pageNo, PAGE_SIZE, sortField, sortOrder);
        List<User> users = page.getContent();
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalRows", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("users", users);
        model.addAttribute("search", searchObject);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", sortOrder.equals("asc") ? "desc" : "asc");
        model.addAttribute("username", userService.getLoggedInUserUsername());

        return "search";
    }

    @PostMapping("/save")
    public String saveUser(Model model, @ModelAttribute("user") UserDTO userDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        if (referer.contains("add")) {
            if (userService.existsByEmail(userDTO.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email ID: " + userDTO.getEmail() + " already exists!");
                return "redirect:" + referer;
            } else if (userDTO.getPassword() == null || userDTO.getPassword().equals("")) {
                redirectAttributes.addFlashAttribute("error", "Please enter password!");
                return "redirect:" + referer;
            } else {
                User user = new User();
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setEmail(userDTO.getEmail());
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                user.setSalary(userDTO.getSalary());
                user.setRoles(new ArrayList<Role>());
//                userService.saveUser(user);
                Role role = roleService.findByName(userDTO.getType()).get();
                List<Role> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("success", "User added successfully");
                return "redirect:/";
            }
        }
        // else for update
        User user = userService.getUser(userDTO.getId());
        // check if updated email already is assigned to someone else
        if (!userDTO.getEmail().equals(user.getEmail()) && userService.existsByEmail(userDTO.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email ID: " + userDTO.getEmail() + " already exists!");
            return "redirect:" + referer;
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() == null || userDTO.getPassword().length() == 0)
            user.setPassword(userService.getUser(userDTO.getId()).getPassword());
        else
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setSalary(userDTO.getSalary());
        Role role = roleService.findByName(userDTO.getType()).get();
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "User with email ID: " + user.getEmail() + " updated successfully");
        return "redirect:" + referer;
    }

    @GetMapping("/update/{id}")
    public String showUpdateUserPage(@PathVariable UUID id, Model model, HttpServletRequest request) {
        User user = userService.getUser(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(null);
        userDTO.setSalary(user.getSalary());
        final Role ROLE_ADMIN = roleService.findByName("ADMIN").get();
        if (user.getRoles().contains(ROLE_ADMIN))
            userDTO.setType("ADMIN");
        else
            userDTO.setType("USER");
        model.addAttribute("heading", "Update User");
        model.addAttribute("passwordHelp", "dummy_text");
        model.addAttribute("user", userDTO);
        model.addAttribute("username", userService.getLoggedInUserUsername());
        return "user";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        User user = userService.getUser(id);
        user.setRoles(new ArrayList<>());
        userService.saveUser(user);
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("error", "User deleted with email ID: " + user.getEmail());
        return "redirect:/";
    }

}