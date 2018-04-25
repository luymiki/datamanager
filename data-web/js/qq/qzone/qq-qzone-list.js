/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {



        var _init = function init(_data) {
            _initListTable();
            _event();
        };

        var params = {"indexName":"qqzone","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
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
                    {field: 'pic',title: '相片张数',sortable:true},
                    {field: 'opt',title: '操作',width:'130px'}
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
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"' >删除</div>";
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
            $("#addBtn").on('click',function () {
                top.contabs.addMenuItem("/view/qq/qzone/qq-qzone.html",'导入空间照片');
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/qq/qzone/qq-qzone-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"));
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

        var _delete = function (id) {

            swalMsg.msg({
                text:"是否删除空间信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/qq/qzone/delete",
                            type: "post",
                            dataType: "json",
                            data:{id: id,},
                            success: function (d) {
                                console.log(d);
                                if (d.status === 200) {
                                    toastrMsg.success("删除成功");
                                    $('#data-table').bootstrapTable("refresh");
                                } else {
                                    toastrMsg.error("删除失败");
                                }
                            },
                            error: function () {
                                toastrMsg.error("删除失败");
                            }
                        });
                    }
                }
            });


        }

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);
