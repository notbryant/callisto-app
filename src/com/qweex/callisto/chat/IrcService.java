package com.qweex.callisto.chat;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import com.qweex.callisto.MasterActivity;
import com.sorcix.sirc.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IrcService extends IntentService {

    String TAG = "Callisto:chat_tab:IrcService";

    public static IrcService instance;
    public static ChatFragment chatFragment;

    private IrcConnection ircConnection;
    private User nickServ;

    public IrcService() {
        super("Callisto/IRC");
        Log.v(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent");

        Bundle extras = intent.getExtras();

        // Retrieve data from intent. This is basically the "constructor"
        String nickname = extras.getString("nickname"),
               username = intent.getStringExtra("username"),
               real_name = intent.getStringExtra("real_name"),
               password = intent.getStringExtra("password");
        String[] channel_names = intent.getStringArrayExtra("channels");


        // Set up the adapter
        ircConnection = new IrcConnection("irc.geekshed.net");

        ircConnection.setNick(nickname);
        if(username!=null && username.length()>0) {
            if(real_name!=null && real_name.length()>0)
                ircConnection.setUsername(username);
            else
                ircConnection.setUsername(username, real_name);
        }

        ircConnection.addMessageListener(chatFragment.ircConnectionAdapter);
        ircConnection.addModeListener(chatFragment.ircConnectionAdapter);
        ircConnection.addServerListener(chatFragment.ircConnectionAdapter);

        try {
            ircConnection.connect();
            chatFragment.createTab(ircConnection);
            nickServ = ircConnection.createUser("nickserv");
            if(password!=null && password.length()>1)
                nickServ.sendMessage("identify " + password);
            // Join the channels
            for(String channel : channel_names) {
                Channel c = ircConnection.createChannel(channel);
                c.join();
                chatFragment.createTab(c);
                Log.i(TAG, "Connecting to  " + channel);
            }

            Log.i(TAG, "--connected? " + ircConnection.isConnected());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NickNameException e) {
            e.printStackTrace();
        } catch (PasswordException e) {
            e.printStackTrace();
        }
    }
}
