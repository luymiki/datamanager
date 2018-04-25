/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var huaduanInfo;
    var huaduan = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
        }
        var params = {"indexName":"huaduan","conditions":[],"sort":"create_time desc"};

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
                            huaduanInfo =file[0];
                            $("#susp-name").html(huaduanInfo["susp_name"]);
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

        var params = {"indexName":"huaduan_list","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').bootstrapTable({
                pagination:true,
                pageSize:20,
                height: utils.getWidowHeight()-135,
                pageList: [10, 20, 50],  //记录数可选列表
                queryParamsType:'',
                sidePagination:'server',
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
                                "groupId":"1",
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
                        "values": [huaduanInfo.id],
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
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/cft/liushui/cft-liushui-detail.html?id="+$(this).attr("data-id"),'查看流水信息');
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

    huaduan.init();
    reg.init();


})(document, window, jQuery);
