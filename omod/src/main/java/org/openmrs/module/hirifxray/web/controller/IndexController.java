package org.openmrs.module.hirifxray.web.controller;

import javax.servlet.http.HttpSession;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    
    @RequestMapping("/index.htm") 
    public String viewIndex(ModelMap model, HttpSession session) {
    	User currentUser = Context.getAuthenticatedUser();
    	if (currentUser == null) {
    		return "redirect:/login.htm";
    	}
    	return null;
    }
}
