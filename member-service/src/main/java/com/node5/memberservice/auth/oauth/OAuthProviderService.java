package com.node5.memberservice.auth.oauth;

import com.node5.memberservice.auth.application.dto.OAuthLoginCommand;
import com.node5.memberservice.auth.oauth.dto.OAuthUserInfo;

public interface OAuthProviderService {
    String getProviderName();
    OAuthUserInfo getUserInfo(OAuthLoginCommand command);
}
