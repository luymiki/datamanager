/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var email = (function () {

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

        var params = {"indexName": "email_reg", "conditions": [], "sort": "create_time desc"};
        // var isDelete = {
        //     "field": "is_delete",
        //     "values": ["1"],
        //     "searchType": 3,
        //     "dataType": 2
        // };

        var _search;//查询的值

        var _initListTable = function () {
            $('#email-table').myTable({
                columns: [
                    {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                    {field: 'xh',title: '序号',width:'50px'},
                    {field: 'id',title: 'ID',visible:false},
                    {field: 'susp_name', title: '姓名', sortable: true, width: '100px'},
                    {field: 'to', title: '接收人', sortable: true},
                    {field: 'to_address', title: '接收地址', sortable: true},
                    {field: 'from', title: '发送人'},
                    {field: 'from_address', title: '发送地址'},
                    {field: 'received_date', title: '接收时间'},
                    {field: 'subject', title: '邮件主题'},
                    {field: 'create_time', title: '创建时间'},
                    {field: 'opt', title: '操作', width: '130px'}
                ],
                ajax: function (request) {
                    var sort = "create_time desc";
                    if (request.data.sortName) {
                        sort = request.data.sortName + " " + request.data.sortOrder;
                    }
                    var con = [];
                    if (_search) {
                        con = [
                            {
                                "field": "susp_name",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            }
                            // ,
                            // {
                            //     "field": "subject",
                            //     "values": [_search],
                            //     "searchType": 2,
                            //     "dataType":1,
                            // },
                            // {
                            //     "field": "to",
                            //     "values": [_search],
                            //     "searchType": 2,
                            //     "dataType":1,
                            // },
                            // {
                            //     "field": "from",
                            //     "values": [_search],
                            //     "searchType": 2,
                            //     "dataType":1,
                            // },
                            // {
                            //     "field": "from_address",
                            //     "values": [_search],
                            //     "searchType": 2,
                            //     "dataType":1,
                            // },
                            // {
                            //     "field": "to_address",
                            //     "values": [_search],
                            //     "searchType": 2,
                            //     "dataType":1,
                            // }
                        ];
                    }
                    //con[con.length] = isDelete;
                    if(suspid && type && code){
                        con[con.length]={
                            "field": "susp_id",
                            "values": [suspid],
                            "searchType": 1,
                            "dataType":1,
                        };
                        if("qq" === type){
                            con[con.length]={
                                "field": "to_address",
                                "values": [code],
                                "searchType": 2,
                                "dataType":1,
                            };
                        }else if("email" === type){
                            con[con.length]={
                                "field": "to_address",
                                "values": [code],
                                "searchType": 1,
                                "dataType":1,
                            };
                        }


                    }
                    params["sort"] = sort;
                    params["conditions"] = con;
                    $.ajax.proxy({
                        url: "/api/eqa/query",
                        type: "post",
                        dataType: "json",
                        data: {
                            "pageNum": request.data.pageNumber,
                            "pageSize": request.data.pageSize,
                            "paramsStr": JSON.stringify(params)
                        },
                        success: function (msg) {
                            if (msg.status === 200) {
                                var data = msg.data.data;
                                var xh = ((request.data.pageNumber - 1) * request.data.pageSize) + 1;
                                for (var i = 0; i < data.length; i++) {
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] = "<div class='btn btn-primary btn-outline btn-xs detail' data-id='" + data[i]["id"] + "'>查看</div>&nbsp;" +
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='" + data[i]["id"] + "'  data-fileId='" + data[i]["file_id"] + "'>删除</div>";
                                }
                                request.success({
                                    rows: data,
                                    total: msg.data.total
                                });
                            } else {
                                request.success({
                                    rows: [],
                                    total: 0
                                });
                            }
                        },
                        error: function () {
                            toastrMsg.error("错误！");
                        }
                    });
                }
            });
        };


        var _event = function () {
            $("#addBtn").on('click', function () {
                top.contabs.addMenuItem("/view/file/email/file-email.html", '导入邮件');
            });

            // /**
            //  * 批注
            //  */
            // $("#pizhu").on('click',function () {
            //     $("#email-table").myTable("comment");
            // });

            $("#email-table").on('click', '.detail', function () {
                top.contabs.addMenuItem("/view/file/email/file-email-detail.html?id=" + $(this).attr("data-id"), '查看邮件');
            });
            $("#email-table").on('click', '.delete', function () {
                _delete($(this).attr("data-id"), $(this).attr("data-fileId"));
            });
            $("#search-btn").on('click', function () {
                _search = $("#search-input").val();
                if (_search && $.trim(_search) !== "") {
                    $('#email-table').bootstrapTable("refresh");
                } else {
                    _search = null;
                    $('#email-table').bootstrapTable("refresh");
                }
            });
        };

        var _delete = function (id, fileId) {

            swalMsg.msg({
                text: "是否删除邮件信息？",
                type: "warning",
                showCancel: true,
                confirm: function (f) {
                    if (f) {

                        $.ajax.proxy({
                            url: "/api/admin/email/delete",
                            type: "post",
                            dataType: "json",
                            data: {id: id, fileId: fileId},
                            success: function (d) {
                                console.log(d);
                                if (d.status === 200) {
                                    toastrMsg.success("删除成功");
                                    $('#email-table').bootstrapTable("refresh");
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
            init: _init
        };
    })();

    email.init();


})(document, window, jQuery);

function setIframeHeight(iframe) {
    if (iframe) {
        var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
        if (iframeWin.document.body) {
            iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
        }
    }
}