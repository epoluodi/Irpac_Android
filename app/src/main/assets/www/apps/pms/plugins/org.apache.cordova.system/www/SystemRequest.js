//fengxf added begin:
cordova.define("com.suypower.stereo.Irpac.CordovaPlugin.system.SystemRequest", function(require,exports,module){

    var exec = require('cordova/exec');

    module.exports = {	
		/** 
		 * 一共5个参数 
		   第一个 :成功回调 
		   第二个 :失败回调 
		   第三个 :将要调用的类的配置名字
		   第四个 :调用的方法名(一个类里可能有多个方法 靠这个参数区分) 
		   第五个 :传递的参数  以json的格式 
		 */


        //显示web alert
        dialog: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "dialog", [jsonparams]);
        },
        //显示info提示框
        info: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
            exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "info", [jsonparams]);
        },
        //设置web标题信息
        title: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "title", [jsonparams]);
        },
        //设置navbar控制
        navbar: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "navbar", [jsonparams]);
        },
        //是否支持web刷新
        webrefresh: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "webrefresh", [jsonparams]);
        },
        //得到账号信息
        getLoginUser: function(getRequestHeaderCallBackSuccess) {

           exec(getRequestHeaderCallBackSuccess, null, "SystemRequest", "getLoginUser", [null]);
        },
        //得到APP信息
        getAppInfo: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "SystemRequest", "getAppInfo", [jsonparams]);
        },
        //android toast
        toast: function(jsonparams) {
           exec(null, null, "SystemRequest", "toast", [jsonparams]);
        },


    };

});
//fengxf added end


