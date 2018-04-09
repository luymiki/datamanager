$(function() {
    'use strict';

    var fileManager = (function($){
        var _init = function(){
            _list();
            _listTags();
            _typeBtn();
            _folderBtn();
            _btnTagsBtn();
            _btnEvent();
        };
        var _folder;
        var _type;
        var _tags;
        var _pageNum=1;
        var _pageSize=10;
        var _pagination;
        var _pagination_reload=false;
        //{"field":"folder","values":["文件","图片"],"searchType":1,	"dataType":1},{"field":"tags","values":["email"],"searchType":3,"dataType":2}
        var params = {"indexName":"attachment","conditions":[],"sort":"create_time desc"};
        var _list = function (page) {
            page = page|| _pageNum;
            var con = [];
            if(_folder){
                var v = {
                    "field": "folder",
                    "values": [_folder],
                    "searchType": 1,
                    "dataType":2,
                };
                con[con.length]=v;
            }
            if(_tags){
                var v = {
                    "field": "tags",
                    "values": [_tags],
                    "searchType": 1,
                    "dataType":2,
                };
                con[con.length]=v;
            }
            params["conditions"]= con;
            $.ajax.proxy({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":page,"pageSize":_pageSize,"paramsStr":JSON.stringify(params)},
                async:true,
                success:function (d) {
                    console.log(d);
                    if(d.status===200){
                        if(d.data.data){
                            var total = d.data.total;
                            var data = d.data.data;
                            var $fileList = $("#file-list").empty();
                            for(var i=0;i<data.length ;i++){
                                var f = data[i];
                                var $fileBox = $('<div class="file-box" id="box-'+f["id"]+'"></div>').appendTo($fileList);

                                var $file =$('<div class="file"></div>').appendTo($fileBox);
                                var $fa = $('<a href="/upload/'+f["path"]+'" target="_blank"></a>').appendTo($file);
                                $('<span class="corner"></span>').appendTo($fa);

                                if(/bmp$|jpg$|jpeg$|png$|gif$/.test(f["suffix"])){
                                    $('<div class="image"><img alt="image" class="img-responsive" src="/upload/'+f["path"]+'"></div>').appendTo($fa);
                                }else if(/avi$|rmvb$|rm$|asf$|divx$|mpg$|mpeg$|mpe$|wmv$|mp4$|mkv$|vob$|flv$/.test(f["suffix"])){
                                    $('<div class="icon"><i class="fa fa-film"></i></div>').appendTo($fa);
                                }else if(/mp3$|aac$|wav$|wma$|cda$|flac$|m4a$|mid$|mka$|mp2$|mpa$|mpc$|ape$|ofr$|ogg$|ra$|wv$|tta$|ac3$|dts$/.test(f["suffix"])){
                                    $('<div class="icon"><i class="fa fa-music"></i></div>').appendTo($fa);
                                }else if(/xls$|xlsx$/.test(f["suffix"])){
                                    $('<div class="icon"><i class="fa fa-bar-chart-o"></i></div>').appendTo($fa);
                                }else{
                                    $('<div class="icon"><i class="fa fa-file"></i></div>').appendTo($fa);
                                }


                                $('<div class="file-name">'+f["name"]+'<br/><small>添加时间：'+f["create_time"]+'</small>' +
                                    '<div class="text-right" style="margin-top: 5px;">' +
                                    '<div class="btn btn-primary btn-outline btn-xs detail" data-id="'+f["id"]+'">查看</div>&nbsp;' +
                                    '<div class="btn btn-danger btn-outline btn-xs delete" data-id="'+f["id"]+'">删除</div>' +
                                    '</div>' +
                                    '</div>').appendTo($fa);

                            }
                            if(total>0){
                                $("#pagination").show();
                                var totalPages = (total%_pageSize===0)?(total/_pageSize):(total/_pageSize)+1
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
                                            _list(page);
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
        };

        var _listTags = function () {
            var tagParams={"aggs": [{ "field": "tags","aggsType": 1,"dataType":1} ],"sort":"create_time desc" };
            $.ajax.proxy({
                url:"/api/eqa/aggs",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(tagParams)},
                async:true,
                success:function (d) {
                    console.log(d);
                    if(d.status===200){
                        if(d.data.aggs){
                            var aggs = d.data.aggs;
                            var $tagsList = $("#tag-list").empty();
                            //$tagsList.append('<li><a href="javascript:void(0);" class="file-control active btn-file-tags" data-tags="证据">证据</a></li>');
                            for(var gp in aggs){
                                var hits = aggs[gp];
                                for(var i=0;i<hits.length ;i++){
                                    var f = hits[i];
                                    $tagsList.append($('<li><a href="javascript:void(0);" class="file-control '+(i===0?'active':'')+' btn-file-tags" data-tags="'+f["key"]+'">'+(f["key"]===''?'&nbsp;':f["key"])+'</a></li>'));
                                }
                            }

                        }
                    }

                },
                error:function (d) {
                    console.log(d);
                }
            });
        }

        var _typeBtn = function () {
            $(".file-manager").on("click",".btn-file-type",function () {
                $(".file-manager .btn-file-type").each(function (i,o) {
                    $(o).removeClass("active");
                });
                $(".file-manager .btn-file-tags").each(function (i,o) {
                    $(o).removeClass("active");
                });
                _type = $(this).addClass("active").attr("data-type");
                _tags = null;
                _pageNum=1;
                _pagination_reload = true;
                _list();
            });
        };
        var _folderBtn = function () {
            $(".file-manager").on("click",".btn-file-folder",function () {
                $(".file-manager .btn-file-folder").each(function (i,o) {
                    $(o).removeClass("active");
                });
                $(".file-manager .btn-file-type").each(function (i,o) {
                    $(o).removeClass("active");
                });
                $(".file-manager .btn-file-tags").each(function (i,o) {
                    $(o).removeClass("active");
                });
                _type = $(".file-manager .btn-file-type-all").addClass("active").attr("data-type");
                _folder = $(this).addClass("active").attr("data-folder");
                _tags = null;
                _pageNum=1;
                _pagination_reload = true;
                _list();
            });
        };
        var _btnTagsBtn = function () {
            $(".file-manager").on("click",".btn-file-tags",function () {
                $(".file-manager .btn-file-tags").each(function (i,o) {
                    $(o).removeClass("active");
                });
                $(".file-manager .btn-file-type").each(function (i,o) {
                    $(o).removeClass("active");
                });
                $(".file-manager .btn-file-folder").each(function (i,o) {
                    $(o).removeClass("active");
                });
                _type = $(".file-manager .btn-file-type-all").addClass("active").attr("data-type");
                _folder = $(".file-manager .btn-file-folder-all").addClass("active").attr("data-folder");
                _tags = $(this).addClass("active").attr("data-tags");
                _pageNum=1;
                _pagination_reload = true;
                _list();
            });
        };
        var _btnEvent = function () {
            $("#file-list").on("click",".file-name .detail",function () {
                top.contabs.addMenuItem("/view/file/file-detail.html?id="+$(this).attr("data-id"),'文件信息');
                return false;
            });
            $("#file-list").on("click",".file-name .delete",function () {
                var _id = $(this).attr("data-id");
                $.ajax.proxy({
                    url:"/api/admin/file/delete",
                    type:"post",
                    dataType:"json",
                    data:{id:_id},
                    async:false,
                    success:function (d) {
                        if(d.status===200) {
                            console.log(d.data);
                            $("#box-"+_id).remove();
                            toastrMsg.success("文件删除成功");
                        }
                    },
                    error:function (d) {
                        console.log(d);
                        toastrMsg.error("文件"+data['name']+"删除失败");
                    }
                });
                return false;
            });
        };
        return {init:_init};
    })(jQuery);

    fileManager.init();
});
