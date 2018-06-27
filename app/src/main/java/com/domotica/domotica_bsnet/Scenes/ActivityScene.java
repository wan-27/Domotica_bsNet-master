package com.domotica.domotica_bsnet.Scenes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.domotica.domotica_bsnet.R;
import com.domotica.domotica_bsnet.fragments.DevicesFragment;
import com.domotica.domotica_bsnet.fragments.SceneFragment;

public class ActivityScene extends AppCompatActivity {
    private String ipServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        SceneFragment sceneFragment = (SceneFragment) getSupportFragmentManager().findFragmentById(R.id.scene_container);

        ipServer = DevicesFragment.getIpServerLongclick();

        if (sceneFragment == null) {
            sceneFragment = SceneFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.scene_container, sceneFragment)
                    .commit();
        }
    }

    public String getIpServer() {
        return ipServer;
    }

}
