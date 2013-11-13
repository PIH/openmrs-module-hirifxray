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
	<spring:message code="hirifxray.title"/>
</h3>
<br/>
<a href="${pageContext.request.contextPath}/index.htm"><spring:message code="hirifxray.backToSearch"/></a>
<br/><br/>

<table width="100%"><tr>
	<td style="vertical-align:top; width:50%;">
		<fieldset>
			<legend><b><spring:message code="hirifxray.participantDetails"/></b></legend>
			<form action="updateParticipant.form" method="post">
				<input type="hidden" name="patientId" value="${patient.patientId}"/>
				<table id="participantTable">
					<tr>
						<td><spring:message code="hirifxray.participantId"/>:</td>
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
						<td><spring:message code="hirifxray.gender"/>:</td>
						<td>
							<span class="viewMode">
								<c:if test="${!empty patient.gender}">
									<spring:message code="hirifxray.gender.${patient.gender}"/>
								</c:if>
							</span>
							<span class="editMode">
								<input type="radio" name="gender" value="M" ${patient.gender == 'M' ? "checked" : ""}/> <spring:message code="hirifxray.gender.M"/>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<input type="radio" name="gender" value="F" ${patient.gender == 'F' ? "checked" : ""}/> <spring:message code="hirifxray.gender.F"/>
							</span>
						</td>
					</tr>
				</table>
				<br/>
				<span class="viewMode">
					<input type="button" id="editButton" value="<spring:message code="hirifxray.edit"/>"/>
				</span>
				<span class="editMode">
					<input type="submit" value="<spring:message code="hirifxray.save"/>"/>
					<input type="button" id="viewButton" value="<spring:message code="hirifxray.cancel"/>"/>
				</span>
			</form>
		</fieldset>
		<br/>
		<fieldset>
			<legend><b><spring:message code="hirifxray.xrays"/></b></legend>
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
										<spring:message code="hirifxray.add"/>
									</c:when>
									<c:when test="${empty xray}">
										<c:choose>
											<c:when test="${xrayStatus.valueCoded == notDoneConcept}"><spring:message code="hirifxray.neverDone"/></c:when>
											<c:otherwise><spring:message code="hirifxray.completed"/></c:otherwise>
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
	<td style="vertical-align:top; width:50%;">
		<c:if test="${!empty type}">
			<fieldset>
				<legend><b><spring:message code="hirifxray.${type}Xray"/></b></legend>
				<div style="height:460px; padding:10px;">

					<form action="uploadXray.form" method="post" enctype="multipart/form-data">
						<input type="hidden" name="patientId" value="${patient.patientId}"/>
						<input type="hidden" name="type" value="${type}"/>
						<input type="hidden" name="concept" value="${xraysConcepts[type].conceptId}"/>
						<input type="hidden" name="statusQuestion" value="${xrayStatusConcepts[type].conceptId}"/>
						<c:set var="showSave" value="true"/>
						<c:if test="${empty xrays[type]}">
							<input id="notDoneField" type="checkbox" name="statusAnswer" value="${notDoneConcept.conceptId}" <c:if test="${xrayStatuses[type].valueCoded == notDoneConcept}">checked</c:if>/><spring:message code="hirifxray.neverDone"/>
							<br/><br/>
						</c:if>

						<div id="xrayDetailSection" <c:if test="${xrayStatuses[type].valueCoded == notDoneConcept}">style="display:none;"</c:if>>
							<c:choose>
								<c:when test="${empty xrays[type]}">
									<b><spring:message code="hirifxray.dateOfXray"/>:</b><br/>
									<openmrs_tag:dateField formFieldName="obsDatetime" startValue=""/>
									<br/><br/>
									<b><spring:message code="hirifxray.imageToUpload"/>:</b><br/>
									<input type="file" name="xrayFile" size="50"/>
									<br/><br/>
								</c:when>
								<c:otherwise>
									<c:set var="showSave" value="false"/>
									<b><openmrs:formatDate date="${xrays[type].obsDatetime}" format="dd/MMM/yyyy"/></b>&nbsp;&nbsp;&nbsp;
									<a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${xrays[type].id}&view=download&viewType=download">
										<spring:message code="hirifxray.downloadFullImage"/>
									</a>
									<br/><br/>
									<img src="${pageContext.request.contextPath}/complexObsServlet?obsId=${xrays[type].id}" height="350"/>
									<div style="width:100%; text-align:right;">
										<input type="button" onclick="if (confirm('<spring:message code="hirifxray.confirmDelete"/>')) {document.location.href='deleteXray.form?patientId=${patient.patientId}&type=${type}&obsId=${xrays[type].id}';}" value="<spring:message code="hirifxray.deleteXray"/>">
									</div>
								</c:otherwise>
							</c:choose>
						</div>
						<c:if test="${showSave}">
							<input type="submit" value="<spring:message code="hirifxray.save"/>"/>
						</c:if>
					</form>
				</div>
			</fieldset>
		</c:if>
	</td>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 