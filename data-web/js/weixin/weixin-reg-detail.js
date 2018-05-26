/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var reg = (function () {
        var file;
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
            $.ajax.proxy({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                async:false,
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        file = d.data.data;
                        if(file && file.length===1){
                            file =file[0];
                            //查询联系人
                            $.ajax.proxy({
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
                            $.ajax.proxy({
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
                            $.ajax.proxy({
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

            if(file["wxlxrList"]&& file["wxlxrList"].length>0){
                for(var i=0;i<file["wxlxrList"].length;i++){
                    file["wxlxrList"][i]["xh"] = i+1;
                }
                $('#wxlxrList-table').myTable({
                    colResizable:false,
                    sidePagination:"client",
                    height:445,
                    pageSize:file["wxlxrList"].length,
                    columns: [
                        // {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'id',title: 'ID',visible:false},
                        // {field: 'susp_name',title: '姓名',sortable:true,width:'100px'},
                        {field: 'zh',title: '微信号',sortable:true},
                        {field: 'qq',title: 'QQ号',sortable:true},
                        {field: 'nc',title: '昵称',sortable:true},
                        {field: 'bm',title: '别名'},
                        {field: 'dh',title: '手机号'},
                        {field: 'email',title: 'EMAIL'},
                        {field: 'wbo',title: '微博'}
                    ],
                    data : file["wxlxrList"]
                });
            }
            if(file["wxqunList"]&& file["wxqunList"].length>0){
                for(var i=0;i<file["wxqunList"].length;i++){
                    file["wxqunList"][i]["xh"] = i+1;
                }
                $('#wxqunList-table').myTable({
                    colResizable:false,
                    sidePagination:"client",
                    height:445,
                    pageSize:file["wxqunList"].length,
                    columns: [
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'id',title: 'ID',visible:false},
                        {field: 'zh',title: '微信号',sortable:true},
                        {field: 'mc',title: '群名称',sortable:true},
                        {field: 'cjsj',title: '创建时间',sortable:true}
                    ],
                    data : file["wxqunList"]
                });
            }
            if(file["wxloginipList"]&& file["wxloginipList"].length>0){
                for(var i=0;i<file["wxloginipList"].length;i++){
                    file["wxloginipList"][i]["xh"] = i+1;
                }
                $('#wxloginipList-table').myTable({
                    colResizable:false,
                    sidePagination:"client",
                    height:445,
                    pageSize:file["wxloginipList"].length,
                    columns: [
                        {field: 'xh',title: '序号',width:'50px'},
                        {field: 'id',title: 'ID',visible:false},
                        {field: 'ip',title: '登录IP',sortable:true},
                        {field: 'cjsj',title: '登录时间',sortable:true}
                    ],
                    data : file["wxloginipList"]
                });
            }


        };


        return {
            init:_init
        };
    })();

    reg.init();


})(document, window, jQuery);