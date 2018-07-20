package com.example.ubt.yunxindemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.config.AVChatOptions;
import com.netease.nim.avchatkit.model.ITeamDataProvider;
import com.netease.nim.avchatkit.model.IUserInfoProvider;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.business.contact.core.util.ContactHelper;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）

        NIMClient.init(this, loginInfo(), options());
        Log.d("chenqiang", "application create ............");
        MultiDex.install(this);
        // 以下逻辑只在主进程初始化时执行
        if (NIMUtil.isMainProcess(this)) {
            initUIKit();
            initAVChatKit();
        }
    }

    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        // 设置app图片/音频/日志等缓存目录
       // options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this) + "/app";
        return options;
    }
    private void initUIKit() {
        // 初始化
        NimUIKit.init(this, buildUIKitOptions());

//        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
//        NimUIKit.setLocationProvider(new NimDemoLocationProvider());
//
//        // IM 会话窗口的定制初始化。
//        SessionHelper.init();
//
//        // 聊天室聊天窗口的定制初始化。
//        ChatRoomSessionHelper.init();
//
//        // 通讯录列表定制初始化
//        ContactHelper.init();
//
//        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
//        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());
//
//        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }


    private void initAVChatKit() {
        AVChatOptions avChatOptions = new AVChatOptions(){
            @Override
            public void logout(Context context) {
              //  MainActivity.logout(context, true);
            }
        };
        avChatOptions.entranceActivity = MainActivity.class;
        avChatOptions.notificationIconRes = R.drawable.ic_launcher_foreground;
        AVChatKit.init(avChatOptions);

        // 设置用户相关资料提供者
        AVChatKit.setUserInfoProvider(new IUserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return NimUIKit.getUserInfoProvider().getUserInfo(account);
            }

            @Override
            public String getUserDisplayName(String account) {
                return UserInfoHelper.getUserDisplayName(account);
            }
        });
        // 设置群组数据提供者
        AVChatKit.setTeamDataProvider(new ITeamDataProvider() {
            @Override
            public String getDisplayNameWithoutMe(String teamId, String account) {
                return TeamHelper.getDisplayNameWithoutMe(teamId, account);
            }

            @Override
            public String getTeamMemberDisplayName(String teamId, String account) {
                return TeamHelper.getTeamMemberDisplayName(teamId, account);
            }
        });
    }


    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
      //  config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
     //   config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        options.appKey = "4fee3408c32c23f945b7cf9af38bdeb3";

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
      //  String sdkPath = getAppCacheDir(context) + "/nim"; // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
       // options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = 1080 / 2;
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    public static LoginInfo loginInfo() {
        //985495e7ff0d56b847016dd2de2ee1df      ////22222222 DE
        //9612967701cd48482600ca970fb8d5a5      ////11111111 DE
        if (Build.MODEL.contains("SHV")) {
            return new LoginInfo("11111111", "9612967701cd48482600ca970fb8d5a5");
        }
        return new LoginInfo("22222222", "985495e7ff0d56b847016dd2de2ee1df");
    }



}
