package com.suypower.stereo.suypowerview.FrameController;

import android.content.Intent;
import android.os.Message;

/**
 * Created by Administrator on 14-11-30.
 */
public interface FragmentName {
    void SetFragmentName(String name);

    String GetFragmentName();

    void SelectMenu(int Menuid);

    void selectcustomer(String guestid, String guestname);

    void onMessage(Message message);

    void returnWeb();

    void startIMessageControl();

    void stopIMessageControl();

    void onResult(int requestCode, int resultCode, Intent data);

    void setWebTitle();

}
