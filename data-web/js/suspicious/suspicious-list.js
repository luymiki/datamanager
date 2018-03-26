/**
 * Created by hc.zeng on 2018/3/21.
 */

var suspicious_list = (function () {

    var _init = function init() {
        _event();
        _list();
    };
    var selected={
        suspName:"",
        suspId:""
    };
    var params = {
        "indexName": "suspicious",
        "conditions": [{"field": "is_delete", "values": ["1"], "searchType": 3, "dataType": 2}],
        "sort": "modify_time desc"
    };

    var _list = function () {
        $.ajax({
            url:"/api/eqa/query",
            type:"post",
            dataType:"json",
            data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params)},
            success : function (d) {
                console.log(d);
                if(d.status===200){
                    var data = d.data.data;
                    if(data && data.length>0){

                        var $list= $("#suspicious-list").empty();
                        $('<option value="">请选择嫌疑人</option>').appendTo($list);
                        for(var i=0; i<data.length;i++){
                            var f = data[i];
                            $("<option value='"+f["name"]+"!!"+f["id"]+"' data-id='"+f["id"]+"'>"+f["name"]+" | "+f["gmsfzh"]+"</option>").appendTo($list);
                        }
                        $("#suspicious-list").chosen({}).change(function(){
                            var v = $(this).val();
                            var ss = v.split("!!");
                            if(ss.length===2){
                                selected.suspName = ss[0];
                                selected.suspId = ss[1];
                            }else{
                                selected.suspName = "";
                                selected.suspId = "";
                            }

                        });
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

    var _event = function () {
        // $("#suspicious-table").on('click', '.update', function () {
        //     _get($(this).attr("data-id"));
        // });
        // $("#suspicious-table").on('click', '.delete', function () {
        //     _delete($(this).attr("data-id"));
        // });
    };


    return {
        init: _init,
        selected: selected
    };
})();

suspicious_list.init();
