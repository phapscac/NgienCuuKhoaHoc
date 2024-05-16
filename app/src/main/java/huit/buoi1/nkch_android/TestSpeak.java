package huit.buoi1.nkch_android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestSpeak extends AppCompatActivity {
    TextToSpeechHelper ap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_speak);
        ap = new TextToSpeechHelper(TestSpeak.this);
        ap.speak("Chào mọi người");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ap.shutdown();
    }
}