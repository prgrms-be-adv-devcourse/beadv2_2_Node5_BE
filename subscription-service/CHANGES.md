# Subscription Service Changes

- Added `ProductClient` (Feign) and `ProductInfoResponse` DTO to call `catalog-service` by `productId`.
- During subscription creation, fetch product details and persist `productId`, `productName`, `thumbnailUrl`, and `price` into the subscription; the create request no longer accepts a client-supplied price.
- Exposed stored product data in `SubscriptionInfo` responses and propagated name/thumbnail to order creation in the batch job.
- Added new subscription error codes for product lookup failures and handled Feign exceptions accordingly.
- Build check: `./gradlew :subscription-service:compileJava`.
