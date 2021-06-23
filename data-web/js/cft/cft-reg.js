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
    ];

var parser = (function () {

    var _init = function () {
        // $("#parserEml").on("click",_parserFile);
        $("#parserEml").on("click",_saveEml);
    };
    var _saveEml = function () {
        var _data = window.uplader;
        if(_data){
            $.ajax.proxy({
                url:"/api/admin/cft/save",
                type:"post",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(_data),
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        // console.log(d.data);
                        toastrMsg.success("文件保存成功");
                        top.contabs.closeTab();
                    }else {
                        console.log(d);
                        toastrMsg.error("文件保存失败");
                    }
                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("文件保存失败");
                }
            });
        }
    };
    return {
        init: _init
    };
})();
parser.init();
