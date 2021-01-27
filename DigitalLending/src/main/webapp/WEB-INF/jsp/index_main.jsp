<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>หน้าหลัก</title>
        <jsp:include page="main.jsp"/>
        <script type="text/javascript">
            $(document).ready(function () {
                loaderHide();
            });
        </script>
    </head>
    <body>        
        <jsp:include page='${leftMenu}'/>
        <jsp:include page="dashboard/dashboard_main.jsp"/>
        <%--<jsp:include page="print/search_pdf_xml.jsp"/>--%>        
    </body>
</html>
