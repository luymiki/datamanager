/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var yhzhInfo;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
        };

        var params = {"indexName":"yhzh_khxx","conditions":[],"sort":"create_time desc"};

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
                async:false,
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            yhzhInfo =file[0];
                            var data = {
                                file: yhzhInfo
                            };
                            var html = template('data-template', data);
                            $("#data-content").append(html);
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