package com.cathaybk.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TB_CUSTOMER_INFO")
// TODO Entity命名規範建議: CustomerInfoEntity
public class CustomerInfoEntity {

    /**
     * 流水號
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_order_seq")
    @SequenceGenerator(name = "customer_order_seq", sequenceName = "SEQ_CUSTOMER_ORDER_ID", allocationSize = 1)
    @Column(name = "ORDER_ID")
    private Long orderId;

    /**
     * 身份證號
     */
    @Column(name = "ID_NUM")
    private String idNum;

    /**
     * 中文姓名
     */
    @Column(name = "CHINESE_NAME")
    private String chineseName;

    /**
     * 性別
     */
    @Column(name = "GENDER")
    private String gender;

    /**
     * 學歷
     */
    @Column(name = "EDUCATION")
    private String education;

    /**
     * 戶籍地址郵遞區號
     */
    @Column(name = "ZIP_CODE_1")
    private String zipCode1;

    /**
     * 戶籍地址
     */
    @Column(name = "ADDRESS_1")
    private String address1;

    /**
     * 戶籍電話
     */
    @Column(name = "TELEPHONE_1")
    private String telephone1;

    /**
     * 現居地址郵遞區號
     */
    @Column(name = "ZIP_CODE_2")
    private String zipCode2;

    /**
     * 現居地址
     */
    @Column(name = "ADDRESS_2")
    private String address2;

    /**
     * 現居電話
     */
    @Column(name = "TELEPHONE_2")
    private String telephone2;

    /**
     * 行動電話
     */
    @Column(name = "MOBILE")
    private String mobile;

    /**
     * Email
     */
    @Column(name = "EMAIL")
    private String email;

    /**
     * 現居年限
     */
    @Column(name = "YEAR")
    private Integer year;
}