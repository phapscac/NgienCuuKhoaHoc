package huit.buoi1.nkch_android;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class docApi {
    private static final String BASE_URL = "http://14.225.192.6:5000/api/v1/auto-create-caption?image";

    public interface ApiResponseListener {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }

    public void executeApiRequest(Context context, int imageResourceId, ApiResponseListener listener) {
        new ApiRequestTask(context, imageResourceId, listener).execute();
    }

    private static class ApiRequestTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final int imageResourceId;
        private final ApiResponseListener listener;

        ApiRequestTask(Context context, int imageResourceId, ApiResponseListener listener) {
            this.context = context;
            this.imageResourceId = imageResourceId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            // Lấy dữ liệu từ tài nguyên drawable
            byte[] imageData = null;
            try {
                imageData = Utils.getImageDataFromDrawable(context, imageResourceId);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }

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
                    return "Request failed with code: " + response.code();
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
