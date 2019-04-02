/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var zfbInfo;
    var user_id;
    var xcbh;
    var zcType;

    var title;
    var hzid;
    var qjfbid;
    var jefbid;
    var dshzid;

    var zfb = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            zcType = params["zcType"];
            _get(id);
        }
        var params = {"indexName":"zfbreginfo","conditions":[],"sort":"create_time desc"};

        var _get = function (id) {
            params["conditions"]=[{
                "field": "id",
                "values": [id],
                "searchType": 1,
                "dataType":2,
            }];
            $.ajax.proxy({
                url:"/api/eqa/query",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1,"paramsStr":JSON.stringify(params)},
                async:false,
                success : function (d) {
                    console.log(d);
                    if(d.status===200){
                        var file = d.data.data;
                        if(file && file.length===1){
                            zfbInfo =file[0];
                            user_id = zfbInfo["user_id"];
                            xcbh = zfbInfo["xcbh"];
                            title ="支付宝账号 [ "+zfbInfo["user_id"]+" ] 的交易汇总";
                            $("#zfb-title").html(title);
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

    var reg = (function () {


        var _init = function init(_data) {
            _initJylsTable();
            _initJydsTable();
            _initJyjeLineChart();
            if(zcType){
                $("#chart-line").removeClass("col-sm-8");
                $("#chart-pie").hide();
            }else {
                _initJyjePieChart();
            }
            _event();
        };


        var _integrated = function () {
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/integrated",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        toastrMsg.success("整合完成");
                    }else {
                        toastrMsg.success("整合失败");
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });

        }

        /**
         * 统计总体交易情况
         * @private
         */
        var _initJylsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyls",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh,zcType:zcType||""},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        data = [msg.data];
                        hzid = msg.data.id;
                        //console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                            data[i]['name'] = zfbInfo["name"];
                        }
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });

            $('#data-table').myTable({
                height:120,
                pagination:false,
                columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
                    {field: 'name',title: '户名',sortable:true},
                    {field: 'ljjyje',title: '累计交易金额',sortable:true,formatter:formatter},
                    {field: 'ljjybs',title: '累计交易笔数',sortable:true,formatter:formatterJybs},
                    {field: 'zdjyje',title: '最大交易金额',sortable:true,formatter:formatter},
                    {field: 'zxjyje',title: '最小交易金额',sortable:true,formatter:formatter},
                    {field: 'ljzrje',title: '累计转入金额',sortable:true,formatter:formatter},
                    {field: 'ljzrbs',title: '累计转入笔数',sortable:true},
                    {field: 'zdzrje',title: '最大转入金额',sortable:true,formatter:formatter},
                    {field: 'zxzrje',title: '最小转入金额',sortable:true,formatter:formatter},
                    {field: 'ljzcje',title: '累计转出金额',sortable:true,formatter:formatter},
                    {field: 'ljzcbs',title: '累计转出笔数',sortable:true},
                    {field: 'zdzcje',title: '最大转出金额',sortable:true,formatter:formatter},
                    {field: 'zxzcje',title: '最小转出金额',sortable:true,formatter:formatter},
                    {field: 'pjjyje',title: '平均交易金额',sortable:true,formatter:formatter},
                    {field: 'zzjysj',title: '最早交易时间',sortable:true},
                    {field: 'zwjysj',title: '最晚交易时间',sortable:true}
                ],
                data : data
            });
        };
        /**
         * 统计交易对手
         * @private
         */
        var _initJydsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyds",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh,zcType:zcType||""},
                success : function (msg) {
                    if(msg.status===200){
                        dshzid = msg.data.id;
                        data = msg.data.data;
                        //console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                        }
                        $("#loadding-icon").hide();
                        $('#jyds-table').myTable({
                            sidePagination:'client',
                            columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
                                {field: 'dfId',title: '对手账号',sortable:true,formatter:formatterJyds},
                                {field: 'ljjyje',title: '累计交易金额',sortable:true,formatter:formatter},
                                {field: 'ljjybs',title: '累计交易笔数',sortable:true,formatter:formatterJydsJybs},
                                {field: 'zdjyje',title: '最大交易金额',sortable:true,formatter:formatter},
                                {field: 'zxjyje',title: '最小交易金额',sortable:true,formatter:formatter},
                                {field: 'ljzrje',title: '累计转入金额',sortable:true,formatter:formatter},
                                {field: 'ljzrbs',title: '累计转入笔数',sortable:true},
                                {field: 'zdzrje',title: '最大转入金额',sortable:true,formatter:formatter},
                                {field: 'zxzrje',title: '最小转入金额',sortable:true,formatter:formatter},
                                {field: 'ljzcje',title: '累计转出金额',sortable:true,formatter:formatter},
                                {field: 'ljzcbs',title: '累计转出笔数',sortable:true},
                                {field: 'zdzcje',title: '最大转出金额',sortable:true,formatter:formatter},
                                {field: 'zxzcje',title: '最小转出金额',sortable:true,formatter:formatter},
                                {field: 'zzjysj',title: '最早交易时间',sortable:true},
                                {field: 'zwjysj',title: '最晚交易时间',sortable:true}
                            ],
                            data : data
                        });
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });


        };
        /**
         * 统计交易金额
         * @private
         */
        var _initJyjeLineChart = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyje",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh,zcType:zcType||""},
                success : function (msg) {
                    if(msg.status===200){
                        qjfbid = msg.data.id;
                        data = msg.data["group_jyje"];
                        //console.log(data)
                        $("#loadding-icon-jyje").hide();
                        var labels = [];
                        var chartData = [];
                        for(var i=0;i<data.length;i++){
                            var group = data[i];
                            labels[labels.length] = group["key"];
                            chartData[chartData.length] = group["doc_count"];
                        }

                        var myChart = echarts.init(document.getElementById('lineChart'));
                        var option = {
                            title: {
                                text: '交易金额区间分布'
                            },
                            //边距
                            grid: {
                                left: '0',
                                right: '30',
                                bottom: '0',
                                containLabel: true
                            },
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                data: ['交易次数']
                            },
                            toolbox: {
                                show: true,
                                feature: {
                                    //mark: {show: true},
                                    dataView: {show: true, readOnly: false},
                                    magicType: {show: true, type: ['line', 'bar']},
                                    restore: {show: true},
                                    saveAsImage: {show: true}
                                }
                            },
                            calculable: true,
                            xAxis: [
                                {
                                    type: 'category',
                                    boundaryGap: false,
                                    data: labels
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            series: [
                                {
                                    name: '交易次数',
                                    type: 'line',
                                    smooth: true,
                                    itemStyle: {
                                        normal: {
                                            color:"rgba(26,179,148,1)",
                                            borderColor:"rgba(26,179,148,1)",
                                            lineStyle: {type: 'solid',color:"rgba(26,179,148,0.5)"},
                                            areaStyle: {type: 'default',color:"rgba(26,179,148,0.5)"}
                                        }},
                                    data: chartData
                                }
                            ]
                        };
                        // 为echarts对象加载数据
                        myChart.setOption(option);
                        myChart.on('click', function (params) {
                            var data = params["data"];
                            var name = params["name"];
                            top.contabs.addMenuItem("/view/zfb/analyze/zfb-range-list.html?id="+zfbInfo["id"]+"&range="+name+"&zcType="+(zcType||""),'查看交易金额['+name+']流水信息');
                        });

                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });


        };
        /**
         * 统计被100整除交易金额饼状图
         * @private
         */
        var _initJyjePieChart = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyjeZc100",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh,zcType:zcType||""},
                success : function (msg) {
                    if(msg.status===200){
                        jefbid =  msg.data.id;
                        var nzc100 = msg.data["nzc100"]["group_zc0"];
                        var zc100 = msg.data["zc100"]["group_zc0"];
                        //console.log(msg)
                        $("#loadding-icon-jyjeZc100").hide();
                        var labels = ["被100整除","不能被100整除"];
                        var chartData = [
                            {"name":"被100整除","value":(zc100||0)},
                            {"name":"不能被100整除","value":(nzc100||0)}
                        ];

                        var myChart = echarts.init(document.getElementById('pieChart'));
                        var option = {
                            title: {
                                text: '交易金额区间分布'
                            },
                            tooltip: {
                                trigger: 'item',
                                formatter: "{a} <br/>{b} : {c} ({d}%)"
                            },
                            legend: {
                                data: labels,
                                type: 'scroll',
                                orient: 'vertical',
                                right: 10,
                                top: 50,
                                bottom: 20,
                            },

                            toolbox: {
                                show: true,
                                feature: {
                                    dataView: {show: true, readOnly: false},
                                    restore: {show: true},
                                    saveAsImage: {show: true}
                                }
                            },
                            calculable: true,

                            series: [
                                {
                                    name: '交易次数',
                                    type: 'pie',
                                    radius : '55%',
                                    center: ['40%', '50%'],
                                    emphasis: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)',
                                    },
                                    data: chartData
                                }
                            ]
                        };
                        // 为echarts对象加载数据
                        myChart.setOption(option);
                        myChart.on('click', function (params) {
                            var data = params["data"];
                            var name = params["name"];
                            //console.log(name);
                            //console.log(data);
                            var type = 100;
                            if("被100整除"!== name){
                                type = -100;
                            }
                            top.contabs.addMenuItem("/view/zfb/analyze/zfb-analyze.html?id="+zfbInfo["id"]+"&zcType="+type,'查看['+name+']的流水信息');
                        });
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });


        };
        var formatter = function (val) {
            return val === undefined || val=== null? val :val.toFixed(2);
        }
        var formatterJyds = function (val) {
            return val === undefined || val=== null ? val :"<a class='jyds' data-dsid='"+val+"'>"+val+"</a>";
        }
        var formatterJydsJybs = function (val,o) {
            return val === undefined || val=== null ? val :"<a class='jybs' data-dsid='"+o["dfId"]+"'>"+val+"</a>";
        }
        var formatterJybs = function (val) {
            return val === undefined || val=== null ? val :"<a class='jybs'>"+val+"</a>";
        }
        var exportExcel = function () {
            var from = $('<form method="post" action="/api/admin/fx/cft/exportExcel" target="_blank"></form>').appendTo('body');
            $('<input type="text" name="title">').val(title).appendTo(from);
            $('<input type="text" name="hzid">').val(hzid).appendTo(from);
            $('<input type="text" name="qjfbid">').val(qjfbid).appendTo(from);
            $('<input type="text" name="jefbid">').val(jefbid).appendTo(from);
            $('<input type="text" name="dshzid">').val(dshzid).appendTo(from);
            from.submit().remove();
        }
        var _event = function () {
            $("#integrated").on('click',_integrated);
            $("#jyds-table").on('click',".jyds",function () {
                var dsid = $(this).attr("data-dsid");
                top.contabs.addMenuItem("/view/zfb/analyze/zfb-jyds.html?id="+zfbInfo["id"]+"&ds_id="+dsid+"&zcType="+(zcType||""),'查看对手['+dsid+']流水信息');
            });
            $("#jyds-table").on('click',".jybs",function () {
                var dsid = $(this).attr("data-dsid");
                top.contabs.addMenuItem("/view/zfb/analyze/zfb-range-list.html?id="+zfbInfo["id"]+"&ds_id="+dsid+"&zcType="+(zcType||""),'查看['+dsid+']交易流水信息');
            });
            $("#data-table").on('click',".jybs",function () {
                top.contabs.addMenuItem("/view/zfb/analyze/zfb-range-list.html?id="+zfbInfo["id"]+"&zcType="+(zcType||""),'查看交易流水信息');
            });
            $("#exportExcel").on('click',exportExcel);
        };

        return {
            init:_init
        };
    })();

    zfb.init();
    reg.init();


})(document, window, jQuery);
