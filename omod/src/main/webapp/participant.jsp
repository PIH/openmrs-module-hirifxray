<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<script type="text/javascript">
	jQuery(document).ready(function(){

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
	<td style="vertical-align:top; border:1px solid black; width:30%; padding:10px;">
		<b>Participant Details</b><br/>
		<table id="participantTable">
			<tr>
				<td>Participant ID:</td>
				<td>
					<c:forEach items="${patient.identifiers}" var="identifier">
						${identifier}
					</c:forEach>
				</td>
			</tr>
			<tr>
				<td>Name:</td>
				<td>${patient.givenName} ${patient.familyName}</td>
			</tr>
			<tr>
				<td>Gender:</td>
				<td>${patient.gender}</td>
			</tr>
			<tr>
				<td>Birthdate:</td>
				<td>
					<c:if test="${patient.birthdateEstimated}">~</c:if>
					<openmrs:formatDate date="${patient.birthdate}" format="dd/MMM/yyyy"/>
				</td>
			</tr>
		</table>
		<br/>
		<b>X-rays</b><br/>
		<table id="xrayTable">
			<tr>
				<td>Participant ID:</td>
				<td>
					TODO
				</td>
			</tr>
		</table>
	</td>
	<td style="vertical-align:top; width:70%;">

	</td>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %> 