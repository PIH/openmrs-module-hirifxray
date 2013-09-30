package org.openmrs.module.hirifxray.web.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.hirifxray.HirifxrayUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ParticipantController {

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
	}
    
    @RequestMapping("/module/hirifxray/participant.form")
    public String viewParticipant(ModelMap model,
					   @RequestParam(value="id", required=true) Integer id) {

		User currentUser = Context.getAuthenticatedUser();
		if (currentUser == null) {
			return "redirect:/login.htm";
		}

		Patient patient = Context.getPatientService().getPatient(id);
		model.addAttribute("patient", patient);

		return null;
    }

	@RequestMapping("/module/hirifxray/createParticipant.form")
	public String createParticipant(ModelMap model,
					   @RequestParam(value="identifier", required=true) String identifier,
					   @RequestParam(value="givenName", required=true) String givenName,
					   @RequestParam(value="familyName", required=true) String familyName,
					   @RequestParam(value="gender", required=true) String gender,
					   @RequestParam(value="birthdate", required=true) Date birthdate) {

		Patient p = new Patient();

		PersonName pn = new PersonName();
		pn.setGivenName(givenName);
		pn.setFamilyName(familyName);
		p.addName(pn);

		p.setGender(gender);
		p.setBirthdate(birthdate);

		PatientIdentifier pi = new PatientIdentifier();
		pi.setPatient(p);
		pi.setIdentifierType(HirifxrayUtil.getIdentifierType());
		pi.setIdentifier(identifier);
		p.addIdentifier(pi);

		p = Context.getPatientService().savePatient(p);

		return "redirect:/module/hirifxray/participant.form?id="+p.getPatientId();
	}

	@RequestMapping("/module/hirifxray/updateParticipant.form")
	public String updateParticipant(ModelMap model,
									@RequestParam(value="patientId", required=true) Integer patientId,
									@RequestParam(value="identifier", required=true) String identifier,
									@RequestParam(value="givenName", required=true) String givenName,
									@RequestParam(value="familyName", required=true) String familyName,
									@RequestParam(value="gender", required=true) String gender,
									@RequestParam(value="birthdate", required=true) Date birthdate) {

		Patient p = Context.getPatientService().getPatient(patientId);

		PersonName pn = p.getPersonName();
		pn.setGivenName(givenName);
		pn.setFamilyName(familyName);

		p.setGender(gender);
		p.setBirthdate(birthdate);

		PatientIdentifier pi = p.getPatientIdentifier();
		pi.setIdentifier(identifier);

		p = Context.getPatientService().savePatient(p);

		return "redirect:/module/hirifxray/participant.form?id="+p.getPatientId();
	}
}
