<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="main2.jsp"/>
<!--<script src="${pageContext.servletContext.contextPath}/assets/js/jquery.ajax-cross-origin.min.js" type="text/javascript"></script>-->
<style type="text/css">
    .uk-padding-remove{
        background: #ffffff;
        /*border: 1px solid black;*/
        text-align: left;
    }
    .uk-search-default{
        width: 100%;
    }
    .uk-padding-remove span{
        margin-right: 8px;
    }
    .uk-modal-dialog{
        min-width: 800px;
        min-height: 600px;
    }
    #modal-example .uk-card-body{
        cursor: pointer;
        border: 2px solid #999999;
        border-radius: 7px;
        box-shadow: none;
        min-height: 150px;
        min-width:  80px;
        padding: 0px;
    }
    table span.uk-icon, span.uk-icon{
        cursor: pointer;
    }
    #modal-example svg{
        width: 80px;
        height: 120px;
        margin:  0px;
    }
    #modal-example .uk-modal-title{
        text-align: center;
        padding-bottom: 20px; 
    }
    #modal-example .text-large{
        font-size: 22px;
    }
    .uk-section {
        padding-top: 1%; 
        padding-bottom: 1%; 
    }
    .uk-navbar-container {
        /*padding-right: 0px;*/
        /*      padding-top: 1%;
              padding-bottom: 1%;*/
        background: #ffd737 !important;
    }
    .uk-navbar-nav{
        padding-left:30px;
    }
    .uk-navbar li 
    {
        border-bottom:1px solid #fff;
        padding:5px;
        margin:2px;

    }
    .uk-navbar li:hover 
    {
        border-bottom:1px solid #999999;
        padding:5px;
        margin:2px;
    }
    .uk-link-heading{
        min-height: 40px !important;
    }
    .menu-heading{
        height: 55px;
        line-height: 65px;
    }
    .login-button, .btn-contact{
        border-radius: 5px;
        width: 120px;
    }
    .regis-button{
        border-radius: 5px;
    }
    .menu-active{
        border-bottom: 1px solid #999999 !important;
    }
    .lang-select{
        background: #ffffff00;
        border: 0px;
    }

    /*   Pricing*/
    * {
        box-sizing: border-box;
    }

    .columns {
        float: left;
        width: 24%;
        padding: 3px;
    }
    .left-columns {
        float: left;
        width: 25%;
        padding-top: 1px;
    }

    .columns .price {
        list-style-type: none;
        /*border: 1px solid #eee;*/
        margin: 0;
        padding: 0;
        -webkit-transition: 0.3s;
        transition: 0.3s;
    }

    .columns .price{
        box-shadow: 0px 0px 20px 0px rgba(0,0,0,0.2)
    }

    .columns .price .header {
        /*background-color: #111;*/
        /*color: white;*/
        font-size: 25px;      
        height:80px;
        padding: 28px;
    }

    .columns .price li {
        /*border-bottom: 1px solid #eee;*/
        padding: 12px;
        text-align: center;
    }

    .columns .price .grey {
        background-color: #eee;
        font-size: 20px;
    }
    .left-columns .price {
        list-style-type: none;
        /*border: 1px solid #eee0;*/
        margin: 0;
        padding: 0;
        -webkit-transition: 0.3s;
        transition: 0.3s;
    }
    .left-columns .price li {
        /*border-bottom: 1px solid #eee0;*/
        padding: 12px;
        text-align: right;
    }
    .button {
        /*background-color: #4CAF50;*/
        border: none;
        /*color: white;*/
        padding: 10px 25px;
        text-align: center;
        text-decoration: none;
        font-size: 18px;
        border-radius: 5px;
        width: 120px;
    }
    .left-header{
        height:80px;
        border: 0px;
    }
    .price-month-header{
        height: 87px;
    }
    .price-year-header{      
        height: 87px;   
    }

    @media only screen and (max-width: 600px) {
        .columns {
            width: 100%;
        }
    }
    .tickgreen{
        color: green;
        font-weight: bold;
    }
    .bg-gray{
        background: #f4f4f4;
    }
    .css-home{
        background-image: url("${pageContext.servletContext.contextPath}/assets/img/landing/bg/BG.svg");
        width: 100%; 
        height: 90%;
        background-position: center;
        background-repeat: no-repeat;
        background-size: cover;
    }
    .css-home .css-icon{
        text-align: right;
        position: relative;
        /*width: 50%;*/
        height: 90%;
    }
    .bg-feature{
        background-image: url("${pageContext.servletContext.contextPath}/assets/img/landing/bg-feature/BG-feature.svg");
        height: 50%;
        width: 100%; 
        background-position: center;
        background-repeat: no-repeat;
        background-size: cover;
    }
    .bg-feature .feature-icon{
        text-align: center;
        width: 50%;
        height: 100%;
    }
    .css-trail div:nth-child(1){
        margin-top: 100px;
    }
    .css-trail div{
        margin: 10px;
        padding-left: 100px;
    }
    .how-tool{
        padding: 10px;
        cursor: pointer;
    }
    .contact-form{
        position: absolute;
        top: 17%;
        right: 8.3%;
        z-index: 1;
        background: white;
        padding-bottom: 10px;
    }
    .remark {
        resize: none;
    }
    .contact-title{
        padding-bottom: 0;
    }
    .contact-info{
        background-color: #ffd737;
        height: 40%;
    }
    .menu-footer{
        font-size: 1.5rem;
        line-height: inherit;
    }
    .footer-menu-box{
        padding-top: 10%;
    }
    .footer-header{
        font-size: 1.5rem;
    }
    .subject-title{
        font-size: 2rem;      
    }
    #popupLogin{
        min-width: fit-content !important;
        min-height: fit-content !important;
    }
