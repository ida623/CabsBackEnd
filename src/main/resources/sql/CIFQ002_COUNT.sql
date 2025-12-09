-- ====================================================================
-- CIFQ002_COUNT.sql
-- 功能：查詢符合條件的客戶總筆數
-- 用途：用於分頁查詢前，先計算總筆數以決定總頁數
-- ====================================================================

-- 查詢客戶總筆數
SELECT COUNT(*) AS TOTAL_COUNT
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