/**
 * Created by hc.zeng on 2018/3/25.
 */
(function () {
    'use strict';


    var search = (function ($) {
        var val = "";
        var _init = function () {
            $("#search-btn").click(_search);
            $("#search").keyup(function (e) {
                if (e.keyCode === 13) {
                    _search();
                }
            });
        };

        var _search = function () {
            var val = $("#search").val();
            if (val !== "") {
                $("#search-list").show();
                var vals = val.split(" ");
                $.ajax.proxy({
                    url: "/api/admin/location/bankbin",
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(vals),
                    async: true,
                    success: function (d) {
                        console.log(d);
                        if (d.status === 200) {
                            if (d.data) {
                                var total = 0;
                                var data = d.data;
                                var $searchList = $("#search-list").empty();
                                var str2 = "<div class='col-sm-2 bold'>查询卡号</div>" +
                                    "<div class='col-sm-2 bold'>银行名称</div>" +
                                    "<div class='col-sm-3 bold'>卡名称</div>" +
                                    "<div class='col-sm-1 bold'>卡长度</div>" +
                                    "<div class='col-sm-2 bold'>卡BIN</div>" +
                                    "<div class='col-sm-1 bold'>卡BIN长度</div>" +
                                    "<div class='col-sm-1 bold'>卡类型</div>";
                                $(str2).appendTo($searchList);
                                for (var yhk in data) {
                                    total++;
                                    var ff = data[yhk];
                                    var str ="";
                                    for(var ii = 0; ii<ff.length ;ii++){
                                        var f=ff[ii];
                                        str += "<div class='col-sm-2'><a>" + yhk + "</a></div>" +
                                            "<div class='col-sm-2'>"+f["bank_name_short"] + "</div>" +
                                            "<div class='col-sm-3'>"+f["card_name"]+ "</div>" +
                                            "<div class='col-sm-1'>"+f["card_length"]+ "</div>" +
                                            "<div class='col-sm-2'>"+f["bin"]+ "</div>" +
                                            "<div class='col-sm-1'>"+f["bin_length"]+ "</div>" +
                                            "<div class='col-sm-1'>"+f["card_type"]+"</div>";
                                    }
                                    $(str).appendTo($searchList);
                                }
                                if (total > 0) {
                                    $("#search-msg").html('为您找到相关结果' + total + '个： <span class="text-navy">“' + val + '”</span>');
                                } else {
                                    $("#search-msg").html('为您找到相关结果0个： <span class="text-navy">“' + val + '”</span>');
                                    $("#index-list").empty();
                                }

                            }
                        }

                    },
                    error: function (d) {
                        console.log(d);
                    }
                });
            }
        };
        return {
            init: _init
        };
    })(jQuery);
    search.init();


})();