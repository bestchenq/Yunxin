package com.example.ubt.yunxindemo;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.AVChatProfile;
import com.netease.nim.avchatkit.activity.AVChatActivity;
import com.netease.nim.avchatkit.common.log.LogUtil;
import com.netease.nim.avchatkit.receiver.PhoneCallStateObserver;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn, btn2, btn3;

    TextView msgText;

    String account = "11111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableAVChat();
        setTargetAccount();

        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        msgText = findViewById(R.id.msg_text);
        msgText.setMovementMethod(ScrollingMovementMethod.getInstance());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(MyApplication.loginInfo());

                loginRequest.setCallback(new RequestCallback() {
                    @Override
                    public void onSuccess(Object param) {
                        Log.d("chenqiang", "log in success param is :" + param);
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.d("chenqiang", "log in fail code :" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusCode status = NIMClient.getStatus();
                Log.d("chenqiang", "status code ===" + status);
                sendTextMsg();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVChatKit.setContext(MainActivity.this);
                AVChatKit.outgoingCall(MainActivity.this, account, "chenq", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        registerMsgReceiver();
    }

    private void setTargetAccount() {
        // 该帐号为示例，请先注册
        if (Build.MODEL.contains("SHV")) {
            account = "22222222";
        } else {
            account = "11111111";
        }
    }

    private void sendTextMsg() {

// 以单聊类型为例
        SessionTypeEnum sessionType = SessionTypeEnum.P2P;
        String text = "this is an example";
// 创建一个文本消息
        IMMessage textMessage = MessageBuilder.createTextMessage(account, sessionType, text);

// 发送给对方
        NIMClient.getService(MsgService.class).sendMessage(textMessage, false);
    }

    //接收来电
    private void enableAVChat() {
        registerAVChatIncomingCallObserver(true);
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                        || AVChatProfile.getInstance().isAVChatting()
                        || AVChatManager.getInstance().getCurrentChatId() != 0) {
                    //  LogUtil.i(TAG, "reject incoming call data =" + data.toString() + " as local phone is not idle");
                    AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
                    return;
                }
                // 有网络来电打开AVChatActivity
                AVChatProfile.getInstance().setAVChatting(true);
                // AVChatActivity.launch(DemoCache.getContext(), data, AVChatActivity.FROM_BROADCASTRECEIVER);
            }
        }, register);
    }


    private void registerMsgReceiver() {
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        Log.d("chenqiang", "messages num is " + messages.size());

                        for (IMMessage message : messages) {
                            Log.d("chenqiang", "msg is " + message.getContent());
                            msgText.setText(msgText.getText().toString() + "\n" + message.getContent());
                        }
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }
}