</style>
<script type="text/javascript">
    $(document).ready(function () {
        $(".uk-link-heading").click(function () {
            $(".uk-link-heading").parent().removeClass("menu-active");
            $(this).parent().addClass("menu-active");
        });
        $('a[href^="#"]').on('click', function (e) {
            e.preventDefault();
            var target = this.hash;
            $target = $(target);
            $('html, body').stop().animate({
                'scrollTop': ($target.offset().top - 100)
            }, 700, 'swing', function () {
                window.location.hash = target;
            });
        });
        $("#btn-login").click(function () {
            loaderShow();
            redirectPost("${pageContext.servletContext.contextPath}/user/login.htm", "post");
        });
        $(".how-tool").click(function () {
            var link = location.protocol + "//" + location.hostname + ":" + location.port + "/document/TOOL/D1-De-Tax User manual v.1.0.0.1.pdf";
            window.open(link, '_blank');
        });
        $("body").on("click", '#popupLogin .btn_regis_pack', function () {
            $('#popupLogin .require').removeClass('error-field');
            var flag = false;
            $('#popupLogin .require').each(function () {
                if ($(this).val() === '' && !$(this).prop("disabled")) {
                    $(this).addClass('error-field');
                    flag = true;
                }
            });
            if (flag) {
                swal("แจ้งเตือน!", "กรุณากรอกข้อมูลให้ครบ", "info");
                return;
            }
            var data = {packageId: $("#popupLogin [name=packageId]").val(), username: $("#popupLogin [name=username]").val(),
                password: $("#popupLogin [name=password]").val(), packType: $('input[name=packType]:checked').val()};
            loaderShow();
            $.post('${pageContext.servletContext.contextPath}/popup/save_package.htm', data, function (data) {
                loaderHide();
                if (data.status === '1') {
                    swal('สมัครสมาชิก', data.errorMsg, 'success');
                    $("#popupLogin .btn_cancel_pack").click();
                } else {
                    swal('สมัครสมาชิก', data.errorMsg, 'error');
                }
            });
        });
        $("[name=contactTel]").keypress(function (event) {
            keyOnLyPhoneNum(event);
        });
            
        $("body").on('click', ".btn-contact", function () {
            
            var resultEmail = validateEmail($("[name=contactEmail]"));
            if (!resultEmail) {
                swal('กรอกอีเมล์ให้ถูกต้อง');
                $(this).val("");
                $("[name=contactEmail]").addClass('error-field');
                return false;
            }
            
            
            var resultPhone = changeOnLyPhoneNum($("[name=contactTel]"));
            if (!resultPhone) {
                swal('กรอกเบอร์โทรศัพท์ให้ถูกต้อง');
                $(this).val("");
                $("[name=contactTel]").addClass('error-field');
                return false;
            }
            if('' === $("[name=contactSubject]").val()){
                swal('เลือกหัวข้อที่ต้องการติดต่อ');
                $("[name=contactSubject]").addClass('error-field');
                return false;
            }
            
            if(requireFildPopUp('contact')){
                $('#spinner').show();
                var param = {contactName: $("[name=contactName]").val(), contactEmail: $("[name=contactEmail]").val()
                    , contactTel: $("[name=contactTel]").val(), contactSubject: $("[name=contactSubject]").val()
                    , contactRemark: $("[name=contactRemark]").val()
                };
                $.post('${pageContext.servletContext.contextPath}/contact_us.htm', {param: JSON.stringify(param)}, function (data) {
                    $('#spinner').hide();
                    $("[name=contactName]").val('');
                    $("[name=contactEmail]").val('');
                    $("[name=contactTel]").val('');
                    $("[name=contactSubject]").val('');
                    $("[name=contactRemark]").val('');
                    if("1" === data.status){                           
                        swal('ส่งข้อมูล', 'ส่งข้อมูลสำเร็จ', 'success');
                    }else{
                        swal('ส่งข้อมูล', 'ส่งข้อมูลไม่สำเร็จ', 'error');
                    }
                });
            }           
            
        });
    });
    function selectPackage(packageId) {
        $("body").append('<div id="popupLogin" class="uk-flex-top" uk-modal></div>');
        $("#popupLogin").load("${pageContext.servletContext.contextPath}/popup/login_package.htm?packageId=" + packageId);
        var modal = UIkit.modal("#popupLogin");
        modal.show();

    }
    
    function validateEmail(sEmail) {
        var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
        if (emailReg.test(sEmail.val())) {
            return true;
        } else {
            return false;
        }
    }
