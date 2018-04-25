/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {

        var _init = function init(_data) {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
            $("#eml-content").on('click','#logininfo',function () {
                top.contabs.addMenuItem("/view/zfb/logininfo/zfb-logininfo-list.html?id="+$(this).attr("data-id"),'登录日志列表');
            });
            $("#eml-content").on('click','#zhinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zhinfo/zfb-zhinfo-list.html?id="+$(this).attr("data-id"),'账户明细列表');
            });
            $("#eml-content").on('click','#txinfo',function () {
                top.contabs.addMenuItem("/view/zfb/txinfo/zfb-txinfo-list.html?id="+$(this).attr("data-id"),'提现记录列表');
            });
            $("#eml-content").on('click','#zzinfo',function () {
                top.contabs.addMenuItem("/view/zfb/zzinfo/zfb-zzinfo-list.html?id="+$(this).attr("data-id"),'转账明细列表');
            });
            $("#eml-content").on('click','#jyjl',function () {
                top.contabs.addMenuItem("/view/zfb/jyjl/zfb-jyjl-list.html?id="+$(this).attr("data-id"),'交易记录列表');
            });
        };

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
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            file =file[0];
                            var data = {
                                file: file
                            };
                            var html = template('eml-template', data);
                            $("#eml-content").append(html);
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

    reg.init();


})(document, window, jQuery);