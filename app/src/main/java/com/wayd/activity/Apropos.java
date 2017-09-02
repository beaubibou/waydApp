package com.wayd.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.application.wayd.R;


public class Apropos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager manager = this.getPackageManager();
        setContentView(R.layout.apropos);

        TextView TV_NUMversion = (TextView) findViewById(R.id.version);
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
      int versionCode = info.versionCode;
            String versionName = info.versionName+versionCode;
            TV_NUMversion.setText(versionName);

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        TextView TV_cgu = (TextView) findViewById(R.id.cgu);
        TV_cgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appel = new Intent(Apropos.this,
                        Cgu.class);
                appel.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(appel);
                finish();

            }
        });

    }

}
