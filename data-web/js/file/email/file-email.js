/**
 * Created by hc.zeng on 2018/3/24.
 */
"use strict"
window.accept = {
    title: 'EMAIL',
    extensions: 'eml,',
    mimeTypes: "message/rfc822"
};
window.uplader = [
    // {createTime:"2018-03-24T15:09:58.645+0000",
    // folder:"文件",
    // id:"088f8e46-0ec4-4b3a-8a90-d00663fc4569",
    // name:"【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml",
    // path:"/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml",
    // size:82260,
    // suffix:"eml",
    // suspId:"15bf33c0-8ec4-4161-ba0e-7887503066fd",
    // suspName:"张洋洋",
    // tags:"测试",
    // type:"文件"},
    // {createTime:"2018-03-24T15:09:58.645+0000",
    // folder:"文件",
    // id:"a9e01e5f-f677-478e-9a9a-f6065a3c7ef8",
    // name:"现在开始试用Wijmo Enterprise产品- (1).eml",
    // path:"/现在开始试用Wijmo Enterprise产品- (1).eml",
    // size:82260,
    // suffix:"eml",
    // suspId:"15bf33c0-8ec4-4161-ba0e-7887503066fd",
    // suspName:"张洋洋",
    // tags:"测试",
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

var emailList=[];

var emailParser = (function () {

    var _init = function () {
        $("#parserEml").on("click",_parserEml);
    };

    var $tabs = $("#eml-tabs").empty();
    var $contents = $("#eml-content").empty();


    var _parserEml = function () {
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
            url:"/api/admin/email/parser",
            type:"post",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(_data),
            async:false,
            success:function (d) {
                if(d.status===200){
                    console.log(d.data);
                    var email = d.data;
                    emailList[emailList.length] = email;

                    $('<li class="'+(i===0?('active'):(' '))+'" ><a data-toggle="tab" href="#tab-'+i+'"> '+ (email['subject']===''?(email['subject']='无标题'):email['subject'])+'</a></li>').appendTo($tabs);
                    //$('<div id="tab-'+i+'" class="tab-pane '+(i===0?('active'):(' '))+'"><div class="panel-body">'+ email['from']+'</div></div>').appendTo($contents);

                    var file = [];//附件
                    var text = email.content;//文本
                    if(email.fileList){
                        for(var v = 0 ;v<email.fileList.length;v++){
                            var cont = email.fileList[v];
                            var contType=cont["type"];
                            var fs = cont["path"].split("/");
                            var fn = fs[fs.length-1];//contType.split(";")[2].replace("name=","");
                            var ty = "file";
                            if(contType.indexOf("image")===0 || contType.indexOf("img")===0){
                                ty = "image";
                            }
                            file[file.length]={"name":fn,"type":ty,"path":cont["path"]};
                        }
                    }
                    var data = {
                        email: email,
                        index: i,
                        file:file,
                        text:text
                    };
                     var html = template('eml-template', data);
                    $contents.append(html);
                    for(var jj = 0 ; jj<text.length ; jj++){
                        var doc1 = window.frames["email-"+i+"-"+jj].document;
                        doc1.write(text[jj]);
                        setIframeHeight(document.getElementById("email-"+i+"-"+jj));
                    }

                }
                else {
                    console.log(d);
                    top.toastrMsg.error("文件"+_data['name']+"解析失败");
                }

            },
            error:function (d) {
                console.log(d);
                top.toastrMsg.error("文件"+_data['name']+"解析失败");
            }
        });
    }

    return {
        init: _init
    };
})();
var emailSave = (function () {

    var _init = function () {
        $("#restetEml").on("click",_restetEml);
        $("#saveEml").on("click",_saveEml);
        $("#eml-content").on("click", ".save-one",_saveEmlOne);
        $("#eml-content").on("click", ".delete-one",_deleteEmlOne);
    };


    var _saveEml = function () {
        var count = 0;
        for(var i=0;i<emailList.length;i++) {
            if(_saveEmlOne(null,i+1)){
                count++;
            }
        }
        top.contabs.closeTab();
    };
    var _saveEmlOne = function (o,indx) {
        var result = false;
        var $btn =  $(this);//disabled
        if(!indx){
            indx = $(this).attr("data-id");
        }else {
            indx = indx-1;//全部保存时加了1防止上面的if判断失败，所以要减1再使用
        }
        var _data = emailList[indx];
        var up = window.uplader[indx];

        //没有保存过，
        if(_data && !_data["save"]){
            _data["fileId"] = up["id"];
            _data["suspId"] = up["suspId"];
            _data["suspName"] = up["suspName"];
            var id=_data["id"];
            $.ajax.proxy({
                url:"/api/admin/email/save",
                type:"post",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(_data),
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        console.log(d.data);
                        _data["save"]=true;//设置已保存标记
                        result = true;
                        toastrMsg.success("邮件"+_data['subject']+"保存成功");
                    }else {
                        console.log(d);
                        toastrMsg.error("邮件"+_data['subject']+"保存失败");
                    }
                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("邮件"+_data['subject']+"保存失败");
                }
            });
        }else {
            toastrMsg.error("邮件"+_data['subject']+"已经保存过");
        }
        return result;
    };
    var _restetEml = function () {
        for(var i=0;i<window.uplader.length;i++){
            _deleteEmlOne(null,i+1);
        }
        toastrMsg.success("邮件删除成功");
        top.contabs.closeTab();

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
        var _email = emailList[indx];


        if(_email && _email["save"]){
            //保存过
            $.ajax.proxy({
                url:"/api/admin/email/delete",
                type:"post",
                dataType:"json",
                data:{id: _email["id"], fileId:_data["id"]},
                async:false,
                success:function (d) {
                    if(d.status===200) {
                        console.log(d.data);
                        result = true;
                        emailList[indx] =false;
                        window.uplader[indx] =false;
                        toastrMsg.success("邮件"+_data['subject']+"删除成功");
                        clearHtml();
                    }else {
                        toastrMsg.error("邮件"+_data['subject']+"删除失败");
                    }

                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("邮件"+_data['name']+"删除失败");
                }
            });
        }else if(_email){
            var id=_data["id"];
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
                        emailList[indx] =false;
                        window.uplader[indx] =false;
                        toastrMsg.success("邮件"+_data['subject']+"删除成功");
                        clearHtml();
                    }else {
                        toastrMsg.error("邮件"+_data['subject']+"删除失败");
                    }

                },
                error:function (d) {
                    console.log(d);
                    toastrMsg.error("邮件"+_data['name']+"删除失败");
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

emailParser.init();
emailSave.init();





function setIframeHeight(iframe) {
    if (iframe) {
        var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
        if (iframeWin.document.body) {
            iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
        }
    }
}