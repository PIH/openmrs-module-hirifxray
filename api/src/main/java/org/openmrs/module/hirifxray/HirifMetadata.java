/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.hirifxray;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

public class HirifMetadata {

	// Concept Mapping Code Constants

	public static String HIRIF_SOURCE_NAME = "org.openmrs.module.hirifxray";
	public static String XRAY_STATUS = "xrayStatus";
	public static String XRAY_STATUS_COMPLETED = "completed";
	public static String XRAY_STATUS_NOT_DONE = "notDone";
	public static String XRAY_IMAGE = "xrayImage";
	public static String XRAY_TYPE = "xrayType";
	public static String XRAY_TYPE_ENROLLMENT = "enrollmentXray";
	public static String XRAY_TYPE_VISIT_9 = "visit9Xray";
	public static String XRAY_TYPE_VISIT_13 = "visit13Xray";
	public static String XRAY_TYPE_EARLY_TERMINATION = "earlyTerminationXray";
	public static String XRAY_LOCATION = "xrayLocation";
	public static String XRAY_LOCATION_POSTERIOR_ANTERIOR = "posteriorAnterior";
	public static String XRAY_LOCATION_LEFT_LATERAL = "leftLateral";
	public static String XRAY_LOCATION_RIGHT_LATERAL = "rightLateral";

	public static Concept getXrayStatusConcept() {
		return lookupConcept(XRAY_STATUS);
	}

	public static Concept getNotDoneStatus() {
		return lookupConcept(XRAY_STATUS_NOT_DONE);
	}

	public static Concept getCompletedStatus() {
		return lookupConcept(XRAY_STATUS_COMPLETED);
	}

	public static List<Concept> getXrayStatuses() {
		List<Concept> l = new ArrayList<Concept>();
		l.add(getNotDoneStatus());
		l.add(getCompletedStatus());
		return l;
	}

	public static Concept getXrayImageConcept() {
		return lookupConcept(XRAY_IMAGE);
	}

	public static Concept getXrayTypeConcept() {
		return lookupConcept(XRAY_TYPE);
	}

	public static List<Concept> getXrayTypes() {
		List<Concept> l = new ArrayList<Concept>();
		l.add(lookupConcept(XRAY_TYPE_ENROLLMENT));
		l.add(lookupConcept(XRAY_TYPE_VISIT_9));
		l.add(lookupConcept(XRAY_TYPE_VISIT_13));
		l.add(lookupConcept(XRAY_TYPE_EARLY_TERMINATION));
		return l;
	}

	public static Concept getXrayLocationConcept() {
		return lookupConcept(XRAY_LOCATION);
	}

	public static List<Concept> getXrayLocations() {
		List<Concept> l = new ArrayList<Concept>();
		l.add(lookupConcept(XRAY_LOCATION_POSTERIOR_ANTERIOR));
		l.add(lookupConcept(XRAY_LOCATION_LEFT_LATERAL));
		l.add(lookupConcept(XRAY_LOCATION_RIGHT_LATERAL));
		return l;
	}

	public static PatientIdentifierType getIdentifierType() {
		return Context.getPatientService().getAllPatientIdentifierTypes().get(0);
	}

	public static Location getUnknownLocation() {
		return Context.getLocationService().getLocation("Unknown Location");
	}

	public static EncounterType getEncounterType() {
		return Context.getEncounterService().getEncounterType("X-ray");
	}

	private static Concept lookupConcept(String code) {
		Concept c = Context.getConceptService().getConceptByMapping(code, HIRIF_SOURCE_NAME);
		if (c == null) {
			throw new IllegalArgumentException("Configuration error.  Not concept with code " + code + " found.");
		}
		return c;
	}
}
