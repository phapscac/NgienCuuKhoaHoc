package huit.buoi1.nkch_android;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeechHelper implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private Context context;

    public TextToSpeechHelper(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Ngôn ngữ không được hỗ trợ.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Lỗi khi khởi tạo TextToSpeech.", Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void stop() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    public void shutdown() {
        textToSpeech.shutdown();
    }
}