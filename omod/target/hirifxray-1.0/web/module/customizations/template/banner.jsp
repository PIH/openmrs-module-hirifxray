<div id="banner">
	<a href="${pageContext.request.contextPath}">
	  <img style="padding:3px; vertical-align:middle;" src="${pageContext.request.contextPath}/moduleResources/pihhaiti/images/logos/PIH_Logo.jpg" alt="PIH" border="0"/>
	</a>
</div>
<script type="text/javascript">
	var userBar = document.getElementById("userBar")
	<c:if test="${pihhaiti:currentUserHasRole('System Administrator')}">
		var adminLink = document.createElement('a');
		adminLink.setAttribute('href', '${pageContext.request.contextPath}/admin/index.htm');
		adminLink.appendChild(document.createTextNode('<spring:message code="Navigation.administration"/>'));
		var adminSpan = document.createElement('span');
		adminSpan.appendChild(adminLink);
		userBar.appendChild(adminSpan);
	</c:if>
</script>

