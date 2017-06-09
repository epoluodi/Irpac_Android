package com.suypower.stereo.suypowerview.Interface;

import android.view.View;
import android.widget.ImageView;

import com.suypower.stereo.suypowerview.Chat.ChatMessage;
import com.suypower.stereo.suypowerview.CustomView.InputView;

/**
 * Created by Stereo on 16/4/8.
 */
public interface IChat {

    void OnMoreItem(InputView.ChatKeyType chatKeyType);
    void OnEmoji(String emoji, String filename);
    void Ondelete();
    void OnClickChatItem(ChatMessage chatMessage);
    void OnLongClickItem(ChatMessage chatMessage, View view);
    void OnClickSender(String senderid);
    void OnChangeSoundState(ImageView imageView,Boolean flag);
    void OnReSendMessage(ChatMessage chatMessage);
    void OnLongClickUser(String senderid);
}
