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
                            $("#susp-name").html(zfbInfo["name"]+"&emsp;"+zfbInfo["user_id"]);
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

        var params = {"indexName":"zfbzhinfo","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
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
                    // con[con.length]={
                    //     "field": "user_id",
                    //     "values": [zfbInfo["user_id"]],
                    //     "searchType": 1,
                    //     "dataType":2,
                    // };
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


        var _event = function () {
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-detail.html?id="+$(this).attr("data-id"),'查看信息');
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