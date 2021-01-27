//alert = function(){};
$(document).ready(function () {
    $(".glyphicon-calendar").parent().click(function () {
        $(this).prev().focus();
    });
    $(".thai-char").on("keypress", function (event) {
        var englishAlphabetAndWhiteSpace = /[ก-๙ ]/g;
        var key = String.fromCharCode(event.which);
        if (event.keyCode == 8 || event.keyCode == 37 || event.keyCode == 39 || englishAlphabetAndWhiteSpace.test(key)) {
            return true;
        }
        return false;
    });

    $('.thai-char').on("paste", function (e)
    {
        e.preventDefault();
    });
    $('.text-char').on("paste", function (e)
    {
        e.preventDefault();
    });
    $(".thai-char").attr({
        "maxlength": 10, // substitute your own
        "minlength": 2          // values (or variables) here
    });
});

function redirectPost(path, params, method) {
    method = method || "post";

    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
}

function redirectGet(path, params, method) {
    method = method || "get";

    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for (var key in params) {
        if (params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
}

function getAmphurList(selectObj, provinceCode, ampCode) {
    var url = "/Provider/get_amphur_by_province.htm";
    var option = $(selectObj).find("option:eq(0)").clone();
    selectObj.empty();
    selectObj.append(option);
    if (provinceCode !== null && provinceCode !== '' && provinceCode != undefined) {
        $.getJSON(url, {"provinceCode": provinceCode}, function (data) {
            var amphur = "";
            $(data).each(function () {
                if ((this.amphurCode != undefined) && (this.amphurNameTh != undefined)) {
                    var select = '';
                    if (ampCode == this.amphurCode)
                        select = 'selected= "selected"';
                    amphur = amphur +
                            "<option value=\"" + this.amphurId + "\" attr=\"" + this.amphurCode + "\"" + select + ">" + this.amphurNameTh + "</option>";
                }
            });
            selectObj.append(amphur);
        });
    }
}

function getDistrictList(selectObj, amphurCode, disCode) {
    var url = "/Provider/get_district_by_amphur.htm";
    var option = $(selectObj).find("option:eq(0)").clone();
    selectObj.empty();
    selectObj.append(option);
    if (null !== amphurCode && amphurCode !== '') {
        $.getJSON(url, {"amphurCode": amphurCode}, function (data) {
            var districtStr = "";
            $(data).each(function () {
                if ((this.districtCode != undefined) && (this.districtNameTh != undefined)) {
                    var select = '';
                    if (disCode == this.districtCode)
                        select = 'selected= "selected"';
                    if (this.zipCode === undefined)
                        this.zipCode = '';
                    districtStr = districtStr +
                            "<option value=\"" + this.districtId + "\" attr=\"" + this.districtCode + "\" zipCode=\"" + this.zipCode + "\"" + select + ">" + this.districtNameTh + "</option>";
                }
            });
            selectObj.append(districtStr);
        });
    }
}

function validate(evt) {
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

function validateUrl(obj) {
    obj.removeClass('error');
    obj.next('label').remove();
    if (obj.val() != '') {
        if (!(/^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:[/?#]\S*)?$/i.test(obj.val()))) {
            obj.addClass('error').parent().append('<label for="error" class="error">Please enter a valid URL.</label>');
        }
    }
}

function resultPwd(e) {
    var returnVal = isOkPass(e.val());
    if (!returnVal.result) { // false
        e.removeClass('error');
        e.nextAll('label').remove();
        e.addClass('error').parent().append('<label for="error" class="error">' + returnVal.error + '.</label>');
    } else {
        e.removeClass('error');
        e.nextAll('label').remove();
    }
}
/* End */
function numberWithCommas(number) {
    var parts = number.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
}

function text2Number(text) {
    if (undefined !== text && text.length > 0)
        text = text.replace(/,/g, '');
    else
        text = '0';
    return Number(text);
}
function b64DecodeUnicode(str) {
    // Going backwards: from bytestream, to percent-encoding, to original string.
    return decodeURIComponent(atob(str).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}
function loaderShow() {
    $('body').loadingModal({text: 'กำลังโหลด...'}).loadingModal('animation', 'threeBounce');
}
function loaderHide() {
    $('body').loadingModal('destroy');
}

