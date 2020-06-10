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

        var params = {"indexName":"tantanzhinfo","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'susp_name',title: '姓名',sortable:true},
                    {field: 'yhid',title: '用户ID',sortable:true},
                    {field: 'nc',title: '昵称',sortable:true},
                    {field: 'sjh',title: '手机号'},
                    {field: 'zcsj',title: '注册时间'},
                    {field: 'ip',title: '注册IP'},
                    {field: 'zhhyzb',title: '最后活跃坐标'},
                    {field: 'zhhycs',title: '最后活跃城市'},
                    {field: 'zhhysj',title: '最后活跃时间'},
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
                                "field": "yhid",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"2",
                                "groupType":"should",
                                "field": "nc",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "sjh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"4",
                                "groupType":"should",
                                "field": "zhhycs",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"5",
                                "groupType":"should",
                                "field": "susp_name",
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
                                        "<div class='btn btn-info btn-outline btn-xs hyinfo' data-id='"+data[i]["id"]+"'>好友列表</div>&nbsp;"+
                                        "<div class='btn btn-info btn-outline btn-xs ltjlinfo' data-id='"+data[i]["id"]+"'>聊天记录</div>&nbsp;"+
                                    "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"'  data-fileId='"+data[i]["file_id"]+"'>删除</div>"
                                    ;
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
            $("#reginfo").on('click',function () {
                top.contabs.addMenuItem("/view/tantan/tantan-zhinfo.html",'导入探探信息');
            });

            $("#data-table").on('click','.hyinfo',function () {
                top.contabs.addMenuItem("/view/tantan/hyxxinfo/tantan-hyxxinfo-list.html?id="+$(this).attr("data-id"),'好友列表');
            });
            $("#data-table").on('click','.ltjlinfo',function () {
                top.contabs.addMenuItem("/view/tantan/ltjlinfo/tantan-ltjlinfo-list.html?id="+$(this).attr("data-id"),'聊天记录列表');
            });


            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"),$(this).attr("data-fileId"));
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/tantan/tantan-zhinfo-detail.html?id="+$(this).attr("data-id"),'查看信息');
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

        var _delete = function (id,fileId) {

            swalMsg.msg({
                text:"是否删除支付宝信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/zfb/delete",
                            type: "post",
                            dataType: "json",
                            data:{id: id, fileId:fileId},
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

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);
