<%@ include file="/WEB-INF/template/include.jsp" %>
<style>
	.appButton { height:150px; width:150px; color:white; font-weight:bold; background-color:#009384; }
</style>

<%@ include file="/WEB-INF/template/header.jsp" %>

<table border="0" align="center" cellspacing="20">
	<tr>
		<td align="center" valign='top'>
			<openmrs:portlet id="findPatient" url="findPatient" parameters="size=full|postURL=../../../patientDashboard.form|showIncludeVoided=false|hideAddNewPatient=true|viewType=shortEdit" />
			<openmrs:hasPrivilege privilege="Add Patients">
				<br/> &nbsp; <spring:message code="general.or"/><br/><br/>
				<openmrs:portlet id="addPersonForm" url="addPersonForm" parameters="personType=patient|postURL=../../../admin/person/addPerson.htm|viewType=shortEdit" />
			</openmrs:hasPrivilege>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 