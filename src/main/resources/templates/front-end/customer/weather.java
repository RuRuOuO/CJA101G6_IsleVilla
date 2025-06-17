import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;

public class Weather {

    public static void main(String[] args) throws IOException, InterruptedException {

        // API 網址和授權碼
        String apiUrl = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/O-A0001-001?Authorization=CWA-97C55E07-4A4C-4F2E-81DA-6CBC30CAC58E&StationId=467110"; // 範例 API 網址
        String apiKey = "CWA-97C55E07-4A4C-4F2E-81DA-6CBC30CAC58E"; // 替換為你的 API 授權碼

        // 建構 HTTP 請求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "&Authorization=" + apiKey))
                .GET()
                .build();

        // 建立 HTTP 客户端
        HttpClient client = HttpClient.newHttpClient();

        // 執行請求
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 檢查狀態碼
        if (response.statusCode() == 200) {
            // 解析 JSON 資料
            JSONObject json = new JSONObject(response.body());

            //  取出資料
            JSONArray data = json.getJSONArray("data");

            // 輸出資料
            for (int i = 0; i < data.length(); i++) {
                JSONObject record = data.getJSONObject(i);
                String weather = record.getString("weather");
                System.out.println("日期: " + record.get("time") + ", 天氣: " + weather);
            }

        } else {
            System.out.println("請求失敗: " + response.statusCode());
        }
    }
}