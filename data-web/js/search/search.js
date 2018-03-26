/**
 * Created by hc.zeng on 2018/3/25.
 */
(function () {
    'use strict';

    var search = (function ($) {
        var _init = function () {
            $("#search-btn").click(_search);
        }

        var _pageNum=1;
        var _pageSize=10;
        var _pagination;
        var _pagination_reload=false;
        var _search = function (page) {
            page = page|| _pageNum;

            var val = $("#search").val();
            if(val !== "" ){
                $.ajax.proxy({
                    url:"/api/eqa/fulltext",
                    type:"post",
                    dataType:"json",
                    data:{"pageNum":page,"pageSize":_pageSize,"keyword":val},
                    async:true,
                    success:function (d) {
                        console.log(d);
                        if(d.status===200){
                            if(d.data.data){
                                var total = d.data.total;
                                var data = d.data.data;
                                var $searchList = $("#search-list").empty();
                                var searchData = [];
                                for(var i=0;i<data.length ;i++){
                                    var f = data[i];
                                    var str = "";
                                    for(var k in f){
                                        if(k==="_index"|| k==="path"
                                            ||k==="_type"||k==="_score"
                                            ||k==="_id" ||k==="id"
                                            ||k==="create_time" ||k==="modify_time"
                                            ||k==="type" ||k==="file_list"
                                            || k.indexOf("id")>0){
                                            continue;
                                        }
                                        str += "&nbsp;"+f[k];
                                        if(str.length>200){
                                            str = str.substring(0,200)+"....";
                                            break;
                                        }
                                    }
                                    var $sr = $('<div class="search-result"></div>').appendTo($searchList);
                                    $('<h4></h4>').appendTo($sr).html(str);
                                    $('<div class="hr-line-dashed"></div>').appendTo($searchList);
                                }
                                if(total>0){
                                    $("#search-msg").html('为您找到相关结果'+total+'个： <span class="text-navy">“'+val+'”</span>');
                                    $("#pagination").show();
                                    var totalPages = (total%_pageSize===0)?(total/_pageSize):Math.floor(total/_pageSize)+1
                                    //后台总页数与可见页数比较如果小于可见页数则可见页数设置为总页数，
                                    var visiblecount = 10;
                                    if (totalPages < visiblecount) {
                                        visiblecount = totalPages;
                                    }
                                    if(_pagination_reload){
                                        _pagination_reload = false;
                                        $("#pagination").empty();
                                        $("#pagination").unbind("page");
                                        $("#pagination").removeData("twbs-pagination");
                                    }
                                    _pagination = $('#pagination').twbsPagination({
                                        totalPages: totalPages,
                                        visiblePages: visiblecount,
                                        onPageClick: function (event, page) {
                                            if(_pageNum != page){
                                                _pageNum = page;
                                                _search(page);
                                            }
                                        }
                                    });
                                }else {
                                    $("#pagination").hide();
                                }

                            }
                        }

                    },
                    error:function (d) {
                        console.log(d);
                    }
                });
            }
        }

        return {
            init:_init
        };
    })(jQuery);
    search.init();



})();