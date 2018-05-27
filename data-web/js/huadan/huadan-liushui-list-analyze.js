/**
 * Created by hc.zeng on 2018/3/21.
 */
(function (e, t, $) {
    "use strict";
    var huadan;
    var cft = (function () {
        var _init = function () {
            var params = utils.getURLParams();
            var id = params["id"];
            _get(id);
        }
        var params = {"indexName":"huaduan","conditions":[],"sort":"create_time desc"};

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
                            huadan =file[0];
                            $("#cft-title").html("电话 [ "+huadan["zjhm"]+" ] 的通话记录汇总");
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


        var hdId ;
        var _init = function init(_data) {
            var params = utils.getURLParams();
            hdId = params["id"];
            _initThlsTable();
            _initDdxxTable();
            _initThscTable();
            _initThsjdTable();
        };


        /**
         * 通话流水
         * @private
         */
        var _initThlsTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/huadan/hdls",
                type:"post",
                dataType:"json",
                data:{"hdId":hdId},
                async:false,
                success : function (msg) {
                    if(msg.status===200){
                        data = [msg.data];
                        console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
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
                    {field: 'ddsl',title: '对端数量',sortable:true},
                    {field: 'zthcs',title: '总通话次数',sortable:true},
                    {field: 'zthsc',title: '总通话时长',sortable:true},
                    {field: 'zcthsc',title: '最长通话时长',sortable:true},
                    {field: 'zdthsc',title: '最短通话时长',sortable:true},
                    {field: 'zjcs',title: '主叫次数',sortable:true},
                    {field: 'zjthsc',title: '主叫总通话时长',sortable:true},
                    {field: 'zjzcthsc',title: '主叫最长通话时长',sortable:true},
                    {field: 'zjzdthsc',title: '主叫最短通话时长',sortable:true},
                    {field: 'bjcs',title: '被叫次数',sortable:true},
                    {field: 'bjthsc',title: '被叫总通话时长',sortable:true},
                    {field: 'bjzcthsc',title: '被叫最长通话时长',sortable:true},
                    {field: 'bjzdthsc',title: '被叫最短通话时长',sortable:true},
                    {field: 'qtcs',title: '其他通讯次数',sortable:true}
                ],
                data : data
            });
        };
        /**
         * 统计对端信息
         * @private
         */
        var _initDdxxTable = function(){
            var data = [];
            var cftzh = huadan["zh"];
            $.ajax.proxy({
                url:"/api/admin/fx/huadan/hdds",
                type:"post",
                dataType:"json",
                data:{"hdId":hdId,"cftzh":cftzh},
                success : function (msg) {
                    if(msg.status===200){
                        data = msg.data;
                        console.log(data)
                        var xh =  1;
                        for(var i= 0;i<data.length;i++){
                            data[i]['xh'] = xh++;
                        }
                        $("#loadding-icon").hide();
                        $('#jyds-table').myTable({
                            sidePagination:'client',
                            columns: [{field: 'xh',title: '序号',width:'50px',sortable:true},
                                {field: 'ddhm',title: '对端号码',sortable:true},
                                {field: 'zzthsj',title: '最早通话时间',sortable:true},
                                {field: 'zwthsj',title: '最晚通话时间',sortable:true},
                                {field: 'zthcs',title: '总通话次数',sortable:true},
                                {field: 'zthsc',title: '总通话时长',sortable:true},
                                {field: 'zcthsc',title: '最长通话时长',sortable:true},
                                {field: 'zdthsc',title: '最短通话时长',sortable:true},
                                {field: 'zjcs',title: '主叫次数',sortable:true},
                                {field: 'zjthsc',title: '主叫总通话时长',sortable:true},
                                {field: 'zjzcthsc',title: '主叫最长通话时长',sortable:true},
                                {field: 'zjzdthsc',title: '主叫最短通话时长',sortable:true},
                                {field: 'bjcs',title: '被叫次数',sortable:true},
                                {field: 'bjthsc',title: '被叫总通话时长',sortable:true},
                                {field: 'bjzcthsc',title: '被叫最长通话时长',sortable:true},
                                {field: 'bjzdthsc',title: '被叫最短通话时长',sortable:true}
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
         * 统计通话时长
         * @private
         */
        var _initThscTable = function(){
            var data = [];
            $.ajax.proxy({
                url:"/api/admin/fx/huadan/thsc",
                type:"post",
                dataType:"json",
                data:{"hdId":hdId},
                success : function (msg) {
                    if(msg.status===200){
                        data = msg.data["group_thsc"];
                        console.log(data)
                        $("#loadding-icon-jyje").hide();
                        var labels = [];
                        var chartData = [];
                        for(var i=0;i<data.length;i++){
                            var group = data[i];
                            labels[labels.length] = group["key"];
                            chartData[chartData.length] = group["doc_count"];
                        }
                        var lineData = {
                            labels: labels,
                            datasets: [
                                {
                                    label: "通话时长",
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


                        var ctx = document.getElementById("lineChart").getContext("2d");
                        var myNewChart = new Chart(ctx).Line(lineData, lineOptions);
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
                        console.log(data)
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

    cft.init();
    reg.init();


})(document, window, jQuery);
