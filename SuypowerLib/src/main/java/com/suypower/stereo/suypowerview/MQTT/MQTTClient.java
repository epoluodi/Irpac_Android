package com.suypower.stereo.suypowerview.MQTT;

import android.util.Log;


import com.suypower.stereo.suypowerview.Chat.ChatMsgJson;
import com.suypower.stereo.suypowerview.Common.BaseUserInfo;
import com.suypower.stereo.suypowerview.Common.Common;
import com.suypower.stereo.suypowerview.Protocol.BaseChatProtocol;
import com.suypower.stereo.suypowerview.Protocol.SystemBaseProtocol;
import com.suypower.stereo.suypowerview.Protocol.WeAppProtocol;
import com.suypower.stereo.suypowerview.ServerReturnData.ReturnData;

import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Stereo on 16/5/9.
 */
public class MQTTClient {


    //聊天消息主题 topic/chat/[userid]
    //通知主题 topic/notice/[userid]
    //遗嘱主题 no订阅 topic/offline


    //    static final String userName = "admin";
//    static final String passWord = "password";
    private static final String myTopic = "test/topic";
    private Topic[] topics;
    private Topic topic;
    private CallbackConnection callbackConnection;
    private MQTT mqtt;
    private MQTTCallBack mqttCallBack;
    private String offlineTopic = "topic/offline";
    private String onlineTopic = "topic/online";
    public String receipMmsgTopic = "topic/msgReceipt";
    public String receiptNoticeTopic = "topic/noticeReceipt";
    public String receiptWeAppTopic = "topic/appMsgReceipt";
    private String lineMessage;

    Boolean IsConnected = false;


    public Boolean getConnected() {
        return IsConnected;
    }

    public MQTTClient(MQTTCallBack mqttCallBack) {
        mqtt = new MQTT();
        this.mqttCallBack = mqttCallBack;
        try {
            mqtt.setHost(MQTTConfig.serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mqtt.setClientId(MQTTConfig.cliectId); //用于设置客户端会话的ID。在setCleanSession(false);被调用时，MQTT服务器利用该ID获得相应的会话。此ID应少于23个字符，默认根据本机地址、端口和时间自动生成
        mqtt.setCleanSession(true); //若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true
        mqtt.setKeepAlive((short) 5);//定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待
        mqtt.setUserName(MQTTConfig.userName);//服务器认证用户名
        mqtt.setPassword(MQTTConfig.password);//服务器认证密码
        mqtt.setWillTopic(offlineTopic);//设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("userId", BaseUserInfo.getBaseUserInfo().getUserId());
        }catch (Exception e)
        {e.printStackTrace();}
//        jsonObject.put("deviceId",Common.DeviceID);
        lineMessage = jsonObject.toString();
        Log.i("遗嘱消息", lineMessage);
        mqtt.setWillMessage(lineMessage);//设置“遗嘱”消息的内容，默认是长度为零的消息
        mqtt.setWillQos(QoS.AT_LEAST_ONCE);//设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
        mqtt.setWillRetain(false);//若想要在发布“遗嘱”消息时拥有retain选项，则为true
        mqtt.setVersion("3.1.1");
        //失败重连接设置说明
        mqtt.setConnectAttemptsMax(-1);//客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
        mqtt.setReconnectAttemptsMax(-1);//客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
        mqtt.setReconnectDelay(100L);//首次重连接间隔毫秒数，默认为10ms
        mqtt.setReconnectDelayMax(10000L);//重连接间隔毫秒数，默认为30000ms
        mqtt.setReconnectBackOffMultiplier(2);//设置重连接指数回归。设置为1则停用指数回归，默认为2
        //Socket设置说明
        mqtt.setReceiveBufferSize(65536);//设置socket接收缓冲区大小，默认为65536（64k）
        mqtt.setSendBufferSize(65536);//设置socket发送缓冲区大小，默认为65536（64k）
        mqtt.setTrafficClass(8);//设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输
        //带宽限制设置说明
        mqtt.setMaxReadRate(0);//设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
        mqtt.setMaxWriteRate(0);//设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制
        callbackConnection = mqtt.callbackConnection();
        callbackConnection.listener(listener);
        topics = new Topic[1];
        topic = new Topic(MQTTConfig.cliectId, QoS.EXACTLY_ONCE);
        topics[0] = topic;
    }


    /**
     * 订阅公告频道
     */
    public void setPublictopic(String strtopic) {
        if (IsConnected) {
            Topic[] topic = new Topic[1];
            Log.i("订阅:", strtopic);
            Topic topic1 = new Topic(strtopic, QoS.EXACTLY_ONCE);
            topic[0] = topic1;
            callbackConnection.subscribe(topic, new Callback<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("========群组订阅成功=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnStartReceiveGroup();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("========订阅失败=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnConnectServerFail();
                }
            });
        }
    }

