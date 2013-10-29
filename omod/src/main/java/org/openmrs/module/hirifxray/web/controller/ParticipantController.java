package org.openmrs.module.hirifxray.web.controller;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.hirifxray.HirifxrayUtil;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ConceptEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ParticipantController {

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
	}
    
    @RequestMapping("/module/hirifxray/participant.form")
    public String viewParticipant(ModelMap model,
					   @RequestParam(value="id", required=true) Integer id,
					   @RequestParam(value="type", required=false) String type) {

		User currentUser = Context.getAuthenticatedUser();
		if (currentUser == null) {
			return "redirect:/login.htm";
		}

		Patient patient = Context.getPatientService().getPatient(id);
		model.addAttribute("patient", patient);

		Map<String, Concept> xraysConcepts = new LinkedHashMap<String, Concept>();
		xraysConcepts.put("enrollmentXray", HirifxrayUtil.getEnrollmentXrayConcept());
		xraysConcepts.put("visit9Xray", HirifxrayUtil.getVisit9XrayConcept());
		xraysConcepts.put("visit13Xray", HirifxrayUtil.getVisit13XrayConcept());
		xraysConcepts.put("earlyTerminationXray", HirifxrayUtil.getEarlyTerminationXrayConcept());
		model.addAttribute("xraysConcepts", xraysConcepts);

		Map<String, Obs> xrays = new LinkedHashMap<String, Obs>();
		xrays.put("enrollmentXray", HirifxrayUtil.getEnrollmentXray(patient));
		xrays.put("visit9Xray", HirifxrayUtil.getVisit9Xray(patient));
		xrays.put("visit13Xray", HirifxrayUtil.getVisit13Xray(patient));
		xrays.put("earlyTerminationXray", HirifxrayUtil.getEarlyTerminationXray(patient));
		model.addAttribute("xrays", xrays);

		model.addAttribute("type", type);

		return null;
    }

	@RequestMapping("/module/hirifxray/createParticipant.form")
	public String createParticipant(ModelMap model,
					   @RequestParam(value="identifier", required=true) String identifier,
					   @RequestParam(value="gender", required=true) String gender) throws Exception {

		Patient p = new Patient();

		PersonName pn = new PersonName();
		pn.setGivenName("XXXX");
		pn.setFamilyName("XXXX");
		p.addName(pn);

		p.setGender(gender);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		p.setBirthdate(df.parse("1900-01-01"));

		PatientIdentifier pi = new PatientIdentifier();
		pi.setPatient(p);
		pi.setLocation(HirifxrayUtil.getUnknownLocation());
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
									@RequestParam(value="gender", required=true) String gender) {

		Patient p = Context.getPatientService().getPatient(patientId);
		p.setGender(gender);

		PatientIdentifier pi = p.getPatientIdentifier();
		pi.setIdentifier(identifier);

		p = Context.getPatientService().savePatient(p);

		return "redirect:/module/hirifxray/participant.form?id="+p.getPatientId();
	}

	@RequestMapping("/module/hirifxray/uploadXray.form")
	public String uploadXray(ModelMap model,
							 @RequestParam(value="patientId", required=true) Integer patientId,
							 @RequestParam(value="type", required=true) String type,
							 @RequestParam(value="concept", required=true) Concept concept,
							 @RequestParam(value="obsDatetime", required=true) Date obsDatetime,
							 @RequestParam(value="xrayFile", required=true) MultipartFile xrayFile) throws Exception {

		Patient p = Context.getPatientService().getPatient(patientId);
		Obs o = new Obs();
		o.setPerson(p);
		o.setObsDatetime(obsDatetime);
		o.setConcept(concept);

		String identifier = p.getPatientIdentifier().getIdentifier();

		String[] fileParts = xrayFile.getOriginalFilename().split("\\.");
		String xrayKey = identifier + "_" + type + "." + fileParts[fileParts.length-1];

		ComplexData data = new ComplexData(xrayKey, xrayFile.getInputStream());
		o.setComplexData(data);

		Context.getObsService().saveObs(o, "Uploading " + type);

		return "redirect:/module/hirifxray/participant.form?id="+p.getPatientId() + "&type="+type;
	}

	@RequestMapping("/module/hirifxray/deleteXray.form")
	public String deleteXray(ModelMap model,
							 @RequestParam(value="patientId", required=true) Integer patientId,
							 @RequestParam(value="type", required=true) String type,
							 @RequestParam(value="obsId", required=true) Integer obsId) throws Exception {

		Obs o = Context.getObsService().getObs(obsId);
		Context.getObsService().voidObs(o, "Deleting xray");

		return "redirect:/module/hirifxray/participant.form?id="+patientId + "&type="+type;
	}
}
