<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.webjars.org/tags" prefix="wj" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="context" value="${pageContext.request.contextPath}" />

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Job seeker app</title>

    <link rel="stylesheet" type="text/css" href="resources/css/style.css">

    <%--https://github.com/webjars/webjars-taglib--%>
    <link rel='stylesheet' href='${context}<wj:locate path="css/bootstrap.min.css" relativeTo="META-INF/resources"/>'>
    <link rel='stylesheet' href='${context}<wj:locate path="css/dataTables.bootstrap4.min.css" relativeTo="META-INF/resources"/>'>
    <link rel='stylesheet' href='${context}<wj:locate path="lib/noty.css" relativeTo="META-INF/resources"/>'>
    <link rel='stylesheet' href='${context}<wj:locate path="jquery.datetimepicker.css" relativeTo="META-INF/resources"/>'>
<%--    <link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">--%>

    <%--font-awesome--%>
    <link rel='stylesheet' href='${context}<wj:locate path="css/all.min.css" relativeTo="META-INF/resources"/>'>
    <link rel='stylesheet' href='${context}<wj:locate path="css/v4-shims.min.css" relativeTo="META-INF/resources"/>'>

    <script type="text/javascript" src='${context}<wj:locate path="jquery.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src='${context}<wj:locate path="bootstrap.min.js" relativeTo="META-INF/resources"/>'></script>

    <script type="text/javascript" src='${context}<wj:locate path="jquery.dataTables.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src='${context}<wj:locate path="dataTables.bootstrap4.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src='${context}<wj:locate path="js/fontawesome.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src='${context}<wj:locate path="lib/noty.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src="${context}<wj:locate path="jquery.datetimepicker.full.min.js" relativeTo="META-INF/resources"/>"></script>
    <script type="text/javascript" src='${context}<wj:locate path="jquery.validate.min.js" relativeTo="META-INF/resources"/>'></script>
    <script type="text/javascript" src='${context}<wj:locate path="jquery-dateFormat.min.js" relativeTo="META-INF/resources"/>'></script>

    <link rel="shortcut icon" href="http://icons.iconarchive.com/icons/pelfusion/long-shadow-ios7/32/Notes-icon.png">
</head>