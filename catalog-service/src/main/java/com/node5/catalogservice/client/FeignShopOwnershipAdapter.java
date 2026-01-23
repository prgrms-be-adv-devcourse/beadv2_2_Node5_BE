package com.node5.catalogservice.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.node5.catalogservice.client.exception.ClientErrorCode;
import com.node5.common.exception.BaseException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeignShopOwnershipAdapter implements ShopOwnershipPort {

	private final ShopOwnershipFeignClient feignClient;

	@Override
	public UUID getOwnerMemberId(UUID shopId) {
		try {
			return feignClient.getOwnerMemberId(shopId);
		} catch (FeignException.NotFound e) {
			throw new BaseException(ClientErrorCode.SHOP_NOT_FOUND);
		} catch (FeignException e) {
			throw new BaseException(ClientErrorCode.SHOP_SERVICE_UNAVAILABLE);
		}
	}
}
