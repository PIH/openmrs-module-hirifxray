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
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.Locale;

public class Xray {

	private Encounter encounter;

	public Xray() {}

	public Xray(Encounter encounter) {
		this.encounter = encounter;
	}

	public Integer getId() {
		return (encounter == null ? null : encounter.getEncounterId());
	}

	public Date getDate() {
		return (encounter == null ? null : encounter.getEncounterDatetime());
	}

	public boolean getMarkedAsNotDone() {
		return HirifMetadata.getNotDoneStatus().equals(getStatus());
	}

	public Concept getStatus() {
		Obs o = HirifUtil.getObs(getEncounter(), HirifMetadata.getXrayStatusConcept());
		return (o == null ? null : o.getValueCoded());
	}

	public Concept getType() {
		Obs o = HirifUtil.getObs(getEncounter(), HirifMetadata.getXrayTypeConcept());
		return (o == null ? null : o.getValueCoded());
	}

	public Concept getLocation() {
		Obs o = HirifUtil.getObs(getEncounter(), HirifMetadata.getXrayLocationConcept());
		return (o == null ? null : o.getValueCoded());
	}

	public Obs getImageObs() {
		return HirifUtil.getObs(getEncounter(), HirifMetadata.getXrayImageConcept());
	}

	@Override
	public int hashCode() {
		return (encounter != null ? encounter.hashCode() : super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Xray) {
			Xray that = (Xray) obj;
			if (this.encounter != null && this.encounter.equals(that.encounter)) {
				return true;
			}
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		if (getMarkedAsNotDone()) {
			return Context.getMessageSourceService().getMessage("hirifxray.neverDone");
		}
		StringBuilder sb = new StringBuilder();
		Concept type = getType();
		if (type != null) {
			sb.append(getType().getDisplayString());
		}
		Concept location = getLocation();
		if (location != null) {
			sb.append(" - ").append(location.getDisplayString());
		}
		return sb.toString();
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
}
