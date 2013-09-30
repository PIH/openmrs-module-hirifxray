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
	});
</script>

<style>
	#participantTable td {padding-right:10px;}
</style>

<h3>
	HiRif Study X-ray Management System
</h3>
<br/>
<a href="${pageContext.request.contextPath}/index.htm">Back to search</a>
<br/><br/>

<table width="100%"><tr>
	<td style="vertical-align:top; width:30%; padding:10px;">
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
								<input type="text" name="identifier" value="${identifier}" size="20"/>
							</span>
						</td>
					</tr>
					<tr>
						<td>Given Name:</td>
						<td>
							<span class="viewMode">${patient.givenName}</span>
							<span class="editMode">
								<input type="text" name="givenName" value="${patient.givenName}" size="20"/>
							</span>
						</td>
					</tr>
					<tr>
						<td>Family Name:</td>
						<td>
							<span class="viewMode">${patient.familyName}</span>
							<span class="editMode">
								<input type="text" name="familyName" value="${patient.familyName}" size="20"/>
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
					<tr>
						<td>Birthdate:</td>
						<td>
							<span class="viewMode">
								<c:if test="${patient.birthdateEstimated}">~</c:if>
								<openmrs:formatDate date="${patient.birthdate}" format="dd/MMM/yyyy"/>
							</span>
							<span class="editMode">
								<openmrs_tag:dateField formFieldName="birthdate" startValue="${patient.birthdate}"/>
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
				<tr>
					<td>Enrollment X-ray:</td>
					<td>
						TODO
					</td>
				</tr>
				<tr>
					<td>Visit 9 X-ray:</td>
					<td>
						TODO
					</td>
				</tr>
				<tr>
					<td>Visit 13 X-ray:</td>
					<td>
						TODO
					</td>
				</tr>
				<tr>
					<td>Early termination X-ray:</td>
					<td>
						TODO
					</td>
				</tr>
			</table>
		</fieldset>
	</td>
	<td style="vertical-align:top; width:70%;">

	</td>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 