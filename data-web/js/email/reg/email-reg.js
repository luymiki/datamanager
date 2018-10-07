/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'TXT',
    extensions: 'txt,',
    mimeTypes: "text/plain"
};
window.uplader = [ ];
window.fileNumLimit =1;
var list=[];
// var suspicious_list={selected:{}};
var parser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_saveEmlOne);
    };



    var _saveEmlOne = function () {
        var result = false;
        var _data = window.uplader[0];
        //没有保存过，
        if(_data){
            $.ajax.proxy({
                url:"/api/admin/email_reg/save",
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
        }
    };

    return {
        init: _init
    };
})();


parser.init();
