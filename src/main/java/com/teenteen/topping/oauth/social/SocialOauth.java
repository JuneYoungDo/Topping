package com.teenteen.topping.oauth.social;

import com.teenteen.topping.oauth.helper.SocialLoginType;

public interface SocialOauth {
    /**
     * 각 소셜 로그인 페이지로 redirect 처리할 url
     * 사용자로부터 로그인 요청을 받아 social login server 인증용 code 요청
     */
    String getOauthRedirectURL();
    /**
     * api 서버로부터 받은 code를 활용하여 사용자 인증 정보 요청
     * api 서버로부터 응답받은 json 형태의 결과를 string으로 return
     */
    String getInformation(String code);

    default SocialLoginType type() {
        if(this instanceof KakaoOauth)
            return SocialLoginType.KAKAO;
        else if(this instanceof AppleOauth)
            return SocialLoginType.APPLE;
        else
            return null;
    }
}
