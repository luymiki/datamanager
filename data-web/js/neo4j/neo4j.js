/**
 * Created by hc.zeng on 2019/2/16.
 */
(function () {
    'use strict';

    /**
     * 注意：要修D3.js 文件的 initializeNodes方法
     * node.index = i;改为 node.index = node.id;
     * 否则会报错
     * 还要它下面新增一行代码，如果已经有坐标信息了，就不在赋值了
     * if (!isNaN(node.vx)) continue;
     *
     */


    var width = $("body").width() - 190;
    var height = $("body").height() - 80;
    var img_w = 20;
    var img_h = 20;
    var graph = $("#graph");
    var tips = $("#tips");
    var nodesData = [];
    var edgesData = [];
    var linksSvg;
    var nodeSvg;
    var nodesTxt;
    var forceSimulation;

    var neo4j = (function () {
        var _init = function () {
            tips.height(height);
            graph.height(height).width(width);
            _event();
        };
        var render = function (data) {
            //准备数据
            nodesData = data.nodes;
            edgesData = data.links;

            if (nodesData.length == 0) {
                toastrMsg.error("查询不到关系！");
                return false;
            }
            var nodes = nodesData;
            var edges = JSON.parse(JSON.stringify(edgesData));
            graph.children('svg').remove();
            var svg = d3.select("#graph").append("svg").attr("width", width).attr("height", height);
            var g = svg.append("g").attr("transform", "translate(0,0)");
                g.append("text")
                    .attr("fill","red")
                    .attr("transform", "translate(" + width/2 + "," + height/2 + ")")
                    .text("+").style("font-size",20);

            //设置一个color的颜色比例尺，为了让不同的扇形呈现不同的颜色
            var colorScale = d3.scaleOrdinal()
                .domain(d3.range(nodes.length))
                .range(d3.schemeCategory10);
            //箭头
            var marker =
                svg.append("marker")
                //.attr("id", function(d) { return d; })
                    .attr("id", "resolved")
                    //.attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("viewBox", "0 -4 10 10")//坐标系的区域
                    .attr("refX", 15)//箭头坐标
                    .attr("refY", 0)
                    .attr("markerWidth", 10)//标识的大小
                    .attr("markerHeight", 12)
                    .attr("orient", "auto")//绘制方向，可设定为：auto（自动确认方向）和 角度值
                    .attr("stroke-width", 1)//箭头宽度
                    .append("path")
                    .attr("d", "M0,-3L6,0L0,3")//箭头的路径
                    .attr('fill', '#aaa');//箭头颜色
            //创建图片
            var defs = svg.append("defs").attr("id", "imgdefs")
            initImg(defs, img_w, img_h);

            //新建一个力导向图
            forceSimulation = d3.forceSimulation()
                .force("link", d3.forceLink().distance(200))
                .force("charge", d3.forceManyBody().strength(-30))
                .force("center", d3.forceCenter())
                .force("collision", d3.forceCollide(10));

            //初始化力导向图，也就是传入数据
            //生成节点数据
            forceSimulation.nodes(nodes)
                .on("tick", ticked);//这个函数很重要，后面给出具体实现和说明
            //生成边数据
            forceSimulation.force("link")
                .links(edges)
                .distance(function (d) {//每一边的长度
                    return 100;
                })
            //设置图形的中心位置
            forceSimulation.force("center")
                .x(width / 2)
                .y(height / 2);
            // //在浏览器的控制台输出
            //console.log(nodes);
            //console.log(edges);

            //有了节点和边的数据后，我们开始绘制
            //绘制边
            linksSvg = g.append("g")
                .selectAll("line")
                .data(edges)
                .enter()
                .append("line")
                .attr("stroke", function (d, i) {
                    return "#aaa";
                })
                .attr("stroke-width", 1)
                .attr("marker-end", "url(#resolved)");//根据箭头标记的id号标记箭头;

            //绘制节点
            //老规矩，先为节点和节点上的文字分组
            nodeSvg = g.append("g").selectAll("circle").data(nodes)
                .enter()
                .append("g")
                .attr("class", "node")
                .attr("transform", function (d, i) {
                    var cirX = d.x;
                    var cirY = d.y;
                    return "translate(" + cirX + "," + cirY + ")";
                })
                .call(d3.drag()
                    .on("start", started)
                    .on("drag", dragged)
                    .on("end", ended)
                );

            //绘制节点
            nodeSvg.append("circle")
                .attr("class", "node")
                .attr("r", function (d) {
                    if (d.label === "PERSON") return 20;
                    return 10;
                })
                .attr("fill", function (d, i) {
                    switch (d.label) {
                        case "PERSON":
                            return "url(#" + icon.user + ")";
                        case "QQ_NODE":
                            return "url(#" + icon.qq + ")";
                            break;
                        case "WEIXIN_NODE":
                            return "url(#" + icon.wechat + ")";
                            break;
                        case "PHONE_NODE":
                            return "url(#" + icon.phone + ")";
                            break;
                        case "TENPLAY_NODE":
                            return "url(#" + icon.tenplay + ")";
                            break;
                        case "ALIPLAY_NODE":
                            return "url(#" + icon.aliplay + ")";
                            break;
                        case "YHZH_NODE":
                            return "url(#" + icon.yhzh + ")";
                            break;
                        case "IDCARD_NODE":
                            return "url(#" + icon.idcard + ")";
                            break;
                        case "IP_NODE":
                            return "url(#" + icon.ip + ")";
                            break;
                        case "EMAIL_NODE":
                            return "url(#" + icon.email + ")";
                            break;
                        default:
                            //image.attr("class", "fa fa-wechat");
                            break;
                    }
                    return colorScale(i);
                });
            //文字
            nodesTxt = nodeSvg.append("text")
                .attr("x", function (d) {
                    var l = d.name.length;
                    var t = (l + 1) * -4;
                    if (l < 3) return -10;
                    if (t < -70) return -75;
                    return t;
                })
                .attr("y", function (d) {
                    if (d.label === "PERSON") return -30;
                    return -20
                })
                .attr("dy", 10)
                .text(function (d) {
                    return d.name;
                }).attr("class", function (d) {
                    switch (d.label) {
                        case "PERSON":
                            return "PERSON";
                            break;
                        default:
                            return "default";
                            break;
                    }
                });

            //缩放
            var zoom = d3.zoom();
            svg.call(zoom
                .scaleExtent([1 / 3, 8])
                .on("zoom", zoomed)).on("dblclick.zoom", null);
            function zoomed() {
                g.attr("transform", d3.event.transform);
            }

            //节点单击事件
            nodeSvg.selectAll("circle[class=node]").on("click", function (d, i) {
                _tips(d);
            })
            //节点双击事件
            nodeSvg.selectAll("circle[class=node]").on("dblclick", function (d, i) {
                _loadNext(d);
            })
            function ticked() {
                linksSvg.attr("x1", function (d) {
                    return d.source.x;
                })
                    .attr("y1", function (d) {
                        return d.source.y;
                    })
                    .attr("x2", function (d) {
                        return d.target.x;
                    })
                    .attr("y2", function (d) {
                        return d.target.y;
                    });
                nodeSvg.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            }


        };

        function started(d) {
            if (!d3.event.active) {
                forceSimulation.alphaTarget(0.8).restart();
            }
            d.fx = d.x;
            d.fy = d.y;
        }

        function dragged(d) {
            d.fx = d3.event.x;
            d.fy = d3.event.y;
        }

        function ended(d) {
            if (!d3.event.active) {
                forceSimulation.alphaTarget(0);
            }
            // d.fx = null;
            // d.fy = null;
        }

        //动态添加节点
        function restart(data) {
            for(var i=0;i<data.nodes.length;i++){
                var n = data.nodes[i];
                var f = false;
                for(var j=0;j<nodesData.length;j++){
                    if(nodesData[j].attr.id===n.attr.id){
                        f =true;
                        break;
                    }
                }
                if(!f){
                    nodesData[nodesData.length]=data.nodes[i];
                }
            }
            for(var i=0;i<data.links.length;i++){
                var n = data.links[i];
                var f = false;
                for(var j=0;j<edgesData.length;j++){
                    if(edgesData[j].id===n.id){
                        f =true;
                        break;
                    }
                }
                if(!f){
                    edgesData[edgesData.length]=data.links[i];
                }

            }
            data.nodes =nodesData;
            data.links = edgesData;
            render(data);
        };

        var icon = {
            "user": "icon-user",
            "qq": "icon-qq",
            "wechat": "icon-wechat",
            "tenplay": "icon-tenplay",
            "aliplay": "icon-aliplay",
            "yhzh": "icon-yhzh",
            "idcard": "icon-idcard",
            "ip": "icon-ip",
            "phone": "icon-phone",
            "email": "icon-email"
        };
        var initImg = function (defs, img_w, img_h) {
            defs.append("pattern")
                .attr("id", "icon-user")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w * 2)
                .attr("height", img_h * 2)
                .attr("xlink:href", "/img/user.jpg");
            defs.append("pattern")
                .attr("id", "icon-qq")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/qq.jpg");
            defs.append("pattern")
                .attr("id", "icon-wechat")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/wechat.jpg");
            defs.append("pattern")
                .attr("id", "icon-tenplay")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/tenplay.jpg");
            defs.append("pattern")
                .attr("id", "icon-aliplay")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/aliplay.jpg");
            defs.append("pattern")
                .attr("id", "icon-yhzh")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/yhzh.jpg");
            defs.append("pattern")
                .attr("id", "icon-idcard")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/idcard.jpg");
            defs.append("pattern")
                .attr("id", "icon-ip")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/ip.jpg");
            defs.append("pattern")
                .attr("id", "icon-phone")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/phone.jpg");
            defs.append("pattern")
                .attr("id", "icon-email")
                .attr("height", 1)
                .attr("width", 1).append("image")
                .attr("x", -(img_w / 2 - 10))
                .attr("y", -(img_h / 2 - 10))
                .attr("width", img_w)
                .attr("height", img_h)
                .attr("xlink:href", "/img/email.jpg");
        }
        var _tips = function (data) {
            var tipsInfo = tips.find("#tips-info").html("");
            for (var k in data) {
                if (k === 'x' || k === 'y'
                    || k === 'vx' || k === 'vy'
                    || k === 'fx' || k === 'fy'
                    || k === 'index' || k === 'id' || k === 'name') {
                    continue;
                }
                if (k === "attr") {
                    var attr = data[k];
                    for (var a in attr) {
                        tipsInfo.append($('<div></div>').text(a + "\t : " + attr[a]))
                    }

                } else {
                    tipsInfo.append($('<div></div>').text(k + "\t : " + data[k]))
                }

            }

        };

        var query = function (data, call) {
            $.ajax.proxy({
                url: "/api/admin/neo4j/query",
                type: "post",
                dataType: "json",
                data: data,
                async: true,
                success: function (d) {
                    if (d.status === 200) {
                        toastrMsg.success("查询完成");
                        console.log(d);
                        call(d.data);
                    } else {
                        console.log(d);
                        toastrMsg.error("查询失败:" + d.message);
                    }
                },
                error: function (d) {
                    console.log(d);
                    top.toastrMsg.error("查询失败");
                }
            });
        }
        var _loadNext = function (d) {
            query({"type": d.label, "keyword": d.attr.id}, function (data) {
                console.log(data);
                restart(data);
            });
        }
        var _event = function () {
            $("#search-btn").click(function () {
                var keyword1 = $("#search-input1").val();
                var keyword2 = $("#search-input2").val();
                var type1 = $("#search-select1").val();
                var type2 = $("#search-select2").val();
                nodesData = [];
                edgesData = [];
                query({"type": type1, "type2": type2, "keyword": keyword1, "keyword2": keyword2}, render);
            });
        }
        return {
            init: _init,
            render: render
        };
    })();
    neo4j.init();

    //neo4j.render(testData);

})();

