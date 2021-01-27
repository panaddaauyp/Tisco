<%-- 
    Document   : index_login
    Created on : Dec 19, 2018, 2:30:46 PM
    Author     : Chalermpol
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>เข้าสู่ระบบ</title>
        <jsp:include page="main2.jsp"/>        
        <style type="text/css">
            button{
                min-width: 150px;
                border-radius: 5px;
            }
        </style>
        <script type="text/javascript">
            $(document).ready(function () {
                loaderHide();
                $('.signin [name=username], .signin [name=password]').keydown(function (event) {
                    var keypressed = event.keyCode || event.which;
                    if (keypressed === 13) {
                        login();
                    }
                });
                $(".btn_login").click(function () {
                    login();
                });
                if (localStorage.chkbox && localStorage.chkbox != '') {
                    $('[name=remember]').attr('checked', 'checked');
                    $('[name=username]').val(localStorage.username);
                    $('[name=password]').val(localStorage.pass);
                } else {
                    $('[name=remember]').removeAttr('checked');
                    $('[name=username]').val('');
                    $('[name=password]').val('');
                }
                $("[name=remember]").click(function () {
                    if ($('[name=remember]').is(':checked')) {
                        localStorage.username = $('[name=username]').val();
                        localStorage.pass = $('[name=password]').val();
                        localStorage.chkbox = 'Y';
                    } else {
                        localStorage.username = '';
                        localStorage.pass = '';
                        localStorage.chkbox = '';
                    }
                });
            });
            function login() {
                $('#spinner').show();
                loaderShow()
                $.post("${pageContext.servletContext.contextPath}/users/do_login", $(".signin").serialize(), function (result) {
                    if (result.status === 1) {
                        $('#spinner').hide();
                        redirectPost('${pageContext.servletContext.contextPath}/main', 'post');
                    } else if (result.status === 2) {
                        loaderHide();
                        swal({
                            title: "ยืนยันการเข้าสู่ระบบ",
                            text: "ไม่สามารถเข้าสู่ระบบได้ ชื่อผู้ใช้นี้ถูกใช้งานอยู่",
                            type: 'warning',
                            showConfirmButton: true,
                            showCancelButton: true,
                            confirmButtonColor: '#3085d6',
                            cancelButtonColor: '#d33',
                            confirmButtonText: 'ยืนยัน',
                            cancelButtonText: 'ยกเลิก'
                        }).then(function (cond) {
                            if (cond) {
                                $('#spinner').show();
                                loaderShow()
                                $.post('${pageContext.servletContext.contextPath}/users/confirm_login', $(".signin").serialize(), function (data) {
                                    $('#spinner').hide();
                                    if (data.status === '1') {
                                        redirectPost('${pageContext.servletContext.contextPath}/main', 'post');
                                    } else {
                                        loaderHide();
                                        swal("เข้าสู่ระบบ!", "ไม่สามารถเข้าสู่ระบบได้", "error");
                                    }
                                });
                            } else {
                                $('#spinner').hide();
                                loaderHide();
                            }
                        });
                    } else {
                        $('#spinner').hide();
                        loaderHide();
                        swal("เข้าสู่ระบบ!", "ไม่สามารถเข้าสู่ระบบได้", "error");
                    }
                });
            }
            function showModal() {
                $('body').loadingModal({text: 'กำลังโหลด...'}).loadingModal('animation', 'threeBounce');
                var delay = function (ms) {
                    return new Promise(function (r) {
                        setTimeout(r, ms);
                    });
                };
                var time = 2000;

                delay(time).then(function () {
                    $('body').loadingModal('destroy');
                });
            }
        </script>
    </head>
    <body>
        <div class="uk-scope" >
            <div class="uk-child-width-1-1@s uk-grid-collapse" uk-grid>
                <div class="uk-tile uk-tile-default">
                    <div class="uk-grid-small" uk-grid>
                        <div class="uk-width-2-3 uk-text-center" >
                            <img class="uk-border" src="${pageContext.servletContext.contextPath}/assets/img/landing/login/login.svg" style="width: 80%"/>
                        </div>
                        <div class="uk-width-1-3@s" style="padding-top: 50px;">   <!--style="border: 1px solid blue;"-->
                            <form class="signin">
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-2-3 uk-text-center">
                                        <p class="uk-text-large">เข้าสู่ระบบ</p>
                                    </div>
                                </div>
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-2-3">
                                        <label class="uk-form-label">ชื่อผู้ใช้</label>
                                        <div class="uk-form-controls">
                                            <input class="uk-input uk-form-width-large" name="username" type="text" placeholder="ชื่อผู้ใช้...">
                                        </div>
                                    </div>
                                </div>
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-2-3">
                                        <label class="uk-form-label">รหัสผ่าน</label>
                                        <div class="uk-form-controls">
                                            <input class="uk-input uk-form-width-large" name="password" type="password" placeholder="รหัสผ่าน...">
                                        </div>
                                    </div>
                                </div>
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-1-2">
                                        <label><input class="uk-checkbox" type="checkbox" name="remember"/>  จดจำไว้ในระบบ</label>
                                    </div>
                                    <div class="uk-width-1-2">
                                        <a href="${pageContext.servletContext.contextPath}/user/forget_password">ลืมรหัสผ่าน</a>
                                    </div>
                                </div>
                                
                                <div class="uk-grid-small " uk-grid>
                                    <div class="uk-width-2-3 uk-text-center">
                                        <button class="uk-button uk-button-secondary btn_login" type="button"><i class="fa fa-spinner fa-spin" id="spinner" style="font-size:18px; display: none;" ></i> เข้าสู่ระบบ</button>
                                    </div>
                                </div>
                                <div class="uk-grid-small" uk-grid>
                                    <div class="uk-width-2-3 uk-text-center">
                                        <p class="register">ยังไม่ได้เป็นสมาชิก ? <a href="${pageContext.servletContext.contextPath}/register">ลงทะเบียนที่นี่</a></p>
                                    </div>
                                </div>
                                
                            </form>
                            
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
