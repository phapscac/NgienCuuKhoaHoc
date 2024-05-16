package huit.buoi1.nkch_android;
import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class dcoApi2 {
    private static final String BASE_URL = "http://14.225.198.134:5000//api/create-caption";
    public interface ApiResponseListener {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }
    public void executeApiRequest(byte[] imageData, ApiResponseListener listener) {
        new ApiRequestTask(imageData, listener).execute();
    }

    private static class ApiRequestTask extends AsyncTask<Void, Void, String> {
        private final byte[] imageData;
        private final ApiResponseListener listener;

        ApiRequestTask(byte[] imageData, ApiResponseListener listener) {
            this.imageData = imageData;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            // Tạo request body với hình ảnh
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/*"), imageData))
                    .build();

            // Tạo request
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();

            // Thực hiện request
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Request failed with codee: " + response.code();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Request failed") || result.startsWith("Error")) {
                listener.onFailure(result);
            } else {
                listener.onSuccess(result);
            }
        }
    }
}
