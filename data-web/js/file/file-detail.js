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
        };

        var params = {"indexName":"attachment","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            $.ajax({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            var f = file[0];
                            var data = {
                                file: f
                            };
                            var html = template('eml-template', data);
                            $("#eml-content").append(html);

                            var $fa = $("#eml-content").find("#file-box");
                            if(/bmp$|jpg$|jpeg$|png$|gif$/.test(f["suffix"])){
                                $('<div class="image"><img alt="image" class="img-responsive" src="/upload/'+f["path"]+'"></div>').appendTo($fa);
                            }else if(/avi$|rmvb$|rm$|asf$|divx$|mpg$|mpeg$|mpe$|wmv$|mp4$|mkv$|vob$|flv$/.test(f["suffix"])){
                                $('<div class="icon"><i class="fa fa-film"></i></div>').appendTo($fa);
                            }else if(/mp3$|aac$|wav$|wma$|cda$|flac$|m4a$|mid$|mka$|mp2$|mpa$|mpc$|ape$|ofr$|ogg$|ra$|wv$|tta$|ac3$|dts$/.test(f["suffix"])){
                                $('<div class="icon"><i class="fa fa-music"></i></div>').appendTo($fa);
                            }else if(/xls$|xlsx$/.test(f["suffix"])){
                                $('<div class="icon"><i class="fa fa-bar-chart-o"></i></div>').appendTo($fa);
                            }else{
                                $('<div class="icon"><i class="fa fa-file"></i></div>').appendTo($fa);
                            }

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