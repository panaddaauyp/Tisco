<%-- 
    Document   : main
    Created on : Dec 14, 2018, 11:38:12 AM
    Author     : Chalermpol
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.servletContext.contextPath}/assets/img/Logo-D-tax_resize.png" />
<meta http-equiv="refresh" content="<%=session.getMaxInactiveInterval() - 60%>;url=${pageContext.servletContext.contextPath}/logout.htm"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/uikit.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/font-style-rsu.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/uikit-datepicker.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/jquery.loadingModal.css" rel="stylesheet" type="text/css"/>


<link href="${pageContext.servletContext.contextPath}/assets/js/sweetalert2/sweetalert2.min.css" rel="stylesheet" type="text/css"/><!--swal-->
<script src="${pageContext.servletContext.contextPath}/assets/js/sweetalert2/promise.min.js" type="text/javascript"></script><!--swal-->
<script src="${pageContext.servletContext.contextPath}/assets/js/sweetalert2/sweetalert2.min.js" type="text/javascript"></script><!--swal-->



<!--<script src="http://172.27.130.23${pageContext.servletContext.contextPath}/assets/js/jquery.min2.1.3.js" type="text/javascript"></script>-->
<!--<script src="https://code.jquery.com/jquery-3.3.1.js" type="text/javascript"></script>-->
<!--<script src="${pageContext.servletContext.contextPath}/assets/js/jquery-3.3.1.js" type="text/javascript"></script>-->
<script src="${pageContext.servletContext.contextPath}/assets/js/jquery.min2.1.3.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/v2/uikit.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/uikit.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/uikit-icons.min.js" type="text/javascript"></script>
<link href="${pageContext.servletContext.contextPath}/assets/css/font-style-rsu.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/master.css" rel="stylesheet" type="text/css"/>
<script src="${pageContext.servletContext.contextPath}/assets/js/function.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/jquery.livequery.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/jquery-maskmoney/jquery.maskMoney.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/numeraljs/numeral.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/numeral.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/global_functions.js" type="text/javascript"></script>

<!--<script src="${pageContext.servletContext.contextPath}/assets/js/tableBlob.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/tableFileSaver.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/tableexport.min.js" type="text/javascript"></script>-->
<script src="${pageContext.servletContext.contextPath}/assets/js/xlsx.core.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/FileSaver.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/tableexport.min.js" type="text/javascript"></script>
<link href="${pageContext.servletContext.contextPath}/assets/css/tableexport.css" rel="stylesheet" type="text/css"/>

<script src="${pageContext.servletContext.contextPath}/assets/js/dataTables.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/dataTables.buttons.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-buttons.flash.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-buttons.html5.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-buttons.print.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-jszip.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-pdfmake.min.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-vfs_fonts.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/datatable-jquery.dataTables.min.js" type="text/javascript"></script>
<link href="${pageContext.servletContext.contextPath}/assets/css/datatable-buttons.dataTables.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/datatable-jquery.dataTables.min.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.servletContext.contextPath}/assets/css/all.css" rel="stylesheet" integrity="sha384-gfdkjb5BdAXd+lj+gudLWI+BXq4IuLW5IT+brZEZsLFm++aCMlF1V92rMkPaX4PP" crossorigin="anonymous">

<link href="${pageContext.servletContext.contextPath}/assets/lib/datatables/css/jquery.dataTables.css" rel="stylesheet" type="text/css"/>
<script src="${pageContext.servletContext.contextPath}/assets/lib/datatables/js/jquery.dataTables.js" type="text/javascript"></script>
<link href="${pageContext.servletContext.contextPath}/assets/css/left_menu.css" rel="stylesheet" type="text/css"/>
<script src="${pageContext.servletContext.contextPath}/assets/js/bootstrap.3.3.5.min.js" type="text/javascript"></script>

<script src="${pageContext.servletContext.contextPath}/assets/js/datepicker.js" type="text/javascript"></script><!--datepicker-->

<script src="${pageContext.servletContext.contextPath}/assets/js/highcharts.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/highcharts-exporting.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/highcharts-export-data.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/highcharts-series-label.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/utility.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/validate.js" type="text/javascript"></script>
<script src="${pageContext.servletContext.contextPath}/assets/js/jquery.loadingModal.js" type="text/javascript"></script>

<style type="text/css">
    .css-notic{
        float: right;
        margin-top: 1em;         
        margin-right: 2em;         
        position: absolute;
        right: 0px;
        padding: 10px;    
        z-index: 1;
    }
    .css-notic .fa-stack[data-count]:after{
        position:absolute;
        right:0%;
        top:1%;
        content: attr(data-count);
        font-size:60%;
        padding:.3em;
        border-radius:999px;
        line-height:.75em;
        color: white;
        background:red;
        text-align:center;
        min-width:2em;
        font-weight:bold;
    }
    .css-notic .fa-1x{
        font-size: 1.3em;
        cursor: pointer;
    }
    .css-notic .fa-circle{
        color: #337ab7;
    }
    #popupNotification{
        min-width: fit-content !important;
        min-height: fit-content !important;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        $("#menu-toggle").click(function (e) {
            e.preventDefault();
            $("#wrapper").toggleClass("toggled");
        });
        $("#menu li").find("i").addClass('fa-stack-1x');
        if (sessionStorage.getItem("root") > -1) {
            $("#menu>li").eq(sessionStorage.getItem("root")).find("ul").slideToggle();
            $("#menu>li").eq(sessionStorage.getItem("root")).find("ul").eq(sessionStorage.getItem("parent") - 1).find("li").eq(sessionStorage.getItem("menuSelected")).addClass('active');
        } else {
            $("#menu>li").eq(sessionStorage.getItem("menuSelected")).addClass('active');
        }
        $("input[type=text]").prop('autocomplete', "off");
        if ('${sessionScope.companyId}' !== '' && '${sessionScope.subCompId}' !== '') {
            var sizeNotification = text2Number('${sessionScope.sizeNotification}');
            if (sizeNotification > 0) {
                $(".uk-scope").before('<div class="css-notic"><span class="fa-stack fa-1x has-badge" data-count="' + sizeNotification + '">' +
                        '<i class="fa fa-circle fa-stack-2x"></i>' +
                        '<i class="fa fa-bell fa-stack-1x fa-inverse"></i></span></div>');
            } else {
                $(".uk-scope").before('<div class="css-notic"><span class="fa-stack fa-1x">' +
                        '<i class="fa fa-circle fa-stack-2x"></i>' +
                        '<i class="fa fa-bell fa-stack-1x fa-inverse"></i><span></div>');
            }
        }
        $("body").on("click", '.css-notic', function (data) {
            $("body").append('<div id="popupNotification" class="uk-flex-top" uk-modal></div>');
            $("#popupNotification").load("${pageContext.servletContext.contextPath}/popup/notification.htm");
            var modal = UIkit.modal("#popupNotification");
            modal.show();
        });
    });
</script>
