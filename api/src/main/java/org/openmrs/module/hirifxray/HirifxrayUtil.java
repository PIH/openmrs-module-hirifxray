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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;

import java.util.List;

/**
 * Utility methods
 */
public class HirifxrayUtil {

	private Log log = LogFactory.getLog(this.getClass());

	public static PatientIdentifierType getIdentifierType() {
		return Context.getPatientService().getAllPatientIdentifierTypes().get(0);
	}

	public static Concept getHirifConcept(String code) {
		return Context.getConceptService().getConceptByMapping(code, "org.openmrs.module.hirifxray");
	}

	public static Obs getHirifObs(Patient p, Concept c) {
		List<Obs> l = Context.getObsService().getObservationsByPersonAndConcept(p, c);
		if (l == null || l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	public static Concept getXrayNotDoneConcept() {
		return getHirifConcept("notDone");
	}

	public static Concept getXrayCompletedConcept() {
		return getHirifConcept("completed");
	}

	public static Location getUnknownLocation() {
		return Context.getLocationService().getLocation("Unknown Location");
	}
}
