package dev.testify.smoke;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Draws one solid, unambiguous frame. Enough to prove the app installed,
 * launched, and that the compositor produced something we can screencap.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView view = new TextView(this);
        view.setText("SMOKE OK");
        view.setTextSize(48f);
        view.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.parseColor("#1B5E20"));
        view.setGravity(Gravity.CENTER);

        setContentView(view);
    }
}
