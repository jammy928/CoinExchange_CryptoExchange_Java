package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author GS
 * @date 2017年12月29日
 */
@Data
public class LoginByEmail {

    @NotBlank(message = "{LoginByEmail.email.null}")
    @Email(message = "{LoginByEmail.email.format}")
    private String email;

    @NotBlank(message = "{LoginByEmail.password.null}")
    @Length(min = 6, max = 20, message = "{LoginByEmail.password.length}")
    private String password;

    @NotBlank(message = "{LoginByEmail.username.null}")
    @Length(min = 3, max = 20, message = "{LoginByEmail.username.length}")
    private String username;

    @NotBlank(message =  "{LoginByEmail.country.null}")
    private String country;

    private String promotion;

    //超级合伙人标识 0 普通  1 超级合伙人
    private String superPartner ;

}
