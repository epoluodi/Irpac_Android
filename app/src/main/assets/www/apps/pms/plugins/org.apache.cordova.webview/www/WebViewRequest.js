//fengxf added begin:
cordova.define("com.suypower.stereo.Irpac.CordovaPlugin.webview.WebViewRequest", function(require,exports,module){

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



        //打开新窗口
        open: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
           exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WebViewRequest", "open", [jsonparams]);
        },
        //web后退
        goback: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WebViewRequest", "goback", [jsonparams]);
        },
        //div跳转
        goback2: function(jsonparams,getRequestHeaderCallBackSuccess,getRequestHeaderCallBackFail) {
                     exec(getRequestHeaderCallBackSuccess, getRequestHeaderCallBackFail, "WebViewRequest", "goback2", [jsonparams]);
        },
        //清除缓存
        clear: function() {
                     exec(null, null, "WebViewRequest", "clear", [null]);
        },
        //关闭web
        close: function(jsonparams) {
                     exec(null, null, "WebViewRequest", "close", [jsonparams]);
        },
        //设置顶部右边按钮
        setBarRight: function(jsonparams) {
                     exec(null, null, "WebViewRequest", "setBarRight", [jsonparams]);
        },
        //关闭注册事件
        closeEvent: function(jsonparams) {
                     exec(null, null, "WebViewRequest", "closeEvent", [jsonparams]);
        },
    };

});
//fengxf added end


