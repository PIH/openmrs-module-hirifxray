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
				jQuery(".xrayDetailSection").hide();
			}
			else {
				jQuery(".xrayDetailSection").show();
			}
		});
		jQuery("#xrayDate").attr("size", "15");
		<c:if test="${xray.status == notDoneStatus}">
			jQuery(".xrayDetailSection").hide();
		</c:if>
        jQuery(".editPrivilegeRequired").hide();
        <openmrs:hasPrivilege privilege="Edit Xray">
            jQuery(".editPrivilegeRequired").show();
        </openmrs:hasPrivilege>
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
	<td style="vertical-align:top; width:35%;">
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
					<input type="button" id="editButton" class="editPrivilegeRequired" value="<spring:message code="hirifxray.edit"/>"/>
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
			<table id="xrayTable" width="100%">

				<c:forEach items="${xrayTypes}" var="xrayType">
					<tr>
						<td style="padding-top:10px;"><b><openmrs:format concept="${xrayType}"/></b></td>
					</tr>
					<c:set var="neverDone" value="false"/>
					<c:forEach items="${xraysByType[xrayType]}" var="xrayItem">
						<tr>
							<td style="padding-left:20px;${xrayItem == xray ? "background-color:yellow;": ""}">
								<a class="xrayLink" href="participant.form?id=${patient.patientId}&type=${xrayType.conceptId}&xrayId=${xrayItem.id}">
									${xrayItem}
									<c:if test="${xrayItem.markedAsNotDone}">
										<c:set var="neverDone" value="true"/>
									</c:if>
								</a>
							</td>
						</tr>
					</c:forEach>
					<c:if test="${!neverDone}">
						<tr class="editPrivilegeRequired">
							<td style="font-size:smaller; padding-left:20px;${empty xray.id && type == xrayType ? "background-color:yellow;": ""}">
								<a href="participant.form?id=${patient.patientId}&type=${xrayType.conceptId}">
									[+] <spring:message code="hirifxray.add"/>
								</a>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</table>
		</fieldset>
	</td>

	<td style="vertical-align:top; width:50%;">
		<c:if test="${!empty type}">
			<fieldset>
				<legend><b>
				<c:choose>
					<c:when test="${empty xray.id}">${type.displayString}</c:when>
					<c:otherwise>${xray}</c:otherwise>
				</c:choose>
				</b></legend>
				<div style="height:460px; padding:10px;">
					<c:choose>
						<c:when test="${empty xray.imageObs}">
							<form action="uploadXray.form" method="post" enctype="multipart/form-data" class="editPrivilegeRequired">
								<input type="hidden" name="patientId" value="${patient.patientId}"/>
								<input type="hidden" name="type" value="${type.conceptId}"/>
								<input type="hidden" name="xrayId" value="${xray.id}"/>

								<table cellpadding="5">
									<tr>
										<td colspan="2">
											<input id="notDoneField" type="checkbox" name="status" value="${notDoneStatus.conceptId}" <c:if test="${xray.status == notDoneStatus}">checked</c:if>/><spring:message code="hirifxray.neverDone"/>
											<br/><br/>
										</td>
									</tr>
									<tr class="xrayDetailSection">
										<td><spring:message code="hirifxray.xrayLocation"/></td>
										<td>
											<select name="location">
												<option value=""></option>
												<c:forEach items="${xrayLocations}" var="location">
													<option value="${location.conceptId}"<c:if test="${xray.location == location}"> selected</c:if>>
													<openmrs:format concept="${location}"/>
													</option>
												</c:forEach>
											</select>
										</td>
									</tr>
									<tr class="xrayDetailSection">
										<td><spring:message code="hirifxray.imageToUpload"/></td>
										<td><input type="file" name="xrayFile" size="50"/></td>
									</tr>
									<tr>
										<td colspan="2"><input type="submit" value="<spring:message code="hirifxray.save"/>"/></td>
									</tr>
								</table>
							</form>
						</c:when>
						<c:otherwise>
							<c:if test="${!empty xray.imageObs}">
								<a href="${pageContext.request.contextPath}/complexObsServlet?obsId=${xray.imageObs.obsId}&view=download&viewType=download">
									<spring:message code="hirifxray.downloadFullImage"/>
								</a>
								<br/><br/>
								<img src="${pageContext.request.contextPath}/complexObsServlet?obsId=${xray.imageObs.obsId}" height="450"/>
							</c:if>
						</c:otherwise>
					</c:choose>
				</div>
				<c:if test="${!empty xray.id}">
					<div style="width:100%; text-align:right;">
						<input type="button" class="editPrivilegeRequired" onclick="if (confirm('<spring:message code="hirifxray.confirmDelete"/>')) {document.location.href='deleteXray.form?xrayId=${xray.id}';}" value="<spring:message code="hirifxray.deleteXray"/>">
					</div>
				</c:if>
			</fieldset>
		</c:if>
	</td>

</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 