/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'EXCEL',
    extensions: 'xls,xlsx',
    mimeTypes: "application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
};
window.uplader = [];
window.fileNumLimit =1;
window.upladerCHecked = function () {
    var yys = $.trim($("#yys").val());
    if(yys===""){
        toastrMsg.error("请选择运营商");
        return false;
    }
    return true;
}

var parser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_saveEmlOne);
    };


    var _saveEmlOne = function () {
        var $btn =  $(this);//disabled
        var _data = window.uplader[0];

        _data["folder"]=$("#yys").val();
        $.ajax.proxy({
            url:"/api/admin/huadan/save",
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

parser.init();
