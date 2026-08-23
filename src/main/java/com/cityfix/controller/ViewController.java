package com.cityfix.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session != null && session.getAttribute("USER_ID") != null) {
            String role = (String) session.getAttribute("ROLE");
            if ("ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/dashboard";
        }
        addUserToModel(session, model);
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        if (session != null && session.getAttribute("USER_ID") != null) {
            return "redirect:/dashboard";
        }
        addUserToModel(session, model);
        return "register";
    }

    @GetMapping({"/dashboard", "/citizen/dashboard"})
    public String citizenDashboard(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "citizen_dashboard";
    }

    @GetMapping({"/report-issue", "/citizen/report"})
    public String reportIssue(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "report_issue";
    }

    @GetMapping({"/my-complaints", "/citizen/complaints"})
    public String myComplaints(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "my_complaints";
    }

    @GetMapping({"/complaints/{id}", "/citizen/complaints/{id}"})
    public String complaintDetails(@PathVariable("id") String id, HttpSession session, Model model) {
        addUserToModel(session, model);
        model.addAttribute("complaintIdParam", id);
        return "complaint_details";
    }

    @GetMapping({"/profile", "/citizen/profile"})
    public String profile(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "profile";
    }

    @GetMapping("/admin/login")
    public String adminLogin(HttpSession session, Model model) {
        if (session != null && "ADMIN".equals(session.getAttribute("ROLE"))) {
            return "redirect:/admin/dashboard";
        }
        addUserToModel(session, model);
        return "admin_login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "admin_dashboard";
    }

    @GetMapping("/admin/complaints")
    public String adminComplaints(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "admin_complaints";
    }

    @GetMapping("/admin/complaints/{id}")
    public String adminComplaintDetails(@PathVariable("id") String id, HttpSession session, Model model) {
        addUserToModel(session, model);
        model.addAttribute("complaintIdParam", id);
        return "admin_complaint_details";
    }

    @GetMapping("/admin/users")
    public String adminUsers(HttpSession session, Model model) {
        addUserToModel(session, model);
        return "admin_users";
    }

    private void addUserToModel(HttpSession session, Model model) {
        if (session != null && session.getAttribute("USER_ID") != null) {
            model.addAttribute("currentUserId", session.getAttribute("USER_ID"));
            model.addAttribute("currentUserName", session.getAttribute("USER_NAME"));
            model.addAttribute("currentUserEmail", session.getAttribute("USER_EMAIL"));
            model.addAttribute("currentUserRole", session.getAttribute("ROLE"));
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("isAdmin", "ADMIN".equals(session.getAttribute("ROLE")));
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("isAdmin", false);
        }
    }
}
