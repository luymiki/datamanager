/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var suspid;
        var type;
        var code;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            suspid = params["suspid"];
            type = params["type"];
            code = params["code"];
            _initListTable();
            _event();
        };

        var params = {"indexName":"qqreginfo","conditions":[],"sort":"create_time desc"};

        var _search;//查询的值
        var _initListTable = function(){
            $('#data-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'id',title: 'ID',visible:false},
                    {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                    {field: 'qq',title: 'QQ号',sortable:true},
                    {field: 'name',title: '昵称',sortable:true},
                    {field: 'xm',title: '真实姓名'},
                    {field: 'xb',title: '性别'},
                    {field: 'csrq',title: '生日'},
                    {field: 'zcsj',title: '注册时间'},
                    {field: 'gj',title: '国家'},
                    {field: 'sf',title: '省份'},
                    {field: 'cs',title: '城市'},
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
                                "groupId":"2",
                                "groupType":"should",
                                "field": "qq",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"3",
                                "groupType":"should",
                                "field": "name",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            }
                        ];
                    }
                    if(suspid && type && code){
                        con[con.length]={
                            "field": "susp_id",
                            "values": [suspid],
                            "searchType": 1,
                            "dataType":1,
                        };
                        if("qq" === type || "weixin" === type){
                            con[con.length]={
                                "field": "qq",
                                "values": [code],
                                "searchType": 1,
                                "dataType":1,
                            };
                        }else if("dh" === type){
                            con[con.length]={
                                "field": "dh",
                                "values": [code],
                                "searchType": 1,
                                "dataType":1,
                            };
                        }else if("email" === type){
                            con[con.length]={
                                "field": "email",
                                "values": [code],
                                "searchType": 1,
                                "dataType":1,
                            };
                        }

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
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"'  data-fileId='"+data[i]["file_id"]+"'>删除</div>";
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
                top.contabs.addMenuItem("/view/qq/reg/qq-reg.html",'导入注册信息');
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/qq/reg/qq-reg-detail.html?id="+$(this).attr("data-id"),'查看信息');
            });
            $("#data-table").on('click','.delete',function () {
                _delete($(this).attr("data-id"),$(this).attr("data-fileId"));
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
                text:"是否删除注册信息？",
                type:"warning",
                showCancel:true,
                confirm:function (f) {
                    if(f) {

                        $.ajax.proxy({
                            url: "/api/admin/qq/reg/delete",
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


        }

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);
