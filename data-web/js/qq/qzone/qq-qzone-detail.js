/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var id;

        var _init = function init(_data) {
            var params = utils.getURLParams();
            id = params["id"];
            _get(id);
            _btnEvent();
        };

        var params = {"indexName":"qqzone","conditions":[],"sort":"create_time desc"};
        var paramsFile =  {"indexName":"attachment","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":1,
            }];
            $.ajax({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            var data = {
                                file: file[0]
                            };
                            var html = template('eml-template', data);
                            $("#eml-content").append(html);
                            var $fileList = $("#eml-content").find("#file-box");

                            var fileIds = file[0]["file_id"];
                            if(fileIds.length>0){
                                paramsFile["conditions"]= [{
                                        "field": "id",
                                        "values": fileIds,
                                        "searchType": 1,
                                        "dataType":1,
                                    }
                                ];
                                $.ajax.proxy({
                                    url:"/api/eqa/query",
                                    type:"post",
                                    dataType:"json",
                                    data:{"pageNum":1,"pageSize":100,"paramsStr":JSON.stringify(paramsFile)},
                                    async:true,
                                    success:function (d) {
                                        console.log(d);
                                        if(d.status===200){
                                            if(d.data.data){
                                                var total = d.data.total;
                                                var data = d.data.data;
                                                $fileList.empty();
                                                for(var i=0;i<data.length ;i++){
                                                    var f = data[i];
                                                    var $fileBox = $('<div class="file-box" id="box-'+f["id"]+'"></div>').appendTo($fileList);

                                                    var $file =$('<div class="file"></div>').appendTo($fileBox);
                                                    var $fa = $('<a href="/upload/'+f["path"]+'" target="_blank"></a>').appendTo($file);
                                                    $('<span class="corner"></span>').appendTo($fa);
                                                    $('<div class="image"><img alt="image" class="img-responsive" src="/upload/'+f["path"]+'"></div>').appendTo($fa);

                                                    $('<div class="file-name">'+f["name"]+'<br/><small>添加时间：'+f["create_time"]+'</small>' +
                                                        '<div class="text-right" style="margin-top: 5px;">' +
                                                        '<div class="btn btn-primary btn-outline btn-xs detail" data-id="'+f["id"]+'">查看</div>&nbsp;' +
                                                        '<div class="btn btn-danger btn-outline btn-xs delete" data-id="'+f["id"]+'">删除</div>' +
                                                        '</div>' +
                                                        '</div>').appendTo($fa);

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
                    }else {
                        toastrMsg.error("查询失败");
                    }
                },
                error:function(){
                    toastrMsg.error("查询失败");
                }
            });
        };

        var _btnEvent = function () {
            $("#eml-content").on("click",".file-name .detail",function () {
                top.contabs.addMenuItem("/view/file/file-detail.html?id="+$(this).attr("data-id"),'文件信息');
                return false;
            });
            $("#eml-content").on("click",".file-name .delete",function () {
                var fileId = $(this).attr("data-id");
                $.ajax.proxy({
                    url:"/api/admin/qq/qzone/delpic",
                    type:"post",
                    dataType:"json",
                    data:{id:id,fileId:fileId},
                    async:false,
                    success:function (d) {
                        if(d.status===200) {
                            console.log(d.data);
                            $("#box-"+fileId).remove();
                            toastrMsg.success("文件删除成功");
                        }
                    },
                    error:function (d) {
                        console.log(d);
                        toastrMsg.error("文件删除失败");
                    }
                });
                return false;
            });
        };

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);