package wvw.mobile.rules;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import wvw.mobile.rules.R;
import wvw.mobile.rules.explanation.ExplanationRunner;
import wvw.mobile.rules.eye_js.EyeJsReasoner;
import wvw.mobile.rules.eyebrow.EyebrowReasoner;
import wvw.mobile.rules.eyebrow.ReasonCmd;
import wvw.mobile.rules.eyebrow.Reasoner;
import wvw.mobile.rules.eyebrow.ReasonerListener;

public class WebviewReasonActivity extends AppCompatActivity implements ReasonerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Needed to set up the emulator with the app.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_reason);

        // Starts the explanation runner:
        ExplanationRunner.run();
    }

    @Override
    public void result(String result) {
        Log.d("android-rules", "result:\n" + result);
    }

    @Override
    public void error(String error) {
        Log.d("android-rules", "error:\n" + error);
    }
}