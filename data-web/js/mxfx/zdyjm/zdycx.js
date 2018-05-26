/**
* Created by hc.zeng on 2018/3/21.
*/


var zdycx = (function () {
    "use strict";

    var metaList = [];
    var metaMap = {};
    var fieldList;
    var _init = function init(_data) {
        _event();
        _getMeta();
        _initMetaSelect();
    };

    var _event = function () {
        //数据源切换事件
        $("#zdyjm-box").on('change', "#meta-index", function () {
            var e = $(this);
            var indexName = e.val();
            $(".condtion-item ").remove();
            fieldList = undefined;
            if (indexName !== "") {
                fieldList = metaMap[indexName]["eqaMetas"];
                var id = e.attr("id");
                _addIndexSelect($("#must-field"), "must","", true);
                _addIndexSelect($("#not-field"), "not","", true);
                _changeMetaFieldSelect();
            }
        });


        // 字段选择切换事件
        $("#zdyjm-box").on('change', ".field-item", function () {
            var e = $(this);
            _createFieldTypeHtml(e);
        });

        // 字段数字运算条件切换事件
        $("#zdyjm-box").on('change', ".number-field", function () {
            var e = $(this);
            var item = e.parent();
            var input = item.find("input");
            var _id = e.attr("data-id");
            if(e.val() === "6"){//范围
                input.remove();
                $("<input id='" + _id + "-field-condtion-val-start' class='form-control field short' type='number'></input>").appendTo(item);
                $("<input id='" + _id + "-field-condtion-val-end' class='form-control field short' type='number'></input>").appendTo(item);
            }else {
                if(input.length>1){
                    input.remove();
                    $("<input id='" + _id + "-field-condtion-val' class='form-control field' type='number'></input>").appendTo(item);
                }
            }
        });
        // 字段时间运算条件切换事件
        $("#zdyjm-box").on('change', ".date-field", function () {
            var e = $(this);
            var item = e.parent();
            var input = item.find("input");
            var _id = e.attr("data-id");
            if(e.val() === "6"){//范围
                input.remove();
                $("<input id='" + _id + "-field-condtion-val-start' class='form-control field short input-date' type='datetime'></input>").appendTo(item);
                $("<input id='" + _id + "-field-condtion-val-end' class='form-control field short input-date' type='datetime'></input>").appendTo(item);
            }else {
                if(input.length>1){
                    input.remove();
                    $("<input id='" + _id + "-field-condtion-val' class='form-control field input-date' type='datetime'></input>").appendTo(item);
                }
            }
            initDate($('.input-date'));
        });

        //添加必要条件
        $("#zdyjm-box").on('click', "#add-must-field", function () {
            _addIndexSelect($("#must-field"), "must","", false);
        });
        //添加排除条件
        $("#zdyjm-box").on('click', "#add-not-field", function () {
            _addIndexSelect($("#not-field"), "not", "",false);
        });
        //删除条件
        $("#zdyjm-box").on('click', ".delete-btn", function () {
            var _id = $(this).attr("data-id");
            $("#" + _id + "-box").remove();
        });

        //高级条件显示隐藏事件
        $("#zdyjm-box").on("change", "#add-group", _changeGroup);

        //高级条件添加一组条件事件
        $("#zdyjm-box").on("click", "#add-should-field", _addShouldGroup);

        //条件组添加条件事件
        $("#zdyjm-box").on("click", ".add-must-field-for-should", function () {
            var group = $(this).attr("data-group");
            _addIndexSelect($("#"+group), "should",group, false);
        });
        //条件组删除条件事件
        $("#zdyjm-box").on("click", ".delete-must-field-for-should", function () {
            var group = $(this).attr("data-group");
            $("#"+group).remove();
        });


        // $("#query-btn").on('click', _query);
        // $("#rest-btn").on('click', _rest);

    };

    /**
     * 获取元数据信息
     * @param id
     * @private
     */
    var _getMeta = function (id) {
        $.ajax.proxy({
            url: "/api/eqa/meta",
            type: "post",
            dataType: "json",
            data: {},
            async: false,
            success: function (d) {
                console.log(d);
                if (d.status === 200) {
                    metaList = d.data;
                    for (var i = 0; i < metaList.length; i++) {
                        var mm = metaList[i];
                        metaMap[mm["indexName"]] = mm;
                    }
                } else {
                    toastrMsg.error("查询元数据失败");
                }
            },
            error: function () {
                toastrMsg.error("查询元数据失败");
            }
        });
    };


    /**
     * 切换选择高级查询
     * @private
     */
    var _changeGroup = function () {
        if (fieldList) {
            var checked = $(this).is(':checked');
            //选择
            if (checked) {
                $("#group-box").show();
            } else {//取消
                $("#group-box").hide();
            }
            return true;
        }
        $(this).prop("checked", false);
        return false;
    }

    /**
     * 初始化元数据选择列表
     * @private
     */
    var _initMetaSelect = function () {
        var $select = $("#meta-index");
        $select.empty();
        $("<option value=''>---请选择---</option>").appendTo($select);
        for (var i = 0; i < metaList.length; i++) {
            var mm = metaList[i];
            $("<option value='" + mm["indexName"] + "'>" + mm["indexNameCn"] + "</option>").appendTo($select);
        }
    }

    /**
     * 添加行选择行
     * @private
     */
    var _addIndexSelect = function ($box, key, group,first) {
        //console.log(fieldList);
        if (fieldList) {
            var _id = "meta-field-" + (new Date().getTime());
            //div
            var fg = $('<div class="condtion-item '+group+'" data-key="' + key + '" id="' + _id + '-box" data-id="' + _id + '" data-group="'+group+'"></div>').appendTo($box);
            if (!first) {
                //删除按钮
                $('<div data-id="' + _id + '" class="btn btn-danger btn-sm delete-btn" ><i class="fa fa-times" aria-hidden="true"></i></div>').appendTo(fg);
            }
            //字段下拉
            var select = $('<select id="' + _id + '-field" data-id="' + _id + '" class="form-control field field-item" ' + (first ? 'style="margin-left: 45px;"' : "") + '></select>').appendTo(fg);
            _createFieldHtml(select);
        } else {
            toastrMsg.error("请选择数据");
        }

    }
    /**
     * 添加数据字段下拉
     * @private
     */
    var _changeMetaFieldSelect = function () {
        if (fieldList) {
            _createFieldHtml($("#meta-index-field"));
        }
    }


    /**
     * 创建字段下拉框
     * @param $select
     * @param indexName
     * @private
     */
    var _createFieldHtml = function ($select) {
        $select.empty();
        $("<option value=''>选择字段</option>").appendTo($select);
        for (var i = 0; i < fieldList.length; i++) {
            var mm = fieldList[i];
            if (mm["isFx"] === 1) {
                $("<option value='" + mm["fieldCode"] + "' data-type='" + mm["fieldType"] + "'>" + mm["fieldName"] + "</option>").appendTo($select);
            }
        }
    }


    /**
     * 根据数据类型创建过滤规则选择框
     * @param $select
     * @private
     */
    var _createFieldTypeHtml = function ($select) {
        var option = $select.find("option:selected[value!='']");
        var _id = $select.attr("data-id");
        var fieldType = option.attr("data-type");
        $("#" + _id + "-field-condtion").remove();
        $("#" + _id + "-field-condtion-val").remove();
        if (fieldType !== undefined) {
            switch (fieldType) {
                case "1"://文本
                case "2"://标签
                    _createFieldTypeTextHtml(_id, $("#" + _id + "-box"));
                    break;
                case "3"://数字
                    _createFieldTypeNumberHtml(_id, $("#" + _id + "-box"));
                    break;
                case "4"://时间
                    _createFieldTypeDateHtml(_id, $("#" + _id + "-box"));
                    break;
                case "5"://字典
                    _createFieldTypeTextHtml(_id, $("#" + _id + "-box"));
                    break;
            }
        } else {

        }
    };

    /**
     * 创建文本查询条件
     * @param _id
     * @param $fieldCondtion
     * @private
     */
    var _createFieldTypeTextHtml = function (_id, $fieldCondtion) {
        //字段下拉
        var $select = $('<select id="' + _id + '-field-condtion" data-id="' + _id + '" class="form-control field ' + _id + '-field-condtion short"></select>').appendTo($fieldCondtion);
        $("<option value='1' >完全匹配</option>").appendTo($select);
        $("<option value='2' >模糊匹配</option>").appendTo($select);
        $("<option value='3' >不包含</option>").appendTo($select);
        $("<textarea id='" + _id + "-field-condtion-val' class='field textarea'></textarea>").appendTo($fieldCondtion);
    };

    /**
     * 数字范围
     * @param _id
     * @param $fieldCondtion
     * @private
     */
    var _createFieldTypeNumberHtml = function (_id, $fieldCondtion) {
        //字段下拉
        var $select = $('<select id="' + _id + '-field-condtion" data-id="' + _id + '" class="form-control field number-field short' + _id + '-field-condtion"></select>').appendTo($fieldCondtion);
        $("<option value='1' >等于</option>").appendTo($select);
        $("<option value='3' >不等于</option>").appendTo($select);
        $("<option value='4' >大于</option>").appendTo($select);
        $("<option value='5' >小于</option>").appendTo($select);
        $("<option value='6' >范围</option>").appendTo($select);
        $("<input id='" + _id + "-field-condtion-val' class='form-control field' type='number' ></input>").appendTo($fieldCondtion);
    }
    /**
     * 时间范围
     * @param _id
     * @param $fieldCondtion
     * @private
     */
    var _createFieldTypeDateHtml = function (_id, $fieldCondtion) {
        //字段下拉
        var $select = $('<select id="' + _id + '-field-condtion" data-id="' + _id + '" class="form-control field date-field short' + _id + '-field-condtion"></select>').appendTo($fieldCondtion);
        $("<option value='1' >等于</option>").appendTo($select);
        $("<option value='4' >大于</option>").appendTo($select);
        $("<option value='5' >小于</option>").appendTo($select);
        $("<option value='6' >范围</option>").appendTo($select);
        $("<input id='" + _id + "-field-condtion-val' class='form-control field input-date' type='datetime' placeholder='2018-01-01 00:00:00'></input>").appendTo($fieldCondtion);
        initDate($('.input-date'));
    }
    var initDate = function (o) {
        o.datetimepicker({
            format: 'YYYY-MM-DD HH:mm:ss',
            locale: moment.locale('zh-cn')
        });
    }

    var _addShouldGroup = function () {
        if (fieldList) {
            var group = "group-field-" + (new Date().getTime());
            var groupbox = $("#should-group-box");
            var fs = $('<fieldset class="table-bordered should-fieldset" id="'+group+'"><legend>可能条件</legend></fieldset>').appendTo(groupbox);
            $('<div class="opt-bar" style="border: 0px;padding-top: 0px;text-align: right;">' +
                '<a class="delete-must-field-for-should" data-group="'+group+'">删除条件组</a>&emsp;' +
                '<a class="add-must-field-for-should" data-group="'+group+'">添加条件</a>' +
                '</div>').appendTo(fs);
            _addIndexSelect(fs,"should",group,true);
        }
    }

    var _rest = function () {
        fieldList = undefined;
        $("#meta-index").val("");
        $("#meta-index-field").val("");
        $("#add-group").prop("checked",false);
        $("#group-box").hide();
        $(".condtion-item").remove();
        $(".should-fieldset").remove();
        //$("#data-show-box").hide();
    }

    /**
     * 构造查询条件
     * @private
     */
    var _condtion = function () {
        var condtionItem = $(".condtion-item");
        if (condtionItem.length <= 0 || !fieldList) {
            return false;
        }
        var conditions = [];
        condtionItem.each(function (i, o) {
            var item = $(o);
            var group = item.attr("data-group");
            var key = item.attr("data-key");
            var _id = item.attr("data-id");
            var field = $("#" + _id + "-field option:selected[value!='']");
            var condtion = $("#" + _id + "-field-condtion option:selected[value!='']");
            var val = $.trim($("#" + _id + "-field-condtion-val").val());
            var vals = $.trim($("#" + _id + "-field-condtion-val-start").val());
            var vale = $.trim($("#" + _id + "-field-condtion-val-end").val());
            var dataType = field.attr("data-type");
            var searchType = condtion.val();
            if (field.length === 1 && condtion.length===1 ) {

                var cond = {
                    "field": field.val(),
                    // "values": val.split(/\s+|\t+|\r+|\n+/),
                    "searchType": searchType,
                    "dataType":dataType,
                };
                switch (searchType){
                    case "6": {
                        if(vals !== "" || vale !== ""){
                            cond["values"] = [vals,vale];
                        }
                        break;
                    }
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":{
                        if(val !== "") {
                            if(dataType==="4"){//日期不切
                                cond["values"] = [val];
                            }else {
                                cond["values"] = val.split(/\s+|\t+|\r+|\n+/);
                            }
                        }
                        break;
                    }
                    default:{
                        return;
                    }
                }

                if(key==="should"){
                    cond["groupId"]= group;
                    cond["groupType"]="should";
                }else if(key=== "not"){
                    cond["groupType"]="not";
                }else{
                    cond["groupType"]="must";
                }
                conditions[conditions.length] = cond;
            }
        });

        var indexName = $("#meta-index").val();
        var params = {"indexName":indexName,"conditions":[],"sort":"create_time desc"};
        //params["sort"]="";
        params["conditions"]=conditions;

        // $("#data-show-box").show();
        // _initListTable(params);
        return params;
    };

    // var _query = function(dataTable,params){
    //     var columns = [];
    //     columns[columns.length] = {field: 'checkbox',title: '选择',width:'50px',checkbox:true};
    //     columns[columns.length] = {field: 'xh',title: '序号',width:'50px'};
    //     columns[columns.length] =  {field: 'id',title: 'ID',visible:false};
    //     for (var i = 0; i < fieldList.length; i++) {
    //         var mm = fieldList[i];
    //         if (mm["fieldCode"] === "id") {
    //             continue;
    //         }
    //         columns[columns.length] =  {field: mm["fieldCode"],title: mm["fieldName"],formatter:formatter};
    //     }
    //     dataTable.myTable({
    //         columns:columns,
    //         ajax : function (request) {
    //             var sort = "create_time desc";
    //             if(request.data.sortName){
    //                 sort = request.data.sortName +" "+request.data.sortOrder;
    //             }
    //             params["sort"]=sort;
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
    //                         }
    //                         request.success({
    //                             rows : data,
    //                             total : msg.data.total
    //                         });
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
    //             if(val instanceof Array){
    //                 var s = "";
    //                 for(var i=0 ; i< val.length;i++){
    //                     var d = val[i];
    //                     s += formatterStr(d)+" ";
    //                 }
    //                 val = s;
    //             }
    //             if(val!==""){
    //                 var la = window.parent.layer || layer;
    //                 la.open({
    //                     // time: 2000, //不自动关闭
    //                     type: 1,
    //                     skin: 'layui-layer-rim', //加上边框
    //                     area: ['450px', '340px'], //宽高
    //                     content:  "<div style='padding: 5px; '>"+val+"</div>"
    //                 });
    //             }
    //         }
    //     });
    //
    // };
    //
    // var formatter = function (val) {
    //     if(val instanceof Array){
    //         return formatterList(val);
    //     }else {
    //         return formatterStr(val);
    //     }
    // };
    //
    // var formatterList = function (val){
    //     if(val){
    //         var s = "";
    //         for(var i=0 ; i< val.length && i<5;i++){
    //             s += val[i]+" ";
    //         }
    //         if(val.length >5){
    //             s += "...";
    //         }
    //         return s;
    //     }
    //     return val;
    // };
    // var formatterStr = function (d){
    //     if(d){
    //         var s = d;
    //         if(d.length >50 ){
    //             s = d.substring(0,50)+"...";
    //         }
    //         return s;
    //     }
    //     return d;
    // };

    var _fieldList = function () {
        return fieldList;
    }

    /**
     * 显示数据字段的下拉框
     * @private
     */
    var _showMetaIndexField = function () {
        $("#meta-index-field-label").show();
        $("#meta-index-field").css({"display": "inline"});
    }
    /**
     * 获取数据字段的下拉框的值
     * @private
     */
    var _getMetaIndexField = function () {
        return $("#meta-index-field").find("option:selected[value!='']");
    }
    /**
     * 获取数据源的值
     * @private
     */
    var _getMetaIndex = function () {
        return $("#meta-index").val();
    }

    return {
        init: _init,
        rest: _rest,
        condtion: _condtion,
        fieldList: _fieldList,
        showMetaIndexField: _showMetaIndexField,
        getMetaIndexField: _getMetaIndexField,
        getMetaIndex: _getMetaIndex,
        // query: _query
    };
})();


