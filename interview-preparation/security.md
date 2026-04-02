# Security 

## OAuth 2.0

OAuth 2.0 is an authorization framework that allow user to grant 3rd-party limited access to data that's located on a resource server without the need to share password

Note: OAuth 2.0 is an authorization protocol and NOT an authentication protocol its nature is to grant access but not to prove who i am

After login successfully with provider like gg, redirect to a callback url with access token or authorization code to exhange for access token and optionally refresh token. Token can be of any format but mostly jwt. Then client can use this access token to call api to get data, for example google grant user:read permission and client can call gg api to get user details

## OIDC

Is authentication protocol that works on top of the OAuth 2.0 framework, allow to log in to multiple applications with a single set of credentials, this process is also known as single sign-on

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