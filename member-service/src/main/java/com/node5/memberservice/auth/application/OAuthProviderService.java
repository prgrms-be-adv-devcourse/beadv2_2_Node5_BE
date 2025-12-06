package com.node5.memberservice.auth.application;

import com.node5.memberservice.auth.application.dto.OAuthUserInfo;

public interface OAuthProviderService {
    String getProviderName();
    OAuthUserInfo getUserInfo(String code);
}
