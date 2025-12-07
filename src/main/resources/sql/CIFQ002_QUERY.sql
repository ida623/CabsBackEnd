SELECT ORDER_ID     AS orderId,
       ID_NUM       AS idNum,
       CHINESE_NAME AS chineseName,
       GENDER       AS gender,
       EDUCATION    AS education,
       ZIP_CODE_1   AS zipCode1,
       ADDRESS_1    AS address1,
       TELEPHONE_1  AS telephone1,
       ZIP_CODE_2   AS zipCode2,
       ADDRESS_2    AS address2,
       TELEPHONE_2  AS telephone2,
       MOBILE       AS mobile,
       EMAIL        AS email,
       YEAR         AS year
FROM TB_CUSTOMER_INFO
WHERE 1=1
[AND ID_NUM LIKE '%' || :idNum || '%']
[AND CHINESE_NAME LIKE '%' || :chineseName || '%']
[AND GENDER = :gender]
[AND EDUCATION = :education]
[AND MOBILE LIKE '%' || :mobile || '%']
[AND EMAIL LIKE '%' || :email || '%']
[AND YEAR = :year]
ORDER BY :ORDER_BY
OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY