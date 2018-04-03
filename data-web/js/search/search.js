/**
 * Created by hc.zeng on 2018/3/25.
 */
(function () {
    'use strict';

    var search = (function ($) {
        var _init = function () {
            $("#search-btn").click(_search);
            $("#search-list ").on("click",".search-result",function(){
                var index = $(this).attr("data-index");
                switch (index){
                    case "qqloginip":{
                        top.contabs.addMenuItem("/view/qq/loginip/qq-loginip-detail.html?id="+$(this).attr("data-id"),'查看信息');
                        break;
                    }
                    case "qqzone":{
                        top.contabs.addMenuItem("/view/qq/qzone/qq-qzone-detail.html?id="+$(this).attr("data-id"),'查看信息');
                        break;
                    }
                    case "qqreginfo":{
                        top.contabs.addMenuItem("/view/qq/reg/qq-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
                        break;
                    }
                    case "email":{
                        top.contabs.addMenuItem("/view/file/email/file-email-detail.html?id="+$(this).attr("data-id"),'查看邮件');
                        break;
                    }
                    case "suspicious":{
                        top.contabs.addMenuItem("/view/suspicious/suspicious-detail.html?id="+$(this).attr("data-id"),'可疑人员信息');
                        break;
                    }
                    case "attachment":{
                        top.contabs.addMenuItem("/view/file/file-detail.html?id="+$(this).attr("data-id"),'文件信息');
                        break;
                    }
                }
                //top.contabs.addMenuItem("/view/search/search-detail.html?id="+$(this).attr("data-id")+"&index="+$(this).attr("data-index"),'查询结果');
            });

        }

        var _pageNum=1;
        var _pageSize=10;
        var _pagination;
        var _pagination_reload=false;
        var _search = function (page) {
            if(!page){
                _pagination_reload = true;
            }
            page = page|| _pageNum;

            var val = $("#search").val();
            if(val !== "" ){
                var vals = val.split(" ");
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
                                    for(var kk = 0;kk <vals.length; kk++){
                                        str = str.replace(vals[kk],"<code>"+vals[kk]+"</code>");
                                    }
                                    str.replace(",",", ");
                                    var $sr = $('<div class="search-result" style="cursor: pointer;" data-id="'+f["id"]+'" data-index="'+f["_index"]+'"></div>').appendTo($searchList);
                                    $('<div class="search-info" ></div>').appendTo($sr).html(str);
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