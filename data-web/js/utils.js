/**
 * Created by hc.zeng on 2018/3/18.
 */
var utils = (function () {
    'use strict';

    /**
     * 获取url的参数列表
     * @returns {{}}
     * @private
     */
    var _getURLParams = function () {
        var url = window.location.href;
        url = url.split("?");
        var params = {};
        if (url.length === 2) {
            url = url[1].split("&");
            for (var i = 0; i < url.length; i++) {
                var p = url[i].split("=");
                params[p[0]] = p[1];
            }
        }
        return params;
    };

    var _getWidowHeight=function () {
        var h = document.body.offsetHeight;
        return h;
    };

    return {
        getURLParams: _getURLParams,
        getWidowHeight: _getWidowHeight
    };
})();