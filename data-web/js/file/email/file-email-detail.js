/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var email = (function () {



        var _init = function init(_data) {
            var url = window.location.href;
            url = url.split("?");
            var params = {};
            if(url.length==2){
                url = url[1].split("&");
                for(var i = 0 ; i<url.length;i++){
                    var p = url[i].split("=");
                    params[p[0]]=p[1];
                }
            }
            var id = params["id"];
            _get(id);
        };

        var params = {"indexName":"email","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":1,
            }];
            $.ajax({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var data = d.data.data;
                        if(data && data.length===1){
                            var email = data[0];
                            var file = [];//附件
                            var text = email.content;//文本
                            if(email.file_list){
                                for(var v = 0 ;v<email.file_list.length;v++){
                                    var cont = email.file_list[v];
                                    cont = JSON.parse(cont);
                                    var contType=cont["type"];
                                    var fn = contType.split(";")[2].replace("name=","");
                                    var ty = "file";
                                    if(contType.indexOf("image")===0 || contType.indexOf("img")===0){
                                        ty = "image";
                                    }
                                    file[file.length]={"name":fn,"type":ty,"path":cont["path"]};
                                }
                            }
                            var td = {
                                email: email,
                                file:file,
                                text:text
                            };
                            var html = template('eml-template', td);
                            $("#eml-content").append(html);
                            for(var jj = 0 ; jj<text.length ; jj++){
                                var doc1 = window.frames["email-"+jj].document;
                                doc1.write(text[jj]);
                                setIframeHeight(document.getElementById("email-"+jj));
                            }
                            $('#email-detail').modal('show');
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

    email.init();


})(document, window, jQuery);

function setIframeHeight(iframe) {
    if (iframe) {
        var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
        if (iframeWin.document.body) {
            iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
        }
    }
}