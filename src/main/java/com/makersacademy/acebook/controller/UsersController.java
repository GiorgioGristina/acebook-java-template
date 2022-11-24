package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Authority;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.AuthoritiesRepository;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UsersController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthoritiesRepository authoritiesRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;

    @GetMapping("/users/new")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @GetMapping("/login")
    public String signin(Model model) {
        return "/logon";
    }

    @GetMapping("/errorUser")
    public String errorUsername(Model model) {
        return "/errorUser";
    }


    @PostMapping("/users")
    public RedirectView signup(@ModelAttribute User user) {
        Optional<User> username = userRepository.findByUsername(user.getUsername());

        if (username.isPresent() == true) {
            return new RedirectView("/errorUser");
        }
        else {
            userRepository.save(user);
            System.out.println("we are in the post request");
            System.out.println(user);
    
            Authority authority = new Authority(user.getUsername(), "ROLE_USER");
            authoritiesRepository.save(authority);
            
        }

        return new RedirectView("/login");
    }

    @GetMapping("/users/{id}")
    public String profilePage(@PathVariable Long id, Model model) {
        ArrayList<Post> relatedPosts = new ArrayList<>();

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Long currentUserId = userRepository.findByUsername(currentUsername).get().getId();

        Optional<User> user = userRepository.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("userId", user.get().getId());
        model.addAttribute("currentUserId", currentUserId);

        Iterable<Post> posts = postRepository.findAll();

        for (Post p: posts) {
            if (p.getUser_id() == id) {
                relatedPosts.add(p);
            }
        }

        List<Post> reversedPosts = new ArrayList<>();

        for (int i = relatedPosts.size() -1 ; i >= 0; i--) {
            reversedPosts.add(relatedPosts.get(i));
         }

        model.addAttribute("username", user.get().getUsername());
        model.addAttribute("posts", reversedPosts);
        model.addAttribute("post", new Post());
        return "/users/profile_page";
    }
}
