/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {



        var _init = function init(_data) {
            _initListTable();
            _event();
        };

        var params = {"indexName":"yhzh_khxx","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'susp_name',title: '姓名',sortable:true},
                    {field: 'ssyh',title: '所属银行',sortable:true},
                    {field: 'kh',title: '卡号',sortable:true},
                    {field: 'zh',title: '账号',sortable:true},
                    {field: 'ljjybs',title: '交易笔数',sortable:true},
                    {field: 'khrq',title: '开户日期',sortable:true},
                    {field: 'xhrq',title: '销户日期',sortable:true},
                    {field: 'khwd',title: '开户网点',sortable:true},
                    {field: 'zzhm',title: '证照号码',sortable:true},
                    {field: 'name',title: '客户名称',sortable:true},
                    {field: 'lxdh',title: '联系电话',sortable:true},
                    {field: 'lxsj',title: '联系手机',sortable:true},
                    {field: 'opt',title: '操作',width:'130px'}
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
                                "field": "ssyh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "kh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "zh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "email",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"5",
                                "groupType":"should",
                                "field": "name",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"6",
                                "groupType":"should",
                                "field": "zzhm",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
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
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"'>查看</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"'>分析</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs jyjl' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"' >交易记录</div>&nbsp;"+
                                    "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"'  data-fileId='"+data[i]["file_id"]+"'>删除</div>"
                                    ;
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
            $("#reginfo").on('click',function () {
                top.contabs.addMenuItem("/view/yhzh2/yhzh-reg.html",'导入银行账号信息');
            });
            $("#jyjlinfo").on('click',function () {
                top.contabs.addMenuItem("/view/yhzh2/jyjl/yhzh-jyjl.html",'导入银行账号交易记录');
            });


            $("#data-table").on('click','.jyjl',function () {
                top.contabs.addMenuItem("/view/yhzh2/jyjl/yhzh-jyjl-list.html?id="+$(this).attr("data-id"),'['+$(this).attr("data-name")+']交易记录列表');
            });

            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"),$(this).attr("data-fileId"));
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/yhzh2/yhzh-reg-detail.html?id="+$(this).attr("data-id"),'查看['+$(this).attr("data-name")+']开户信息');
            });
            $("#data-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/yhzh2/analyze/yhzh-analyze.html?id="+$(this).attr("data-id"),'银行流水信息分析');
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
                text:"是否删除银行账号信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/yhzh2/khxx/delete",
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
