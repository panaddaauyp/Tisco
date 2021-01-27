/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



(function ($) {
    $.fn.searchCustComponent = function (callback) {
        var $inputs = $(this);
        var _tranFormInput = function () {
            $inputs.each(function () {
                if ($(this).is(":text") && !$(this).hasClass('hasSearchCustComponent')) {
                    $(this).addClass('hasSearchCustComponent')
                            .wrap('<form class="uk-search uk-search-default uk-background-muted"> </form>')
                            .after('<span class="uk-search-icon-flip btn-search-cust-comp" uk-search-icon uk-toggle="target: #popup_search_cust_comp"></span>');
                }
            });
        };
        var _generatePopup = function () {
            if ($("#popup_search_cust_comp").length === 0) {
                $("body").append('<div id="popup_search_cust_comp" class="uk-flex-top popop-search-form" uk-modal></div>');
                $("#popup_search_cust_comp").load("/Provider/customer/popup.htm");
            }
        };

        var _clearFrom = function () {
        };

        var _setFromData = function ($text) {
        };

        var _doPopUp = function ($text) {
            // clear form
            _clearFrom();

            // set data to form
            _setFromData($text);

            // unbind and bind even dblclick
            $("#popup_search_cust_comp").off("dblclick", "#buyerList tbody tr");
            $("#popup_search_cust_comp").on("dblclick", "#buyerList tbody tr", function () {
                var data = $("#popup_search_cust_comp").find('.dataTable').DataTable().row($(this)).data();
                $(this).addClass("uk-modal-close").click();
                $(this).removeClass("uk-modal-close");
                if (callback && data) {
                    callback(data, $text.get(0));
                }
            });
        };

        var _bindEvent = function () {
            $inputs.each(function () {
                if ($(this).is(":text")) {
                    var $text = $(this);
                    $text.keypress(function (e) {
                        if (e.keyCode === 13) {
                            $(this).trigger("enterKey");
                            return false;
                        }
                    });
                    $text.bind("enterKey", function (e) {
                        $('.btn-search-cust-comp').trigger('click');
                    });
                    $text.parent().find(".btn-search-cust-comp").click(function () {
                        if (!$text.is(":disabled")) {
                            _doPopUp($text);
                            $("#popupCustomer .uk-search-input").val($("input[name=custCode]").val()).keyup();
                        }
                    });
                }
            });
        };

        _tranFormInput();
        _generatePopup();
        _bindEvent();

        return $(this);
    };

    $.fn.searchInvoiceComponent = function (callback) {
        var $inputs = $(this);
        var _tranFormInput = function () {
            $inputs.each(function () {
                if ($(this).is(":text") && !$(this).hasClass('hasSearchInvoiceComponent')) {
                    $(this).addClass('hasSearchInvoiceComponent')
                            .wrap('<form class="uk-search uk-search-default uk-background-muted"> </form>')
                            .after('<span class="uk-search-icon-flip btn-search-inv-comp" uk-search-icon uk-toggle="target: #popup_search_inv_comp"></span>');
                }
            });
        };
        var _generatePopup = function () {
            if ($("#popup_search_inv_comp").length === 0) {
                $("body").append('<div id="popup_search_inv_comp" class="uk-flex-top popop-search-form" uk-modal></div>');
                $("#popup_search_inv_comp").load("/Provider/invoice/popup.htm");
            }
        };

        var _clearFrom = function () {
        };

        var _setFromData = function ($text) {
        };

        var _doPopUp = function ($text) {
            // clear form
            _clearFrom();

            // set data to form
            _setFromData($text);

            // unbind and bind even dblclick
            $("#popup_search_inv_comp").off("dblclick", "#invoiceList tbody tr");
            $("#popup_search_inv_comp").on("dblclick", "#invoiceList tbody tr", function () {
                loaderShow();
                var data = $("#popup_search_inv_comp").find('.dataTable').DataTable().row($(this)).data();
                $(this).addClass("uk-modal-close").click();
                $(this).removeClass("uk-modal-close");
                if (callback && data) {
                    callback(data, $text.get(0));
                }
            });
        };

        var _bindEvent = function () {
            $inputs.each(function () {
                if ($(this).is(":text")) {
                    if('invoiceCode' === $(this).attr("name")){
                        var $text = $(this);
                        var index = $text.parents("tr").index();
                        $text.keypress(function (e) {
                            if (e.keyCode === 13) {
                                $(this).trigger("enterKey");
                                return false;
                            }
                        });
                        $text.bind("enterKey", function (e) {
                            $("#item-edit tbody tr:eq(" + index + ") .btn-search-inv-comp").click();
                        });
                        $text.parent().find('.btn-search-inv-comp').click(function () {
                            if (!$text.is(":disabled")) {
                                _doPopUp($text);
                                $("#popupInvoice .uk-search-input").val($text.val()).keyup();
                            }
                        });
                    }else if('refInvoiceCode' === $(this).attr("name")){
                        var $text = $(this);
                        $text.keypress(function (e) {
                            if (e.keyCode === 13) {
                                $(this).trigger("enterKey");
                                return false;
                            }
                        });
                        $text.bind("enterKey", function (e) {
                            $('.btn-search-inv-comp').trigger('click');
                        });
                        $text.parent().find('.btn-search-inv-comp').click(function () {
                            if (!$text.is(":disabled")) {
                                _doPopUp($text);
                                $("#popupInvoice .uk-search-input").val($text.val()).keyup();
                            }
                        });
                    }    
                }
            });
        };

        _tranFormInput();
        _generatePopup();
        _bindEvent();

        return $(this);
    };

    $.fn.searchTaxInvoiceComponent = function (callback) {
        var $inputs = $(this);
        var _tranFormInput = function () {
            $inputs.each(function () {
                if ($(this).is(":text") && !$(this).hasClass('hasSearchTaxInvComponent')) {
                    $(this).addClass('hasSearchTaxInvComponent')
                            .wrap('<form class="uk-search uk-search-default uk-background-muted"> </form>')
                            .after('<span class="uk-search-icon-flip btn-search-tax-inv-comp" uk-search-icon uk-toggle="target: #popup_search_tax_inv_comp"></span>');
                }
            });
        };
        var _generatePopup = function () {
            if ($("#popup_search_tax_inv_comp").length === 0) {
                $("body").append('<div id="popup_search_tax_inv_comp" class="uk-flex-top popop-search-form" uk-modal></div>');
                $("#popup_search_tax_inv_comp").load("/Provider/tax-invoice/popup.htm");
            }
        };

        var _clearFrom = function () {
        };

        var _setFromData = function ($text) {
        };

        var _doPopUp = function ($text) {
            // clear form
            _clearFrom();

            // set data to form
            _setFromData($text);

            // unbind and bind even dblclick
            $("#popup_search_tax_inv_comp").off("dblclick", "#taxInvList tbody tr");
            $("#popup_search_tax_inv_comp").on("dblclick", "#taxInvList tbody tr", function () {
                var data = $("#popup_search_tax_inv_comp").find('.dataTable').DataTable().row($(this)).data();
                $(this).addClass("uk-modal-close").click();
                $(this).removeClass("uk-modal-close");
                if (callback && data) {
                    callback(data, $text.get(0));
                }
            });
        };

        var _bindEvent = function () {
            $inputs.each(function () {
                if ($(this).is(":text")) {
                    var $text = $(this);
                    $text.keypress(function (e) {
                        if (e.keyCode === 13) {
                            $(this).trigger("enterKey");
                            return false;
                        }
                    });
                    $text.bind("enterKey", function (e) {
                        $('.btn-search-tax-inv-comp').trigger('click');
                    });
                    $text.parent().find(".btn-search-tax-inv-comp").click(function () {
                        if (!$text.is(":disabled")) {
                            _doPopUp($text);
                            $("#popupTaxInvoice .uk-search-input").val($("input[name=searchRefDocCode]").val()).keyup();
                        }
                    });

                }
            });
        };

        _tranFormInput();
        _generatePopup();
        _bindEvent();

        return $(this);
    };

    $.fn.searchBillingComponent = function (callback) {
        var $inputs = $(this);
        var _tranFormInput = function () {
            $inputs.each(function () {
                if ($(this).is(":text") && !$(this).hasClass('hasSearchBillingComponent')) {
                    $(this).addClass('hasSearchBillingComponent')
                            .wrap('<form class="uk-search uk-search-default uk-background-muted"> </form>')
                            .after('<span class="uk-search-icon-flip btn-search-bill-comp" uk-search-icon uk-toggle="target: #popup_search_bill_comp"></span>');
                }
            });
        };
        var _generatePopup = function () {
            if ($("#popup_search_bill_comp").length === 0) {
                $("body").append('<div id="popup_search_bill_comp" class="uk-flex-top popop-search-form" uk-modal></div>');
                $("#popup_search_bill_comp").load("/Provider/bill/popup.htm");
            }
        };

        var _clearFrom = function () {
        };

        var _setFromData = function ($text) {
        };

        var _doPopUp = function ($text) {
            // clear form
            _clearFrom();

            // set data to form
            _setFromData($text);

            // unbind and bind even dblclick
            $("#popup_search_bill_comp").off("dblclick", "#billList tbody tr");
            $("#popup_search_bill_comp").on("dblclick", "#billList tbody tr", function () {
                loaderShow();
                var data = $("#popup_search_bill_comp").find('.dataTable').DataTable().row($(this)).data();
                $(this).addClass("uk-modal-close").click();
                $(this).removeClass("uk-modal-close");
                if (callback && data) {
                    callback(data, $text.get(0));
                }
            });
        };

        var _bindEvent = function () {
            $inputs.each(function () {
                if ($(this).is(":text")) {
                    var $text = $(this);
                    $text.keypress(function (e) {
                        if (e.keyCode === 13) {
                            $(this).trigger("enterKey");
                            return false;
                        }
                    });
                    $text.bind("enterKey", function (e) {
                        $('.btn-search-bill-comp').trigger('click');
                    });
                    $text.parent().find(".btn-search-bill-comp").click(function () {
                        if (!$text.is(":disabled")) {
                            _doPopUp($text);
                            $("#popupBilling .uk-search-input").val($("input[name=billInvoice]").val()).keyup();
                        }
                    });
                }
            });
        };

        _tranFormInput();
        _generatePopup();
        _bindEvent();

        return $(this);
    };

    $.fn.searchUnitComponent = function (callback) {
        var $inputs = $(this);
        var _tranFormInput = function () {
            $inputs.each(function () {
                if ($(this).is(":text") && !$(this).hasClass('hasSearchUnitComponent')) {
                    $(this).addClass('hasSearchUnitComponent')
                            .wrap('<form class="uk-search uk-search-default uk-background-muted"> </form>')
                            .after('<span class="uk-search-icon-flip btn-search-unit-comp" uk-search-icon uk-toggle="target: #popup_search_unit_comp"></span>');
                }
            });
        };
        var _generatePopup = function () {
            if ($("#popup_search_unit_comp").length === 0) {
                $("body").append('<div id="popup_search_unit_comp" class="uk-flex-top popop-search-form" uk-modal></div>');
                $("#popup_search_unit_comp").load("/Provider/unit/popup.htm");
            }
        };

        var _clearFrom = function () {
        };

        var _setFromData = function ($text) {
        };

        var _doPopUp = function ($text) {
            // clear form
            _clearFrom();

            // set data to form
            _setFromData($text);

            // unbind and bind even dblclick
            $("#popup_search_unit_comp").off("dblclick", "#unitList tbody tr");
            $("#popup_search_unit_comp").on("dblclick", "#unitList tbody tr", function () {
                var data = $("#popup_search_unit_comp").find('.dataTable').DataTable().row($(this)).data();
                $(this).addClass("uk-modal-close").click();
                $(this).removeClass("uk-modal-close");
                if (callback && data) {
                    callback(data, $text.get(0));
                }
            });
        };

        var _bindEvent = function () {
            $inputs.each(function () {
                if ($(this).is(":text")) {
                    var $text = $(this);
                    $text.keypress(function (e) {
                        if (e.keyCode === 13) {
                            _doSearchCust($text);
                            return false;
                        }
                    });
                    $text.parent().find(".btn-search-unit-comp").click(function () {
                        if (!$text.is(":disabled")) {
//                            _doSearchCust($text);
                            _doPopUp($text);
                        }
                    });
                }
            });
        };

        _tranFormInput();
        _generatePopup();
        _bindEvent();

        return $(this);
    };

    /*
     $.fn.searchUnitComponent = function (callback) {
     var $inputs = $(this);
     var _tranFormInput = function () {
     $inputs.each(function () {
     if ($(this).is(":text") && !$(this).hasClass('hasSearchUnitComponent')) {
     $(this).addClass('hasSearchUnitComponent')
     .wrap("<div class='input-group' ></div>")
     .after('<span class="input-group-addon input-addon-height btn btn-search-unit-comp"><i class="fa fa-search"></i></span>');
     }
     });
     };
     
     var _generatePopup = function () {
     if ($("#popup_search_unit_comp").length === 0) {
     $("body").append('<div class="modal fade" id="popup_search_unit_comp" tabindex="-1" role="dialog" aria-labelledby="searchModalLabel"></div>');
     $("#popup_search_unit_comp").load("/Provider/bill/popup.htm");
     
     $('#popup_search_unit_comp').on('shown.bs.modal', function () {
     $("#popup_search_unit_comp :input:text").filter(function () {
     return $(this).val() !== '';
     }).focus();
     });
     }
     };
     
     var _clearFrom = function () {
     $("#popup_search_unit_comp .searchForm input, #popup_search_unit_comp .searchForm select").each(function () {
     $(this).val('');
     });
     };
     
     var _setFromData = function ($text) {
     var key = $text.attr("for");
     var value = $text.val();
     
     if (value || value !== '') {
     value = value.trim();
     }
     
     switch (key) {
     case 'unitCode':
     $("#popup_search_unit_comp").find('input:text[name=unitCode]').val(value);
     break;
     case 'unitName':
     $("#popup_search_unit_comp").find('input:text[name=unitName]').val(value);
     break;
     }
     };
     
     var _doPopUp = function ($text) {
     // clear form
     _clearFrom();
     
     // set data to form
     _setFromData($text);
     
     // auto
     //            if ($text.val() != '') {
     //             $("#popup_search_cust_comp #btnSearchModal").click();
     //             } else {
     //             // clear data table
     //             $("#popup_search_cust_comp").find('input:text[name=custNoLast]').val("clear");
     //             $("#popup_search_cust_comp #btnSearchModal").click();
     //             $("#popup_search_cust_comp").find('input:text[name=custNoLast]').val("");
     //             }
     
     // unbind and bind even dblclick
     $("#popup_search_unit_comp").off("dblclick", "#documentList tbody tr");
     $("#popup_search_unit_comp").on("dblclick", "#documentList tbody tr", function () {
     var data = $("#popup_search_unit_comp").find('.dataTable').DataTable().row($(this)).data();
     $("#popup_search_unit_comp").modal("hide");
     if (callback && data) {
     callback(data, $text.get(0));
     }
     });
     
     // show
     $("#popup_search_unit_comp").modal("show");
     };
     
     var _bindEvent = function () {
     $inputs.each(function () {
     if ($(this).is(":text")) {
     var $text = $(this);
     $text.keypress(function (e) {
     if (e.keyCode === 13) {
     _doSearchCust($text);
     return false;
     }
     });
     
     $text.parent().find(".btn-search-unit-comp").click(function () {
     if (!$text.is(":disabled")) {
     //                            _doSearchCust($text);
     _doPopUp($text);
     }
     });
     }
     });
     };
     
     _tranFormInput();
     _generatePopup();
     _bindEvent();
     
     return $(this);
     };
     */
}(jQuery));
