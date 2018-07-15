/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";

    var tjtb = (function () {


        var _init = function init(_data) {
             _initKyrsjl();
            _initPizhusjl();
            _initAllsjl();
            _initBysjl();
            _initSjlTable();
            // _initThsjdTable();
        };


        /**
         * 统计可疑人数据量
         * @private
         */
        var _initKyrsjl = function(){
            var params={"indexName":"suspicious","aggs": [{ "groupName":"count_id","field": "id","aggsType": 7} ],"sort":"create_time desc" };
            _aggs(params,function (data) {
                var aggs = data.aggs["count_id"];
                $("#kyr-count").html(aggs);
            });
        };

        /**
         * 统计批注数据量
         * @private
         */
        var _initPizhusjl = function(){
            var params={"indexName":"comment","aggs": [{ "groupName":"count_id","field": "id","aggsType": 7} ],"sort":"create_time desc" };
            _aggs(params,function (data) {
                var aggs = data.aggs["count_id"];
                $("#comment-count").html(aggs);
            });
        };

        /**
         * 统计总数据量
         * @private
         */
        var _initAllsjl = function(){
            var params={"indexName":"","aggs": [{ "groupName":"count_id","field": "id","aggsType": 7} ],"sort":"create_time desc" };
            _aggs(params,function (data) {
                var aggs = data.aggs["count_id"];
                $("#all-count").html(aggs);
            });
        };

        /**
         * 统计本月数据量
         * @private
         */
        var _initBysjl = function(){
            var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth()+1;
            var first = getCurrentMonthFirst().getDate();
            var last = getCurrentMonthLast().getDate();
            var start = year+"-"+(month<10?"0"+month:month)+"-"+(first<10?"0"+first:first)+" 00:00:00";
            var end = year+"-"+(month<10?"0"+month:month)+"-"+(last<10?"0"+last:last)+" 23:59:59";
            var params={"indexName":"","aggs": [{ "groupName":"count_id","field": "id","aggsType": 7}],
                "conditions":[{"field":"create_time","searchType":"6","dataType":"4",
                    "values":[start,end],
                    "groupType":"must"}],
                "sort":"create_time desc" };
            _aggs(params,function (data) {
                var aggs = data.aggs["count_id"];
                $("#by-count").html(aggs);
            });
        };

        function getCurrentMonthFirst(){
            var date=new Date();
            date.setDate(1);
            return date;
        }

        function getCurrentMonthLast(){
            var date=new Date();
            var currentMonth=date.getMonth();
            var nextMonth=++currentMonth;
            var nextMonthFirstDay=new Date(date.getFullYear(),nextMonth,1);
            var oneDay=1000*60*60*24;
            return new Date(nextMonthFirstDay-oneDay);
        }

        var _aggs = function (params,success) {
            $.ajax.proxy({
                url:"/api/eqa/aggs",
                type:"post",
                dataType:"json",
                data:{"pageNum":1,"pageSize":1000,"paramsStr":JSON.stringify(params)},
                async:true,
                success:function (d) {
                    //console.log(d);
                    if(d.status===200){
                        if(d.data.aggs){
                            success(d.data);
                        }
                    }

                },
                error:function (d) {
                    console.log(d);
                }
            });
        }
        //
        //     $('#data-table').myTable({
        //         height:120,
        //         pagination:false,
        //         columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
        //             {field: 'ddsl',title: '对端数量',sortable:true},
        //             {field: 'zthcs',title: '总通话次数',sortable:true},
        //             {field: 'zthsc',title: '总通话时长',sortable:true},
        //             {field: 'zcthsc',title: '最长通话时长',sortable:true},
        //             {field: 'zdthsc',title: '最短通话时长',sortable:true},
        //             {field: 'zjcs',title: '主叫次数',sortable:true},
        //             {field: 'zjthsc',title: '主叫总通话时长',sortable:true},
        //             {field: 'zjzcthsc',title: '主叫最长通话时长',sortable:true},
        //             {field: 'zjzdthsc',title: '主叫最短通话时长',sortable:true},
        //             {field: 'bjcs',title: '被叫次数',sortable:true},
        //             {field: 'bjthsc',title: '被叫总通话时长',sortable:true},
        //             {field: 'bjzcthsc',title: '被叫最长通话时长',sortable:true},
        //             {field: 'bjzdthsc',title: '被叫最短通话时长',sortable:true},
        //             {field: 'qtcs',title: '其他通讯次数',sortable:true}
        //         ],
        //         data : data
        //     });
        // };
        // /**
        //  * 统计对端信息
        //  * @private
        //  */
        // var _initDdxxTable = function(){
        //     var data = [];
        //     var cftzh = huadan["zh"];
        //     $.ajax.proxy({
        //         url:"/api/admin/fx/huadan/hdds",
        //         type:"post",
        //         dataType:"json",
        //         data:{"hdId":hdId,"cftzh":cftzh},
        //         success : function (msg) {
        //             if(msg.status===200){
        //                 data = msg.data;
        //                 console.log(data)
        //                 var xh =  1;
        //                 for(var i= 0;i<data.length;i++){
        //                     data[i]['xh'] = xh++;
        //                 }
        //                 $("#loadding-icon").hide();
        //                 $('#jyds-table').myTable({
        //                     sidePagination:'client',
        //                     columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
        //                         {field: 'ddhm',title: '对端号码',sortable:true},
        //                         {field: 'zzthsj',title: '最早通话时间',sortable:true},
        //                         {field: 'zwthsj',title: '最晚通话时间',sortable:true},
        //                         {field: 'zthcs',title: '总通话次数',sortable:true},
        //                         {field: 'zthsc',title: '总通话时长',sortable:true},
        //                         {field: 'zcthsc',title: '最长通话时长',sortable:true},
        //                         {field: 'zdthsc',title: '最短通话时长',sortable:true},
        //                         {field: 'zjcs',title: '主叫次数',sortable:true},
        //                         {field: 'zjthsc',title: '主叫总通话时长',sortable:true},
        //                         {field: 'zjzcthsc',title: '主叫最长通话时长',sortable:true},
        //                         {field: 'zjzdthsc',title: '主叫最短通话时长',sortable:true},
        //                         {field: 'bjcs',title: '被叫次数',sortable:true},
        //                         {field: 'bjthsc',title: '被叫总通话时长',sortable:true},
        //                         {field: 'bjzcthsc',title: '被叫最长通话时长',sortable:true},
        //                         {field: 'bjzdthsc',title: '被叫最短通话时长',sortable:true}
        //                     ],
        //                     data : data
        //                 });
        //             }
        //         },
        //         error:function(){
        //             toastrMsg.error("系统错误");
        //         }
        //     });
        //
        //
        // };
        /**
         * 统计各月的数据量
         * @private
         */
        var _initSjlTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/tjtb/cjsj",
                type:"post",
                dataType:"json",
                data:{},
                success : function (msg) {
                    if(msg.status===200){
                        data = msg.data["group_createTime"];
                        var rangeList = msg.data["rangeList"];
                        //console.log(data)
                        $("#loadding-icon-jyje").hide();
                        var labels = [];
                        var chartData = [];

                        for(var i=0;i<rangeList.length;i++){
                            var range = rangeList[i];
                            var key = range[0];
                            for(var j=0;j<data.length;j++){
                                var group = data[j];
                                var sp= group["from_as_string"];
                                if(key === sp) {
                                    var spl= sp.split("-");
                                    var from = spl[0]+"-"+spl[1];
                                    labels[labels.length] = from;
                                    chartData[chartData.length] = group["doc_count"];
                                    break;
                                }
                            }

                        }

                        // var lineData = {
                        //     labels: labels,
                        //     datasets: [
                        //         {
                        //             label: "通话时长",
                        //             fillColor: "rgba(26,179,148,0.5)",
                        //             strokeColor: "rgba(26,179,148,0.7)",
                        //             pointColor: "rgba(26,179,148,1)",
                        //             pointStrokeColor: "#fff",
                        //             pointHighlightFill: "#fff",
                        //             pointHighlightStroke: "rgba(26,179,148,1)",
                        //             data: chartData
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
                        // };
                        //
                        //
                        // var ctx = document.getElementById("cjsj-dashboard-chart").getContext("2d");
                        // var myNewChart = new Chart(ctx).Line(lineData, lineOptions);
                        // 使用
                        // 基于准备好的dom，初始化echarts图表
                        var myChart = echarts.init(document.getElementById('cjsj-dashboard-chart'));
                        var option = {
                            // title: {
                            //     text: '各月数据增量',
                            //     subtext: '--'
                            // },
                            //边距
                            grid: {
                                left: '0',
                                right: '25',
                                bottom: '0',
                                containLabel: true
                            },
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                data: ['数据量']
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
                                    name: '数据量',
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
                        // myChart.on('click', function (params) {
                        //     console.log(params);
                        // });

                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });


        };
        /**
         * 统计通话时间点
         * @private
         */
        var _initThsjdTable = function(){

            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/huadan/thsjd",
                type:"post",
                dataType:"json",
                data:{"hdId":hdId},
                success : function (msg) {
                    if(msg.status===200){
                        data = msg.data["group_kssj_hh"];
                        //console.log(data)
                        $("#loadding-icon-thsjd").hide();
                        var labels = [];
                        var chartData = [];
                        for(var i=0;i< 24; i++){
                            labels[i] = i<10?"0"+i:""+i;
                            chartData[i] = 0;
                        }
                        for(var j=0;j<data.length;j++){
                            var group = data[j];
                            var lab = group["key"];
                            for(i=0;i< 24; i++){
                                if(labels[i] === lab){
                                    chartData[i] = group["doc_count"];
                                    break;
                                }
                            }
                        }
                        var lineData = {
                            labels: labels,
                            datasets: [
                                {
                                    label: "通话时间点",
                                    fillColor: "rgba(26,179,148,0.5)",
                                    strokeColor: "rgba(26,179,148,0.7)",
                                    pointColor: "rgba(26,179,148,1)",
                                    pointStrokeColor: "#fff",
                                    pointHighlightFill: "#fff",
                                    pointHighlightStroke: "rgba(26,179,148,1)",
                                    data: chartData
                                }
                            ]
                        };

                        var lineOptions = {
                            scaleShowGridLines: true,
                            scaleGridLineColor: "rgba(0,0,0,.05)",
                            scaleGridLineWidth: 1,
                            bezierCurve: true,
                            bezierCurveTension: 0.4,
                            pointDot: true,
                            pointDotRadius: 4,
                            pointDotStrokeWidth: 1,
                            pointHitDetectionRadius: 20,
                            datasetStroke: true,
                            datasetStrokeWidth: 2,
                            datasetFill: true,
                            responsive: true,
                        };

                        var ctx = document.getElementById("lineChart-thsjd").getContext("2d");
                        var myNewChart = new Chart(ctx).Line(lineData, lineOptions);
                    }
                },
                error:function(){
                    toastrMsg.error("系统错误");
                }
            });


        };
        return {
            init:_init
        };
    })();

    tjtb.init();


})(document, window, jQuery);
