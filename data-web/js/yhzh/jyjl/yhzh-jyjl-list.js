/**
 * Created by hc.zeng on 2018/3/21.
 */

(function (e, t, $) {
    "use strict";
    var yhzhInfo;
    var zfb = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
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
                            $("#susp-name").html(yhzhInfo["name"]+"&emsp;卡号："+yhzhInfo["kh"]+"&emsp;账号："+ (yhzhInfo["zh"]||""));
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



        var _init = function init(_data) {
            _initListTable();
            _event();
        };

        var params = {"indexName":"yhzh_jyls","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
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
                    {field: 'jywdmc',title: '交易网点名称',sortable:true},
                    {field: 'jywddm',title: '交易网点代码',sortable:true},
                    {field: 'rzh',title: '日志号',sortable:true},
                    {field: 'cph',title: '传票号',sortable:true},
                    {field: 'pzzl',title: '凭证种类',sortable:true},
                    {field: 'pzh',title: '凭证号',sortable:true},
                    {field: 'xjbz',title: '现金标志',sortable:true},
                    {field: 'zdh',title: '终端号',sortable:true},
                    {field: 'jysfcg',title: '交易是否成功',sortable:true},
                    {field: 'jyfsd',title: '交易发生地',sortable:true},
                    {field: 'shmc',title: '商户名称',sortable:true},
                    {field: 'shh',title: '商户号',sortable:true},
                    {field: 'ip',title: 'IP地址',sortable:true},
                    {field: 'mac',title: 'MAC地址',sortable:true},
                    {field: 'jygyh',title: '交易柜员号',sortable:true},
                    {field: 'bz',title: '备注',sortable:true},
                    {field: 'jyssfbz',title: '交易手续费币种',sortable:true},
                    {field: 'jyssfje',title: '交易手续费金额',sortable:true},
                    {field: 'jyssfzy',title: '交易手续费摘要',sortable:true},
                    {field: 'dlyhm',title: '登录用户名',sortable:true},
                    {field: 'dbrxm',title: '代办人姓名',sortable:true},
                    {field: 'dbrzjlx',title: '代办人证件类型',sortable:true},
                    {field: 'dbrzjhm',title: '代办人证件号码',sortable:true},
                    {field: 'dbrlxdhg',title: '代办人联系电话',sortable:true}
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
                                "field": "jdlx",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "jyje",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "dfmc",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "dfzh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "dfkh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
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


        var _event = function () {
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/yhzh/jyjl/yhzh-jyjl-detail.html?id="+$(this).attr("data-id"),'查看信息');
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

        return {
            init:_init
        };
    })();

    zfb.init();
    reg.init();


})(document, window, jQuery);