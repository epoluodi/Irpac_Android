package com.suypower.stereo.suypowerview.Protocol;

/**
 * Created by Stereo on 16/7/29.
 */
public class EBNoticesProtocol {


    private String content = "";
    private String title = "";
    private String url = "";



    private SystemBaseProtocol systemBaseProtocol;


    public EBNoticesProtocol(SystemBaseProtocol systemBaseProtocol) throws Exception {
        this.systemBaseProtocol = systemBaseProtocol;

        if (systemBaseProtocol.getNoticeType() == SystemBaseProtocol.EBNOTICESALERTINFO ||
                systemBaseProtocol.getNoticeType() == SystemBaseProtocol.EBNOTICES17 ) {
            content = systemBaseProtocol.getBody().getString("content");
            title = systemBaseProtocol.getBody().getString("title");
            url = systemBaseProtocol.getBody().getString("url");
            return;
        }


    }


    public SystemBaseProtocol getSystemBaseProtocol() {
        return systemBaseProtocol;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
