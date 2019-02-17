/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var suspicious = (function () {
        var meta;
        var _search;
        var type_ ;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            type_ = params["type"];
            _initListTable();
            _event();
        };

        var params = {"indexName": "suspicious", "conditions": [], "sort": "modify_time desc"};

        var _initListTable = function () {
            var kyr = {
                "field": "type",
                "values": [type_],
                "searchType": 1,
                "dataType": 2,
            };
            $('#suspicious-table').myTable({
                // copyRow:$("#copyRow"),
                exportXls: $("#exportXls"),
                exportXlsFun: function () {
                    params["conditions"] = [kyr];
                    var from = $('<form method="post" action="/api/eqa/exportEXcel" target="_blank"></form>').appendTo('body');
                    $('<input type="text" name="paramsStr">').val(JSON.stringify(params)).appendTo(from);
                    from.submit().remove();
                },
                comment: $("#pizhu"),
                columns: [
                    {field: 'checkbox', title: '选择', width: '50px', checkbox: true},
                    {field: 'xh', title: '序号', width: '50px'},
                    {field: 'id', title: 'ID', visible: false},
                    {field: 'type', title: '类型', width: '100px'},
                    {field: 'name', title: '姓名', formatter: formatterName},
                    {field: 'gmsfzh', title: '身份证号', sortable: true},
                    {field: 'qkjj', title: '情况简介', class: 'qkjj-cell', formatter: formatterStr},
                    {field: 'qq', title: 'QQ', formatter: formatterList},
                    {field: 'weixin', title: '微信', formatter: formatterList},
                    {field: 'phone', title: '手机号', formatter: formatterList},
                    {field: 'imei', title: 'IMEI', formatter: formatterList},
                    {field: 'imsi', title: 'IMSI', formatter: formatterList},
                    {field: 'cft', title: '财付通', formatter: formatterList},
                    {field: 'zfb', title: '支付宝', formatter: formatterList},
                    {field: 'yhzh', title: '银行账号', formatter: formatterList},
                    {field: 'ip', title: 'IP', formatter: formatterList},
                    {field: 'email', title: '电子邮箱', formatter: formatterList},
                    {field: 'other', title: '其他码值', formatter: formatterList},
                    {field: 'gzjd', title: '工作进度', sortable: true},
                    {field: 'opt', title: '操作', width: '110px'}
                ],
                ajax: function (request) {
                    var sort = "modify_time desc";
                    if (request.data.sortName) {
                        sort = request.data.sortName + " " + request.data.sortOrder;
                    }
                    var con = [];
                    if (_search) {
                        con = [
                            {
                                "groupId": "1",
                                "groupType": "should",
                                "field": "name_na",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "1-1",
                                "groupType": "should",
                                "field": "gmsfzh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "1-2",
                                "groupType": "should",
                                "field": "qkjj",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "2",
                                "groupType": "should",
                                "field": "qq",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "3",
                                "groupType": "should",
                                "field": "weixin",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "4",
                                "groupType": "should",
                                "field": "phone",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "5",
                                "groupType": "should",
                                "field": "imei",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "6",
                                "groupType": "should",
                                "field": "imsi",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "7",
                                "groupType": "should",
                                "field": "cft",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "8",
                                "groupType": "should",
                                "field": "zfb",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "9",
                                "groupType": "should",
                                "field": "yhzh",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "10",
                                "groupType": "should",
                                "field": "ip",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            },
                            {
                                "groupId": "11",
                                "groupType": "should",
                                "field": "email",
                                "values": [_search],
                                "searchType": 2,
                                "dataType": 1,
                            }
                        ];
                    }
                    con[con.length] = kyr;
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
                                console.log(msg)
                                var data = msg.data.data;
                                var xh = ((request.data.pageNumber - 1) * request.data.pageSize) + 1;
                                for (var i = 0; i < data.length; i++) {
                                    data[i]['xh'] = xh++;
                                    data[i]['opt'] =
                                        "<div class='btn btn-primary btn-outline btn-xs update' data-id='" + data[i]["id"] + "'>修改</div><br>" +
                                        "<div class='btn btn-info btn-outline btn-xs tiqu' data-id='" + data[i]["id"] + "'>提取</div><br>" +
                                        "<div class='btn btn-info btn-outline btn-xs neo4j' data-id='" + data[i]["id"] + "'>上图</div><br>" +
                                        "<div class='btn btn-danger btn-outline btn-xs delete' data-id='" + data[i]["id"] + "'>删除</div>";
                                }
                                request.success({
                                    rows: data,
                                    total: msg.data.total
                                });
                                meta = msg.data.meta["suspicious"];
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

                },
                onDblClickCell: function (field, value, row, $element) {
                    //console.log(row);
                    var val = row[field];
                    if (val instanceof Array) {
                        var s = "";
                        for (var i = 0; i < val.length; i++) {
                            var d = val[i];
                            s += formatter(field, row["id"], d);
                        }
                        //val =  val.join("&emsp;");
                        val = s;
                    }
                    if (val !== "") {
                        layer.open({
                            // time: 2000, //不自动关闭
                            type: 1,
                            skin: 'layui-layer-rim', //加上边框
                            area: ['450px', '340px'], //宽高
                            content: "<div style='padding: 5px;'>" + val + "</div>"
                        });
                    }
                }
            });

        };


        var _event = function () {
            $("#search-btn").on('click', function () {
                _search = $("#search-input").val();
                if (_search && $.trim(_search) !== "") {
                    $('#suspicious-table').bootstrapTable("refresh");
                } else {
                    _search = null;
                    $('#suspicious-table').bootstrapTable("refresh");
                }
            });

            $("#suspicious-table").on('click', '.update', function () {
                top.contabs.addMenuItem("/view/suspicious/suspicious-add.html?id="+$(this).attr("data-id"), '人员信息');
            });
            $("#suspicious-table").on('click', '.delete', function () {
                _delete($(this).attr("data-id"));
            });
            $("#suspicious-table").on('click', '.tiqu', function () {
                _tiqu($(this).attr("data-id"));
            });
            $("#suspicious-table").on('click', '.neo4j', function () {
                _neo4j($(this).attr("data-id"));
            });

            //列表数据点击链接
            $("body").on('click', '.btn-name', function () {
                var id = $(this).attr("data-id");
                var name = $(this).attr("data-name");
                top.contabs.addMenuItem("/view/suspicious/suspicious-detail.html?id=" + id, name);
            });
            $("body").on('click', '.data-qq', function () {
                var qq = $(this).attr("data-qq");
                _addItem($(this).attr("data-id"), qq, "qq", 'QQ[' + qq + ']信息列表');
            });
            $("body").on('click', '.data-weixin', function () {
                var weixin = $(this).attr("data-weixin");
                _addItem($(this).attr("data-id"), weixin, "weixin", '微信[' + weixin + ']信息列表');
            });
            $("body").on('click', '.data-dh', function () {
                var dh = $(this).attr("data-dh");
                _addItem($(this).attr("data-id"), dh, "dh", '手机号[' + dh + ']信息列表');
            });
            $("body").on('click', '.data-cft', function () {
                var cft = $(this).attr("data-cft");
                _addItem($(this).attr("data-id"), cft, "cft", '财付通[' + cft + ']信息列表');
            });
            $("body").on('click', '.data-yhzh', function () {
                var yhzh = $(this).attr("data-yhzh");
                _addItem($(this).attr("data-id"), yhzh, "yhzh", '银行账号[' + yhzh + ']信息列表');
            });
            $("body").on('click', '.data-email', function () {
                var email = $(this).attr("data-email");
                _addItem($(this).attr("data-id"), email, "email", '电子邮件[' + email + ']信息列表');
            });
            $("body").on('click', '.data-ip', function () {
                var ip = $(this).attr("data-ip");
                _addItem($(this).attr("data-id"), ip, "ip", 'IP[' + ip + ']信息列表');
            });


        };
        var _addItem = function (id, val, type, title) {
            top.contabs.addMenuItem("/view/suspicious/suspicious-page.html?id=" + id + "&type=" + type + "&code=" + val, title);
        };

        var formatter = function (field, id, val) {
            switch (field) {
                case "qq":
                    return _formatter(id, val, 'data-qq');
                case "weixin":
                    return _formatter(id, val, 'data-weixin');
                case "phone":
                    return _formatter(id, val, 'data-dh');
                case "cft":
                    return _formatter(id, val, 'data-cft');
                case "yhzh":
                    return _formatter(id, val, 'data-yhzh');
                case "email":
                    return _formatter(id, val, 'data-email');
                case "ip":
                    return _formatter(id, val, 'data-ip');
                default:
                    return val + " ";
            }
        }
        var _formatter = function (id, val, clazz) {
            return "<a class='" + clazz + "' data-id='" + id + "' " + clazz + "='" + val + "' >" + val + "</a> ";
        }

        var formatterList = function (d, item, idx, field) {
            if (d) {
                var s = "";
                for (var i = 0; i < d.length && i < 5; i++) {
                    s += formatter(field, item["id"], d[i]);
                }
                if (d.length > 5) {
                    s += "...";
                }
                // for(var i=0 ; i< d.length;i++){
                //     s+= d[i]+" ";
                // }
                return s;
            }
            return d;
        };
        var formatterStr = function (d) {
            if (d) {
                var s = d;
                if (d.length > 50) {
                    s = d.substring(0, 50) + "...";
                }
                return "<div class='qkjj-cell'>" + s + "</div>";
            }
            return d;
        };

        var formatterName = function (d,item) {
            return "<a class='btn-name' data-id='" + item["id"] + "' data-name='" + item["name"] + "' >" + d + "</a> ";
        };

        var _delete = function (id) {

            swalMsg.msg({
                text: "是否删除可疑人员信息？",
                type: "warning",
                showCancel: true,
                confirm: function (f) {
                    if (f) {

                        $.ajax.proxy({
                            url: "/api/admin/suspicious/delete",
                            type: "post",
                            dataType: "json",
                            data: {"id": id},
                            success: function (d) {
                                console.log(d);
                                if (d.status === 200) {
                                    toastrMsg.success("删除成功");
                                    $('#suspicious-table').bootstrapTable("refresh");
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

        var _tiqu = function (id) {
            $.ajax.proxy({
                url: "/api/admin/suspicious/analyze",
                type: "post",
                dataType: "json",
                data: {"id": id},
                async: true,
                success: function (d) {

                    if (d.status === 200) {
                        toastrMsg.success("提取完成");
                        $('#suspicious-table').bootstrapTable("refresh");
                    }
                    else {
                        console.log(d);
                        toastrMsg.error("提取失败");
                    }

                },
                error: function (d) {
                    console.log(d);
                    top.toastrMsg.error("提取失败");
                }
            });
        };
        /**
         * 数据上neo4j图库
         * @param id
         * @private
         */
        var _neo4j = function (id) {
            toastrMsg.success("信息上图中，请稍后。。。");
            $.ajax.proxy({
                url: "/api/admin/neo4j/susp2neo4j",
                type: "post",
                dataType: "json",
                data: {"id": id},
                async: true,
                success: function (d) {
                    if (d.status === 200) {
                        toastrMsg.success("上图完成");
                    }
                    else {
                        console.log(d);
                        toastrMsg.error("上图失败");
                    }
                },
                error: function (d) {
                    console.log(d);
                    top.toastrMsg.error("上图失败");
                }
            });
        }

        return {
            init: _init
        };
    })();

    suspicious.init();


})(document, window, jQuery);