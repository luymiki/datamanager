/**
 * Created by hc.zeng on 2018/3/21.
 */
(function () {
    "use strict";

    var jhfx = (function () {
        var zdycxArr=[null,null];
        var _init = function init(_data) {
            $("#iframe0").on('load',function () {
                var ww =  document.getElementById("iframe0").contentWindow;
                var zdycx = ww.zdycx;
                zdycx.init();
                zdycx.showMetaIndexField();
                zdycxArr[0] = zdycx;
                var $this = $(this);
                var setWin = function () {
                    $this.height(ww.document.body.scrollHeight);
                };
                window.setInterval(setWin,1000);
            });
            $("#iframe1").on('load',function () {
                var ww =  document.getElementById("iframe1").contentWindow;
                var zdycx = ww.zdycx;
                zdycx.init();
                zdycx.showMetaIndexField();
                zdycxArr[1] = zdycx;
                var $this = $(this);
                var setWin = function () {
                    $this.height(ww.document.body.scrollHeight);
                };
                window.setInterval(setWin,1000);
            });

            //添加数据源
            $("#source-btn").click(_addDataSource);
            //删除按钮
            $("#more-data-source").on("click",".delete-btn",function () {
                var id = $(this).attr("data-id");
                $(this).off("click").remove();
                var f = $("#iframe-"+id);
                var invid = parseInt(f.attr("data-invid"),10);
                //console.log(invid);
                window.clearInterval(invid);
                f.remove();
                $("#"+id).remove();
            });
            //重置按钮
            $("#rest-btn").on("click",function () {
                $("#more-data-source").find(".delete-btn").each(function (i,o) {
                    var id = $(o).attr("data-id");
                    var index = parseInt($(o).attr("data-index"),10);
                    $(o).off("click").remove();
                    var f = $("#iframe-"+id);
                    var invid = parseInt(f.attr("data-invid"),10);
                    //console.log(invid);
                    window.clearInterval(invid);
                    f.remove();
                    $("#"+id).remove();
                    zdycxArr.splice(index-1,1);
                    //console.log(index)
                    //console.log(zdycxArr)
                });
                for(var i=0;i<zdycxArr.length;i++){
                    var zd = zdycxArr[i];
                    zd.rest();
                }
            });

            //查询
            $("#query-btn").on("click",_query);
        };

        var _addDataSource = function () {
            var dataSourceBox = $("#more-data-source");
            var id = new Date().getTime();
            var index = zdycxArr.length;
            $('<a class="delete-btn" data-id="'+id+'" data-index="'+index+'">删除数据源</a>').appendTo(dataSourceBox);
            $('<div class="row J_mainContent" id="'+id+'">'+
                '<iframe class="J_iframe" id="iframe-'+id+'" width="100%" height="200px" src="/view/mxfx/zdyjm/zdycx.html" frameborder="0" seamless></iframe>'+
            '</div>').appendTo(dataSourceBox);
            $("#iframe-"+id).on('load',function () {
                var ww =  document.getElementById("iframe-"+id).contentWindow;
                var zdycx = ww.zdycx;
                zdycx.init();
                zdycx.showMetaIndexField();
                zdycxArr[zdycxArr.length] = zdycx;
                var $this = $(this);
                var setWin = function () {
                    $this.height(ww.document.body.scrollHeight);
                };
                var invid = window.setInterval(setWin,1000);
                //console.log(invid)
                $this.attr("data-invid",invid);
            });
        }

        /**
         * 查询
         * @private
         */
        var _query = function () {
            var paramsArr = [];
            var fieldArr = [];
            var fieldNameArr = [];
            var indexNameArr = [];
            for(var i=0;i<zdycxArr.length;i++){
                var zd = zdycxArr[i];
                var condtion = zd.condtion();
                var field = zd.getMetaIndexField();
                var indexName = zd.getMetaIndex();
                if(field && field.length ===1 ){
                    fieldArr[fieldArr.length] = field.val();
                    fieldNameArr[fieldNameArr.length] = field.html();
                    paramsArr[paramsArr.length] = condtion;
                    indexNameArr[indexNameArr.length] = indexName;
                }
            }
            // console.log(indexNameArr);
            // console.log(fieldArr);
            // console.log(fieldNameArr);
            // console.log(paramsArr);

            $("#data-table").empty();
            if(fieldArr.length>1){
                $.ajax.proxy({
                    url:"/api/admin/fx/jhfx",
                    type:"post",
                    dataType:"json",
                    data:{"paramsArr":JSON.stringify(paramsArr),"fieldArr":JSON.stringify(fieldArr)},
                    async:false,
                    success : function (d) {
                        //console.log(d);
                        if(d.status===200){
                            console.log(d.data);
                            var table = $("#data-table");
                            $("<tr><td  style='width: 100px;'>序号</td><td>"+fieldNameArr[0]+"</td></tr>").appendTo(table);
                            for(var i=0;i < d.data.length ;i++){
                                var dd = d.data[i];
                                $("<tr><td>"+(i+1)+"</td><td>"+dd+"</td></tr>").appendTo(table);
                            }
                        }else {
                            toastrMsg.error("查询失败");
                        }
                    },
                    error:function(){
                        toastrMsg.error("查询失败2");
                    }
                });
            }else {
                toastrMsg.error("数据源少于2个！");
            }

        }

        return {
            init:_init
        };
    })();

    jhfx.init();


})();