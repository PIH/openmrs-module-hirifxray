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
import java.util.List;
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

		String[] xrayTypes = {"enrollment", "visit9", "visit13", "earlyTermination"};

		Map<String, Concept> xraysConcepts = new LinkedHashMap<String, Concept>();
		Map<String, Obs> xrays = new LinkedHashMap<String, Obs>();
		Map<String, Concept> xrayStatusConcepts = new LinkedHashMap<String, Concept>();
		Map<String, Obs> xrayStatuses = new LinkedHashMap<String, Obs>();

		for (String xrayType : xrayTypes) {
			String xrayCode = xrayType + "Xray";
			Concept xrayConcept = HirifxrayUtil.getHirifConcept(xrayCode);
			xraysConcepts.put(xrayType, xrayConcept);
			xrays.put(xrayType, HirifxrayUtil.getHirifObs(patient, xrayConcept));

			String xrayStatusCode = xrayCode + "Status";
			Concept xrayStatusConcept = HirifxrayUtil.getHirifConcept(xrayStatusCode);
			xrayStatusConcepts.put(xrayType, xrayStatusConcept);
			xrayStatuses.put(xrayType, HirifxrayUtil.getHirifObs(patient, xrayStatusConcept));
		}

		model.addAttribute("xrayTypes", xrayTypes);
		model.addAttribute("xraysConcepts", xraysConcepts);
		model.addAttribute("xrays", xrays);
		model.addAttribute("xrayStatusConcepts", xrayStatusConcepts);
		model.addAttribute("xrayStatuses", xrayStatuses);
		model.addAttribute("type", type);
		model.addAttribute("notDoneConcept", HirifxrayUtil.getXrayNotDoneConcept());
		model.addAttribute("completedConcept", HirifxrayUtil.getXrayCompletedConcept());

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
							 @RequestParam(value="statusQuestion", required=true) Concept statusQuestion,
							 @RequestParam(value="statusAnswer", required=false) Concept statusAnswer,
							 @RequestParam(value="concept", required=true) Concept concept,
							 @RequestParam(value="obsDatetime", required=false) Date obsDatetime,
							 @RequestParam(value="xrayFile", required=false) MultipartFile xrayFile) throws Exception {

		Patient p = Context.getPatientService().getPatient(patientId);

		if (statusAnswer != null) {
			Obs o = new Obs();
			o.setPerson(p);
			o.setObsDatetime(obsDatetime == null ? new Date() : obsDatetime);
			o.setConcept(statusQuestion);
			o.setValueCoded(statusAnswer);
			Context.getObsService().saveObs(o, "Saved status for " + type);
		}
		else {
			List<Obs> l = Context.getObsService().getObservationsByPersonAndConcept(p, statusQuestion);
			for (Obs o : l) {
				Context.getObsService().voidObs(o, "Voided.");
			}
		}

		if (xrayFile != null && !xrayFile.isEmpty()) {
			Obs o = new Obs();
			o.setPerson(p);
			o.setObsDatetime(obsDatetime);
			o.setConcept(concept);

			String identifier = p.getPatientIdentifier().getIdentifier();

			String[] fileParts = xrayFile.getOriginalFilename().split("\\.");
			String xrayKey = identifier + "_" + type + "." + fileParts[fileParts.length-1];

			ComplexData data = new ComplexData(xrayKey, xrayFile.getInputStream());
			o.setComplexData(data);

			Context.getObsService().saveObs(o, "Uploaded image for " + type);
		}

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
