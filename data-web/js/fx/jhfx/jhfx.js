/**
 * Created by hc.zeng on 2018/3/21.
 */
(function () {
    "use strict";

    var jhfx = (function () {
        var metaList=[];
        var metaMap={};

        var _init = function init(_data) {
            //_initListTable();
            _event();
            _getMeta();
            _initMetaSelect();
        };

        var params = {"indexName":"suspicious","conditions":[],"sort":"modify_time desc"};
        var kyr= {
            "field": "type",
            "values": ['2'],
            "searchType": 3,
            "dataType":2,
        };

        // var _initListTable = function(){
        //     $('#suspicious-table').myTable({
        //         columns:[
        //             {field: 'checkbox',title: '选择',width:'50px',checkbox:true},
        //             {field: 'xh',title: '序号',width:'50px'},
        //             {field: 'id',title: 'ID',visible:false},
        //             {field: 'type',title: '类型',width:'100px',formatter:formatterType},
        //             {field: 'name',title: '姓名'},
        //             {field: 'gmsfzh',title: '身份证号',sortable:true},
        //             {field: 'qkjj',title: '情况简介',class:'qkjj-cell',formatter:formatterStr},
        //             {field: 'qq',title: 'QQ',formatter:formatterList},
        //             {field: 'weixin',title: '微信',formatter:formatterList},
        //             {field: 'phone',title: '手机号',formatter:formatterList},
        //             {field: 'imei',title: 'IMEI',formatter:formatterList},
        //             {field: 'imsi',title: 'IMSI',formatter:formatterList},
        //             {field: 'cft',title: '财付通',formatter:formatterList},
        //             {field: 'zfb',title: '支付宝',formatter:formatterList},
        //             {field: 'yhzh',title: '银行账号',formatter:formatterList},
        //             {field: 'ip',title: 'IP',formatter:formatterList},
        //             {field: 'email',title: '电子邮箱',formatter:formatterList},
        //             {field: 'other',title: '其他码值',formatter:formatterList},
        //             {field: 'gzjd',title: '工作进度'},
        //             {field: 'opt',title: '操作',width:'110px'}
        //         ],
        //         ajax : function (request) {
        //             var sort = "modify_time desc";
        //             if(request.data.sortName){
        //                 sort = request.data.sortName +" "+request.data.sortOrder;
        //             }
        //             params["sort"]=sort;
        //             params["conditions"]=[kyr];
        //             $.ajax.proxy({
        //                 url:"/api/eqa/query",
        //                 type:"post",
        //                 dataType:"json",
        //                 data:{"pageNum":request.data.pageNumber,"pageSize":request.data.pageSize,"paramsStr":JSON.stringify(params)},
        //                 success : function (msg) {
        //                     if(msg.status===200){
        //                         console.log(msg)
        //                         var data = msg.data.data;
        //                         var xh =  ((request.data.pageNumber-1)*request.data.pageSize)+1;
        //                         for(var i= 0;i<data.length;i++){
        //                             data[i]['xh'] = xh++;
        //                             data[i]['opt'] = "<div class='btn btn-info btn-outline btn-xs gxr' data-id='"+data[i]["id"]+"'>关系人</div><br>" +
        //                                 "<div class='btn btn-info btn-outline btn-xs tiqu' data-id='"+data[i]["id"]+"'>提取</div><br>" +
        //                                 "<div class='btn btn-primary btn-outline btn-xs update' data-id='"+data[i]["id"]+"'>修改</div><br>"+
        //                                 "<div class='btn btn-danger btn-outline btn-xs delete' data-id='"+data[i]["id"]+"'>删除</div>";
        //                         }
        //                         request.success({
        //                             rows : data,
        //                             total : msg.data.total
        //                         });
        //                         meta = msg.data.meta["suspicious"];
        //                     }else {
        //                         request.success({
        //                             rows : [],
        //                             total : 0
        //                         });
        //                     }
        //
        //                 },
        //                 error:function(){
        //                     toastrMsg.error("错误！");
        //                 }
        //             });
        //
        //         },
        //         onDblClickCell:function(field, value, row, $element) {
        //             //console.log(row);
        //             var val = row[field];
        //             if(field === "type"){
        //                 val = formatterType(val);
        //             }
        //             if(val instanceof Array){
        //                 var s = "";
        //                 for(var i=0 ; i< val.length;i++){
        //                     var d = val[i];
        //                     s += formatter(field,row["id"],d);
        //                 }
        //                 //val =  val.join("&emsp;");
        //                 val = s;
        //             }
        //             if(val!==""){
        //                 layer.open({
        //                     // time: 2000, //不自动关闭
        //                     type: 1,
        //                     skin: 'layui-layer-rim', //加上边框
        //                     area: ['450px', '340px'], //宽高
        //                     content:  "<div style='padding: 5px; word-break: break-all;'>"+val+"</div>"
        //                 });
        //             }
        //         }
        //     });
        //
        // };


        var _event = function () {
            $("#add-index").on('click',_addIndexSelect);
            $("#selectForm").on('click',".delete-index",function () {
                var _id = $(this).attr("data-id");
                $("#"+_id+"-box").remove();
            });
            $("#selectForm").on('change',".meta-index",function () {
                var e = $(this);
                var indexName = e.val();
                var id = e.attr("id");
                _createFieldHtml($("#"+id+"-field"),indexName);
            });
            $("#query-btn").on('click',_query);

        };
        // var _addItem =function (id,val,type,title) {
        //     top.contabs.addMenuItem("/view/suspicious/suspicious-page.html?id="+id+"&type="+type+"&code="+val,title);
        // }
        // var _addImportItem =function (id,title,url) {
        //     top.contabs.addMenuItem(url+"?suspid="+id,title);
        // }
        //
        // var formatter = function (field,id,val) {
        //     switch (field){
        //         case "qq":
        //             return _formatter(id,val,'data-qq');
        //         case "weixin":
        //             return _formatter(id,val,'data-weixin');
        //         case "phone":
        //             return _formatter(id,val,'data-dh');
        //         case "cft":
        //             return _formatter(id,val,'data-cft');
        //         case "yhzh":
        //             return _formatter(id,val,'data-yhzh');
        //         case "email":
        //             return _formatter(id,val,'data-email');
        //         case "ip":
        //             return _formatter(id,val,'data-ip');
        //         default:
        //             return val +" ";
        //     }
        // }
        // var _formatter = function (id,val,clazz) {
        //     return "<a class='"+clazz+"' data-id='"+id+"' "+clazz+"='"+val+"' >"+val+"</a> ";
        // }
        //
        // var formatterList = function (d,item, idx, field){
        //     if(d){
        //         var s = "";
        //         for(var i=0 ; i< d.length && i<5;i++){
        //             s += formatter(field,item["id"],d[i]);
        //         }
        //         if(d.length >5){
        //             s += "...";
        //         }
        //         // for(var i=0 ; i< d.length;i++){
        //         //     s+= d[i]+" ";
        //         // }
        //         return s;
        //     }
        //     return d;
        // };
        // var formatterStr = function (d){
        //     if(d){
        //         var s = d;
        //         if(d.length >50){
        //             s = d.substring(0,50)+"...";
        //         }
        //         return "<div class='qkjj-cell'>"+s+"</div>";
        //     }
        //     return d;
        // };
        // var formatterType = function (d){
        //     if(d && d ==="2"){
        //         return "关系人";
        //     }
        //     return "可疑人";
        // };

        /**
         * 获取元数据信息
         * @param id
         * @private
         */
        var _getMeta = function (id) {
            $.ajax.proxy({
                url:"/api/eqa/meta",
                type:"post",
                dataType:"json",
                data:{},
                async:false,
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        metaList = d.data;
                        for(var i= 0 ; i< metaList.length;i++){
                            var mm = metaList[i];
                            metaMap[mm["indexName"]]=mm;
                        }
                    }else {
                        toastrMsg.error("查询元数据失败");
                    }
                },
                error:function(){
                    toastrMsg.error("查询元数据失败");
                }
            });
        };


        /**
         * 初始化元数据选择列表
         * @private
         */
        var _initMetaSelect = function () {
            _createIndexHtml($("#meta-index-1"));
        }

        /**
         * 添加行选择行
         * @private
         */
        var _addIndexSelect = function () {
            var selectForm = $("#selectForm");
            var _id = "meta-index-"+(new Date().getTime());
            //div
            var fg = $('<div class="form-group" id="'+_id+'-box"></div>').appendTo(selectForm);
            $('<label class="col-xs-2 control-label">数据源：</label>').appendTo(fg);
            //索引下拉
            var indexDiv = $('<div class="col-xs-4"></div>').appendTo(fg);
            var indexSelect = $('<select id="'+_id+'" class="form-control meta-index"></select>').appendTo(indexDiv);
            _createIndexHtml(indexSelect);
            //字段下拉
            var fieldDiv = $('<div class="col-xs-4"></div>').appendTo(fg);
            $('<select id="'+_id+'-field" class="form-control"></select>').appendTo(fieldDiv);
            //删除按钮
            var deleteDiv = $('<div class="col-xs-2"></div>').appendTo(fg);
            $('<div data-id="'+_id+'" class="btn btn-danger btn-sm delete-index" style="font-size: 20px;padding-top: 0px;padding-bottom: 0px;"><i class="fa fa-times" aria-hidden="true"></i></div>').appendTo(deleteDiv);

        }

        /**
         * 创建数据索引下拉框
         * @param $select
         * @private
         */
        var _createIndexHtml = function ($select) {
            $select.empty();
            $("<option value=''>选择数据源</option>").appendTo($select);
            for(var i= 0 ; i< metaList.length;i++){
                var mm = metaList[i];
                $("<option value='"+mm["indexName"]+"'>"+mm["indexNameCn"]+"</option>").appendTo($select);
            }
        }

        /**
         * 创建字段下拉框
         * @param $select
         * @param indexName
         * @private
         */
        var _createFieldHtml = function ($select,indexName) {
            $select.empty();
            var metas = metaMap[indexName]["eqaMetas"];
            $("<option value=''>选择字段</option>").appendTo($select);
            for(var i= 0 ; i< metas.length;i++){
                var mm = metas[i];
                if(mm["isFx"] ===1){
                    $("<option value='"+mm["fieldCode"]+"'>"+mm["fieldName"]+"</option>").appendTo($select);
                }

            }
        }


        /**
         * 查询
         * @private
         */
        var _query = function () {
            var queryItem=[];
            var index = $(".meta-index option:selected[value!='']");
            if(index.length>1){
                index.each(function(i,o){
                    //console.log(o)
                    var _id = $(o).parent().attr("id");
                    var field = $("#"+_id+"-field option:selected[value!='']");
                    if(field.length===1){
                        queryItem[queryItem.length] = {"indexName": $(o).val(),"fieldCode":field.val()};
                    }
                });
                if(queryItem.length > 1){
                    console.log(queryItem);
                    $.ajax.proxy({
                        url:"/api/eqa/fx/jhfx",
                        type:"post",
                        dataType:"json",
                        data:{"queryJson":JSON.stringify(queryItem)},
                        async:false,
                        success : function (d) {
                            console.log(d);
                            if(d.status===200){
                                console.log(d.data);

                            }else {
                                toastrMsg.error("查询元数据失败");
                            }
                        },
                        error:function(){
                            toastrMsg.error("查询元数据失败");
                        }
                    });

                } else {
                    toastrMsg.error("请选择对应个数的字段！");
                }

            }else {
                toastrMsg.error("请选择多个数据源！");
            }

        }

        return {
            init:_init
        };
    })();

    jhfx.init();


})();