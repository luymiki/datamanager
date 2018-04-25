/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {



        var _init = function init(_data) {
            var params = utils.getURLParams();
            var id = params["id"];
            var ip = params["ip"];
            _initListTable(id,ip);
            _event();
        };

        var params = {"indexName":"qqloginip_list","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(id,ip){
            $('#data-table').bootstrapTable({
                pagination:true,
                pageSize:10,
                height: utils.getWidowHeight()-75,
                pageList: [5, 10, 15, 20, 25],  //记录数可选列表
                queryParamsType:'',
                sidePagination:'server',
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'susp_name',title: '姓名',sortable:true},
                    {field: 'qq',title: 'QQ号',sortable:true},
                    {field: 'ip',title: '登录IP',sortable:true},
                    {field: 'login_time',title: '登录时间',sortable:true},
                    {field: 'logout_time',title: '下线时间'},
                    {field: 'login_type',title: '登录方式'},
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
                                "field": "susp_name",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"1",
                                "groupType":"should",
                                "field": "qq",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    con[con.length]={
                            "field": "login_id",
                            "values": [id],
                            "searchType": 1,
                            "dataType":2,
                        };
                    con[con.length]={
                            "field": "ip",
                            "values": [ip],
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
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;" +
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"' data-fileId='"+data[i]["file_id"]+"' >删除</div>";
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

    reg.init();


})(document, window, jQuery);
