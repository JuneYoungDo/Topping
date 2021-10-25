package com.teenteen.topping.oauth.social;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth{
    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public String getInformation(String code) {
        return null;
    }
}
