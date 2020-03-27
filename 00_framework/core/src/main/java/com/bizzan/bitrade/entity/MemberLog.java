package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
@Document(collection = "member_log")
@ToString
public class MemberLog {

    @JsonFormat(timezone = "GMX+8",pattern = "yyyy-MM-dd")
    private Date date ;

    private int year ;

    @Max(value = 12)
    @Min(value = 1)
    private int month ;

    @Max(value = 31)
    @Min(value = 1)
    private int day ;

    /**
     * 当天的注册人数
     */
    private int registrationNum = 0;

    /**
     * 当天的实名认证人数
     */
    private int applicationNum = 0;

    /**
     * 当天的认证商家人数
     */
    private int bussinessNum = 0;
}
