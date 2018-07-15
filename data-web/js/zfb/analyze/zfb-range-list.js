/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var zfbInfo;
    var user_id;
    var ds_id;
    var xcbh;
    var range;
    var zcType;
    var zfb = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            range = params["range"];
            ds_id = params["ds_id"];
            zcType = params["zcType"];
            _get(id);
        }
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
                            user_id = zfbInfo["user_id"];
                            xcbh = zfbInfo["xcbh"];
                            $("#zfb-title").html("支付宝账号 [ "+zfbInfo["user_id"]+" ] "+(ds_id?"与对手["+ds_id+"]":"")+"的交易汇总");
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


        return {
            init:_init
        };
    })();

    var reg = (function () {
        var con = [];

        var _init = function init(_data) {

            con[con.length]={
                "field": "user_id",
                "values": [zfbInfo["user_id"]],
                "searchType": 1,
                "dataType":2,
            };
            con[con.length]={
                "field": "xcbh",
                "values": [zfbInfo["xcbh"]],
                "searchType": 1,
                "dataType":2,
            };
            if(range){
                con[con.length]={
                    "field": "jyje",
                    "values": range.replace("*","").split("-"),
                    "searchType": 6,
                    "dataType":2,
                };
            }

            if(ds_id){
                con[con.length]={
                    "field": "ds_id",
                    "values": [ds_id],
                    "searchType": 1,
                    "dataType":2,
                };
            }
            if(zcType){
                if(zcType==="100"){
                    con[con.length]={
                        "field": "zc100",
                        "values": ["0"],
                        "searchType": 1,
                        "dataType":2,
                    };
                }else if(zcType==="-100"){
                    con[con.length]={
                        "field": "zc100",
                        "values": ["0"],
                        "searchType": 3,
                        "dataType":2,
                    };
                }

            }
            _initJylsTable();
            _initZhxxList();
            _initTxxxList();
            _initZzxxList();
            _initJylsList();
            _event();
        };

        var _initJylsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyls",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh,"jyjeRange":range,"dsId":ds_id,"zcType":zcType||""},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        data = [msg.data];
                        //console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                            data[i]['name'] = zfbInfo["name"];
                        }
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });

            $('#data-table').myTable({
                height:120,
                pagination:false,
                columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
                    {field: 'name',title: '户名',sortable:true},
                    {field: 'ljjyje',title: '累计交易金额',sortable:true,formatter:formatter},
                    {field: 'ljjybs',title: '累计交易笔数',sortable:true},
                    {field: 'zdjyje',title: '最大交易金额',sortable:true,formatter:formatter},
                    {field: 'zxjyje',title: '最小交易金额',sortable:true,formatter:formatter},
                    {field: 'ljzrje',title: '累计转入金额',sortable:true,formatter:formatter},
                    {field: 'ljzrbs',title: '累计转入笔数',sortable:true},
                    {field: 'zdzrje',title: '最大转入金额',sortable:true,formatter:formatter},
                    {field: 'zxzrje',title: '最小转入金额',sortable:true,formatter:formatter},
                    {field: 'ljzcje',title: '累计转出金额',sortable:true,formatter:formatter},
                    {field: 'ljzcbs',title: '累计转出笔数',sortable:true},
                    {field: 'zdzcje',title: '最大转出金额',sortable:true,formatter:formatter},
                    {field: 'zxzcje',title: '最小转出金额',sortable:true,formatter:formatter},
                    {field: 'pjjyje',title: '平均交易金额',sortable:true,formatter:formatter},
                    {field: 'zzjysj',title: '最早交易时间',sortable:true},
                    {field: 'zwjysj',title: '最晚交易时间',sortable:true}
                ],
                data : data
            });
        };
        var _initZhxxList = function(){
            var data = [];
            var params ={"indexName":"zfbzhinfo","conditions":[],"sort":"create_time desc"};
            $('#zhxx-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'user_id',title: '支付宝账号',sortable:true},
                    {field: 'name',title: '账户名称',sortable:true},
                    {field: 'df_user_id',title: '对方ID',sortable:true},
                    {field: 'df_name',title: '对方户名',sortable:true},
                    {field: 'xfmc',title: '消费名称',sortable:true},
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
                                if(msg.data.total===0){
                                    $('#zhxx-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                                    return false;
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
        var _initTxxxList = function(){
            var data = [];
            var params = {"indexName":"zfbtxinfo","conditions":[],"sort":"create_time desc"};
            $('#txxx-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    // {field: 'user_id',title: '支付宝账号',sortable:true},
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
                                if(msg.data.total===0){
                                    $('#txxx-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                                    return false;
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
        var _initZzxxList = function(){
            var data = [];
            var params = {"indexName":"zfbzzinfo","conditions":[],"sort":"create_time desc"};
            $('#zzxx-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'jyh',title: '交易号',sortable:true},
                    {field: 'fkf_id',title: '付款方账号',sortable:true},
                    {field: 'skf_id',title: '收款方账号',sortable:true},
                    {field: 'skjgmc',title: '收款机构信息',sortable:true},
                    {field: 'dzsj',title: '到账时间',sortable:true},
                    {field: 'je',title: '转账金额（元）',sortable:true},
                    {field: 'jdlx',title: '借贷类型',sortable:true},
                    {field: 'zzcpmc',title: '转账产品名称',sortable:true},
                    {field: 'jyfsd',title: '交易发生地',sortable:true},
                    {field: 'txlsh',title: '提现流水号',sortable:true},
                    {field: 'xcbh',title: '协查编号',sortable:true}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
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
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
                                }
                                if(msg.data.total===0){
                                    $('#zzxx-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                                    return false;
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
        var _initJylsList = function(){
            var data = [];
            var params = {"indexName":"zfbjyjlinfo","conditions":[],"sort":"create_time desc"};
            $('#jyls-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'hzhb_id',title: '合作伙伴ID',sortable:true},
                    {field: 'mj_id',title: '买家ID',sortable:true},
                    {field: 'mjxx',title: '买家户名',sortable:true},
                    {field: 'maijia_id',title: '卖家ID',sortable:true},
                    {field: 'maijiaxx',title: '卖家户名',sortable:true},
                    {field: 'je',title: '交易金额（元）',sortable:true},
                    {field: 'spmc',title: '商品名称',sortable:true},
                    {field: 'sksj',title: '收款时间',sortable:true},
                    {field: 'opt',title: '操作',width:'50px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
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
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
                                }
                                if(msg.data.total===0){
                                    $('#jyls-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                                    return false;
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

        var formatter = function (val) {
            return val === undefined || val=== null? val :val.toFixed(2);
        }
        var _event = function () {
            $("#zhxx-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#jyls-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#txxx-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#zzxx-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
        };

        return {
            init:_init
        };
    })();

    zfb.init();
    reg.init();


})(document, window, jQuery);
