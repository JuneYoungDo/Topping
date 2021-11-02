package com.teenteen.topping.oauth.OauthService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


@Service
@RequiredArgsConstructor
public class KakaoService {

    public String getKakaoUserInfo(String accessToken) throws BaseException, IOException {
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        URL url = new URL(reqUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // Header에 포함될 요청에 필요한 내용
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);
        if (conn.getResponseCode() >= 400) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        String result = "";
        while ((line = br.readLine()) != null) {
            result += line;
        }
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(result);
        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

        return kakaoAccount.getAsJsonObject().get("email").getAsString();
    }

}
