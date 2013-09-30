<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<script type="text/javascript">
	jQuery(document).ready(function(){
		jQuery("#participantLookup").focus().val("${identifier}");
		jQuery("#clearButton").click(function(event) {
			document.location.href='index.htm';
		});
	});
</script>

<h3>
	HiRif Study X-ray Management System
</h3>
<br/>
<table width="100%">
	<tr>
		<td>
			<form method="get">
				Enter Participant ID:
				<input type="text" size="20" id="participantLookup" name="identifier"/>
				<input type="submit"/>
				<input type="button" value="Clear" id="clearButton"/>
			</form>
			<hr/>
			<c:if test="${patients != null}">
				<c:choose>
					<c:when test="${!empty invalidIdentifierFormat}">
						<span style="font-weight:bold; color:red">
							Invalid identifier supplied.  Expected format is ${invalidIdentifierFormat}
						</span>
					</c:when>
					<c:when test="${fn:length(patients) == 0}">
						<span style="font-weight:bold; color:red">
							No matching participants found.
						</span>
						<br/><br/>
						If you wish to save a new participant with this identifier, enter details below<br/><br/>
						<form action="${pageContext.request.contextPath}/module/hirifxray/createParticipant.form">
							<b>Participant ID:</b><br/>
							<input type="hidden" name="identifier" value="${identifier}"/>
							${identifier}
							<br/><br/>
							<b>Given Name:</b><br/>
							<input type="text" name="givenName" size="30"/>
							<br/><br/>
							<b>Family Name:</b><br/>
							<input type="text" name="familyName" size="30"/>
							<br/><br/>
							<b>Gender</b><br/>
							<input type="radio" name="gender" value="M"/> Male
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="radio" name="gender" value="F"/> Female
							<br/><br/>
							<b>Birth Date</b><br/>
							<openmrs_tag:dateField formFieldName="birthdate" startValue=""/>
							<br/><br/>
							<input type="submit" value="Create Participant"/>
						</form>
					</c:when>
					<c:otherwise></c:otherwise>
				</c:choose>
				<table style="padding:10px;">
					<c:forEach items="${patients}" var="p">
						<tr>
							<td style="padding:10px; border:1px solid black; ">
								<c:forEach items="${p.identifiers}" var="identifier">
									<b>${identifier}</b><br/>
								</c:forEach>
								${p.givenName} ${p.familyName}
							</td>
							<td>
								<button style="width:100px; height: 50px;" onclick="document.location.href='module/hirifxray/participant.form?id=${p.patientId}';">
									Open
								</button>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 