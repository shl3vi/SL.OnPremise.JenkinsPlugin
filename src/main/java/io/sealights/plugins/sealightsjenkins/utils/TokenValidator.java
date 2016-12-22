package io.sealights.plugins.sealightsjenkins.utils;

import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.entities.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class TokenValidator {
    public static final String FIELD_CANT_BE_NULL_OR_EMPTY = "field cannot be null or empty.";
    public static final String FIELD_CANT_BE_NULL = "field cannot be null.";
    public static final String TOKEN_DATA_FIELD = "tokenData";
    public static final String ROLE_FIELD  = "Role";
    public static final String CUSTOMER_ID_FIELD  = "CustomerId";
    public static final String SERVER_FIELD  = "Server";

    public List<ValidationError> validate(TokenData tokenData){
        List<ValidationError> validationErrors = new ArrayList<>();
        if (tokenData == null)
        {
            validationErrors.add(new ValidationError(TOKEN_DATA_FIELD, FIELD_CANT_BE_NULL));
            return validationErrors;
        }

        String role = tokenData.getRole();
        if (StringUtils.isNullOrEmpty(role)){
            validationErrors.add(new ValidationError(ROLE_FIELD, FIELD_CANT_BE_NULL_OR_EMPTY));
        }else if (!role.equals(TokenData.AgentRole)){
            validationErrors.add(new ValidationError(ROLE_FIELD, "Expected role: '" + TokenData.AgentRole + "'. Actual role: '" + tokenData.getRole() +"'"));
        }

        if (StringUtils.isNullOrEmpty(tokenData.getCustomerId())){
            validationErrors.add(new ValidationError(CUSTOMER_ID_FIELD, FIELD_CANT_BE_NULL_OR_EMPTY));
        }

        if (StringUtils.isNullOrEmpty(tokenData.getServer())){
            validationErrors.add(new ValidationError(SERVER_FIELD, FIELD_CANT_BE_NULL_OR_EMPTY));
        }

        return validationErrors;
    }



}