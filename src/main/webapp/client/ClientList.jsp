<%@ page language="java" contentType="text/html"%>
<%@ taglib prefix="bean"   uri="/WEB-INF/struts-bean.tld" %>
<%@ taglib prefix="html"   uri="/WEB-INF/struts-html.tld" %>
<%@ taglib prefix="logic"  uri="/WEB-INF/struts-logic.tld" %>
<%@ taglib prefix="nested" uri="/WEB-INF/struts-nested.tld" %>

<html:html>


    <head>
        <link rel="stylesheet" type="text/css" href="/static/style2/common.css">
        <title>Client List</title>
        <html:base/>
    </head>

    <body>
        <html:errors/> 
        <h1>All Clients</h1>
        
        <table border="1" cellspacing="1">
            <tr>
	            <th>ID</th>
	            <th>First Name</th>
	            <th>Last Name</th>
	            <th>Birth Date</th>
	            <th>Phone Number</th>
	            <th>Actions</th>
            </tr>

  			<logic:present name="clientList" scope="request">
                <logic:iterate id="client" name="clientList" scope="request">
                    <tr>
	                    <td>
	                        <bean:write name="client" property="id"/>
	                    </td>
	                    <td>
	                        <bean:write name="client" property="firstName"/>
	                    </td>
	                    <td>
	                        <bean:write name="client" property="lastName"/>
	                    </td>
	                    <td>
	                        <bean:write name="client" property="birthDate" format="yyyy-MM-dd"/>
	                    </td>
	                    <td>
	                        <bean:write name="client" property="phone"/>
	                    </td>
	                    <td>
	                        <html:link page="/client/Client.do" paramId="id" paramName="client" paramProperty="id">
	                        	<html:param name="command" value="edit" />
	                            Edit
	                        </html:link>
	                        <html:link page="/client/ClientList.do" paramId="id" paramName="client" paramProperty="id">
	                        	<html:param name="command" value="delete" />
	                            Delete
	                        </html:link>
	                    </td>
                    </tr>
                </logic:iterate>
            </logic:present>

        </table>
        
		<br/>
	    <html:link page="/client/Client.do?command=create" >
	        Create
	    </html:link>
    </body>
</html:html>
