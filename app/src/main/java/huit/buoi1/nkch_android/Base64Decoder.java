package huit.buoi1.nkch_android;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Base64Decoder {
    public static interface AudioPlaybackCallback {
        void onPlaybackFinished();
    }
    public static void playFromBase64(Context context, String base64Data, AudioPlaybackCallback callback) {
        byte[] decodedAudio = Base64.decode(base64Data, Base64.DEFAULT);
        File tempFile;
        try {
            tempFile = File.createTempFile("temp_audio", ".mp3", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(decodedAudio);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(tempFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                tempFile.delete();
                if (callback != null) {
                    callback.onPlaybackFinished();
                }
            }
        });

    }
}
