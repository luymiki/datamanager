/**
 * Created by hc.zeng on 2018/3/18.
 */

$.ajax.proxy = function(option){
    'use strict';

    var token = sessionStorage.getItem("token");
    option["headers"] = {
        "Authorization":token
    };
    var v_success ;
    if(option["success"]){
        v_success = option.success;
    }
    var _success = function(d){
        if(d && d["status"]===401){
            window.top.location.href="/index.html";
        }
        if(v_success){
            v_success(d);
        }
    };
    option["success"] = _success;

    var v_error ;
    if(option["error"]){
        v_error = option.error;
    }
    var _error = function(d){
        if(v_error){
            v_error(d);
        }
    };
    option["error"] = _error;
    $.ajax(option);
};