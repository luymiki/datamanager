/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var zfbInfo;
    var user_id;
    var xcbh;
    var zfb = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
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
                            $("#zfb-title").html("支付宝账号 [ "+zfbInfo["user_id"]+" ] 的交易汇总");
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
            _initJyjeTable();
        };


        var _initJylsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyls",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        data = [msg.data];
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
                    {field: 'ljjybs',title: '累计交易笔数',sortable:true},
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
                    // {field: 'zzljjyje',title: '转账累计交易金额',sortable:true,formatter:formatter},
                    // {field: 'zzljjybs',title: '转账累计交易笔数',sortable:true},
                    // {field: 'zzzdjyje',title: '转账最大交易金额',sortable:true,formatter:formatter},
                    // {field: 'zzzxjyje',title: '转账最小交易金额',sortable:true,formatter:formatter},
                    // {field: 'zzljzrje',title: '转账累计转入金额',sortable:true,formatter:formatter},
                    // {field: 'zzljzrbs',title: '转账累计转入笔数',sortable:true},
                    // {field: 'zzzdzrje',title: '转账最大转入金额',sortable:true,formatter:formatter},
                    // {field: 'zzzxzrje',title: '转账最小转入金额',sortable:true,formatter:formatter},
                    // {field: 'zzljzcje',title: '转账累计转出金额',sortable:true,formatter:formatter},
                    // {field: 'zzljzcbs',title: '转账累计转出笔数',sortable:true},
                    // {field: 'zzzdzcje',title: '转账最大转出金额',sortable:true,formatter:formatter},
                    // {field: 'zzzxzcje',title: '转账最小转出金额',sortable:true,formatter:formatter},
                    // {field: 'zzpjjyje',title: '转账平均交易金额',sortable:true,formatter:formatter},
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
                data:{"userId":user_id,"xcbh":xcbh},
                success : function (msg) {
                    if(msg.status===200){
                        data = msg.data;
                        //console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                        }
                        $("#loadding-icon").hide();
                        $('#jyds-table').myTable({
                            sidePagination:'client',
                            columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
                                {field: 'dfId',title: '对手账号',sortable:true},
                                {field: 'ljjyje',title: '累计交易金额',sortable:true,formatter:formatter},
                                {field: 'ljjybs',title: '累计交易笔数',sortable:true},
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
                                // {field: 'pjjyje',title: '平均交易金额',sortable:true,formatter:formatter},
                                // {field: 'zzljjyje',title: '转账累计交易金额',sortable:true,formatter:formatter},
                                // {field: 'zzljjybs',title: '转账累计交易笔数',sortable:true},
                                // {field: 'zzzdjyje',title: '转账最大交易金额',sortable:true,formatter:formatter},
                                // {field: 'zzzxjyje',title: '转账最小交易金额',sortable:true,formatter:formatter},
                                // {field: 'zzljzrje',title: '转账累计转入金额',sortable:true,formatter:formatter},
                                // {field: 'zzljzrbs',title: '转账累计转入笔数',sortable:true},
                                // {field: 'zzzdzrje',title: '转账最大转入金额',sortable:true,formatter:formatter},
                                // {field: 'zzzxzrje',title: '转账最小转入金额',sortable:true,formatter:formatter},
                                // {field: 'zzljzcje',title: '转账累计转出金额',sortable:true,formatter:formatter},
                                // {field: 'zzljzcbs',title: '转账累计转出笔数',sortable:true},
                                // {field: 'zzzdzcje',title: '转账最大转出金额',sortable:true,formatter:formatter},
                                // {field: 'zzzxzcje',title: '转账最小转出金额',sortable:true,formatter:formatter},
                                // {field: 'zzpjjyje',title: '转账平均交易金额',sortable:true,formatter:formatter},
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
        var _initJyjeTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/zfb/jyje",
                type:"post",
                dataType:"json",
                data:{"userId":user_id,"xcbh":xcbh},
                success : function (msg) {
                    if(msg.status===200){
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
                        // var lineData = {
                        //     labels: labels,
                        //     datasets: [
                        //         {
                        //             label: "交易次数",
                        //             fillColor: "rgba(26,179,148,0.5)",
                        //             strokeColor: "rgba(26,179,148,0.7)",
                        //             pointColor: "rgba(26,179,148,1)",
                        //             pointStrokeColor: "#fff",
                        //             pointHighlightFill: "#fff",
                        //             pointHighlightStroke: "rgba(26,179,148,1)",
                        //             data: chartData,
                        //         }
                        //     ]
                        // };
                        //
                        // var lineOptions = {
                        //     scaleShowGridLines: true,
                        //     scaleGridLineColor: "rgba(0,0,0,.05)",
                        //     scaleGridLineWidth: 1,
                        //     bezierCurve: true,
                        //     bezierCurveTension: 0.4,
                        //     pointDot: true,
                        //     pointDotRadius: 4,
                        //     pointDotStrokeWidth: 1,
                        //     pointHitDetectionRadius: 20,
                        //     datasetStroke: true,
                        //     datasetStrokeWidth: 2,
                        //     datasetFill: true,
                        //     responsive: true,
                        //     events: ['click'],
                        //     onClick : function (event, bars){
                        //         alert(event)
                        //     },
                        // };
                        //
                        //
                        // var ctx = document.getElementById("lineChart").getContext("2d");
                        // var myNewChart = new Chart(ctx).Line(lineData, lineOptions);

                        var myChart = echarts.init(document.getElementById('lineChart'));
                        var option = {
                            // title: {
                            //     text: '各月数据增量',
                            //     subtext: '--'
                            // },
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
                            console.log(name);
                            console.log(data);
                            top.contabs.addMenuItem("/view/zfb/analyze/zfb-range-list.html?id="+zfbInfo["id"]+"&range="+name,'查看交易金额['+name+']流水信息');
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
        // var _event = function () {
        //     $("#data-table").on('click','.detail',function () {
        //         top.contabs.addMenuItem("/view/zfb/liushui/zfb-liushui-detail.html?id="+$(this).attr("data-id"),'查看流水信息');
        //     });
        //     $("#addBtn").on('click',function () {
        //         top.contabs.addMenuItem("/view/zfb/liushui/zfb-liushui.html?id="+zfbid,'导入支付宝流水信息');
        //     });
        //     $("#analyze-btn").on('click',function () {
        //         top.contabs.addMenuItem("/view/zfb/liushui/zfb-liushui-analyze.html?id="+zfbid,'支付宝流水信息分析');
        //     });
        //     $("#search-btn").on('click',function () {
        //         _search = $("#search-input").val();
        //         if(_search && $.trim(_search) !== ""){
        //             $('#data-table').bootstrapTable("refresh");
        //         }else {
        //             _search=null;
        //             $('#data-table').bootstrapTable("refresh");
        //         }
        //     });
        // };

        return {
            init:_init
        };
    })();

    zfb.init();
    reg.init();


})(document, window, jQuery);
