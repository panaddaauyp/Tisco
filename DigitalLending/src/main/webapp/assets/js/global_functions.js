$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

function ucfirst(text) {
    return text.charAt(0).toUpperCase() + text.slice(1);
};

(function ($) {
    $.fn.cleanVal = function () {
        return numeral().unformat(($(this).is(':input') ? this.val() : this.text()));
    };

    $.isN = function (flag) {
        return flag === 'N';
    }

    $.isY = function (flag) {
        return flag === 'Y';
    }

    $.fn.visible = function () {
        return $(this).css("visibility", "visible");
    };

    $.fn.invisible = function () {
        return $(this).css("visibility", "hidden");
    };

    $.fn.removeDigit = function () {
        var fn = function (index, css) {
            return (css.match(/(^|\s)digit-\S+/g) || []).join(' ');
        };
        return $(this).removeClass(fn);
    };

    $.fn.setDigit = function (digit) {
        var $element = $(this);
        var value = numeral().unformat($(this).val());
        $(this).removeDigit();

        setTimeout(function () {
            $element.addClass(!!digit ? 'digit-' + digit : '');
        }, 50);

        if ($(this).val() !== "") {
            setTimeout(function () {
                $element.val(valueUtil.formatVal(value, digit));
            }, 60);
        }

        return $element;
    };

    $.fn.textCurrency = function (text) {
        var $element = $(this);
        $element.text(text);
        $element.addClass('currency-editing');
        setTimeout(function () {
            $element.removeClass('currency-editing');
        }, 75);

        return $element;
    };
}(jQuery));

// make $(selector) to be masked follow to method
/**
 * @description <p>Set currency mask for element</p><p>Use this method in $(document).ready()</p>
 *
 * @param $element  {object}    element to set mask, normally - input
 * @param hasDigit  {boolean}   (optional) display digit or not, default: not display
 * @param numDigit              (optional) number of digits, default: 2 digits
 */

function maskCurrency($element, hasDigit, numDigit) {
    $element = $($element);
    if (!($element.is("th, :input"))) {
        var content = $element.text();
        // array of patterns: number[0] and decimal[1]
        var pattern = ["", ""];
        if (!!hasDigit) {
            pattern[1] = ".00";
            if (typeof numDigit === 'number') {
                pattern[1] = "";
                if (numDigit > 0) {
                    pattern[1] = ".";
                    var count = 1;
                    while (count <= numDigit) {
                        pattern[1] += "0";
                        count++;
                    }
                }
            }
        }
        content = numeral(content).format('0,0' + pattern[1]);
        $element.text(content);
    }
}

// DataTable, DatePicker
$(document).ready(function () {

    // bind functions to element with jQuery Plugin: livequery
    $('.currency-input').livequery(
        function (index, element) {
            reMaskMoney(element);
        },
        function (index, element) {
            if ($(element).hasClass('currency-input')) {
                reMaskMoney(element);
            } else {
                $(element).removeClass('currency-negative').removeDigit();
                $(element).maskMoney('destroy');
            }
        }
    );
    $('.currency-negative').livequery(
        function (index, element) {
            reMaskMoney(element);
        },
        function (index, element) {
            if ($(element).hasClass('currency-input')) {
                reMaskMoney(element);
            } else {
                $(element).removeClass('currency-negative').removeDigit();
                $(element).maskMoney('destroy');
            }
        }
    );
    $('[class*=digit-]').livequery(
        function (index, element) {
            if ($(element).hasClass('currency-input')) {
                reMaskMoney(element);
            }
        },
        function (index, element) {
            if ($(element).hasClass('currency-input')) {
                reMaskMoney(element);
            } else {
                $(element).removeClass('currency-negative').removeDigit();
                $(element).maskMoney('destroy');
            }
        }
    );

    // currency-text for format number on display field
    // var interval = null;
    // $('.currency-text').livequery(
    //     function (index, $element) {
    //         $element = $($element);
    //         if ($element.is('[class*=digit-]')) {
    //             var digit = findDigit($element);
    //             maskCurrency($element, !!digit, digit);
    //         } else {
    //             maskCurrency($element, false);
    //         }
    //     },
    //     function (index, $element) {
    //         $element = $($element);
    //         var content = numeral().unformat($element.text());
    //         $element.text(content);
    //     }
    // );

    // currency-text for format number on display field
    var interval = null;
    $('.currency-text').livequery(
        function (index, $element) {
            $element = $($element);
            interval = setInterval(function () {
                if ($element.is('[class*=digit-]')) {
                    var digit = findDigit($element);
                    if (digit > 0) {
                        maskCurrency($element, true, digit);
                    } else {
                        maskCurrency($element, false);
                    }
                } else {
                    maskCurrency($element, false);
                }
            }, 200);
        },
        function (index, $element) {
            $element = $($element);
            clearInterval(interval);
            var content = $element.text().trim();
            content = numeral().unformat(content);
            $element.text(content);
        }
    );

    $('.currency-text.currency-editing').livequery(
        function (index, $element) {
            $element = $($element);
            var digit = findDigit($element);
            maskCurrency($element, !!digit, digit);
        }, function (index, $element) {
            $element = $($element);
            var digit = findDigit($element);
            maskCurrency($element, !!digit, digit);
        }
    );

    $('body')
        .on('keyup', '.currency-input', function (e) {
            var keyCode = e.keyCode || e.which;
            if (keyCode === 8 || keyCode === 46) {
                if ($(this).val() == 0) {
                    $(this).val("");
                }
            }
        })
        .on('blur', '.currency-input', function (e) {
            var dom = $(this);
            if (dom.val() === '-') {
                dom.val('');
            }
            if (dom.val() == 0) {
                dom.val(dom.val().replace('-', ''));
            }
        })
});

function reMaskMoney(element) {
    var digit = findDigit(element);
    var negative = $(element).hasClass('currency-negative');
    $(element).maskMoney({
        precision: digit,
        allowNegative: negative,
        allowZero: true
    });

    $(element).maskMoney('mask');

}

function findDigit(element) {
    var ret = 0;
    $.grep($(element).attr("class").split(" "), function (e) {
        if (/digit-\d+/g.test(e)) {
            var digit = +(e.split("-")[1]);
            if (digit > 0) {
                ret = digit
            } else {
                ret = 0;
            }
        }
    });
    return ret;
}

function isInt(n) {
    return Number(n) === n && n % 1 === 0;
}

function isFloat(n) {
    return Number(n) === n && n % 1 !== 0;
}
//tooltip

// $(document).tooltip({
//    position: {
//       my: "center bottom-10",
//       at: "center top",
//       using: function (position, feedback) {
//          $(this).css(position);
// $("<div>")
//         .addClass("arrow")
//         .addClass(feedback.vertical)
//         .addClass(feedback.horizontal)
//         .appendTo(this);
// }
// }
// });

$(document).ready(function () {
    $("body").on("mouseover", ":input", function () {
//      console.log($(this).get(0));
        var type = $(this).attr("type");
//      console.log("in");
        if (type == "password" || type == "checkbox" || type == "radio") {
            return false;
        } else {

            var text = "";
            if ($(this).is("select")) {
                text = $(this).find("option:selected").text();
            } else {
                text = $(this).val();
            }
            $(this).attr("title", text.trim());
        }
    });
//   $("body").on("click", "select:disabled", function () {
//      console.log($(this).get(0));
//
//   });
});

//$(":input").tooltip({
//   track: true
//});

//End tooltip