package com.teenteen.topping.oauth.OauthService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.oauth.OauthDto.ApplePublicKeyRes;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleService2 {

    public void getClaimsBy(String identityToken) {
        try {
            URL url = new URL("https://appleid.apple.com/auth/keys");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result);
            JsonParser parser = new JsonParser();
            JsonObject keys = (JsonObject) parser.parse(result.toString());
            JsonArray keyArray = (JsonArray) keys.get("keys");
            System.out.println(keys);
            System.out.println(keyArray);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
