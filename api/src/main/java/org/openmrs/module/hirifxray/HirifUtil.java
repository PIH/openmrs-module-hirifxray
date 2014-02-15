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
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods
 */
public class HirifUtil {

	public static Map<Concept, List<Xray>> getXraysByType(Patient p) {
		Map<Concept, List<Xray>> ret = new HashMap<Concept, List<Xray>>();
		for (Encounter e : Context.getEncounterService().getEncountersByPatient(p)) {
			Obs typeObs = getObs(e, HirifMetadata.getXrayTypeConcept());
			Concept type = (typeObs == null ? null : typeObs.getValueCoded());
			List<Xray> typeList = ret.get(type);
			if (typeList == null) {
				typeList = new ArrayList<Xray>();
				ret.put(type, typeList);
			}
			typeList.add(new Xray(e));
		}
		return ret;
	}

	public static Obs getObs(Encounter e, Concept c) {
		List<Obs> found = new ArrayList<Obs>();
		if (e != null) {
			for (Obs o : e.getAllObs()) {
				if (o.getConcept().equals(c)) {
					found.add(o);
				}
			}
		}
		if (found.isEmpty()) {
			return null;
		}
		if (found.size() > 1) {
			throw new RuntimeException("Found more than one Obs for " + c + " in encounter " + e);
		}
		return found.get(0);
	}


	public static void updateCodedObs(Encounter encounter, Concept question, Concept newValue) {
		Obs o = getObs(encounter, question);
		if (o == null) {
			if (newValue != null) {
				o = new Obs();
				o.setConcept(question);
				encounter.addObs(o);
				o.setValueCoded(newValue);
			}
		}
		else {
			o.setValueCoded(newValue);
		}
	}

	public static void updateComplexObs(Encounter encounter, Concept question, ComplexData data) {
		Obs o = getObs(encounter, question);
		if (o == null) {
			if (data != null) {
				o = new Obs();
				o.setConcept(question);
				encounter.addObs(o);
				o.setComplexData(data);
			}
		}
		else {
			o.setComplexData(data);
		}
	}

	public static Date dateMidnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}
