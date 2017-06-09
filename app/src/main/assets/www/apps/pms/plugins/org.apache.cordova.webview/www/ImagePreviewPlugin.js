cordova.define("com.suypower.stereo.Irpac.CordovaPlugin.webview.ImagePreviewPlugin", function(require,exports,module){

    var exec = require('cordova/exec');

    module.exports = {
        //开始预览
        open: function(jsonparams) {
           exec(null,null,"ImagePreviewPlugin", "open", [jsonparams]);
        }
    };

});



