/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'TEXT',
    extensions: 'sql',
    mimeTypes: "text/sql"
};
window.uplader = [];
window.fileNumLimit =1;
var suspicious_list={selected:{}};
var parser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_saveEmlOne);
        suspicious_list.selected.suspName="公共人员";
        suspicious_list.selected.suspId="0155acf5-e2bc-472a-b7bc-01ac81cee56f";
    };


    var _saveEmlOne = function () {
        var $btn =  $(this);//disabled
        var _data = window.uplader[0];
        $.ajax.proxy({
            url:"/api/admin/xndw/sx/save",
            type:"post",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(_data),
            async:true,
            success:function (d) {
                if(d.status===200) {
                    console.log(d.data);
                    toastrMsg.success("文件"+_data['name']+"保存成功");
                    top.contabs.closeTab();
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

parser.init();
