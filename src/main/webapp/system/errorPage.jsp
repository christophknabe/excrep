<!-- /system/errorPage.jsp   Central  JSP for reporting exceptions, which have not been reported on the input page,
	or after pressing the "Details" link beneath an error message on an input page. Knabe 2007-05-04
	
	Precondition:
	   JSP scripting variable 'exception' or request attribute "javax.servlet.error.exception" 
	   or the session attribute, set by struts_ui.CentralExceptionReporter, is a Throwable.
	   
	Parameters:
	   request parameter "short" may be true or false (default).
	
-->

<%@page contentType="text/html" isErrorPage="true" %>



<%@page import="struts_ui.CentralExceptionReporter" %>

<%@page import="java.io.*, java.util.*, multex.Failure, javax.servlet.http.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%!
    /**Returns the attribute i_key of the request i_request as a String in the
      format name = value
    */
    private String  _attribute(final HttpServletRequest i_request, final String i_key){
      return i_key + " = " + i_request.getAttribute(i_key);
    }

    /**Returns the attribute i_key of the session i_session as a String in the
      format name = value
    */
    private String  _attribute(final HttpSession i_session, final String i_key){
      return i_key + " = " + i_session.getAttribute(i_key);
    }
    //javax.servlet.http.HttpSession
%>

<%
    //fb6._any.ui.UiUtil.setJspHeaders(response);
    final String requestURI = request.getRequestURI();
%>
<html>

<head>
    <META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Fehler <%=requestURI%></title>
    <link rel="stylesheet" type="text/css" href="/static/style2/common.css">
    <link rel="stylesheet" type="text/css" href="/static/style2/jspApplication.css">
</head>
<body class="farbe">
    <h3>Leider konnten wir Ihren Befehl wegen folgenden Fehlers nicht ausführen:</h3>

    <%
    final Throwable servletException = (Throwable)request.getAttribute("javax.servlet.error.exception");

    final Throwable messagesException
    = exception!=null ? exception //stored by JSP engine
    : servletException!=null ? servletException //stored by servlet engine
    : CentralExceptionReporter.getException(session) //stored by application
    ;
    CentralExceptionReporter.setException(session, messagesException);
    final ResourceBundle responseBundle = CentralExceptionReporter.getRequestLocaleBundle(CentralExceptionReporter.BASE_NAME, request);
    %>
    <div class="inPageErrorMessage">
        <%= CentralExceptionReporter.getMessagesAsHtml(messagesException, responseBundle) %>
    </div>
    <br>
	<table width="100%" padding="0">
		<tr> 
			<td>
			    <logic:equal parameter="short" value="true">
			        <html:link page="/system/errorPage.jsp?short=false">
			           <span class="alleNavi">Technische Details zeigen</span>
			        </html:link>
			    </logic:equal>
			    <logic:notEqual parameter="short" value="true">
			        <html:link page="/system/errorPage.jsp?short=true">
			           <span class="alleNavi">Technische Details ausblenden</span>
			        </html:link>
			    </logic:notEqual>
			</td>
			<td>    
				<a href="javascript:history.go(-1);"><span class="alleNavi">Zurück</span></a>
			</td>
			<td align="right"><html:link page="/"><span class="alleNavi">Home</span></html:link></td>
		</tr>
	</table>

    <logic:notEqual parameter="short" value="true">
        <br>
        <BR>
        <H3>Stack Trace:</H3>
        <PRE><%=multex.Msg.getStackTrace(messagesException)%></PRE>
        <br>
        <H3>Request Cookies:</H3>
        <UL>
        <%
            //TODO Auf JSTL-Schleife umstellen, Knabe 2004-11-04
            final Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for(int i=0; i<cookies.length; i++) {
                    final Cookie cookie = cookies[i];
                    out.print("<LI>");
                    out.print(cookie.getName() + " = " + cookie.getValue());
                    out.print("</LI>");
                    out.println();
                }
            }
        %>
        </UL>
        <br>
        <br>
        <H3>Request Attributes:</H3>
        <UL>
        <%
            for(final java.util.Enumeration names = request.getAttributeNames();
                names.hasMoreElements();
            ){
                final String name = names.nextElement().toString();
                out.print("<LI>");
                out.print(_attribute(request, name));
                out.print("</LI>");
                out.println();
            }//for
        %>
        </UL>
        <br>
        <br>
        <H3>Session Attributes:</H3>
        <UL>
        <%
            //final javax.servlet.http.HttpSession session = request.getSession();
            for(final java.util.Enumeration names = session.getAttributeNames();
                names.hasMoreElements();
            ){
                final String name = names.nextElement().toString();
                out.print("<LI>");
                out.print(_attribute(session, name));
                out.print("</LI>");
                out.println();
            }//for
        %>
        </UL>

    </logic:notEqual>

</body>
</html>
