/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var suspicious = (function () {
        var type;
        var id;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            type = params["type"];
            id = params["id"];
            _validator();
            _event();
            if(id){
                _get(id);
            }

        };
        var _event = function () {
            $(".import-btn").hide();
            $("#addBtn").on('click', function () {
                $("#signupForm").find("input").each(function (i, o) {
                    $(o).val("");
                });
                $("#signupForm").find("textarea").each(function (i, o) {
                    $(o).val("");
                });
                $('#addModal').modal("show");
            });

            $("#suspicious-table").on('click', '.update', function () {
                $(".import-btn").show();
                _get($(this).attr("data-id"));
            });


            //修改框各项导入按钮跳转
            $('#addModal').on('click', '.btn-qq', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, 'QQ注册信息导入', "/view/qq/reg/qq-reg.html");
            });
            $('#addModal').on('click', '.btn-qq-login', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, 'QQ登录IP导入', "/view/qq/loginip/qq-loginip.html");
            });
            $('#addModal').on('click', '.btn-qq-qzone', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, 'QQ空间照片导入', "/view/qq/qzone/qq-qzone.html");
            });
            $('#addModal').on('click', '.btn-weixin', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '微信注册信息导入', "/view/weixin/weixin-reg.html");
            });
            $('#addModal').on('click', '.btn-cft', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '财付通信息导入', "/view/cft/cft-reg.html");
            });

            $('#addModal').on('click', '.btn-zfb', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝注册信息导入', "/view/zfb/zfb-reg.html");
            });
            $('#addModal').on('click', '.btn-zfb-login', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝登陆日志导入', "/view/zfb/logininfo/zfb-logininfo.html");
            });
            $('#addModal').on('click', '.btn-zfb-zhmx', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝账户明细导入', "/view/zfb/zhinfo/zfb-zhinfo.html");
            });
            $('#addModal').on('click', '.btn-zfb-txjl', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝提现记录导入', "/view/zfb/txinfo/zfb-txinfo.html");
            });
            $('#addModal').on('click', '.btn-zfb-zzmx', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝转账明细导入', "/view/zfb/zzinfo/zfb-zzinfo.html");
            });
            $('#addModal').on('click', '.btn-zfb-jyjl', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '支付宝交易记录导入', "/view/zfb/jyjl/zfb-jyjl.html");
            });

            $('#addModal').on('click', '.btn-huadan', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '话单信息导入', "/view/huadan/huadan.html");
            });
            $('#addModal').on('click', '.btn-email', function () {
                var id = $('#addModal').find("#id").val();
                _addImportItem(id, '电子邮件导入', "/view/file/email/file-email.html");
            });

            //数据变动验证
            $('#addModal').on('change', '.data-value', function () {
                changeVal($(this));
            });
        };
        var _addImportItem = function (id, title, url) {
            top.contabs.addMenuItem(url + "?suspid=" + id, title);
        };
        /**
         * 校验新输入的值是否存在于其他可疑人员中
         * @param type
         * @param allval
         * @param val
         */
        var changeVal = function (obj) {
            var old = "";
            var id = $("#id").val();
            var type = obj.attr("data-type");
            var dataValue = obj.attr("data-value").split(/\s+|,|、|，/);
            var nv = obj.val().split(/\s+|,|、|，/);
            if (dataValue.length > nv.length) {
                //删除了内容，不比较
                return false;
            }
            var targert = difference(nv, dataValue);
            targert = targert.notempty();
            if (targert.length === 0) {
                return false;
            }
            var params = {
                "indexName": "suspicious", "conditions": [
                    {
                        "field": type,
                        "searchType": "1",
                        "dataType": "2",
                        "values": targert,
                        "groupType": "must"
                    }

                ], "sort": "modify_time desc"
            };
            if (id && id !== "") {
                params["conditions"][params["conditions"].length] = {
                    "field": "id",
                    "searchType": "1",
                    "dataType": "2",
                    "values": [id],
                    "groupType": "not"
                };
            }
            $.ajax.proxy({
                url: "/api/eqa/query",
                type: "post",
                dataType: "json",
                data: {"pageNum": 1, "pageSize": 10, "paramsStr": JSON.stringify(params)},
                success: function (msg) {
                    if (msg.status === 200) {
                        console.log(msg.data);
                        var data = msg.data.data;
                        var errormsg = "";
                        if (data.length > 0) {
                            for (var i = 0; i < data.length; i++) {
                                var d = data[i];
                                var ds = d[type];
                                if (ds instanceof Array) {
                                    targert.forEach(function (val1, i) {
                                        if (ds.indexOf(val1) >= 0) {
                                            errormsg += '<label class="error ' + type + '-error" for="name"><i class="fa fa-times-circle"></i> ' + val1 + "存在于可疑人“" + d["name"] + '”中</label>&emsp;';
                                        }
                                    });

                                } else {
                                    errormsg += '<label class="error ' + type + '-error" for="name"><i class="fa fa-times-circle"></i> ' + ds + "存在于可疑人“" + d["name"] + '”中</label>&emsp;';
                                }

                            }
                            obj.parent().append(errormsg);
                        } else {
                            obj.attr("data-value", nv);
                        }


                    }
                },
                error: function () {
                    toastrMsg.error("错误！");
                }
            });
        };
        /**
         * 求集合arr1与arr2的差集
         * @param arr1
         * @param arr2
         */
        var difference = function (arr1, arr2) {
            var diff = [];
            arr1.forEach(function (val1, i) {
                if (arr2.indexOf(val1) < 0) {
                    diff.push(val1);
                }
            });
            console.log(diff);
            return diff;
        }

        var validator;
        var _validator = function () {
            var icon = "<i class='fa fa-times-circle'></i> ";
            $("#submit").click(function () {
                $("#signupForm").submit();
            });
            validator = $("#signupForm").validate({
                rules: {
                    name: {
                        required: true,
                        minlength: 2,
                        maxlength: 10
                    },
                    gmsfzh: {
                        required: true,
                        minlength: 6,
                        maxlength: 18
                    },
                    type: {
                        required: true,
                        minlength: 1,
                        maxlength: 18
                    },
                    gzjd: {
                        required: true,
                        minlength: 2
                    }
                },
                messages: {
                    name: {
                        required: icon + "请输入可疑人员姓名",
                        minlength: icon + "姓名必须2个字符以上",
                        maxlength: icon + "姓名必须10个字符以内"
                    },
                    gmsfzh: {
                        required: icon + "请输入证件号码",
                        minlength: icon + "证件号码必须5个字符以上",
                        maxlength: icon + "姓名必须18个字符以内"
                    },
                    type: {
                        required: icon + "请输入人员类型",
                        minlength: icon + "人员类型必须1个字符以上",
                        maxlength: icon + "人员类型必须18个字符以内"
                    },
                    gzjd: {
                        required: icon + "请输入工作进度",
                        minlength: icon + "工作进度必须2个字符以上"
                    }
                },
                submitHandler: function (form) {
                    var data = {};
                    $(form).find("input").each(function (i, o) {
                        var _name = $(o).attr("name");
                        var _value = $(o).val();
                        data[_name] = _value;
                    });
                    $(form).find("textarea").each(function (i, o) {
                        var _name = $(o).attr("name");
                        var _value = $(o).val();
                        data[_name] = _value;
                    });
                    _save(data);
                }
            });
        };

        var _save = function (data) {
            $.ajax.proxy({
                url: "/api/admin/suspicious/save",
                type: "post",
                dataType: "json",
                data: data,
                async: true,
                success: function (d) {
                    if (d.status === 200) {
                        if (data["id"]) {
                            toastrMsg.success("修改成功");
                        } else {
                            toastrMsg.success("保存成功");
                        }
                        top.contabs.closeTab();
                    }
                    else {
                        console.log(d);
                        toastrMsg.error("保存失败");
                    }
                },
                error: function (d) {
                    console.log(d);
                    toastrMsg.error("保存失败");
                }
            });
        };

        var _getParams = {"indexName": "suspicious", "conditions": [], "sort": "create_time desc"};
        var _get = function (id) {
            _getParams["conditions"] = [{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType": 2,
            }];
            $.ajax.proxy({
                url: "/api/eqa/query",
                type: "post",
                dataType: "json",
                data: {"pageNum": 1, "pageSize": 1, "paramsStr": JSON.stringify(_getParams)},
                success: function (d) {
                    console.log(d);
                    if (d.status === 200) {
                        var data = d.data.data;
                        if (data && data.length === 1) {
                            var susp = data[0];
                            for (var key in susp) {
                                var value = susp[key];
                                $("#" + key).val(value).attr("data-value", value);
                            }
                        }
                    } else {
                        toastrMsg.error("查询失败");
                    }
                },
                error: function () {
                    toastrMsg.error("查询失败");
                }
            });
        };
        return {
            init:_init
        };
    })();

    suspicious.init();


})(document, window, jQuery);