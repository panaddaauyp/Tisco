/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function requireFild() {
    $('#displayCreate .error-field').removeClass('error-field');
    $('#displayCreate .require').removeClass('error-field');
    var flag = false;
    $('#displayCreate .require').each(function () {
        if ($(this).val() === '' && !$(this).prop("disabled")) {
            $(this).addClass('error-field');
            flag = true;
        }
    });
    if (flag) {
        swal("แจ้งเตือน!", "กรุณากรอกข้อมูลให้ครบ", "info");
        return;
    }
    return true;
}

function requireForm(idTable) {
    var length = $('#' + idTable + ' tbody tr:first-child').find("input").length;
    if (length === 0) {
        swal("แจ้งเตือน!", "กรุณาเพิ่มสินค้าและบริการ", "info");
        return;
    }
    var flag = false;
    $('#' + idTable + ' tbody tr').each(function () {
        var total = text2Number($(this).find("td:nth-child(7)").find('span').text());
        if (total < 0) {
            $(this).find("[name=discount]").addClass('error-field');
            flag = true;
        }
    });
    if (flag) {
        swal("แจ้งเตือน!", "ส่วนลดต้องน้อยกว่าราคาสินค้า", "info");
        return;
    }
    return true;
}

function requireFildPopUp(idPopup) {
    $('#' + idPopup + ' .error-field').removeClass('error-field');
    $('#' + idPopup + ' .require').removeClass('error-field');
    var flag = false;
    $('#' + idPopup + ' .require').each(function () {
        if ($(this).val() === '' && !$(this).prop("disabled")) {
            $(this).addClass('error-field');
            flag = true;
        }
    });
    if (flag) {
        swal("แจ้งเตือน!", "กรุณากรอกข้อมูลให้ครบ", "info");
        return;
    }
    return true;
}

function duplicate() {
    if ($dataItem.length > 0 && $fromEdit === false) {
        for (i = 0; i < $dataItem.length; ++i) {
            if ("" + $dataItem[i] === $("#popupInvItem [name=codeItem]").val()) {
                loaderHide();
                swal("แจ้งเตือน!", "รหัสสินค้าซ้ำ", "info");
                checkCodeItem = true;
                return;
            }
        }
        if (checkCodeItem === false) {
            $dataItem.push(itemName);
        }
    } else {
        $dataItem.push(itemName);
    }
}
function keyOnLyEngText(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[A-Za-z0-9]|\s|\.|\+|_|@|#|-/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function validateEnText(obj) {
    var regex = /[^A-Za-z0-9\s\.\+\_\@\#\-]/g;
    if (regex.exec(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function keyOnLyEng(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[A-Za-z0-9]/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyEn(obj) {
    var regex = /[^A-Za-z0-9]/g;
    if (regex.exec(obj.val())) {
        return false;
    } else {
        return true;
    }
}
function keyOnLyUrl(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
//    var regex = /[A-Za-z0-9]/;
    var regex = /^((http[s]?|ftp):\/)?\/?([^:\/\s]+)((\/\w+)*\/)([\w\-\.]+[^#?\s]+)(.*)?(#[\w\-]+)?$/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyUrl(obj) {
    var regex = /^((http[s]?|ftp):\/)?\/?([^:\/\s]+)((\/\w+)*\/)([\w\-\.]+[^#?\s]+)(.*)?(#[\w\-]+)?$/;
    console.log(obj.val());
    console.log(regex);
    console.log(regex.test(obj.val()));
    if (!regex.exec(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function keyOnLyNum(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[0-9]/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function keyOnLyText(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[A-Za-zก-๙]/;
//    var regex = /[0-9]/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyNum(obj) {
    var regex = /[^0-9]/g;
    if (regex.test(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function keyOnLyPhoneNum(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[0-9,]|-/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyPhoneNum(obj) {
    var str = obj.val();
    var splitted = str.split(',');
    for (var j = 0; j < splitted.length; j++) {
        var result = replaceAll(splitted[j], '-', '');
        if (9 !== result.length) {
            return false;
        }
    }
    var regex = /[^0-9-,]/g;
    var dashRegex = /(-){2}/g;
    if (regex.test(obj.val()) || dashRegex.test(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function keyOnLyDate(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[0-9/]/g;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyDate(obj) {
    var regex = /[^0-9/]/g;
    if (regex.test(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function keyOnLyCodeId(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[0-9]/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function changeOnLyCodeId(obj) {
    var regex = /[^0-9]/g;
    if (regex.test(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function checkDate(DateVal) {
    var inputText = DateVal;
    var dateArray = inputText.split('/'); //false
    var dd = parseInt(dateArray[0]);
    var mm = parseInt(dateArray[1]);
    var yy = parseInt(dateArray[2]);
    var ListofDays = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    //ตรวจสอบวันที่ว่าครวเป็น 30 หรือ 31 วัน
    if (mm === 1 || mm > 2) {
        if (dd > ListofDays[mm - 1]) {
            return false;
        }
    }
    if (dd < 1 || mm < 1 || yy < 1 || yy > 3000) {
        return false;
    }
    //ตรวจสอบว่าเดือนกุมภาพันธ์ ว่าครวมี 28 หรือ 29 วัน
    if (mm === 2) {
        var lyear = false;
        if ((!(yy % 4) && yy % 100) || !(yy % 400)) {
            lyear = true;
        }
        if ((lyear === false) && (dd >= 29)) {
            return false;
        }
        if ((lyear === true) && (dd > 29)) {
            return false;
        }
    }
    return true;
}

function checkID(taxNo) {
    if (taxNo.length !== 13) {
        return false;
    }
    var sum = 0;
    for (i = 0; i < 12; i++) {
        sum += parseFloat(taxNo.charAt(i)) * (13 - i);
    }
    if ((11 - sum % 11) % 10 !== parseFloat(taxNo.charAt(12))) {
        return false;
    } else {
        return true;
    }
}

function requireItem(idPopup) {
    var $qty = $('#' + idPopup + ' input[name=qty]');
    var $unitPrice = $('#' + idPopup + ' input[name=unitPrice]');
    $qty.removeClass('error-field');
    $unitPrice.removeClass('error-field');
    var qty = text2Number($qty.val());
    var unitPrice = text2Number($unitPrice.val());
    if (qty <= 0) {
        swal("แจ้งเตือน!", "จำนวนสินค้าต้องมากกว่า 0", "info");
        return;
    }
    if (unitPrice <= 0) {
        swal("แจ้งเตือน!", "ราคา/หน่วยต้องมีค่ามากกว่า 00.00", "info");
        return;
    }
    return true;
}

function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}

function keyOnLyThaiText(evt) {
    var theEvent = evt || window.event;
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
    var regex = /[ก-๙0-9]|\s|\.|\+|_|@|#|-/;
    if (!regex.test(key)) {
        theEvent.returnValue = false;
        if (theEvent.preventDefault)
            theEvent.preventDefault();
    }
}

function validateThText(obj) {
    var regex = /[^ก-๙0-9\s\.\+\_\@\#\-]/g;
    if (regex.exec(obj.val())) {
        return false;
    } else {
        return true;
    }
}

function validateEmail(sEmail) {
    console.log('ascas');
    var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    if (emailReg.test(sEmail.val())) {
        return true;
    } else {
        return false;
    }
}