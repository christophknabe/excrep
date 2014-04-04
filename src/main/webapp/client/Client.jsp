<%@ page language="java" contentType="text/html" %>
<%@ taglib prefix="bean"   uri="/WEB-INF/struts-bean.tld" %>
<%@ taglib prefix="html"   uri="/WEB-INF/struts-html.tld" %>
<%@ taglib prefix="logic"  uri="/WEB-INF/struts-logic.tld" %>
<%@ taglib prefix="nested" uri="/WEB-INF/struts-nested.tld" %>

<html:html>

    <head>
        <title>Create or edit a client</title>
        <link rel="stylesheet" type="text/css" href="/static/style2/common.css">
        <html:base/>
    </head>

    <body class="farbe">
        <h3><logic:notPresent name="struts_ui.ClientForm" property="id">Create</logic:notPresent> 
          <logic:present name="struts_ui.ClientForm" property="id">Edit</logic:present> 
          Client
        </h3>
        <html:errors/>
        
        <html:form action="/client/Client.do" method="GET">
            <html:hidden property="id"/>
            <table border="0" cellpadding="3">
                <tr>
                    <td align="right"><font class="text">ID: </font></td>
                    <td align="left"><html:hidden property="id" write="true" /></td>
                </tr>
                <tr>
                    <td align="right"><font class="text">First Name: </font></td>
                    <td align="left"><html:text property="firstName" size="30" /></td>
                </tr>
                <tr>
                    <td align="right"><font class="text">Last Name: </font></td>
                    <td align="left"><html:text property="lastName" size="30" /></td>
                </tr>
                <tr>
                    <td align="right"><font class="text">Birth Date: </font></td>
                    <td align="left"><html:text property="birthDate" size="30" /></td>
                    <td align="left"><font class="text">[<html:hidden property="dateFormat" write="true" />]</font></td>
                </tr>
                <tr>
                    <td align="right"><font class="text">Phone Number: </font></td>
                    <td align="left"><html:text property="phone" size="30" /></td>
                </tr>
                <tr>
                    <td><html:submit property="command" value="Save" title="Save the Client with the data given above"/></td>
                    <td><html:submit property="command" value="Reset" title="Reset the form to null values in order to create a new Client"/></td>
                    <logic:present name="struts_ui.ClientForm" property="id">
                    	<td>
                        	<html:submit property="command" value="Delete" title="Delete the shown Client"/>
                        </td>
                    </logic:present>
                    <td><html:submit property="command" value="List" title="List all Clients"/></td>
                </tr>
            </table>
        </html:form>

    </body>
</html:html>


