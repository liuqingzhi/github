<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>

<%@ page import="org.acegisecurity.ui.AbstractProcessingFilter" %>
<%@ page import="org.acegisecurity.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.acegisecurity.AuthenticationException" %>

<html>
  <head>
    <title>登录</title>
  </head>

  <body>
    <h1>登录</h1>

	<P>Valid users:
	<P>
	<P>username <b>marissa</b>, password <b>koala</b>
	<P>username <b>dianne</b>, password <b>emu</b>
	<p>username <b>scott</b>, password <b>wombat</b>
	<p>username <b>peter</b>, password <b>opal</b> (user disabled)
	<p>username <b>bill</b>, password <b>wombat</b>
	<p>username <b>bob</b>, password <b>wombat</b>
	<p>username <b>jane</b>, password <b>wombat</b>
	<p>
	
    <%-- this form-login-page form is also used as the 
         form-error-page to ask for a login again.
         --%>
    <%
    /*
    <c:if test="${not empty param.login_error}">
    <font color="red">
      Your login attempt was not successful, try again.<BR><BR>
      Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY)).getMessage() % >
    </font>
  </c:if>
  */
    %>

    <form action="<c:url value='j_acegi_security_check'/>" method="POST">
      <table>
        <tr>
        	<td>User:</td>
        	<td><input type='text' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY) %>'</c:if>/></td>
        </tr>
        <tr><td>Password:</td><td><input type='password' name='j_password'/></td></tr>
        <tr><td><input type="checkbox" name="_acegi_security_remember_me"/></td><td>Don't ask for my password for two weeks</td></tr>

        <tr><td colspan='2'><input name="submit" type="submit" /></td></tr>
        <tr><td colspan='2'><input name="reset" type="reset" /></td></tr>
      </table>

    </form>

  </body>
</html>
