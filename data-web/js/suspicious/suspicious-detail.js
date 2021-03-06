/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var suspicious = (function () {
        var id;
        var conditions;
        var kyr;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            var id = params["id"];
            id = params["id"];
            conditions=[{
                "field": "susp_id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            _get(id);
            getQQ(id);
            getWeixin(id);
            getCft(id);
            getZfb(id);
            getYhzh(id);
            _event();
        };

        var params = {"indexName":"suspicious","conditions":[],"sort":"create_time desc"};

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
                            kyr =  file[0];
                            var data = {
                                file: kyr
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
        var getQQ = function (id) {
            var queryparams = {"indexName":"qqreginfo","conditions":[],"sort":"create_time desc"};
            $('#qqreg-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'id',title: 'ID',visible:false},
                    {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'qq',title: 'QQ号',sortable:true},
                    {field: 'name',title: '昵称',sortable:true},
                    {field: 'xm',title: '真实姓名'},
                    {field: 'xb',title: '性别'},
                    {field: 'csrq',title: '生日'},
                    {field: 'zcsj',title: '注册时间'},
                    {field: 'gj',title: '国家'},
                    {field: 'sf',title: '省份'},
                    {field: 'cs',title: '城市'},
                    {field: 'opt',title: '操作',width:'50px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    queryparams["sort"]=sort;
                    queryparams["conditions"]=conditions;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(queryparams)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>";
                                }
                                if(msg.data.total===0){
                                    $('#qqreg-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
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
        var getWeixin = function (id) {
            var queryparams = {"indexName":"wxreginfo","conditions":[],"sort":"create_time desc"};
            $('#weixin-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'id',title: 'ID',visible:false},
                    {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'weixin',title: '微信号',sortable:true},
                    {field: 'qq',title: 'QQ号',sortable:true},
                    {field: 'name',title: '昵称',sortable:true},
                    {field: 'bm',title: '别名'},
                    {field: 'xb',title: '性别'},
                    {field: 'dh',title: '手机号'},
                    {field: 'qm',title: '签名'},
                    {field: 'sf',title: '省份'},
                    {field: 'cs',title: '城市'},
                    {field: 'opt',title: '操作',width:'50px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    queryparams["sort"]=sort;
                    queryparams["conditions"]=conditions;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(queryparams)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>";
                                }
                                if(msg.data.total===0){
                                    $('#weixin-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
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
        var getCft = function (id) {
            var queryparams = {"indexName":"cftreginfo","conditions":[],"sort":"create_time desc"};
            $('#cft-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'zh',title: '账号',sortable:true},
                    {field: 'zhzt',title: '账户状态',sortable:true},
                    {field: 'name',title: '注册姓名',sortable:true},
                    {field: 'sfzh',title: '注册身份证号'},
                    {field: 'zcsj',title: '注册时间'},
                    {field: 'dh',title: '手机号'},
                    // {field: 'khxx_list',title: '开户行信息',formatter:formatterList},
                    {field: 'yhzh_list',title: '银行账号',formatter:formatterList},
                    {field: 'opt',title: '操作',width:'150px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    queryparams["sort"]=sort;
                    queryparams["conditions"]=conditions;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(queryparams)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs liushui' data-id='"+data[i]["id"]+"'>流水</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"'>分析</div>";
                                }
                                if(msg.data.total===0){
                                    $('#cft-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
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
        var getZfb = function (id) {
            var queryparams = {"indexName":"zfbreginfo","conditions":[],"sort":"create_time desc"};
            var zfbdata = kyr["zfb"];
            zfbdata =  zfbdata.join(" ");
            if(zfbdata == ""){
                $('#zfb-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                return false;
            }

            zfbdata = zfbdata.split(/\s+|,|、|，/);
            if(zfbdata.length===0){
                $('#zfb-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                return false;
            }
            var zfbdatacon = [{
                "field": "user_id",
                "values": zfbdata,
                "searchType": 1,
                "dataType":2,
            }];
            $('#zfb-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'user_id',title: '账号',sortable:true},
                    {field: 'dlsj',title: '登陆手机',sortable:true},
                    {field: 'email',title: '登陆邮箱',sortable:true},
                    {field: 'name',title: '注册姓名',sortable:true},
                    {field: 'sfzh',title: '注册身份证号'},
                    {field: 'bdsj',title: '绑定手机'},
                    {field: 'khxx_list',title: '开户行信息',formatter:formatterList},
                    {field: 'yhzh_list',title: '银行账号',formatter:formatterList},
                    {field: 'opt',title: '操作',width:'145px'}
                ],
                ajax : function (request) {
                    var sort = "create_time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    queryparams["sort"]=sort;
                    queryparams["conditions"]=zfbdatacon;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(queryparams)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看详情</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"' data-userId='"+data[i]["user_id"]+"'>流水分析</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs logininfo' data-id='"+data[i]["id"]+"'>登陆日志</div>&nbsp;" +
                                        "<div class='btn btn-info btn-outline btn-xs zhinfo' data-id='"+data[i]["id"]+"'>账户明细</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs txinfo' data-id='"+data[i]["id"]+"'>提现记录</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs zzinfo' data-id='"+data[i]["id"]+"'>转账明细</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs jyjl' data-id='"+data[i]["id"]+"'>交易记录</div>"
                                    ;
                                }
                                if(msg.data.total===0){
                                    $('#zfb-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
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
        var getYhzh = function (id) {
            var queryparams = {"indexName":"yhzh_khxx","conditions":[],"sort":"create_time desc"};
            var zfbdata = kyr["yhzh"];
            zfbdata =  zfbdata.join(" ");
            if(zfbdata === ""){
                $('#yhzh-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                return false;
            }
            zfbdata = zfbdata.split(/\s+|,|、|，/);
            if(zfbdata.length===0){
                $('#yhzh-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
                return false;
            }
            var zfbdatacon = [{
                "groupId": "kh",
                "field": "kh",
                "values": zfbdata,
                "searchType": 1,
                "dataType":2,
            },{
                "groupId": "zh",
                "field": "zh",
                "values": zfbdata,
                "searchType": 1,
                "dataType":2,
            }];
            $('#yhzh-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
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
                    queryparams["sort"]=sort;
                    queryparams["conditions"]=zfbdatacon;
                    $.ajax.proxy({
                        url:"/api/eqa/query",
                        type:"post",
                        dataType:"json",
                        data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(queryparams)},
                        success : function (msg) {
                            if(msg.status===200){
                                var data = msg.data.data;
                                var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
                                for(var i= 0;i<data.length;i++){
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"'>查看详情</div>" +
                                        "<div class='btn btn-info btn-outline btn-xs jyjl' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"' >交易记录</div>"+
                                        "<div class='btn btn-info btn-outline btn-xs analyze' data-id='"+data[i]["id"]+"' data-name='"+data[i]["name"]+"'>流水分析</div>"
                                    ;
                                }
                                if(msg.data.total===0){
                                    $('#yhzh-table').bootstrapTable("destroy").html("<tr><td>无</td></tr>");
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

        var _event = function () {
            $("#qqreg-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/qq/reg/qq-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#weixin-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/weixin/weixin-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#cft-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/cft/cft-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#cft-table").on('click','.liushui',function () {
                top.contabs.addMenuItem("/view/cft/liushui/cft-liushui-list.html?id="+$(this).attr("data-id"),'财付通流水列表');
            });
            $("#cft-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/cft/analyze/cft-analyze.html?id="+$(this).attr("data-id"),'财付通流水信息分析');
            });

            $("#zfb-table").on('click','.logininfo',function () {
                top.contabs.addMenuItem("/view/zfb/logininfo/zfb-logininfo-list.html?id="+$(this).attr("data-id"),'登录日志列表');
            });
            $("#zfb-table").on('click','.zhinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-list.html?id="+$(this).attr("data-id"),'账户明细列表');
            });
            $("#zfb-table").on('click','.txinfo',function () {
                top.contabs.addMenuItem("/view/zfb/txinfo/zfb-txinfo-list.html?id="+$(this).attr("data-id"),'提现记录列表');
            });
            $("#zfb-table").on('click','.zzinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zzinfo/zfb-zzinfo-list.html?id="+$(this).attr("data-id"),'转账明细列表');
            });
            $("#zfb-table").on('click','.jyjl',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl-list.html?id="+$(this).attr("data-id"),'交易记录列表');
            });
            $("#zfb-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/zfb/analyze/zfb-analyze.html?id="+$(this).attr("data-id"),"["+$(this).attr("data-userId")+']分析');
            });
            $("#zfb-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zfb-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });

            $("#yhzh-table").on('click','.jyjl',function () {
                top.contabs.addMenuItem("/view/yhzh2/jyjl/yhzh-jyjl-list.html?id="+$(this).attr("data-id"),'['+$(this).attr("data-name")+']交易记录列表');
            });
            $("#yhzh-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/yhzh2/yhzh-reg-detail.html?id="+$(this).attr("data-id"),'查看['+$(this).attr("data-name")+']开户信息');
            });
            $("#yhzh-table").on('click','.analyze',function () {
                top.contabs.addMenuItem("/view/yhzh2/analyze/yhzh-analyze.html?id="+$(this).attr("data-id"),'银行流水信息分析');
            });
        }
        return {
            init:_init
        };
    })();

    suspicious.init();


})(document, window, jQuery);