</script>
<!--#ffd737-->
<div class="uk-scope">
    <div class="uk-child-width-1-1@s uk-grid-collapse" uk-grid>
        <!--<div>-->
        <div uk-sticky="media: 960" class="uk-navbar-container tm-navbar-container uk-sticky uk-sticky-fixed" style="position: fixed; top: 0px; width: 100%;">
            <div class="uk-container uk-container-expand">
                <nav class="uk-navbar">
                    <div class="uk-navbar">
                        <a href="#" class="uk-navbar-item uk-logo">
                            <img class="uk-border" width="200" src="${pageContext.servletContext.contextPath}/assets/img/Logo-D-tax.png" alt="" >
                            <!--                                <img uk-svg="" src="../images/uikit-logo.svg" class="uk-margin-small-right" hidden="true">
                                                                <svg width="28" height="34" viewBox="0 0 28 34" xmlns="http://www.w3.org/2000/svg" class="uk-margin-small-right uk-svg" data-svg="../images/uikit-logo.svg">
                                                                    <polygon fill="#fff" points="19.1,4.1 13.75,1 8.17,4.45 13.6,7.44 "></polygon>
                                                                    <path fill="#fff" d="M21.67,5.43l-5.53,3.34l6.26,3.63v9.52l-8.44,4.76L5.6,21.93v-7.38L0,11.7v13.51l13.75,8.08L28,25.21V9.07 L21.67,5.43z"></path>
                                                                </svg> UIkit-->
                        </a>
                    </div> 
                    <div class="uk-navbar">
                        <ul class="uk-navbar-nav uk-visible@m menu-heading">
                            <li class="menu-heading"><a class="uk-link-heading" href="#home">Home</a></li> 
                            <li class="menu-heading"><a class="uk-link-heading feature-menu" href="#features">Features</a></li> 
                            <li class="menu-heading"><a class="uk-link-heading" href="#benefits">Benefit</a></li>
                            <li class="menu-heading"><a class="uk-link-heading" href="#pricing">Pricing</a></li>
                            <li class="menu-heading"><a class="uk-link-heading" href="#contact">Contact us</a></li>
                        </ul> 
                    </div>
                    <div class="uk-navbar-right">
                        <button class="uk-button uk-button-secondary login-button" id="btn-login" type="button">เข้าสู่ระบบ</button><!--LOGIN-->
                        <div class="uk-form-controls" style="padding-left: 10px;">
                            <select class="uk-select lang-select" id="form-stacked-select">
                                <option>TH</option>
                                <option>EN</option>
                            </select>
                        </div>
                    </div>
                </nav>
            </div>
        </div>
        <!--</div>-->
        <section id="home" class="section-content">
            <div class="css-home">
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-2-5 uk-text-left css-trail">
                        <div class="uk-width-1-1">
                            <span class="uk-text-bold uk-text-large">DE-TAX</span>
                        </div>
                        <div class="uk-width-1-1">
                            <span class="uk-text-bold">ระบบที่ช่วยให้การออกใบเอกสารต่างๆ ภายใต้นามบรษัท</span>
                        </div>
                        <div class="uk-width-1-1">
                            <span class="uk-text-bold">ของคุณ เป็นเรื่องง่าย ช่วยเพิ่มประสิทธิภาพในการทำงาน</span>
                        </div>
                        <div class="uk-width-1-1">
                            <span class="uk-text-bold">ของคุณให้มีความทันสมัย สะดวก และรวดเร็ว</span>
                        </div>
