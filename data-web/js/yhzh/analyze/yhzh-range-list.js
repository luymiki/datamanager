/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var yhzhInfo;
    var susp_id;
    var ds_id;
    var yhzh_id;
    var range;
    var zcType;
    var cft = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            yhzh_id = params["id"];
            range = params["range"];
            ds_id = params["ds_id"];
            zcType = params["zcType"];
            _get(yhzh_id);
        }
        var params = {"indexName":"yhzh_khxx","conditions":[],"sort":"create_time desc"};

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
                            yhzhInfo =file[0];
                            susp_id = yhzhInfo["susp_id"];
                            $("#cft-title").html("银行账号 [ "+yhzhInfo["kh"]+" ] "+(ds_id?"与对手["+ds_id+"]":"")+"的交易汇总");
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
                "field": "ssyh",
                "values": [yhzhInfo["ssyh"]],
                "searchType": 1,
                "dataType":2,
            };
            con[con.length]={
                "field": "kh",
                "values": [yhzhInfo["kh"]],
                "searchType": 1,
                "dataType":2,
            };
            if(yhzhInfo["ssyh"]!=="农业银行"){
                con[con.length]={
                    "field": "zh",
                    "values": [yhzhInfo["zh"]],
                    "searchType": 1,
                    "dataType":2,
                };
            }
            if(range) {
                con[con.length] = {
                    "field": "jyje",
                    "values": range.replace("*", "").split("-"),
                    "searchType": 6,
                    "dataType": 2,
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
            _initJylsList();
            _event();
        };

        var _initJylsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/yhzh/jyls",
                type:"post",
                dataType:"json",
                data:{"ssyh":yhzhInfo["ssyh"],"kh":yhzhInfo["kh"],"zh":yhzhInfo["zh"],"jyjeRange":range,"dsId":ds_id,"zcType":zcType||""},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        data = [msg.data];
                        //console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                            data[i]['name'] = yhzhInfo["name"];
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

        var _initJylsList = function(){
            var data = [];
            var params = {"indexName":"yhzh_jyls","conditions":[],"sort":"create_time desc"};
            $('#jyls-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'ssyh',title: '所属银行',sortable:true},
                    {field: 'kh',title: '查询卡号',sortable:true},
                    {field: 'zh',title: '查询账号',sortable:true},
                    {field: 'jylx',title: '交易类型',sortable:true},
                    {field: 'jdlx',title: '借贷标志',sortable:true},
                    {field: 'jybz',title: '币种',sortable:true},
                    {field: 'jyje',title: '交易金额',sortable:true},
                    {field: 'jyye',title: '交易余额',sortable:true},
                    {field: 'jysj',title: '交易时间',sortable:true},
                    {field: 'jylsh',title: '交易流水号',sortable:true},
                    {field: 'dfmc',title: '交易对方名称',sortable:true},
                    {field: 'dfzh',title: '交易对方账号',sortable:true},
                    {field: 'dfkh',title: '交易对方卡号',sortable:true},
                    {field: 'dfzjhm',title: '交易对方证件号码',sortable:true},
                    {field: 'dfye',title: '交易对手余额',sortable:true},
                    {field: 'dfzhkhh',title: '交易对方账号开户行',sortable:true},
                    {field: 'jyzy',title: '交易摘要',sortable:true},
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
            $("#jyls-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/yhzh/jyjl/yhzh-jyjl-detail.html?id="+$(this).attr("data-id"),'查看流水信息');
            });
        };

        return {
            init:_init
        };
    })();

    cft.init();
    reg.init();


})(document, window, jQuery);