    /**
     * @param strlist
     */
    public void setGrouptopic(List<String> strlist) {
        if (IsConnected) {
            Topic[] topic = new Topic[strlist.size()];
            for (int i = 0; i < strlist.size(); i++) {
                Topic topic1 = new Topic(strlist.get(i), QoS.EXACTLY_ONCE);
                topic[i] = topic1;

            }
            callbackConnection.subscribe(topic, new Callback<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("========群组订阅成功=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnStartReceiveGroup();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("========订阅失败=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnConnectServerFail();
                }
            });
        }
    }

    /**
     * 发送消息
     *
     * @param receiver
     * @param msgjson
     */
    public void sendMessage(String receiver, String msgjson, final IMQTTSendCallBack imqttSendCallBack) {
        callbackConnection.publish(receiver, msgjson.getBytes(), QoS.EXACTLY_ONCE, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                imqttSendCallBack.SendSuccess();
            }

            @Override
            public void onFailure(Throwable throwable) {
                imqttSendCallBack.Sendfail();
            }
        });
    }

    /**
     * 取消与服务器连接
     */
    public void disConnectServer() {


//        UTF8Buffer[] utf8Buffers=new UTF8Buffer[]{topic.name()};
//        callbackConnection.unsubscribe(utf8Buffers,null);
        callbackConnection.publish(offlineTopic, lineMessage.getBytes(),
                QoS.EXACTLY_ONCE, false, null);
        callbackConnection.disconnect(null);
        mqttCallBack = null;
    }


    /**
     * 连接服务
     */
    public void connecetServer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                callbackConnection.connect(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackConnection.subscribe(topics, new Callback<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                System.out.println("========订阅成功=======");

                                if (mqttCallBack != null)
                                    mqttCallBack.OnStartReceive();
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                System.out.println("========订阅失败=======");
                                if (mqttCallBack != null)
                                    mqttCallBack.OnConnectServerFail();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (mqttCallBack != null)
                            mqttCallBack.OnConnectServerFail();
                    }
                });
            }
        }).start();
    }


    /**
     * MQTT连接监控
     * 连接成功，失败，接收消息
     */
    Listener listener = new Listener() {
        @Override
        public void onConnected() {
            IsConnected = true;
            if (mqttCallBack != null)
                mqttCallBack.OnConnected();

            System.out.println("=============服务已连接=============");
            System.out.println("========" + callbackConnection.getDispatchQueue());

            callbackConnection.publish(onlineTopic, lineMessage.getBytes(),
                    QoS.EXACTLY_ONCE, false, new Callback<Void>() {
                        public void onSuccess(Void v) {
                            System.out.println("===========online消息发布成功============");
                        }

                        public void onFailure(Throwable throwable) {
                            System.out.println("========online消息发布失败=======");
                            throwable.printStackTrace();
                            callbackConnection.disconnect(null);
                        }
                    });
        }

        @Override
        public void onDisconnected() {
            IsConnected = false;
            if (mqttCallBack != null)
                mqttCallBack.OnDisConnected();

        }

        @Override
        public void onPublish(org.fusesource.hawtbuf.UTF8Buffer utf8Buffer, org.fusesource.hawtbuf.Buffer buffer, Runnable runnable) {
            runnable.run();
            Log.i("UTF8Buffer ", new String(utf8Buffer.getData()));
            Log.i("收到的byte ", new String(buffer.getData()));
            Topic topic = new Topic(utf8Buffer, QoS.EXACTLY_ONCE);
            String strtopic = topic.name().toString();
//            Log.i("topic",topic.name().toString());

            String json = new String(buffer.toByteArray());
            try {

                if (strtopic.equals(MQTTConfig.ChatNoice)) {
                    final BaseChatProtocol baseChatProtocol=new BaseChatProtocol(json);
                    baseChatProtocol.setMode(1);//登录非拉取模式下
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("ope",baseChatProtocol.getChatMsgJson().getOpe());
                    jsonObject.put("msgId",baseChatProtocol.getChatMsgJson().getMsgid());
                    jsonObject.put("userId",BaseUserInfo.getBaseUserInfo().getUserId());
                    sendMessage(receipMmsgTopic, jsonObject.toString(), new IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功","");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败","");
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mqttCallBack != null)
                                mqttCallBack.OnMsgCallBack(baseChatProtocol);
                        }
                    }).start();
                    return;
                }
                if (strtopic.equals(MQTTConfig.SystemNoice)) {
                    final SystemBaseProtocol systemBaseProtocol = new SystemBaseProtocol(json);
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("noticeId",systemBaseProtocol.getNoticeId());

                    sendMessage(receiptNoticeTopic, jsonObject.toString(), new IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功","");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败","");
                        }
                    });

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mqttCallBack != null)
                                mqttCallBack.OnMsgCallBack(systemBaseProtocol);
                        }
                    }).start();
                    return;
                }
                if (strtopic.equals(MQTTConfig.WeAppNoice)) {
                    final WeAppProtocol weAppProtocol=new WeAppProtocol(json);

                    weAppProtocol.setMode(1);//提醒打开
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("msgId",weAppProtocol.getMsgid());
                    jsonObject.put("userId",BaseUserInfo.getBaseUserInfo().getUserId());
                    sendMessage(receiptWeAppTopic, jsonObject.toString(), new IMQTTSendCallBack() {
                        @Override
                        public void SendSuccess() {
                            Log.e("发送回执成功","");
                        }

                        @Override
                        public void Sendfail() {
                            Log.e("发送回执失败","");
                        }
                    });

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mqttCallBack != null)
                                mqttCallBack.OnMsgCallBack(weAppProtocol);
                        }
                    }).start();
//                    return;
                }




            } catch (Exception e) {
                e.printStackTrace();
                if (mqttCallBack != null)
                    mqttCallBack.OnMsgCallBack(buffer.toByteArray());
            }


        }

        @Override
        public void onFailure(Throwable throwable) {

        }
    };


    /**
     * mqtt回调
     */
    public interface MQTTCallBack {
        void OnMsgCallBack(byte[] buffer);//消息回调

        void OnMsgCallBack(BaseChatProtocol baseChatProtocol);//消息回调

        void OnMsgCallBack(SystemBaseProtocol systemBaseProtocol);//消息回调

        void OnMsgCallBack(WeAppProtocol weAppProtocol);//微应用回调

        void OnConnected();//连接成功

        void OnDisConnected();//连接断开

        void OnStartReceive();

        void OnStartReceiveGroup();

        void OnConnectServerFail();

    }


    public interface IMQTTSendCallBack {
        void SendSuccess();

        void Sendfail();
    }
}