<!--                        <div class="uk-width-1-1">
                            <a href="${pageContext.servletContext.contextPath}/comp/register.htm"><button class="uk-button uk-button-secondary uk-width-2-5 regis-button" type="button">ทดลองใช้งาน</button></a>
                            <span class="how-tool" uk-icon="info" title="คู่มือการใช้งาน"></span>
                        </div>-->
                    </div>
                    <div class="uk-width-3-5@s uk-text-right">
                        <img class="uk-border css-icon" src="${pageContext.servletContext.contextPath}/assets/img/landing/hero/hero.svg" />
                        <!--</div>-->
                    </div>
                </div>
            </div>
        </section>
        <section id="features"> 
            <div class="uk-tile uk-tile-default">
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-1 uk-padding-remove-left">
                        <p class="subject-title" style="text-align: center">FEATURES</p>
                    </div>
                </div>
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/dashboard/dashboard.svg" />
                            <!--<img class="uk-border-circle" width="100" height="100" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxNi4wLjQsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+DQo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkViZW5lXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4Ig0KCSB3aWR0aD0iMTIwcHgiIGhlaWdodD0iMTIwcHgiIHZpZXdCb3g9IjAgMCAxMjAgMTIwIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCAxMjAgMTIwIiB4bWw6c3BhY2U9InByZXNlcnZlIj4NCjxyZWN0IGZpbGw9IiNGRkZGRkYiIHdpZHRoPSIxMjAiIGhlaWdodD0iMTIwIi8+DQo8Zz4NCgk8cGF0aCBmaWxsPSIjRTBFMEUwIiBkPSJNMTA5LjM1NCw5OS40NzhjLTAuNTAyLTIuODA2LTEuMTM4LTUuNDA0LTEuOTAzLTcuODAxYy0wLjc2Ny0yLjM5Ny0xLjc5Ny00LjczMi0zLjA5My03LjAxMQ0KCQljLTEuMjk0LTIuMjc2LTIuNzc4LTQuMjE3LTQuNDU1LTUuODIzYy0xLjY4MS0xLjYwNC0zLjcyOS0yLjg4Ny02LjE0OC0zLjg0NmMtMi40MjEtMC45NTgtNS4wOTQtMS40MzgtOC4wMTctMS40MzgNCgkJYy0wLjQzMSwwLTEuNDM3LDAuNTE2LTMuMDIsMS41NDVjLTEuNTgxLDEuMDMyLTMuMzY3LDIuMTgyLTUuMzU1LDMuNDVjLTEuOTksMS4yNzEtNC41NzgsMi40MjEtNy43NjUsMy40NTENCgkJQzY2LjQxLDgzLjAzNyw2My4yMSw4My41NTIsNjAsODMuNTUyYy0zLjIxMSwwLTYuNDEtMC41MTUtOS41OTgtMS41NDZjLTMuMTg4LTEuMDMtNS43NzctMi4xODEtNy43NjUtMy40NTENCgkJYy0xLjk5MS0xLjI2OS0zLjc3NC0yLjQxOC01LjM1NS0zLjQ1Yy0xLjU4Mi0xLjAyOS0yLjU4OC0xLjU0NS0zLjAyLTEuNTQ1Yy0yLjkyNiwwLTUuNTk4LDAuNDc5LTguMDE3LDEuNDM4DQoJCWMtMi40MiwwLjk1OS00LjQ3MSwyLjI0MS02LjE0NiwzLjg0NmMtMS42ODEsMS42MDYtMy4xNjQsMy41NDctNC40NTgsNS44MjNjLTEuMjk0LDIuMjc4LTIuMzI2LDQuNjEzLTMuMDkyLDcuMDExDQoJCWMtMC43NjcsMi4zOTYtMS40MDIsNC45OTUtMS45MDYsNy44MDFjLTAuNTAyLDIuODAzLTAuODM5LDUuNDE1LTEuMDA2LDcuODM1Yy0wLjE2OCwyLjQyMS0wLjI1Miw0LjkwMi0wLjI1Miw3LjQ0DQoJCWMwLDEuODg0LDAuMjA3LDMuNjI0LDAuNTgyLDUuMjQ3aDEwMC4wNjNjMC4zNzUtMS42MjMsMC41ODItMy4zNjMsMC41ODItNS4yNDdjMC0yLjUzOC0wLjA4NC01LjAyLTAuMjUzLTcuNDQNCgkJQzExMC4xOTIsMTA0Ljg5MywxMDkuODU3LDEwMi4yOCwxMDkuMzU0LDk5LjQ3OHoiLz4NCgk8cGF0aCBmaWxsPSIjRTBFMEUwIiBkPSJNNjAsNzguMTZjNy42MiwwLDE0LjEyNi0yLjY5NiwxOS41Mi04LjA4OGM1LjM5Mi01LjM5Myw4LjA4OC0xMS44OTgsOC4wODgtMTkuNTE5DQoJCXMtMi42OTYtMTQuMTI2LTguMDg4LTE5LjUxOUM3NC4xMjYsMjUuNjQzLDY3LjYyLDIyLjk0Niw2MCwyMi45NDZzLTE0LjEyOCwyLjY5Ny0xOS41MTksOC4wODkNCgkJYy01LjM5NCw1LjM5Mi04LjA4OSwxMS44OTctOC4wODksMTkuNTE5czIuNjk1LDE0LjEyNiw4LjA4OSwxOS41MTlDNDUuODcyLDc1LjQ2NCw1Mi4zOCw3OC4xNiw2MCw3OC4xNnoiLz4NCjwvZz4NCjwvc3ZnPg0K" alt="">-->
                            <p class="footer-header">Smart report / dashboard</p>
                            <p>รองรับการออกแบบรายงานภาษีซื้อ/ขาย สามารถดูสถานะใบกำกับแบบ real time บน Smart dashboard ซึ่งจะช่วยให้การบริหารงานง่ายและมีประสิทธิภาพมากขึ้น</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/erp/ERP.svg" />
                            <p class="footer-header">Smart integration</p>
                            <p>สามารถเชื่อมต่อกับระบบ ERP หรือระบบบัญชีด้วย API เช่น เชื่อมฟังก์ชันสั่งซื้อ หรือ ฟังก์ชันการวางบิล</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/sign/sign.svg" alt="">
                            <p class="footer-header">Smart signing</p>
                            <p>การลงลายมือชื่ออิเล็กทรอนิกส์ เพื่อยืนยันความถูกต้องของข้อมูล รองรับมาตรฐานความปลอดภัย ระดับสูง (FIPS 140-3)</p>
                        </div>
                    </div>
                </div>
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/document/document.svg" alt="">
                            <p class="footer-header">Smart Form</p>
                            <p>แบบฟอร์มสำหรับคีย์ข้อมูล ได้ถูกออกแบบให้เป็นไปตามข้อกำหนดประมวลรัษฎากร ป้องกันให้ไม่เกิดความผิดพลาดที่อาจเกิดขึ้นจากการ human error อีกทั้งยัง export ออกมาได้ในหลายรูปแบบ</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/payment/payment.svg" alt="">
                            <p class="footer-header">Smart payment</p>
                            <p>เชื่อมกับ Payment gateway เพื่อสะดวกต่อการชำระเงิน อำนวยความสะดวกในการชำระเงินให้แก่ ผู้ใช้บริการ</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/download/Download.svg" alt="">
                            <p class="footer-header">Smart archive</p>
                            <p>บริการค้นหาข้อมูลย้อนหลัง ที่สามารถตั้งค่าการเก็บเอกสารตามนะโยบายบริษัทหรือตามที่กรมสรรพากรกำหนด</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bg-feature">
                <div class="uk-panel uk-panel-box uk-text-center">
                    <img class="uk-border feature-icon" src="${pageContext.servletContext.contextPath}/assets/img/landing/mock/MOCK2@3x.png" />
                </div>
            </div>
        </section>
        <section id="benefits"> 
            <div class="uk-tile uk-tile-default">
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-1 uk-padding-remove-left">
                        <p class="subject-title" style="text-align: center">BENEFITS</p>
                    </div>
                </div>
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/document/document.svg" /><br/><br/><br/>
                            <span class="uk-text-large">ลดค่าใช้จ่ายในการจัดทำ</span>
                            <p>ฟอร์มที่ตรงตามประมวลรัษฎากรไม่ผิดรูปแบบ เพิ่มความสะดวกแก่ผู้ซื้อในการรับ ใบกำกับ/ใบเสร็จ</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/delivery/delivery.svg" alt=""><br/><br/><br/>
                            <span class="uk-text-large">ลดค่าใช้จ่ายในการจัดส่ง</span>
                            <p>สะดวกสบายในการจัดส่งเอกสารไม่ต้องเสียค่า Messenger และค่าน้ำมันต่างๆ</p>
                        </div>
                    </div>
                    <div class="uk-width-1-3">
                        <div class="uk-panel uk-panel-box uk-text-center" style="width: 70%; margin: auto">
                            <img class="uk-border" width="100" height="100" src="${pageContext.servletContext.contextPath}/assets/img/download/Download.svg" alt=""><br/><br/><br/>
                            <span class="uk-text-large">ลดค่าใช้จ่ายในการจัดเก็บ</span>
                            <p>ประหยัดค่าจัดเก็บเอกสารในโกดัง สะดวกต่อการค้นหาเอกสารย้อนหลัง</p>
                        </div>
                    </div>
                </div>            
            </div>
        </section>
        <section id="pricing"> 
            <div class="uk-tile uk-tile-default">
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-1 uk-padding-remove-left">
                        <p class="subject-title" style="text-align: center">PRICING</p>
                    </div>
                </div>
                <div class="left-columns">
                    <ul class="price">
                        <li class="left-header"></li>
                        <li class="price-month-header"></li>
                        <li class="price-month-header"></li>
                        <li class="">จำนวนผู้ใช้งาน</li>
                        <li class="bg-gray">จำนวนผู้ซื้อ-ขาย (ราย)</li>
                        <li class="">ใบแจ้งหนี้อิเล็กทรอนิกส์</li>
                        <li class="bg-gray">ใบกำกับภาษีอิเล็กทรอนิกส์</li>
                        <li class="">ใบเพิ่มหนี้ / ใบลดหนี้อิเล็กทรอนิกส์</li>
                        <li class="bg-gray">ใบสั่งซื้อ</li>
                        <li class="">ใบวางบิล</li>
                        <li class="bg-gray">นำเข้าข้อมูลในรูปแบบไฟล์ CSV</li>
                        <li class="">ออกรายงานในรูปแบบ Excel</li>
                        <li class="bg-gray">หน้าจอ Dashboard แสดงข้อมูลการซื้อขาย</li>
                        <li class="">ระบบจัดเก็บเอกสาร</li>
                    </ul>
                </div>
                <c:forEach items="${list}" var="item" varStatus="loop">
                    <div class="columns">
                        <ul class="price">
                            <li class="header bg-gray" style="color: #4a4a4a">${item.packageName}</li>
                            <li class="" style="background:${item.attribute4}; color: white;"><span style="font-size: 30px; color: white"><fmt:formatNumber value="${item.pricePerMonth}" pattern="#,##0"/></span><br>บาท / เดือน</li>
                            <li class="bg-gray"><span style="font-size: 30px; color: #4a4a4a"><fmt:formatNumber value="${item.pricePerYear}" pattern="#,##0"/></span><br><span style="color: #4a4a4a">บาท / ปี</span></li>
                            <li><fmt:formatNumber value="${item.numUser}" pattern="#,##0"/></li>
                            <li class="bg-gray"><fmt:formatNumber value="${item.numCustomer}" pattern="#,##0"/></li>
                            <li>
                                <span class="tickgreen">
                                    <c:if test = "${item.flagInvoice == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.flagInvoice != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li class="bg-gray">
                                <span class="tickgreen">
                                    <c:if test = "${item.flagTaxReceipt == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.flagTaxReceipt != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li>
                                <span class="tickgreen">
                                    <c:if test = "${item.flagDcnCln == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.flagDcnCln != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li class="bg-gray">
                                <span class="tickgreen">
                                    <c:if test = "${item.attribute1 == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.attribute1 != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li>
                                <span class="tickgreen">
                                    <c:if test = "${item.attribute2 == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.attribute2 != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li class="bg-gray">
                                <span class="tickgreen">
                                    <c:if test = "${item.flagImport == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.flagImport != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li>
                                <span class="tickgreen">
                                    <c:if test = "${item.attribute3 == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.attribute3 != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li class="bg-gray">
                                <span class="tickgreen">

                                    <c:if test = "${item.flagDashboard == 'Y'}">
                                        <i class="fas fa-check"></i>
                                    </c:if>
                                    <c:if test = "${item.flagDashboard != 'Y'}">
                                        <i class="fas fa-times"></i>
                                    </c:if>
                                </span>
                            </li>
                            <li>                                
                                <c:if test = "${item.flagArchive == 'Y'}">
                                    <span class="">${item.archiveSpace} GB</span>
                                </c:if>
                                <c:if test = "${item.flagArchive != 'Y'}">
                                    <span class="tickgreen"><i class="fas fa-times"></i></span>
                                    </c:if>                                
                            </li>
                            <li class=""><button class="uk-button uk-button-secondary login-button" type="button" onclick="selectPackage('${item.packageId}')">เลือก</button></li>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </section>
        <section id="contact"> 
            <div class="uk-tile uk-tile-default contact-title">
                <div class="uk-grid-small" uk-grid>
                    <div class="uk-width-1-1 uk-padding-remove-left">
                        <p class="subject-title" style="text-align: center">CONTACT</p>
                    </div>
                </div>
                <div class="uk-grid-small" uk-grid>
                    <div class="map">
                        <iframe width="100%" height="100%" frameborder="0" scrolling="no" marginwidth="0"
                                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3876.7618825359286!2d100.54754066742193!3d13.672238041824913!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x30e2a1ee4fd18323%3A0xb95e34be127baec6!2sDataOne+Asia(Thailand)+Co.%2CLtd.!5e0!3m2!1sth!2sth!4v1549250615580"
                                style="border:0">
                        </iframe>
                        <div class="container">
                            <div class="row contact-form">
                                <div class="col-md-12">
                                    <div class="section-title center">
                                        <h2><strong>แบบฟอร์มติดต่อ</strong></h2>
                                        <p style="word-wrap: break-word;">กรุณาเลือกหัวข้อที่ต้องการติดต่อและข้อมูลติดต่อกลับ</p>
                                        <div class="uk-grid-small" uk-grid>
                                            <div class="uk-width-1-2">
                                                <label class="uk-form-label">ชื่อ - สกุล</label>
                                                <div class="uk-form-controls">
                                                    <input class="uk-input require" type="text" name="contactName" placeholder="">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="uk-grid-small" uk-grid>
                                            <div class="uk-width-1-2">
                                                <label class="uk-form-label">E-mail ติดต่อกลับ</label>
                                                <div class="uk-form-controls">
                                                    <input class="uk-input require" type="text" name="contactEmail" placeholder="">
                                                </div>
                                            </div>
                                            <div class="uk-width-1-2">
                                                <label class="uk-form-label">เบอร์โทรศัพท์</label>
                                                <input class="uk-input require"  type="text" name="contactTel" placeholder="">  
                                            </div>
                                        </div>
                                        <div class="uk-grid-small" uk-grid>
                                            <div class="uk-width-1-1">
                                                <label class="uk-form-label">หัวข้อที่ต้องการติดต่อ</label>
                                                <div class="uk-form-controls">
                                                    <select class="uk-select" id="contactSubject" name="contactSubject">
                                                        <option value="">เลือกหัวข้อ</option>
                                                        <c:forEach items="${list_contact}" var="list_contact">
                                                            <option value="${list_contact.lookupNameTh}">${list_contact.lookupNameTh}</option>                            
                                                        </c:forEach> 
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="uk-grid-small" uk-grid>
                                            <div class="uk-width-1-1">
                                                <label class="uk-form-label">รายละเอียดที่ต้องการติดต่อ</label>
                                                <div class="uk-form-controls">
                                                    <textarea class="uk-textarea uk-width-1-1 remark require" rows="5" placeholder="" name="contactRemark" maxlength="256"></textarea>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="uk-grid-small" uk-grid>
                                            <div class="uk-width-1-1">
                                                <div class="uk-text-center">
                                                    
                                                    <button class="uk-button uk-button-secondary btn-contact " style='width: 40%' type="button">
                                                         <i id="spinner" class="fa fa-spinner fa-spin" style="font-size:18px; display: none;" ></i>&nbsp;&nbsp;ส่ง</button>
                                                </div> 
                                            </div> 
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>                  
                    </div>                
                </div>
            </div>
            <div class="contact-info">
                <div class="uk-panel uk-panel-box ">
                    <div class="uk-grid-small" uk-grid>
                        <div class="uk-width-1-3">
                            <div class="uk-grid-small" uk-grid>
                                <div class="uk-width-1-2">
                                    <img class="uk-border" src="/SMEetaxUI/assets/img/Logo-D-tax.png" alt="">
                                </div>
                                <div class="uk-width-1-2">
                                    <ul style="list-style-type:none" class="footer-menu-box">
                                        <li class="menu-footer"><a class="uk-link-heading" href="#home">Home</a></li> 
                                        <li class="menu-footer"><a class="uk-link-heading feature-menu" href="#features">Features</a></li> 
                                        <li class="menu-footer"><a class="uk-link-heading" href="#benefits">Benefit</a></li>
                                        <li class="menu-footer"><a class="uk-link-heading" href="#pricing">Pricing</a></li>
                                        <li class="menu-footer"><a class="uk-link-heading" href="#contact">Contact us</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="uk-width-1-3">
                            <div style='padding-top: 5%'>
                                <p class='footer-header'>CONTACT</p>
                                <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-location-1.svg"/></span><p>DataOne Asia (Thailand) Company Limited</p>
                                <span class="fa-stack pull-left"></span><p>900/29 Rama III Rd. Bangpongpang, Yannawa, Bangkok 10120 Thailand</p>
                                <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-phone-1.svg"/></span><p>026863000</p>
                                <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-email-4.svg"/></span><p>dataoneinfo@d1asia.co.th</p>                     
                            </div>
                        </div>
                        <div class="uk-width-1-3">
                            <div style='padding-top: 5%'>
                                <p class='footer-header'>SUBSCRIBE</p>
                                <p>To be updated with all news</p>
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-3-4">
                                        <form class="uk-search uk-search-default">
                                            <input class="uk-search-input" style='border-color: black' type="text" placeholder="Your E-mail address" name='email-subcribe'>
                                        </form>
                                    </div>
                                    <div style='padding-left: 0px'>
                                        <button class="uk-button uk-button-secondary" style='padding: 0 5' type="button"><span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/paper-plane.svg"/></span>&nbsp;</button>
                                    </div>
                                </div>
                                <div class="uk-grid-small" uk-grid>                           
                                    <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-facebook-3.svg"/></span>
                                    <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-twitter-3.svg"/></span>
                                    <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-linkedin-3.svg"/></span>
                                    <span class="fa-stack pull-left"><img class="uk-border css-icon" src="/SMEetaxUI/assets/img/social/iconmonstr-pinterest-3.svg"/></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>
