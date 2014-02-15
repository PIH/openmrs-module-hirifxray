package org.openmrs.module.hirifxray.web.controller;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.hirifxray.HirifMetadata;
import org.openmrs.module.hirifxray.HirifUtil;
import org.openmrs.module.hirifxray.Xray;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.PatientEditor;
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

@Controller
public class ParticipantController {

	@InitBinder
	public void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		SimpleDateFormat dateFormat = Context.getDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,true, 10));
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Patient.class, new PatientEditor());
		binder.registerCustomEditor(Encounter.class, new EncounterEditor());
	}
    
    @RequestMapping("/module/hirifxray/participant.form")
    public String viewParticipant(ModelMap model,
					   @RequestParam(value="id", required=true) Integer id,
					   @RequestParam(value="type", required=false) Concept type,
					   @RequestParam(value="xrayId", required=false) Encounter encounter) {

		User currentUser = Context.getAuthenticatedUser();
		if (currentUser == null) {
			return "redirect:/login.htm";
		}

		Xray xray = new Xray(encounter);

		model.addAttribute("type", type);
		model.addAttribute("xray", xray);

		Patient patient = Context.getPatientService().getPatient(id);
		model.addAttribute("patient", patient);

		model.addAttribute("xrayTypes", HirifMetadata.getXrayTypes());
		model.addAttribute("xraysByType", HirifUtil.getXraysByType(patient));

		model.addAttribute("xrayLocations", HirifMetadata.getXrayLocations());

		model.addAttribute("xrayStatuses", HirifMetadata.getXrayStatuses());
		model.addAttribute("notDoneStatus", HirifMetadata.getNotDoneStatus());
		model.addAttribute("completedStatus", HirifMetadata.getCompletedStatus());

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
		pi.setLocation(HirifMetadata.getUnknownLocation());
		pi.setIdentifierType(HirifMetadata.getIdentifierType());
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
							 @RequestParam(value="patientId", required=true) Patient patient,
							 @RequestParam(value="xrayId", required=false) Encounter encounter,
							 @RequestParam(value="xrayDate", required=false) Date encounterDate,
							 @RequestParam(value="type", required=true) Concept type,
							 @RequestParam(value="status", required=false) Concept status,
							 @RequestParam(value="location", required=false) Concept location,
							 @RequestParam(value="xrayFile", required=false) MultipartFile xrayFile) throws Exception {

		ComplexData xrayData = null;
		if (xrayFile != null && !xrayFile.isEmpty()) {
			String identifier = patient.getPatientIdentifier().getIdentifier();
			String[] fileParts = xrayFile.getOriginalFilename().split("\\.");
			String xrayKey = identifier + "_" + type + "." + fileParts[fileParts.length-1];
			xrayData = new ComplexData(xrayKey, xrayFile.getInputStream());
		}

		if (encounter == null) {
			encounter = new Encounter();
			encounter.setPatient(patient);
			encounter.setEncounterType(HirifMetadata.getEncounterType());
		}
		encounter.setEncounterDatetime(encounterDate == null ? HirifUtil.dateMidnight() : encounterDate);

		HirifUtil.updateCodedObs(encounter, HirifMetadata.getXrayTypeConcept(), type);
		HirifUtil.updateCodedObs(encounter, HirifMetadata.getXrayStatusConcept(), status);
		HirifUtil.updateCodedObs(encounter, HirifMetadata.getXrayLocationConcept(), location);
		HirifUtil.updateComplexObs(encounter, HirifMetadata.getXrayImageConcept(), xrayData);

		encounter = Context.getEncounterService().saveEncounter(encounter);

		return "redirect:/module/hirifxray/participant.form?id="+patient.getPatientId() + "&type="+type + "&xrayId="+ encounter.getEncounterId();
	}

	@RequestMapping("/module/hirifxray/deleteXray.form")
	public String deleteXray(ModelMap model,
							 @RequestParam(value="xrayId", required=true) Encounter encounter) throws Exception {

		Integer pId = encounter.getPatient().getPatientId();
		Context.getEncounterService().voidEncounter(encounter, "Deleting xray");

		return "redirect:/module/hirifxray/participant.form?id="+pId;
	}
}
