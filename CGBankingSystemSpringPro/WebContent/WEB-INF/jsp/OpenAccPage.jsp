<%@taglib prefix="m" uri="http://www.springframework.org/tags/form"%>
<html>
<body>
	<div align="center">
		<h1>CG Banking System</h1>
		<hr color="mediumseagreen" size="5" />
		<h2 align="center">Enter Details to Open Account</h2>
		<m:form action="AcceptAccountDetails.obj" modelAttribute="acc"
			method="post">
			<table border="3" style="width:30%">
			
				<tr>
					<td>Initial Balance</td>
					<td><m:input path="accountBalance" /></td>
				</tr>
				
				<tr>
					<td>Acc Type</td>
					<td>Savings <m:radiobutton value="Savings"
							path="accountType" /> Current <m:radiobutton value="Current"
							path="accountType" /> Salary <m:radiobutton value="Salary"
							path="accountType" />
				</tr>
				
				<tr>
					<td><input type="submit" value="Submit"></td>
					<td><input type="reset" value="Reset"></td>
				</tr>

			</table>
		</m:form>
		${msg}
	</div>
</body>
</html>