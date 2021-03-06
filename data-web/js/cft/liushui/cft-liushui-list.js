/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var cftInfo;
    var cft = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
        }
        var params = {"indexName":"cftreginfo","conditions":[],"sort":"create_time desc"};

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
                            cftInfo =file[0];
                            $("#susp-name").html(cftInfo["susp_name"]);
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


        var cftid ;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            cftid = params["id"];
            _initListTable();
            _event();
        };

        var params = {"indexName":"cfttrades","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    // {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'zh',title: '账号',sortable:true},
                    {field: 'jdlx',title: '借贷类型',sortable:true},
                    {field: 'jyje',title: '交易金额',sortable:true},
                    {field: 'jyye',title: '交易余额',sortable:true},
                    {field: 'jysj',title: '交易时间',sortable:true},
                    // {field: 'yhlx',title: '交易类型'},
                    // {field: 'jysm',title: '描述'},
                    {field: 'shmc',title: '商户名称',sortable:true},
                    {field: 'fsf',title: '发送方',sortable:true},
                    {field: 'fsje',title: '发送金额',sortable:true},
                    {field: 'jsf',title: '接收方',sortable:true},
                    {field: 'jsje',title: '接收金额',sortable:true},
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
                                "field": "jdlx",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "shmc",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "fsf",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "jsf",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    con[con.length]={
                        "field": "cft_id",
                        "values": [cftInfo.id],
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


        var _event = function () {
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/cft/liushui/cft-liushui-detail.html?id="+$(this).attr("data-id"),'查看流水信息');
            });
            $("#addBtn").on('click',function () {
                top.contabs.addMenuItem("/view/cft/liushui/cft-liushui.html?id="+cftid,'导入财付通流水信息');
            });
            $("#analyze-btn").on('click',function () {
                top.contabs.addMenuItem("/view/cft/analyze/cft-analyze.html?id="+cftid,'财付通流水信息分析');
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

    cft.init();
    reg.init();


})(document, window, jQuery);
