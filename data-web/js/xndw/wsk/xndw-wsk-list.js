/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var sx = (function () {



        var _init = function init(_data) {
            _initListTable();
            _event();
        };

        var params = {"indexName":"xndw_wsk","conditions":[],"sort":"time desc"};

        var _search;//查询的值

        var _initListTable = function(){
            $('#data-table').bootstrapTable({
                pagination:true,
                pageSize:10,
                height: utils.getWidowHeight()-135,
                pageList: [5, 10, 15, 20, 25],  //记录数可选列表
                queryParamsType:'',
                sidePagination:'server',
                columns: [{field: 'xh',title: '序号',width:'50px'},
                    {field: 'wechat_id',title: '微信号',sortable:true},
                    {field: 'token',title: 'TOKEN',sortable:true},
                    // {field: 'qq',title: 'QQ号码',sortable:true},
                    {field: 'mobilephone',title: '手机号',sortable:true},
                    // {field: 'imei',title: 'IMEI',sortable:true},
                    {field: 'ip',title: 'IP',sortable:true},
                    {field: 'longtude',title: '经度',sortable:true},
                    {field: 'latitude',title: '纬度',sortable:true},
                    {field: 'vaddr',title: '虚拟地址',sortable:true},
                    {field: 'adword',title: '信息内容',sortable:true},
                    {field: 'address',title: '真实地址',sortable:true},
                    {field: 'opt',title: '操作',width:'130px'}
                ],
                ajax : function (request) {
                    var sort = "time desc";
                    if(request.data.sortName){
                        sort = request.data.sortName +" "+request.data.sortOrder;
                    }
                    var con = [];
                    if(_search){
                        con=[
                            {
                                "groupId":"1",
                                "groupType":"should",
                                "field": "real_addr",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"1",
                                "groupType":"should",
                                "field": "phone_num",
                                "values": [_search],
                                "searchType": 2,
                                "dataType":1,
                            },
                            {
                                "groupId":"1",
                                "groupType":"should",
                                "field": "ip",
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
                                    data[i]['opt'] = "<div class='btn btn-info btn-outline btn-xs detail' data-id='"+data[i]["id"]+"'>查看</div>&nbsp;";
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
                top.contabs.addMenuItem("/view/xndw/wsk/xndw-wsk.html",'导入定位信息');
            });
            $("#data-table").on('click','.detail',function () {
                top.contabs.addMenuItem("/view/xndw/wsk/xndw-wsk-detail.html?id="+$(this).attr("data-id"),'查看信息');
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

    sx.init();


})(document, window, jQuery);
