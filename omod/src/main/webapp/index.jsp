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
	<spring:message code="hirifxray.title"/>
</h3>
<br/>
<table width="100%">
	<tr>
		<td>
			<form method="get">
				<spring:message code="hirifxray.enterParticipantId"/>:
				<input type="text" size="20" id="participantLookup" name="identifier"/>
				<input type="submit" value="<spring:message code="hirifxray.submit"/>"/>
				<input type="button" value="<spring:message code="hirifxray.clear"/>" id="clearButton"/>
			</form>
			<hr/>
			<c:if test="${patients != null}">
				<c:choose>
					<c:when test="${!empty invalidIdentifierFormat}">
						<span style="font-weight:bold; color:red">
							<spring:message code="hirifxray.invalidIdentifier"/> ${invalidIdentifierFormat}
						</span>
					</c:when>
					<c:when test="${fn:length(patients) == 0}">
						<span style="font-weight:bold; color:red">
							<spring:message code="hirifxray.noMatchingParticipants"/>.
						</span>
                        <openmrs:hasPrivilege privilege="Edit Xray">
                            <br/><br/>
                            <spring:message code="hirifxray.ifWishToSaveEnterDetails"/><br/><br/>
                            <form method="post" action="${pageContext.request.contextPath}/module/hirifxray/createParticipant.form">
                                <b><spring:message code="hirifxray.participantId"/>:</b><br/>
                                <input type="hidden" name="identifier" value="${identifier}"/>
                                ${identifier}
                                <br/><br/>
                                <b><spring:message code="hirifxray.gender"/></b><br/>
                                <input type="radio" name="gender" value="M"/> <spring:message code="hirifxray.gender.M"/>
                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="radio" name="gender" value="F"/> <spring:message code="hirifxray.gender.F"/>
                                <br/><br/>
                                <input type="submit" value="<spring:message code="hirifxray.createParticipant"/>"/>
                            </form>
                        </openmrs:hasPrivilege>
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
							</td>
							<td>
								<button style="width:100px; height: 50px;" onclick="document.location.href='module/hirifxray/participant.form?id=${p.patientId}';">
									<spring:message code="hirifxray.open"/>
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