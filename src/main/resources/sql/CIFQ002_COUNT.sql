SELECT COUNT(*) AS TOTAL_COUNT
FROM TB_CUSTOMER_INFO
WHERE 1=1
[AND ID_NUM LIKE '%' || :idNum || '%']
[AND CHINESE_NAME LIKE '%' || :chineseName || '%']
[AND GENDER = :gender]
[AND EDUCATION = :education]
[AND MOBILE LIKE '%' || :mobile || '%']
[AND EMAIL LIKE '%' || :email || '%']
[AND YEAR = :year]