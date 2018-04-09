/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'TXT',
    extensions: 'txt',
    mimeTypes: "text/plain"
};
window.uplader = [];
window.fileNumLimit = 1;
var list=[];
var suspicious_list={};

var cftInfo;
var cft = (function () {
    var _init = function () {
        var params = utils.getURLParams();
        var id = params["id"];
        _get(id);
    }
    var params = {"indexName":"cftreginfo","conditions":[],"sort":"create_time desc"};

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
            async:false,
            success : function (d) {
                console.log(d);
                if(d.status===200){
                    var file = d.data.data;
                    if(file && file.length===1){
                        cftInfo =file[0];
                        suspicious_list.selected = {
                            "suspId":cftInfo.susp_id,
                            "suspName":cftInfo.susp_name
                        };
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

var parser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_saveEmlOne);
    };

    var _saveEmlOne = function () {
        var _data = window.uplader[0];
        _data["folder"]=cftInfo.id;
        $.ajax.proxy({
            url:"/api/admin/cft/trades/save",
            type:"post",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(_data),
            async:false,
            success:function (d) {
                if(d.status===200) {
                    console.log(d.data);
                    toastrMsg.success("文件"+_data['name']+"保存成功");
                    window.location.reload();
                }else {
                    console.log(d);
                    toastrMsg.error("文件"+_data['name']+"保存失败");
                }
            },
            error:function (d) {
                console.log(d);
                toastrMsg.error("文件"+_data['name']+"保存失败");
            }
        });
    };
    return {
        init: _init
    };
})();
cft.init();
parser.init();

