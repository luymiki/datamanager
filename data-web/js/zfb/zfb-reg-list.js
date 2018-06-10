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

        var params = {"indexName":"zfbreginfo","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'user_id',title: '账号',sortable:true},
                    {field: 'dlsj',title: '登陆手机',sortable:true},
                    {field: 'email',title: '登陆邮箱',sortable:true},
                    {field: 'name',title: '注册姓名',sortable:true},
                    {field: 'sfzh',title: '注册身份证号'},
                    {field: 'bdsj',title: '绑定手机'},
                    {field: 'khxx_list',title: '开户行信息',formatter:formatterList},
                    {field: 'yhzh_list',title: '银行账号',formatter:formatterList},
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
                                "field": "user_id",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "dlsj",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "email",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "bdsj",
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
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"' data-userId='"+data[i]["user_id"]+"'>分析</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs logininfo' data-id='"+data[i]["id"]+"'>登陆日志</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs zhinfo' data-id='"+data[i]["id"]+"'>账户明细</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs txinfo' data-id='"+data[i]["id"]+"'>提现记录</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs zzinfo' data-id='"+data[i]["id"]+"'>转账明细</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs jyjl' data-id='"+data[i]["id"]+"'>交易记录</div>&nbsp;"+
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
                top.contabs.addMenuItem("/view/zfb/zfb-reg.html",'导入支付宝信息');
            });
            $("#logininfo").on('click',function () {
                top.contabs.addMenuItem("/view/zfb/logininfo/zfb-logininfo.html",'导入支付宝登录日志');
            });
            $("#zhinfo").on('click',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo.html",'导入支付宝账户明细');
            });
            $("#txinfo").on('click',function () {
                top.contabs.addMenuItem("/view/zfb/txinfo/zfb-txinfo.html",'导入支付宝提现记录');
            });
            $("#zzinfo").on('click',function () {
                top.contabs.addMenuItem("/view/zfb/zzinfo/zfb-zzinfo.html",'导入支付宝转账明细');
            });
            $("#jyjlinfo").on('click',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl.html",'导入支付宝交易记录');
            });


            $("#data-table").on('click','.logininfo',function () {
                top.contabs.addMenuItem("/view/zfb/logininfo/zfb-logininfo-list.html?id="+$(this).attr("data-id"),'登录日志列表');
            });
            $("#data-table").on('click','.zhinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-list.html?id="+$(this).attr("data-id"),'账户明细列表');
            });
            $("#data-table").on('click','.txinfo',function () {
                top.contabs.addMenuItem("/view/zfb/txinfo/zfb-txinfo-list.html?id="+$(this).attr("data-id"),'提现记录列表');
            });
            $("#data-table").on('click','.zzinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zzinfo/zfb-zzinfo-list.html?id="+$(this).attr("data-id"),'转账明细列表');
            });
            $("#data-table").on('click','.jyjl',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl-list.html?id="+$(this).attr("data-id"),'交易记录列表');
            });
            $("#data-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/zfb/analyze/zfb-analyze.html?id="+$(this).attr("data-id"),"["+$(this).attr("data-userId")+']分析');
            });


            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"),$(this).attr("data-fileId"));
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zfb-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
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
                text:"是否删除支付宝信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/zfb/delete",
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
