/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {

        var suspid;
        var type;
        var code;

        var _init = function init(_data) {
            var params = utils.getURLParams();
            suspid = params["suspid"];
            type = params["type"];
            code = params["code"];
            _initListTable();
            _event();
        };

        var params = {"indexName":"huaduan","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'yys',title: '运营商',sortable:true},
                    {field: 'zjhm',title: '手机号码',sortable:true},
                    {field: 'size',title: '流水数量',sortable:true},
                    {field: 'opt',title: '操作',width:'100px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    var con = [];
                    if(_search){
                        con=[
                            {
                                "groupId":"1",
                                "groupType":"should",
                                "field": "susp_name",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "yys",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "zjhm",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    if(suspid && type && code){
                        con[con.length]={
                            "field": "susp_id",
                            "values": [suspid],
                            "searchType": 1,
                            "dataType":1,
                        };
                        if("dh" === type){
                            con[con.length]={
                                "field": "zjhm",
                                "values": [code],
                                "searchType": 1,
                                "dataType":1,
                            };
                        }

                    }
                    params["sort"]=sort;
                    params["conditions"]=con;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(params)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-info btn-outline btn-xs liushui' data-id='"+data[i]["id"]+"'>流水</div>&nbsp;" +
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"'  data-fileId='"+data[i]["file_id"]+"'>删除</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"'>分析</div>";
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
                            toastrMsg.error("系统错误");
                        }
                    });
                }
            });
        };


        var _event = function () {
            $("#addBtn").on('click',function () {
                top.contabs.addMenuItem("/view/huadan/huadan.html",'导入话单信息');
            });
            $("#data-table").on('click','.liushui',function () {
                top.contabs.addMenuItem("/view/huadan/huadan-liushui-list.html?id="+$(this).attr("data-id"),'话单流水列表');
            });
            $("#data-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/huadan/huadan-liushui-list-analyze.html?id="+$(this).attr("data-id"),'话单流水分析');
            });
            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"),$(this).attr("data-fileId"));
            });
            $("#search-btn").on('click',function () {
                _search = $("#search-input").val();
                if(_search && $.trim(_search) !== ""){
                    $('#data-table').bootstrapTable("refresh");
                }else {
                    _search=null;
                    $('#data-table').bootstrapTable("refresh");
                }
            });
        };

        var _delete = function (id,fileId) {

            swalMsg.msg({
                text:"是否删除话单信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/huadan/delete",
                            type: "post",
                            dataType: "json",
                            data:{id: id, fileId:fileId},
                            success: function (d) {
                                console.log(d);
                                if (d.status === 200) {
                                    toastrMsg.success("删除成功");
                                    $('#data-table').bootstrapTable("refresh");
                                } else {
                                    toastrMsg.error("删除失败");
                                }
                            },
                            error: function () {
                                toastrMsg.error("删除失败");
                            }
                        });
                    }
                }
            });


        };

        var formatterList = function (d){
            if(d){
                var s = "";
                for(var i=0 ; i<d.length;i++){
                    s+= d[i]+" ";
                    if(i>10){
                        s+= "...";
                        break;
                    }
                }
                return s;
            }
            return d;
        };

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);
