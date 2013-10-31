<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery(".editMode").hide();
		jQuery("#editButton").click(function(event) {
			jQuery(".viewMode").hide();
			jQuery(".editMode").show();
		});
		jQuery("#viewButton").click(function(event) {
			jQuery(".editMode").hide();
			jQuery(".viewMode").show();
		});
		jQuery("#notDoneField").click(function(event) {
			if (jQuery(this).attr("checked")) {
				jQuery("#xrayDetailSection").hide();
			}
			else {
				jQuery("#xrayDetailSection").show();
			}
		});
	});
</script>

<style>
	#participantTable td {padding-right:10px;}
	#xrayTable td {padding-right:10px;}
</style>

<h3>
	HiRif Study X-ray Management System
</h3>
<br/>
<a href="${pageContext.request.contextPath}/index.htm">Back to search</a>
<br/><br/>

<table width="100%"><tr>
	<td style="vertical-align:top; width:30%;">
		<fieldset>
			<legend><b>Participant Details</b></legend>
			<form action="updateParticipant.form" method="post">
				<input type="hidden" name="patientId" value="${patient.patientId}"/>
				<table id="participantTable">
					<tr>
						<td>Participant ID:</td>
						<td>
							<c:set var="identifier" value=""/>
							<c:forEach items="${patient.identifiers}" var="ident">
								<c:set var="identifier" value="${ident}"/>
							</c:forEach>
							<span class="viewMode">${identifier}</span>
							<span class="editMode">
								<input type="text" name="identifier" value="${identifier}" size="25"/>
							</span>
						</td>
					</tr>
					<tr>
						<td>Gender:</td>
						<td>
							<span class="viewMode">${patient.gender}</span>
							<span class="editMode">
								<input type="radio" name="gender" value="M" ${patient.gender == 'M' ? "checked" : ""}/> Male
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="gender" value="F" ${patient.gender == 'F' ? "checked" : ""}/> Female
							</span>
						</td>
					</tr>
				</table>
				<br/>
				<span class="viewMode">
					<input type="button" id="editButton" value="Edit"/>
				</span>
				<span class="editMode">
					<input type="submit" value="Save"/>
					<input type="button" id="viewButton" value="Cancel"/>
				</span>
			</form>
		</fieldset>
		<br/>
		<fieldset>
			<legend><b>X-rays</b></legend>
			<table id="xrayTable">

				<c:forEach items="${xrayTypes}" var="xrayType">

					<c:set var="xrayConcept" value="${xrayConcepts[xrayType]}"/>
					<c:set var="xray" value="${xrays[xrayType]}"/>
					<c:set var="xrayStatusConcept" value="${xrayStatusConcepts[xrayType]}"/>
					<c:set var="xrayStatus" value="${xrayStatuses[xrayType]}"/>

					<tr style="${type == xrayType ? 'background-color:lightgrey;' : ''}">
						<td><spring:message code="hirifxray.${xrayType}Xray"/>:</td>
						<td>
							<a href="participant.form?id=${patient.patientId}&type=${xrayType}">
								<c:choose>
									<c:when test="${empty xray && empty xrayStatus}">
										Add
									</c:when>
									<c:when test="${empty xray}">
										<c:choose>
											<c:when test="${xrayStatus.valueCoded == notDoneConcept}">X-ray never done</c:when>
											<c:otherwise>X-ray completed</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<openmrs:formatDate date="${xray.obsDatetime}" format="dd/MMM/yyyy"/>
									</c:otherwise>
								</c:choose>
							</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</fieldset>
	</td>
	<td style="vertical-align:top; width:70%;">
		<c:if test="${!empty type}">
			<fieldset>
				<legend><b><spring:message code="hirifxray.${type}Xray"/></b></legend>
				<div style="height:460px; padding:10px;">

					<form action="uploadXray.form" method="post" enctype="multipart/form-data">
						<input type="hidden" name="patientId" value="${patient.patientId}"/>
						<input type="hidden" name="type" value="${type}"/>
						<input type="hidden" name="concept" value="${xraysConcepts[type].conceptId}"/>
						<input type="hidden" name="statusQuestion" value="${xrayStatusConcepts[type].conceptId}"/>

						<c:if test="${empty xrays[type]}">
							<input id="notDoneField" type="checkbox" name="statusAnswer" value="${notDoneConcept.conceptId}" <c:if test="${xrayStatuses[type].valueCoded == notDoneConcept}">checked</c:if>/>X-ray never done
							<br/><br/>
						</c:if>

						<div id="xrayDetailSection" <c:if test="${xrayStatuses[type].valueCoded == notDoneConcept}">style="display:none;"</c:if>>
							<c:choose>
								<c:when test="${empty xrays[type]}">
									<b>Date of x-ray:</b><br/>
									<openmrs_tag:dateField formFieldName="obsDatetime" startValue=""/>
									<br/><br/>
									<b>Image to upload:</b><br/>
									<input type="file" name="xrayFile" size="50"/>
									<br/><br/>
								</c:when>
								<c:otherwise>
									<b><openmrs:formatDate date="${xrays[type].obsDatetime}"/></b>&nbsp;&nbsp;&nbsp;
									<a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${xrays[type].id}&view=download&viewType=download">
										Download Full Image
									</a>
									<br/><br/>
									<img src="${pageContext.request.contextPath}/complexObsServlet?obsId=${xrays[type].id}" height="350"/>
									<div style="width:100%; text-align:right;">
										<input type="button" onclick="if (confirm('Are you sure you wish to delete this x-ray?')) {document.location.href='deleteXray.form?patientId=${patient.patientId}&type=${type}&obsId=${xrays[type].id}';}" value="Delete X-ray">
									</div>
								</c:otherwise>
							</c:choose>
						</div>
						<input type="submit" value="Save"/>
					</form>
				</div>
			</fieldset>
		</c:if>
	</td>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 