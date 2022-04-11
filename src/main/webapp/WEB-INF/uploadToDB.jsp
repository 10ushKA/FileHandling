<%--
  Created by IntelliJ IDEA.
  User: Asus
  Date: 29.03.2022
  Time: 11:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload files</title>
</head>
<body>
<div style="padding: 5px; color: red;font-style:italic;">
    ${errorMessage}
</div>

<h2>Upload Files</h2>

<from method ="post" action = "/UploadToDBServlet" enctype = "multipart/form-data">
    Select to upload:
    <br/>
    <input type = "file" name="file" />
    <br/>
    <br/>
    Description:
    <br/>
    <input type = "text" name = "description" size = "100" />
    <br/>
    <br/>
    <input type = "submit" value = "Upload" />
</from>
</body>
</html>
