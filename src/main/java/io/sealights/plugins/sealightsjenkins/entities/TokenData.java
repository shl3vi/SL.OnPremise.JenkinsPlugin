package io.sealights.plugins.sealightsjenkins.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sealights.plugins.sealightsjenkins.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenData {
    private static final int NUMBER_OF_IWJT_TOKEN_PARTS = 3;
    private String token;
    private String customerId;
    private String subject;
    private String role;
    private String server;
    /*
    * Users should use tokens which has the "agent" role.
    * Any other role is not allowed.
    * */
    public final static String AgentRole = "agent";

    /*
    * That's the first part of token. It is encoded in Base64.
    * The part includes metadata about the token (the fact that's a JWT Token and the encryption type).
    * */
    public final static String TokenPrefix = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.";

    @JsonProperty("x-sl-customerId")
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        if (customerId != null)
            return customerId;

        if (this.getSubject() != null && this.getSubject().contains("@")) {
            String[] parts = this.getSubject().split("@");
            if (parts.length == 2) {
                this.customerId = parts[1];
            }
        }

        return customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRole() {
        return role;
    }

    @JsonProperty("x-sl-role")
    public void setRole(String role) {
        this.role = role;
    }

    public String getServer() {
        return server;
    }

    @JsonProperty("x-sl-server")
    public void setServer(String server) {
        this.server = server;
    }

    public static TokenData parse(String token) {
        try {
            String fullToken = token;
            if (StringUtils.isNullOrEmpty(token)) {
                return new TokenData();
            }
            String[] parts = token.split("\\.");
            validateTokenParts(token, parts);
            token = parts[1];

            byte[] bytes = Base64.decodeBase64(token);
            String tokenAsJson = new String(bytes);
            ObjectMapper mapper = new ObjectMapper();
            TokenData tokenData = mapper.readValue(tokenAsJson, TokenData.class);
            tokenData.setToken(fullToken);
            return tokenData;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return new TokenData();
        }
    }

    private static void validateTokenParts(String token, String[] parts) {
        if (parts.length != NUMBER_OF_IWJT_TOKEN_PARTS)
            throw new IllegalArgumentException("Token is not valid. The token should have " + NUMBER_OF_IWJT_TOKEN_PARTS + " parts but had " + parts.length + ". Token:" + token);

        token = parts[1];
        if (!Base64.isBase64(token)) {
            throw new IllegalArgumentException("Token is not a valid Base64 string. Token:" + token);
        }

    }
}
