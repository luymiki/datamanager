/**
 * Created by hc.zeng on 2018/3/21.
 */
(function () {
    "use strict";
    var zdycx;
    $("#iframe0").on('load',function () {
        var ww =  document.getElementById("iframe0").contentWindow;
        zdycx = ww.zdycx;
        zdycx.init();
        var $this = $(this);
        var setWin = function () {
            $this.height(ww.document.body.scrollHeight);
        };
        window.setInterval(setWin,1000);
    });


    $("#query-btn").on('click', function () {
        var params = zdycx.condtion();
        console.log(params);
        if(params){
            $("#data-show-box").show();
            _query($('#data-table'),params);
        }

    });
    $("#rest-btn").on('click', function () {
        $("#data-show-box").hide();
        zdycx.rest();
    });

    var _query = function(dataTable,params){
        var columns = [];
        columns[columns.length] = {field: 'checkbox',title: '选择',width:'50px',checkbox:true};
        columns[columns.length] = {field: 'xh',title: '序号',width:'50px'};
        columns[columns.length] =  {field: 'id',title: 'ID',visible:false};
        var fieldList = zdycx.fieldList();
        for (var i = 0; i < fieldList.length; i++) {
            var mm = fieldList[i];
            if (mm["fieldCode"] === "id") {
                continue;
            }
            columns[columns.length] =  {field: mm["fieldCode"],title: mm["fieldName"],formatter:formatter};
        }
        dataTable.myTable({
            // copyRow:$("#copy-row"),
            columns:columns,
            exportXls:$("#exportXls"),
            exportXlsFun:function () {
                console.log(JSON.stringify(params));
                var from = $('<form method="post" action="/api/eqa/exportEXcel" target="_blank"></form>').appendTo('body');
                $('<input type="text" name="paramsStr">').val(JSON.stringify(params)).appendTo(from);
                from.submit().remove();
            },
            ajax : function (request) {
                var sort = "create_time desc";
                if(request.data.sortName){
                    sort = request.data.sortName +" "+request.data.sortOrder;
                }
                params["sort"]=sort;
                $.ajax.proxy({
                    url:"/api/eqa/query",
                    type:"post",
                    dataType:"json",
                    data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(params)},
                    success : function (msg) {
                        if(msg.status===200){
                            console.log(msg)
                            var data = msg.data.data;
                            var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                            for(var i= 0;i<data.length;i++){
                                data[i]['xh'] = xh++;
                            }
                            request.success({
                                rows : data,
                                total : msg.data.total
                            });
                        }else {
                            request.success({
                                rows : [],
                                total : 0
                            });
                        }

                    },
                    error:function(){
                        toastrMsg.error("错误！");
                    }
                });

            },
            onDblClickCell:function(field, value, row, $element) {
                //console.log(row);
                var val = row[field];
                if(val instanceof Array){
                    var s = "";
                    for(var i=0 ; i< val.length;i++){
                        var d = val[i];
                        s += formatterStr(d)+" ";
                    }
                    val = s;
                }
                if(val!==""){
                    var la = window.parent.layer || layer;
                    la.open({
                        // time: 2000, //不自动关闭
                        type: 1,
                        skin: 'layui-layer-rim', //加上边框
                        area: ['450px', '340px'], //宽高
                        content:  "<div style='padding: 5px; '>"+val+"</div>"
                    });
                }
            }
        });

    };

    var formatter = function (val) {
        if(val instanceof Array){
            return formatterList(val);
        }else {
            return formatterStr(val);
        }
    };

    var formatterList = function (val){
        if(val){
            var s = "";
            for(var i=0 ; i< val.length && i<5;i++){
                s += val[i]+" ";
            }
            if(val.length >5){
                s += "...";
            }
            return s;
        }
        return val;
    };
    var formatterStr = function (d){
        if(d){
            var s = d;
            if(d.length >50 ){
                s = d.substring(0,50)+"...";
            }
            return s;
        }
        return d;
    };

})();