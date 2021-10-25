package com.teenteen.topping.oauth.social;

public class AppleOauth implements SocialOauth{
    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public String getInformation(String code) {
        return null;
    }
}
