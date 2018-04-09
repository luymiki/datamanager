/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'TXT',
    extensions: 'txt',
    mimeTypes: "text/plain"
};
window.uplader = [
    // {createTime:"2018-03-24T15:09:58.645+0000",
    // folder:"文件",
    // id:"69048397-a3f2-44ff-91b4-43f2b5a4b3ee",
    // name:"reg_12344335.txt",
    // path:"/eb94748f-9de5-4d63-9fcd-93290c27312b/cece1e59-3ad1-45fe-b527-ee475b29fb34-reg_12344335.txt",
    // size:82260,
    // suffix:"eml",
    // suspId:"eb94748f-9de5-4d63-9fcd-93290c27312b",
    // suspName:"王五",
    // tags:"注册",
    // type:"文件"},
    // {createTime:"2018-03-24T15:09:58.645+0000",
    //     folder:"文件",
    //     id:"a9e01e5f-f677-478e-9a9a-f6065a3c7ef8",
    //     name:"现在开始试用Wijmo Enterprise产品- (1).eml",
    //     path:"/现在开始试用Wijmo Enterprise产品- (1).eml",
    //     size:82260,
    //     suffix:"eml",
    //     suspId:"15bf33c0-8ec4-4161-ba0e-7887503066fd",
    //     suspName:"张洋洋",
    //     tags:"测试",
    //     type:"文件"},
    // {createTime:"2018-03-24T15:09:58.645+0000",
    //     folder:"文件",
    //     id:"a9e01e5f-f677-478e-9a9a-f6065a3c7ef8",
    //     name:"现在开始试用Wijmo Enterprise产品- (1).eml",
    //     path:"/现在开始试用Wijmo Enterprise产品- (1).eml",
    //     size:82260,
    //     suffix:"eml",
    //     suspId:"15bf33c0-8ec4-4161-ba0e-7887503066fd",
    //     suspName:"张洋洋",
    //     tags:"测试",
    //     type:"文件"}
    ];

window.upladerCHecked = function () {
    var qq = $.trim($("#qq-num").val());
    if(qq===""){
        toastrMsg.error("请选择或填写QQ号码");
        return false;
    }
    return true;
}



var Opt = (function () {

    var _init = function () {
        $("#parserEml").on("click",_saveFile);
    };

    var params = {"indexName":"qqzone","conditions":[],"sort":"create_time desc"};
    var qzone;
    var _saveFile = function () {
        //没有空间信息，查询或创建一条记录
        var result = false;
        if(!qzone){
            params["conditions"]= [{
                "field": "susp_id",
                "values": [suspicious_list.selected.suspId],
                "searchType": 1,
                "dataType":2,
            },{
                "field": "qq",
                "values": [$("#qq-num").val()],
                "searchType": 1,
                "dataType":2,
            }
            ];
            $.ajax({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        var data = msg.data.data;
                        if(data.length===1){
                            qzone = data[0];
                        }
                        result = true;
                    }else {
                        toastrMsg.error(msg.message);
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });
        }else {
            result = true;
        }
        if(!result){
            toastrMsg.error("系统错误,新增空间信息失败");
            return false;
        }

        var qq = $.trim($("#qq-num").val());
        if(qq===""){
            toastrMsg.error("请选择或填写QQ号码");
            return false;
        }
        var fileIds = [];
        var suspId = '';
        var suspName = '';
        for(var i=0;i< window.uplader.length;i++) {
            if(!window.uplader[i]["save"]){
                var f = window.uplader[i]["id"];
                suspId = window.uplader[i]["suspId"];
                suspName = window.uplader[i]["suspName"];
                fileIds[fileIds.length] =f;
            }
        }
        var _data;
        if(!qzone){
            _data = {
                suspId:suspId,
                suspName:suspName,
                qq:qq,
                fileId:fileIds,
                pic:fileIds.length
            };
        }else {
            var fl = qzone["file_id"];
            if(fl){
                for(var i=0;i<fl.length;i++){
                    fileIds[fileIds.length] =fl[i];
                }
            }
            _data = {
                id:qzone["id"],
                suspId:qzone["susp_id"],
                suspName:qzone["susp_name"],
                qq:qzone["qq"],
                fileId:fileIds,
                pic:fileIds.length,
                tags:$("#image-tags").val()
            };
        }

        $.ajax.proxy({
            url:"/api/admin/qq/qzone/save",
            type:"post",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(_data),
            async:false,
            success:function (d) {
                if(d.status===200) {
                    console.log(d.data);
                    qzone = d.data;
                    for(var i=0;i< window.uplader.length;i++) {
                        window.uplader[i]["save"]=true;
                    }
                    toastrMsg.success("空间信息保存成功");
                }else {
                    console.log(d);
                    toastrMsg.error("空间信息保存失败");
                }
            },
            error:function (d) {
                console.log(d);
                toastrMsg.error("空间信息保存失败");
            }
        });

        // if(count ===  list.length){
        //     toastrMsg.success("文件保存成功");
        //     window.location.reload();
        // }else {
        //     toastrMsg.error("文件保存失败");
        // }
    };

    return {
        init: _init
    };
})();

Opt.init();
