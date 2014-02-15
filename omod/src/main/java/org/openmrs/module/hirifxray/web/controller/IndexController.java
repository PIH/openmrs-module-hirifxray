package org.openmrs.module.hirifxray.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.hirifxray.HirifMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class IndexController {
    
    @RequestMapping("/index.htm")
    public String viewIndex(ModelMap model,
							@RequestParam(value="identifier", required=false) String identifier) {

		User currentUser = Context.getAuthenticatedUser();
		if (currentUser == null) {
			return "redirect:/login.htm";
		}

		if (StringUtils.isNotEmpty(identifier)) {
			List<Patient> patients = Context.getPatientService().getPatients(identifier);
			model.addAttribute("patients", patients);

			if (patients.isEmpty()) {
				PatientIdentifierType pit = HirifMetadata.getIdentifierType();
				String format = pit.getFormat();
				if (StringUtils.isNotEmpty(format)) {
					Pattern pattern = Pattern.compile(format);
					Matcher matcher = pattern.matcher(identifier);
					if (!matcher.matches()) {
						model.addAttribute("invalidIdentifierFormat", pit.getFormatDescription());
					}
				}
			}
		}

		model.addAttribute("identifier", StringUtils.isEmpty(identifier) ? "HRP" : identifier);

    	return "module/hirifxray/index";
    }
}
