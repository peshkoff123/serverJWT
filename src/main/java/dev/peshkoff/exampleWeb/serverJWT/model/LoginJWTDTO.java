package dev.peshkoff.exampleWeb.serverJWT.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginJWTDTO {
    private String name;
    private String password;
}
