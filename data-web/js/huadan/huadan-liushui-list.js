/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {

        var id ;

        var _init = function init(_data) {
            var params = utils.getURLParams();
            id = params["id"];
            _initListTable();
            _event();
        };

        var params = {"indexName":"huaduan_list","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'yys',title: '运营商',sortable:true},
                    {field: 'zjhm',title: '手机号码',sortable:true},
                    {field: 'ddhm',title: '对端号码',sortable:true},
                    {field: 'thlx',title: '通话类型'},
                    {field: 'thdd',title: '通话地点'},
                    {field: 'ddhmgsd',title: '对端归属地'},
                    {field: 'kssj',title: '开始时间'},
                    {field: 'jssj',title: '结束时间',sortable:true},
                    {field: 'thsc',title: '通话时长',sortable:true},
                    {field: 'hjlx',title: '呼叫类型',sortable:true},
                    {field: 'xqh',title: '小区号'}
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
                                "field": "ddhm",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "hjlx",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    con[con.length]={
                        "field": "hd_id",
                        "values": [id],
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
            $("#analyze-btn").on('click',function () {
                top.contabs.addMenuItem("/view/huadan/huadan-liushui-list-analyze.html?id="+id,'话单流水信息分析');
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

    reg.init();


})(document, window, jQuery);
