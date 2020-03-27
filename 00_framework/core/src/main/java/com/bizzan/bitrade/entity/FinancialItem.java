package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.ToString;


/**
 * 币理财项目表
 * @author daishuyang
 *
 */
@Entity
@Data
@Table(name = "financial_item")
@ToString
public class FinancialItem {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	private String itemId;

	private String itemName;

	private String itemDesc;

	private Double yield;

	private int deadline;

	private BigDecimal coinMinnum;

	private String coinName;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	private int itemState;

}
