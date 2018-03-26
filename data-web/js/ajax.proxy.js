/**
 * Created by hc.zeng on 2018/3/18.
 */
$.ajax.proxy = function(option){
    var v_success ;
    if(option["success"]){
        v_success = option.success;
    }
    var _success = function(d){
        if(v_success){
            v_success(d);
        }
    };
    var v_error ;
    if(option["error"]){
        v_error = option.error;
    }

    $.ajax(option);
};