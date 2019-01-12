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
                    url: "/api/admin/location/ip",
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(vals),
                    async: true,
                    success: function (d) {
                        console.log(d);
                        if (d.status === 200) {
                            if (d.data) {
                                var total = d.data.length;
                                var data = d.data;
                                var $searchList = $("#search-list").empty();
                                for (var i = 0; i < data.length; i++) {
                                    var f = data[i];
                                    var str = "<a>"+f["ip"] + "</a>&emsp;" + f["countryName"] + f["regionName"] + f["cityName"];
                                    var $sr = $('<div class="search-result"></div>').appendTo($searchList);
                                    $('<div class="search-info"></div>').appendTo($sr).html(str);
                                    $('<div class="hr-line-dashed"></div>').appendTo($searchList);
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