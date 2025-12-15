-- CIFQ002_QUERY.sql

-- TODO 保留字小寫
select
    ORDER_ID as "orderId",
    ID_NUM as "idNum",
    CHINESE_NAME as "chineseName",
    GENDER as "gender",
    EDUCATION as "education",
    ZIP_CODE_1 as "zipCode1",
    ADDRESS_1 as "address1",
    TELEPHONE_1 as "telephone1",
    ZIP_CODE_2 as "zipCode2",
    ADDRESS_2 as "address2",
    TELEPHONE_2 as "telephone2",
    MOBILE as "mobile",
    EMAIL as "email",
    YEAR as "year"
-- TODO +schema
from TB_CUSTOMER_INFO
where 1=1

    -- TODO 建議於impl拼接字串
    [and ID_NUM like :idNum]
    [and CHINESE_NAME like :chineseName]
    [and GENDER like :gender]
    [and EDUCATION like :education]
    [and MOBILE like :mobile]
    [and EMAIL like :email]
    [and YEAR = :year]
order by :ORDER_BY
offset :offset rows fetch next :pageSize rows only