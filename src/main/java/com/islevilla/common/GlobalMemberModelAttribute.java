package com.islevilla.common;

import com.islevilla.lai.members.model.Members;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalMemberModelAttribute {

    @ModelAttribute
    public void addMemberToModel(HttpSession session, Model model) {
        Members member = (Members) session.getAttribute("member");
        if (member != null) {
            model.addAttribute("member", member);
        }
    }
} 