/**
 * Created by hc.zeng on 2018/3/21.
 */

(function (e, t, $) {
    "use strict";
    var zfbInfo;
    var zfb = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
        }
        var params = {"indexName":"tantanzhinfo","conditions":[],"sort":"create_time desc"};

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
                            $("#susp-name").html(zfbInfo["nc"]+"&emsp;"+zfbInfo["yhid"]);
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

        var params = {"indexName":"tantanltjlinfo","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'fsid',title: '发送用户ID',sortable:true},
                    {field: 'jsid',title: '接收用户ID',sortable:true},
                    {field: 'fssj',title: '发送时间',sortable:true},
                    {field: 'nr',title: '内容',width:'300px'}
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
                                "field": "fsid",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "jsid",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "nr",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    con[con.length]={
                        "field": "zhid",
                        "values": [zfbInfo["id"]],
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