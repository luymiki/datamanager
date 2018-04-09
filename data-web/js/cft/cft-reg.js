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

var list=[];

var parser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_parserFile);
    };

    var $tabs = $("#eml-tabs").empty();
    var $contents = $("#eml-content").empty();


    var _parserFile = function () {
        $("#eml-upload").hide();
        $("#eml-parser").show();
         $tabs.empty();
         $contents.empty();
        for(var i=0;i<window.uplader.length;i++){
            var _data = window.uplader[i];
            _parser(_data,i);
        }
    };
    var _parser = function (_data,i) {
        $.ajax.proxy({
            url:"/api/admin/cft/parser",
            type:"post",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(_data),
            async:false,
            success:function (d) {
                if(d.status===200){
                    console.log(d.data);
                    var file = d.data;
                    list[list.length] = file;

                    $('<li class="'+(i===0?('active'):(' '))+'" ><a data-toggle="tab" href="#tab-'+i+'"> '+file['name']+'['+file['zh']+']</a></li>').appendTo($tabs);
                    //$('<div id="tab-'+i+'" class="tab-pane '+(i===0?('active'):(' '))+'"><div class="panel-body">'+ email['from']+'</div></div>').appendTo($contents);


                    var data = {
                        file: file,
                        index: i
                    };
                     var html = template('eml-template', data);
                    $contents.append(html);
                }
                else {
                    console.log(d);
                    top.toastrMsg.error("文件"+data['name']+"解析失败");
                }

            },
            error:function (d) {
                console.log(d);
                top.toastrMsg.error("文件"+data['name']+"解析失败");
            }
        });
    }

    return {
        init: _init
    };
})();
var Opt = (function () {

    var _init = function () {
        $("#restetEml").on("click",_restetEml);
        $("#saveEml").on("click",_saveEml);
        $("#eml-content").on("click", ".save-one",_saveEmlOne);
        $("#eml-content").on("click", ".delete-one",_deleteEmlOne);
    };


    var _saveEml = function () {
        var count = 0;
        for(var i=0;i<list.length;i++) {
            if(_saveEmlOne(null,i+1)){
                count++;
            }
        }
        if(count === list.length){
            toastrMsg.success("文件保存成功");
            window.location.reload();
        }else {
            toastrMsg.error("文件保存失败");
        }
    };
    var _saveEmlOne = function (o,indx) {
        var result = false;
        var $btn =  $(this);//disabled
        if(!indx){
            indx = $(this).attr("data-id");
        }else {
            indx = indx-1;//全部保存时加了1防止上面的if判断失败，所以要减1再使用
        }
        var _data = list[indx];
        var up = window.uplader[indx];

        //没有保存过，
        if(_data && !_data["save"]){
            _data["fileId"] = up["id"];
            _data["suspId"] = up["suspId"];
            _data["suspName"] = up["suspName"];
            var id=_data["id"];
            $.ajax.proxy({
                url:"/api/admin/cft/save",
                type:"post",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(_data),
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        console.log(d.data);
                        _data["id"]=d.data["id"];
                        _data["save"]=true;//设置已保存标记
                        result = true;
                        toastrMsg.success("文件"+_data['name']+"保存成功");
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
        }else {
            result = true;
        }
        return result;
    };
    var _restetEml = function () {
        var count = 0;
        for(var i=0;i<window.uplader.length;i++){
            _deleteEmlOne(null,i+1);
        }
        toastrMsg.success("文件删除成功");
        window.location.reload();

    };
    var _deleteEmlOne = function (o,indx) {
        var result = false;
        if(!indx){
            indx = $(this).attr("data-id");
        }else {
            indx = indx-1;//全部保存时加了1防止上面的if判断失败，所以要减1再使用
        }
        var count = 0;
        var _data = window.uplader[indx];
        var _file = list[indx];
        var id=_data["id"];

        //保存过
        if(_file && _file["save"]){
            $.ajax.proxy({
                url:"/api/admin/cft/delete",
                type:"post",
                dataType:"json",
                data:{id: _file["id"], fileId:_data["id"]},
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        console.log(d.data);
                        result = true;
                        window.uplader[indx] = false;
                        list[indx] = false;
                        toastrMsg.success("文件"+_data['name']+"删除成功");
                        clearHtml();
                    }else {
                        toastrMsg.error("文件"+_data['name']+"删除失败");
                    }

                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("文件"+_data['name']+"删除失败");
                }
            });
        }else if(_file){//没有保存过
            $.ajax.proxy({
                url:"/api/admin/file/delete",
                type:"post",
                dataType:"json",
                data:{id:id},
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        console.log(d.data);
                        result =true;
                        window.uplader[indx] = false;
                        list[indx] = false;
                        toastrMsg.success("文件"+_data['name']+"删除成功");
                        clearHtml();
                    }else {
                        toastrMsg.error("文件"+_data['name']+"删除失败");
                    }

                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("文件"+_data['name']+"删除失败");
                }
            });
        }

        return result;
    };

    var clearHtml = function () {
        //清除tabs
        $("#eml-tabs .active").remove();
        $($("#eml-tabs li").first()).addClass("active");
        $("#eml-content .active").remove();
        $($("#eml-content .tab-pane").first()).addClass("active");

    }

    return {
        init: _init
    };
})();

parser.init();
Opt.init();
