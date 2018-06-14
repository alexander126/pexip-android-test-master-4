package com.gearedapp.ajentapexip;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.pexip.android.wrapper.PexView;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_PERM = "PERM";
    private static final String TAG_INIT_PEX = "INIT_PEX";
    private static final String TAG_BTN = "ON_CLICK_BTN";
    private static final String TAG_FINISH = "FINISH_CALLBACK";

    // Permissions
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private RxPermissions rxPermissions;

    // Layouts
    private RelativeLayout layout;
    private PexView pexView;
    private WebView selfView;
    private RelativeLayout.LayoutParams pexLayoutParams;

    private String username;
    private String roomextension;
    private Button mutemicBtn;
    private Button disconnectBtn;
    private Button muteBtn;
    private Button unmuteBtn;
    private Button unmutemicBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handlePermissions();
        Intent intent = getIntent();
        username = intent.getStringExtra("usr");
        roomextension = intent.getStringExtra("ext");
        Log.d("roomext",roomextension);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pexView != null) {
            pexView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pexView != null) {
            pexView.onResume();
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        if (pexView != null) {
            pexView.evaluateFunction("disconnect");

        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if (pexView != null) {
            pexView.load();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pexView != null) {
            pexView.evaluateFunction("disconnect");

        }
    }

    private void handlePermissions() {
        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(this);
        }

        rxPermissions.request(PERMISSIONS).subscribe(granted -> {
            if (granted) {
                Log.i(TAG_PERM, "All permissions granted");
                start();
            } else {
                Log.e(TAG_PERM, "At least one permission is not granted");
            }
        });
    }

    private void start() {
        if (pexView == null) {
            pexView = new PexView(this);
        }

        if (pexLayoutParams == null) {
            pexLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        disconnectBtn = findViewById(R.id.btn_disconnect);
        muteBtn = findViewById(R.id.btn_mute);
        unmuteBtn = findViewById(R.id.btn_unmute);
        mutemicBtn = findViewById(R.id.btn_mutemic);
        unmutemicBtn = findViewById(R.id.btn_turnonmic);

        disconnectBtn.setOnClickListener(onDisconnect);
        muteBtn.setOnClickListener(onMute);
        unmuteBtn.setOnClickListener(onUnmute);
        mutemicBtn.setOnClickListener(onmuteMic);
        unmutemicBtn.setOnClickListener(onunmuteMic);

        layout = findViewById(R.id.pex_layout);
        layout.addView(pexView, pexLayoutParams);

        selfView = findViewById(R.id.pex_self_layout);
        pexView.setSelfView(selfView);

        initPexip();
    }

    private void initPexip() {
        pexView.setEvent("onSetup", pexView.new PexEvent() {
            @Override
            public void onEvent(String[] strings) {
                Log.i(TAG_INIT_PEX, "onSetup...");
                pexView.setSelfViewVideo(strings[0]);
                pexView.evaluateFunction("connect");
            }
        });

        pexView.setEvent("onConnect", pexView.new PexEvent() {
            @Override
            public void onEvent(String[] strings) {
                Log.i(TAG_INIT_PEX, "onConnect...");
                if (strings.length > 0 && strings[0] != null) {
                    pexView.setVideo(strings[0]);
                }
            }
        });

        pexView.addPageLoadedCallback(pexView.new PexCallback() {
            @Override
            public void runOnUI(String returnValue) {
                super.runOnUI(returnValue);
                Log.d(TAG_INIT_PEX, "runOnUI.... " + returnValue);
            }

            @Override
            public void callback(String args) {
                Log.d(TAG_INIT_PEX, "addPageLoadedCallback... " + args);
                // make a call
                pexView.evaluateFunction("makeCall", "pex-pool.vscene.net", roomextension, username);
                //john_vmr
            }

        });


        pexView.load();
    }

    private View.OnClickListener onDisconnect = ((View v) -> {
        Log.d(TAG_BTN, "Disconnect");
        if (pexView != null) {
            pexView.evaluateFunction("disconnect");
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }
    });

    private View.OnClickListener onMute = ((View v) -> {
        Log.d(TAG_BTN, "Connect");
        if (pexView != null) {
            pexView.evaluateFunction("muteVideo", true);
        }

    });
    private View.OnClickListener onUnmute = ((View v) -> {
        Log.d(TAG_BTN, "Connect");
        if (pexView != null) {
            pexView.evaluateFunction("muteVideo", false);
        }

    });

    private View.OnClickListener onmuteMic = ((View v) -> {
        Log.d(TAG_BTN, "MUTEMIC");
        if (pexView != null) {
            pexView.evaluateFunction("muteAudio", true);
        }
    });
    private View.OnClickListener onunmuteMic = ((View v) -> {
        Log.d(TAG_BTN, "unmutemic");
        if (pexView != null) {
            pexView.evaluateFunction("muteAudio", false);
        }
    });

}
