<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style type="text/css">
table.gridtable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}

table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}

table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}

.mainDiv {
	width: 900px;
}

.mainForm {
	width: 900px;
	border: 2px solid #B7B0B0;
}
</style>


<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AUTHBRIDGE | PANCARD VERIFICATION</title>
</head>
<body>

	<center>
		<div class="mainDiv">

			<div class="mainForm">

				<br>
				<br>
				<br>
				<br>
				<br>
				<br>

				<div style="color: teal; font-size: 30px">AUTHBRIDGE | PANCARD
					VERIFICATION</div>
				<br>
				<br>
				<!-- Form Div Start -->

				<c:url var="panForm" value="processPancard.html" />
				<form:form id="panForm" modelAttribute="user" action="${panForm}">

					<table width="550px" height="150px">

						<tr>
							<td><form:label path="pancardnumber"
									style="color: #5B5182;font-size: 17px;font-Weight: bold;"> Pancard Number :</form:label></td>
							<td><form:input path="pancardnumber"
									style="width: 264px;height: 34px;"></form:input></td>
						</tr>

						<tr>
							<td colspan="2"><label
								style="color: #D01717; font-weight: bold; font-size: 11px; font-family: normal; margin-left: 49px;">(NOTE:
									USED ',' FOR MORE THAN ONE PANCARD NUMEBER ! 5 ENTRIES PER
									REQUEST)</label></td>
						</tr>
						<tr>
							<td></td>
							<td><input type="submit" value="VERIFY"
								style="width: 200px; height: 38px; font-weight: bold; margin-left: 38px; background-color: #614892; border: 0px; color: #fff;" />
							</td>
						</tr>


					</table>

				</form:form>

			</div>


			<!-- Form DIV END -->

			<!--  Out Put Div-->
			<div class="outPut">
				<table class="gridtable">
					<tbody>
						<c:choose>
							<c:when test="${model ==''}">

							</c:when>
							<c:otherwise>
          ${model}
      </c:otherwise>
						</c:choose>
					</tbody>

				</table>
			</div>
			<!-- OutPUT DIV END -->

		</div>

	</center>

</body>
</html>