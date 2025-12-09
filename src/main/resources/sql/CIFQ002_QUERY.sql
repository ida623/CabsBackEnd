-- ====================================================================
-- CIFQ002_QUERY.sql
-- 功能：查詢符合條件的客戶列表（含分頁及排序）
-- 用途：取得分頁後的客戶資料清單，並依指定欄位排序
-- ====================================================================

SELECT
    -- 將資料庫欄位名稱對應到 Java DTO 的屬性名稱（使用駝峰命名）
    ORDER_ID AS "orderId",              -- 訂單編號（主鍵）
    ID_NUM AS "idNum",                  -- 身份證字號
    CHINESE_NAME AS "chineseName",      -- 中文姓名
    GENDER AS "gender",                 -- 性別
    EDUCATION AS "education",           -- 教育程度
    ZIP_CODE_1 AS "zipCode1",           -- 戶籍地郵遞區號
    ADDRESS_1 AS "address1",            -- 戶籍地址
    TELEPHONE_1 AS "telephone1",        -- 戶籍電話
    ZIP_CODE_2 AS "zipCode2",           -- 通訊地郵遞區號
    ADDRESS_2 AS "address2",            -- 通訊地址
    TELEPHONE_2 AS "telephone2",        -- 通訊電話
    MOBILE AS "mobile",                 -- 手機號碼
    EMAIL AS "email",                   -- 電子郵件
    YEAR AS "year"                      -- 年度
FROM TB_CUSTOMER_INFO
WHERE 1=1  -- 基礎條件，方便後續動態條件組合
    -- 以下為動態條件，由 [] 包裹表示為可選條件
    -- 只有當參數有值時，SqlUtils 才會將這些條件加入 SQL

    [AND ID_NUM LIKE '%' || :idNum || '%']              -- 身份證字號模糊查詢
    [AND CHINESE_NAME LIKE '%' || :chineseName || '%']  -- 中文姓名模糊查詢
    [AND GENDER LIKE '%' || :gender || '%']             -- 性別模糊查詢
    [AND EDUCATION LIKE '%' || :education || '%']       -- 教育程度模糊查詢
    [AND MOBILE LIKE '%' || :mobile || '%']             -- 手機號碼模糊查詢
    [AND EMAIL LIKE '%' || :email || '%']               -- 電子郵件模糊查詢
    [AND YEAR = :year]                                  -- 年度精確查詢

-- 動態排序：由 :ORDER_BY 參數決定
-- 例如：ORDER BY ORDER_ID DESC 或 ORDER BY CHINESE_NAME ASC
ORDER BY :ORDER_BY

-- Oracle 分頁語法（12c 以上版本）
-- OFFSET :offset ROWS：跳過前 N 筆資料
-- FETCH NEXT :pageSize ROWS ONLY：取接下來的 M 筆資料
-- 例如：OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY（取第 21~30 筆）
OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY