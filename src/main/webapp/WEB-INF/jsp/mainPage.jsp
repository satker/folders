<%--
  Created by IntelliJ IDEA.
  User: satke
  Date: 05.08.2018
  Time: 18:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Main page</title>
</head>
<body>

<spring:form method="post"  modelAttribute="folder" action="/enter-directory">
    Enter directory: <spring:input path="directory"/>
  <spring:button>Start search</spring:button>

</spring:form>



</body>
</html>
