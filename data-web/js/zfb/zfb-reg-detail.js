/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var zfbInfo;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
            $("#eml-content").on('click','#logininfo',function () {
                top.contabs.addMenuItem("/view/zfb/logininfo/zfb-logininfo-list.html?id="+$(this).attr("data-id"),'登录日志列表');
            });
            $("#eml-content").on('click','#zhinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-list.html?id="+$(this).attr("data-id"),'账户明细列表');
            });
            $("#eml-content").on('click','#txinfo',function () {
                top.contabs.addMenuItem("/view/zfb/txinfo/zfb-txinfo-list.html?id="+$(this).attr("data-id"),'提现记录列表');
            });
            $("#eml-content").on('click','#zzinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zzinfo/zfb-zzinfo-list.html?id="+$(this).attr("data-id"),'转账明细列表');
            });
            $("#eml-content").on('click','#jyjl',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl-list.html?id="+$(this).attr("data-id"),'交易记录列表');
            });

            loginfo.loadLoginInfoList();
            //zhmx.loadZhmxList();
            //txjl.loadZhmxList();
        };

        var params = {"indexName":"zfbreginfo","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            $.ajax.proxy({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                async:false,
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            zfbInfo =file[0];
                            var data = {
                                file: zfbInfo
                            };
                            var html = template('eml-template', data);
                            $("#eml-content").append(html);
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

        var loginfo = (function () {
            var loadLoginInfoList = function () {
                var params = {"indexName":"zfblogininfo","conditions":[],"sort":"create_time desc"};
                var _search;//查询的值
                $('#logininfo-table').myTable({
                    columns: [
                        {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'user_id',title: '支付宝账号',sortable:true},
                        {field: 'dlzh',title: '登陆账号',sortable:true},
                        {field: 'name',title: '账户名称',sortable:true},
                        {field: 'ip',title: 'IP',sortable:true},
                        {field: 'gsd',title: 'IP归属地',sortable:true},
                        {field: 'czsj',title: '登录时间',sortable:true},
                        {field: 'xcbh',title: '协查编号'}
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
                                    "field": "dlzh",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"2",
                                    "groupType":"should",
                                    "field": "name",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"3",
                                    "groupType":"should",
                                    "field": "ip",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"4",
                                    "groupType":"should",
                                    "field": "xcbh",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                }
                            ];
                        }
                        con[con.length]={
                            "field": "xcbh",
                            "values": [zfbInfo["xcbh"]],
                            "searchType": 1,
                            "dataType":2,
                        };
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
                                        //data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
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
            return {loadLoginInfoList:loadLoginInfoList};
        })();
        var zhmx = (function () {
            var loadZhmxList = function () {
                var params = {"indexName":"zfbzhinfo","conditions":[],"sort":"create_time desc"};
                var _search;//查询的值
                $('#zhmx-table').myTable({
                    columns: [
                        {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'user_id',title: '支付宝账号',sortable:true},
                        {field: 'name',title: '账户名称',sortable:true},
                        {field: 'df_user_id',title: '对方ID',sortable:true},
                        {field: 'df_name',title: '对方户名',sortable:true},
                        {field: 'xfmc',title: '消费名称'},
                        {field: 'je',title: '转账金额（元）',sortable:true},
                        {field: 'sjbj',title: '收/支标记',sortable:true},
                        {field: 'fksj',title: '付款时间',sortable:true},
                        {field: 'opt',title: '操作',width:'50px'}
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
                                    "field": "name",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"3",
                                    "groupType":"should",
                                    "field": "df_user_id",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"4",
                                    "groupType":"should",
                                    "field": "df_name",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                }
                            ];
                        }
                        con[con.length]={
                            "field": "xcbh",
                            "values": [zfbInfo["xcbh"]],
                            "searchType": 1,
                            "dataType":2,
                        };
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
                                        data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
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
            return {loadZhmxList:loadZhmxList};
        })();
        var txjl = (function () {
            var loadTxjlList = function () {
                var params = {"indexName":"zfbtxinfo","conditions":[],"sort":"create_time desc"};
                var _search;//查询的值
                $('#zhmx-table').myTable({
                    columns: [
                        {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'txlsh',title: '流水号',sortable:true},
                        {field: 'txlx',title: '提现类型',sortable:true},
                        {field: 'khyh',title: '开户银行',sortable:true},
                        {field: 'yhzh',title: '银行账号',sortable:true},
                        {field: 'sqsj',title: '申请时间',sortable:true},
                        {field: 'clsj',title: '处理时间',sortable:true},
                        {field: 'je',title: '转账金额（元）',sortable:true},
                        {field: 'zt',title: '状态',sortable:true},
                        {field: 'jyzt',title: '交易状态',sortable:true},
                        {field: 'sbyy',title: '失败原因',sortable:true}
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
                                    "field": "txlx",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"2",
                                    "groupType":"should",
                                    "field": "khyh",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"3",
                                    "groupType":"should",
                                    "field": "yhzh",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                },
                                {
                                    "groupId":"4",
                                    "groupType":"should",
                                    "field": "zt",
                                    "values": [_search],
                                    "searchType": 2,
                                    "dataType":1,
                                }
                            ];
                        }
                        con[con.length]={
                            "field": "xcbh",
                            "values": [zfbInfo["xcbh"]],
                            "searchType": 1,
                            "dataType":2,
                        };
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
                                        data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
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
            return {loadZhmxList:loadTxjlList};
        })();

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);