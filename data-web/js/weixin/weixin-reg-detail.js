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

        var params = {"indexName":"wxreginfo","conditions":[],"sort":"create_time desc"};
        var params_lxr = {"indexName":"wxlxr","conditions":[],"sort":"create_time desc"};
        var params_qun = {"indexName":"wxqun","conditions":[],"sort":"create_time desc"};
        var params_ip = {"indexName":"wxloginip","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            var infoId = [{
                "field": "info_id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            params_lxr["conditions"]=infoId;
            params_qun["conditions"]=infoId;
            params_ip["conditions"]=infoId;
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
                            file =file[0];
                            //查询联系人
                            $.ajax({
                                url:"/api/eqa/query",
                                type:"post",
                                dataType:"json",
                                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params_lxr)},
                                async:false,
                                success : function (d) {
                                    console.log(d);
                                    if(d.status===200){
                                        var ff = d.data.data;
                                        if(ff && ff.length>=0){
                                            file["wxlxrList"]=ff;
                                        }
                                    }else {
                                        toastrMsg.error("查询失败");
                                    }
                                },
                                error:function(){
                                    toastrMsg.error("查询失败");
                                }
                            });
                            //查询群
                            $.ajax({
                                url:"/api/eqa/query",
                                type:"post",
                                dataType:"json",
                                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params_qun)},
                                async:false,
                                success : function (d) {
                                    console.log(d);
                                    if(d.status===200){
                                        var ff = d.data.data;
                                        if(ff && ff.length>=0){
                                            file["wxqunList"]=ff;
                                        }
                                    }else {
                                        toastrMsg.error("查询失败");
                                    }
                                },
                                error:function(){
                                    toastrMsg.error("查询失败");
                                }
                            });
                            //查询ip
                            $.ajax({
                                url:"/api/eqa/query",
                                type:"post",
                                dataType:"json",
                                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params_ip)},
                                async:false,
                                success : function (d) {
                                    console.log(d);
                                    if(d.status===200){
                                        var ff = d.data.data;
                                        if(ff && ff.length>=0){
                                            file["wxloginipList"]=ff;
                                        }
                                    }else {
                                        toastrMsg.error("查询失败");
                                    }
                                },
                                error:function(){
                                    toastrMsg.error("查询失败");
                                }
                            });

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