package com.example.jhonjimenez.pruebaconceptofcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SharedMemory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String SP_TOPICS = "sharedPreferencesTopics";

    @BindView(R.id.spTopics)
    Spinner spTopics;
    @BindView(R.id.tvTopics)
    TextView tvTopics;

    private Set<String> mTopicsSet;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configSharedPreferences();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
            }
        });
    }

    private void configSharedPreferences() {
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        mTopicsSet = mSharedPreferences.getStringSet(SP_TOPICS, new HashSet<String>());

        showTopics();
    }

    private void showTopics() {
        tvTopics.setText(mTopicsSet.toString());
    }


    @OnClick({R.id.btnSuscribir, R.id.btnDesuscribir})
    public void onViewClicked(View view) {

        String topic = getResources().getStringArray(R.array.topicsValues)[spTopics.getSelectedItemPosition()];

        switch (view.getId()) {
            case R.id.btnSuscribir:
                if (!mTopicsSet.contains(topic)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    mTopicsSet.add(topic);
                    saveSharedPreferences();
                }
                break;
            case R.id.btnDesuscribir:
                if (mTopicsSet.contains(topic)) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    mTopicsSet.remove(topic);
                    saveSharedPreferences();
                }
                break;
        }
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.putStringSet(SP_TOPICS, mTopicsSet);
        editor.apply();

        showTopics();
    }
}
