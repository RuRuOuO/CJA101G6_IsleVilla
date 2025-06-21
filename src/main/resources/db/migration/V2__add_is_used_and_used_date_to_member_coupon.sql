-- 在 member_coupon 表格中新增 is_used 和 used_date 欄位
ALTER TABLE member_coupon 
ADD COLUMN is_used INT NOT NULL DEFAULT 0 COMMENT '0=未使用, 1=已使用',
ADD COLUMN used_date DATE NULL COMMENT '使用日期';

-- 更新現有假資料，設定一些優惠券為已使用狀態
-- 假設 member_id=1 的會員使用過 coupon_id=1 的優惠券
UPDATE member_coupon 
SET is_used = 1, used_date = '2025-01-11' 
WHERE member_id = 1 AND coupon_id = 1;

-- 假設 member_id=5 的會員使用過 coupon_id=1 的優惠券
UPDATE member_coupon 
SET is_used = 1, used_date = '2024-04-05' 
WHERE member_id = 5 AND coupon_id = 1;


-- 其他優惠券保持未使用狀態（is_used = 0, used_date = NULL） 