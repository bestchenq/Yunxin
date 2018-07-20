package com.example.ubt.yunxindemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class MainActivity extends AppCompatActivity {

    Button btn, btn2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableAVChat();

        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo());

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

                Log.d("chenqiang","status code ===" + status);
                AVChatKit.setContext(MainActivity.this);
              //  sendTextMsg();
                AVChatKit.outgoingCall(MainActivity.this, "11111111", "chenq", AVChatType.VIDEO.getValue(), AVChatActivity.FROM_INTERNAL);
            }
        });

    }


    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        //985495e7ff0d56b847016dd2de2ee1df      ////22222222 DE
        //9612967701cd48482600ca970fb8d5a5      ////11111111 DE
        return new LoginInfo("22222222", "985495e7ff0d56b847016dd2de2ee1df");
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void sendTextMsg() {
        // 该帐号为示例，请先注册
        String account = "bestchenq";
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
}
