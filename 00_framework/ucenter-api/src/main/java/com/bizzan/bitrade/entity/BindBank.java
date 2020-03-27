package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author GS
 * @date 2018年01月16日
 */
@Data
public class BindBank {
    @NotBlank(message = "{BindBank.realName.null}")
    private String realName;
    @NotBlank(message = "{BindBank.bank.null}")
    private String bank;
    @NotBlank(message = "{BindBank.branch.null}")
    private String branch;
    @NotBlank(message = "{BindBank.cardNo.null}")
    @Length(min = 15,max = 19,message = "{BindBank.cardNo.length}")
    private String cardNo;
    @NotBlank(message = "{BindBank.jyPassword.null}")
    private String jyPassword;
}
