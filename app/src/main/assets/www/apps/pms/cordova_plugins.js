cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.apache.cordova.webview/www/WebViewRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.webview.WebViewRequest",
        "merges": [
        "stereo.webview"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.system/www/SystemRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.system.SystemRequest",
        "merges": [
        "stereo.system"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.http/www/FavoriteRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.http.FavoriteRequest",
        "merges": [
        "stereo.FavoriteRequest"
        ]
     },
    {
         "file": "plugins/org.apache.cordova.camera/www/CameraRequest.js",
         "id": "com.suypower.stereo.Irpac.CordovaPlugin.camera.CameraRequest",
         "merges": [
         "stereo.camera"
         ]
     },
     {
        "file": "plugins/org.apache.cordova.file/www/FileRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.file.FileRequest",
        "merges": [
        "stereo.file"
        ]
     },
     {
        "file": "plugins/org.apache.cordova.ajax/www/AjaxRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.ajax.AjaxRequest",
        "merges": [
        "stereo.ajax"
        ]
     },
     {
        "file": "plugins/org.apache.cordova.comments/www/CommentsRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.Comments.CommentsRequest",
        "merges": [
        "stereo.comments"
        ]
     },
     {
        "file": "plugins/org.apache.cordova.scan/www/ScanRequest.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.scan.ScanRequest",
        "merges": [
        "stereo.scan"
        ]
     },
     {
         "file": "plugins/org.apache.cordova.webview/www/ImagePreviewPlugin.js",
         "id": "com.suypower.stereo.Irpac.CordovaPlugin.webview.ImagePreviewPlugin",
         "merges": [
         "stereo.previewImage"
         ]
      },
        {
        "file": "plugins/org.apache.cordova.phonebook/www/PhoneBookPlugin.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.PhoneBook.PhoneBookPlugin",
        "merges": [
        "stereo.phonebook"
                ]
        },
        {
        "file": "plugins/org.apache.cordova.share/www/SharePlugin.js",
        "id": "com.suypower.stereo.Irpac.CordovaPlugin.share.SharePlugin",
        "merges": [
                 "stereo.share"
                 ]
        },
        {
            "file": "plugins/org.apache.cordova.preview/www/PreviewPlugin.js",
            "id": "com.suypower.stereo.Irpac.CordovaPlugin.preview.PreviewPlugin",
            "merges": [
            "stereo.preview"
            ]
        }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.suypower.stereo.Irpac.CordovaPlugin.http.FavoriteRequest": "0.0.1",


}
// BOTTOM OF METADATA
});