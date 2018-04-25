/**
 * Created by hc.zeng on 2018/4/24.
 */
(function ($) {
    "use strict";
    var token = sessionStorage.getItem("token");
    //验证是否登录
    $.ajax({
        url:"/api/admin/authorization/validate",
        type:"post",
        dataType:"json",
        data:{"authorization":token},
        async:false,
        success:function (d) {
            console.log(d);
            if(d.status!==200){
                sessionStorage.clear();
                window.top.location.href="/index.html";
            }
        },
        error:function (d) {
            sessionStorage.clear();
            window.top.location.href="/index.html";
        }
    });


    var home = (function () {
        var _init = function () {

            $(".logout").click(_logout);

        };

        var _logout = function () {

            $.ajax({
                url:"/api/admin/authorization/logout",
                type:"post",
                dataType:"json",
                data:{"authorization":token},
                async:false,
                success:function (d) {
                    console.log(d);
                    if(d.status===200){
                        sessionStorage.clear();
                        window.top.location.href="/index.html";
                    }else {
                        toastrMsg.error(d.message);
                    }
                },
                error:function (d) {
                    toastrMsg.error("注销失败");
                }
            });


            return false;
        };


        return{
            init:_init
        };
    })();

    home.init();

})(jQuery);