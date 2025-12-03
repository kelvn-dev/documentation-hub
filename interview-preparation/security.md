# Security 

## OAuth 2.0

What is ?
- Là authorization framework cho phép user grant 3rd-party limited access tới data của họ nằm trên 1 service hay còn gọi là resource server (nơi host data), mà k cần phải share password trực tiếp
- Note: OAuth 2.0 is an authorization protocol and NOT an authentication protocol vì bản chất của nó là đi grant access to resource, chứ k phải provide i am

How it works ?
- Sử dụng access token để grant permission cho client app. Access token có thể có nhiều format, but mostly jwt

Roles
- Client: 3rd-party client app request access
- Authorization server: Server that issue access token, For example Auth0
- Resource server: Server that host data
- Resource owner: The user own resource

Scopes
- Specific permission to be granted, for example: read:user

Access token and Authorization code
- 1 vài authorization server có thể k trực tiếp trả về access token mà trả về authorization code dùng để exchange access token và optionally refresh token, vd như social login bằng gg

## OIDC

Is authentication protocol that works on top of the OAuth 2.0 framework, allow to log in to multiple applications with a single set of credentials, a process known as single sign-on (SSO)

Flows:
- OIDC process starts when a user tries to log in to an application (Relying Party).
- User is redirected to their identity provider login page, such as Google.
- user authenticates with the idP, and the provider sends an ID token back to the Relying Party.
- This ID token contains information about the user, which the Relying Party uses to verify their identity without needing to see the user's password

## Auth0

### Flow social login in auth0 spa

- Initiation: SPA initiates authentication request by redirecting the user's browser to the Auth0 authorization server.
- Auth0 to Google: Auth0 acting as a Relying Party (RP) to Google, redirects to Google login page.
- Google Authentication: The user logs in directly with Google and grants consent for sharing their information
- Callback: user login success thì browser back to Auth0's callback URL with authorization code
- Token Exchange: Auth0 exchanges the authorization code for an ID Token and Access Token to call gg api lấy user detail, sau đó tự gen token và trả về
- Auth0 to SPA: Auth0 then returns to SPA, providing its own tokens

OAuth 2.0 protocol is used at step return authorization code to exchange for access token

OIDC protocol is used at step login with gg and grant consent for sharing information

Why ID Token already user info but still need use access token to call api ? Because regarding OIDC specification, ID token's minimal structure has no data about user, just info about the authentication operation

### Use case nếu social login ở app B r dùng token để call api ở app A ?

Vậy dùng social login thì token dc trả về vẫn được kí bằng auth0 private key nên token từ app khác k verify được. Còn nếu 2 app dùng cùng 1 auth0 tenant thì validate field aud sẽ chứa clientId của app B chứ k phải app A.

### Flow gen and validate token

full flow là user login success thì auth0 là issuer sẽ dựa trên thuật toán bất đối xứng RS256 dùng private key để gen token và trả về, FE dùng token này để request data từ SpringBoot server through restapi, lúc này SpringBoot server là 1 resource server và sẽ verify token bằng cách đi fetch public key từ endpoint JWK (Json web key) Set URI do auth0 cung cấp. Ngoài ra server còn có thể implement thêm các bước validation như issuer, aud, exp, ...