<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <meta name="_csrf" content="${fn:escapeXml(_csrf.token)}"/>
    <meta name="_csrf_header" content="${fn:escapeXml(_csrf.headerName)}"/>

    <meta name="auto" content="width=device-width, initial-scale=1">
    <title>Auto manager app</title>

    <base href="${pageContext.request.contextPath}/"/>

    <link rel="stylesheet" type="text/css" href="static/css/style.css">

    <link rel="stylesheet" type="text/css" href="static/bootstrap/4.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="static/datatables/css/dataTables.bootstrap4.css">
    <link rel="stylesheet" type="text/css" href="static/noty/lib/noty.css">
    <link rel="stylesheet" type="text/css" href="static/Eonasdan-bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css">

    <link rel="stylesheet" type="text/css" href="static/font-awesome/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="static/font-awesome/css/v4-shims.min.css">

    <link rel="shortcut icon" href="static/images/icon.png">

    <script type="text/javascript" src="static/jquery/jquery.min.js" defer></script>
    <script type="text/javascript" src="static/jquery-validation/jquery.validate.min.js" defer></script>
    <script type="text/javascript" src="static/jquery-dateFormat/jquery-dateFormat.min.js" defer></script>

    <script type="text/javascript" src="static/popper.js/umd/popper.min.js" defer></script>
    <script type="text/javascript" src="static/bootstrap/js/bootstrap.min.js" defer></script>

    <script type="text/javascript" src="static/datatables/js/jquery.dataTables.min.js" defer></script>
    <script type="text/javascript" src="static/datatables/js/dataTables.bootstrap4.min.js" defer></script>

    <script type="text/javascript" src="static/momentjs/moment.js" defer></script>
    <script type="text/javascript" src="static/Eonasdan-bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" defer></script>

    <script type="text/javascript" src="static/noty/lib/noty.min.js" defer></script>
    <script type="text/javascript" src="static/font-awesome/js/fontawesome.min.js" defer></script>
</head>

<script type="text/javascript">
    const dataUrl = '${dataUrl}';
</script>