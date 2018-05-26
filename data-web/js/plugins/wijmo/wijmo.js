/**
 * Created by hc.zeng on 2018/4/28.
 */
(function($) {
    "use strict";

    var wijmoGrid = (function () {
        var elment;
        var options;
        var sourceData;
        var flexGrid;
        var columns;
        var rowHeight;
        var total =0;
        var request = {success:_success};
        var _init = function (_elment,_options) {
            options = _options;
            elment = _elment;

            if(options["columns"]){
                columns = options["columns"];
            }else {
                columns = [];
            }

            if(options["rowHeight"]){
                rowHeight = options["rowHeight"];
            }

            for(var i=0;i <columns .length;i++){
                if(columns[i]["isReadOnly"]===false){
                    columns[i]["isReadOnly"]  = false;
                }else {
                    columns[i]["isReadOnly"]  = true;
                }
            }

            flexGrid = new wijmo.grid.FlexGrid("#suspicious-table",{
                autoGenerateColumns: false,
                columns:columns,
                frozenColumns:options["frozenColumns"]||0,
                frozenRows:options["frozenRows"]||0,
                //     [
                //     { header: 'Country', binding: 'country', width: '*' },
                //     { header: 'Date', binding: 'date' },
                //     { header: 'Revenue', binding: 'amount', format: 'n0' },
                //     { header: 'Active', binding: 'active' },
                // ],
                //itemsSource: []
                formatItem:function(s, e){
                    //e.panel !== s.cells &&
                    if (wijmo.grid.CellType.ColumnHeader === e.panel.cellType) {
                        if (e.cell.innerHTML === '序号' ) {
                            e.cell.innerHTML = "<input type='checkbox' class='check-all wj-cell-check'/>选择";
                        }
                    }

                    if (wijmo.grid.CellType.RowHeader === e.panel.cellType) {
                        var item = s.rows[e.row].dataItem;
                        if (s.columns[e.col].binding === 'xh' ) {
                            e.cell.innerHTML = '<div class="v-center">' + item["xh"] + '</div>';
                        }
                        // console.log(e.cell.innerHTML);
                    }

                    if (wijmo.grid.CellType.Cell === e.panel.cellType) {
                        if (s.columns[e.col].binding === 'xh' ) {
                            var item2 = s.rows[e.row].dataItem;
                            e.cell.innerHTML = "<div class='v-center  h-center'><input type='checkbox' class='check-item wj-cell-check'/></div>";
                        }else {
                            if(options["formatItem"]){
                                options["formatItem"](s, e);
                            }
                            if (s.columns[e.col].binding === 'opt' ) {
                                e.cell.innerHTML = '<div class="v-center opt">'+e.cell.innerHTML+'</div>';
                            }else {
                                e.cell.innerHTML = '<div class="v-center">'+e.cell.innerHTML+'</div>';
                            }
                        }
                    }


                },
                //headersVisibility: wijmo.grid.HeadersVisibility.All,
                loadedRows: function(s,e) { // apply cssClass to rows after loading them
                    if(options["loadedRows"]){
                        options["loadedRows"](s,e);
                    }
                    // for (var i = 0; i < s.rows.length; i++) {
                    //     var row = s.rows[i];
                    //     var item = row.dataItem;
                    //     flexGrid.RowHeaders[i, 0] = item.xh;
                    // }
                }
            });
            flexGrid.rows.defaultSize = rowHeight;
            if(options["data"]){
                sourceData = options["data"];
            }else if(options["ajax"]){
                request = {success:_success};
                options["ajax"](request);
            }
            flexGrid.addEventListener(flexGrid.hostElement, 'dblclick', function(e) {
                var ht = flexGrid.hitTest(e);
                if(ht.col>0 && ht.cellType === wijmo.grid.CellType.Cell ){
                    if(ht.panel.columns[ht.col].binding === 'opt'){
                        return false;
                    }
                    if(options["dblclick"]){
                        options["dblclick"](e,ht);
                    }
                }
                // var logText = wijmo.format('panel <b>{cellType}</b> row <b>{row}</b> col <b>{col}</b>', {
                //     cellType: wijmo.grid.CellType[ht.cellType],
                //     row: ht.row,
                //     col: ht.col
                // });
                return true;
            });
            flexGrid.addEventListener(flexGrid.hostElement, 'click', function(e) {
                var ht = flexGrid.hitTest(e);
                if(ht.col===0 && ht.cellType === wijmo.grid.CellType.ColumnHeader ){
                    if(ht.panel.columns[ht.col].binding === 'xh'){
                        $(e.toElement).val("true").attr("checked",true);
                        console.log($(e.toElement).val());
                    }
                }
            });

        };

        var _success = function (data) {
            sourceData = data["rows"];
            total = data["total"];
            var cv = new wijmo.collections.CollectionView(sourceData);
            flexGrid.itemsSource = cv;
            //flexGrid.autoSizeRows(0,cv.length);
        };

        var _checkedEvent = function () {
            $(elment).on("clicl","check-all",function (e) {
                alert();
            });
        }

        return {
            init :_init
        };
    })();


    /**
     * The plugin is added to the jQuery library
     * @param {Object} options -  an object that holds some basic customization values
     */
    $.fn.extend({
        wijmoGrid: function(options) {
            var defaults = {
                autoGenerateColumns: false,
                columns:[],
                itemsSource:[],
                formatItem: function () {}
            }
            var options =  $.extend(defaults, options);

            // options.fixed = true;
            // options.overflow = false;
            // switch(options.resizeMode){
            //     case 'flex': options.fixed = false; break;
            //     case 'overflow': options.fixed = false; options.overflow = true; break;
            // }

            return this.each(function() {
                wijmoGrid.init( this, options);
            });
        }
    });

})(jQuery);