/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var id;

        var _init = function init(_data) {
            var params = utils.getURLParams();
            id = params["id"];
            _get(id);
            _btnEvent();
        };

        var params = {"indexName":"qqloginip","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":2
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
                            var data = {
                                file: file[0]
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

        var _btnEvent = function () {
            $("#eml-content").on("click",".ip-item",function () {
                top.contabs.addMenuItem("/view/qq/loginip/qq-loginiplist-list.html?id="+$(this).attr("data-id")+"&ip="+$(this).html(),'IP列表信息');
                return false;
            });
        };

        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);