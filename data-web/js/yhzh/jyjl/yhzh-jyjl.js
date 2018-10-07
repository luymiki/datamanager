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
window.fileNumLimit = 1;
window.upladerCHecked = function () {
    var yys = $.trim($("#yys").val());
    if(yys===""){
        toastrMsg.error("请选择开户银行");
        return false;
    }
    return true;
}
var list = [];
var suspicious_list = {selected: {}};
var parser = (function () {
    var yhzhInfo;
    var _init = function () {
        _khyh($("#yys"));
        $("#parserEml").on("click", _saveEmlOne);
        suspicious_list.selected.suspName = "公共人员";
        suspicious_list.selected.suspId = "0155acf5-e2bc-472a-b7bc-01ac81cee56f";
    };

    var _khyh= function (input) {
        $.ajax({
            url: "/khyhxx.json",
            type: "get",
            dataType: "json",
            contentType: "application/json",
            success: function (d) {
                for(var i =0 ;i<d.length;i++) {
                    input.append('<option value="'+d[i]+'">'+d[i]+'</option>');
                }
            }
        });
    }

    var _saveEmlOne = function () {
        var result = false;
        var _data = window.uplader[0];
        _data["folder"]=$("#yys").val();
        //没有保存过，
        if (_data) {
            $.ajax.proxy({
                url: "/api/admin/yhzh/jyls/save",
                type: "post",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(_data),
                async: true,
                success: function (d) {
                    if (d.status === 200) {
                        console.log(d.data);
                        toastrMsg.success("文件" + _data['name'] + "保存成功");
                        top.contabs.closeTab();
                    } else {
                        console.log(d);
                        toastrMsg.error("文件" + _data['name'] + "保存失败");
                    }
                },
                error: function (d) {
                    console.log(d);
                    toastrMsg.error("文件" + _data['name'] + "保存失败");
                }
            });
        }
    };

    return {
        init: _init
    };
})();


parser.init();