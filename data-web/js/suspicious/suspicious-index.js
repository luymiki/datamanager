/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var tjtb = (function () {

        var _init = function init(_data) {
             _initKyrsjl();
            _event();
        };


        /**
         * 统计可疑人数据量
         * @private
         */
        var _initKyrsjl = function(){
            var params={"indexName":"suspicious","aggs": [{ "groupName":"count_id","field": "type","aggsType": 1} ],"sort":"create_time desc" };
            _aggs(params,function (data) {
                var aggs = data.aggs["count_id"];
                var td = {
                    aggs: aggs
                };
                var html = template('data-template', td);
                $("#data-box").html(html);
            });
        };

        var _aggs = function (params,success) {
            $.ajax.proxy({
                url:"/api/eqa/aggs",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params)},
                async:true,
                success:function (d) {
                    //console.log(d);
                    if(d.status===200){
                        if(d.data.aggs){
                            success(d.data);
                        }
                    }

                },
                error:function (d) {
                    console.log(d);
                }
            });
        };
        var _event=function () {
          $("#data-box").on('click','.kyr-count',function () {
              var title = $(this).attr("data-type");
              top.contabs.addMenuItem("/view/suspicious/suspicious.html?type=" + encodeURI(title), title);
          });
          $("#data-box").on('click','.kyr-add',function () {
              top.contabs.addMenuItem("/view/suspicious/suspicious-add.html", "新增人员");
          });
        };
        return {
            init:_init
        };
    })();

    tjtb.init();


})(document, window, jQuery);
