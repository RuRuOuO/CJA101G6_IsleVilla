CREATE DATABASE IF NOT EXISTS islevilla;

use islevilla;
/*
TABLE刪除順序：被關聯最多的TABLE最晚刪除
其他（最先刪除）
員工
購物
接駁
訂房
會員（最後刪除）
*/

-- 其他
DROP TABLE IF EXISTS feedback;                   -- 問卷紀錄
DROP TABLE IF EXISTS news;                       -- 最新消息
DROP TABLE IF EXISTS carousel_photo;             -- 輪播圖片
-- 員工
DROP TABLE IF EXISTS employee_permission;        -- 員工權限對應
DROP TABLE IF EXISTS permission;                 -- 權限
DROP TABLE IF EXISTS operation_log;              -- 操作日誌
DROP TABLE IF EXISTS employee;                   -- 員工
DROP TABLE IF EXISTS department;                 -- 部門
-- 購物
DROP TABLE IF EXISTS product_order_detail;       -- 商品訂單明細
DROP TABLE IF EXISTS product_order;              -- 商品訂單
DROP TABLE IF EXISTS product_photo;              -- 商品圖片
DROP TABLE IF EXISTS cart;                       -- 購物車
DROP TABLE IF EXISTS product;                    -- 商品
DROP TABLE IF EXISTS member_coupon;              -- 會員優惠券對應
DROP TABLE IF EXISTS coupon;                     -- 優惠券
DROP TABLE IF EXISTS product_category;           -- 商品種類
-- 接駁
DROP TABLE IF EXISTS temp_shuttle_reservation_request; -- 臨時接駁預約請求表
DROP TABLE IF EXISTS shuttle_reservation_seat;   -- 接駁預約座位
DROP TABLE IF EXISTS shuttle_reservation;        -- 接駁預約
DROP TABLE IF EXISTS shuttle_seat_availability;  -- 各接駁班次座位剩餘量
DROP TABLE IF EXISTS shuttle_schedule;           -- 接駁班次
DROP TABLE IF EXISTS seat;                       -- 座位
-- 訂房
DROP TABLE IF EXISTS room_reservation_detail;    -- 訂房明細
DROP TABLE IF EXISTS room_reservation_order;     -- 訂房訂單
DROP TABLE IF EXISTS room_promotion_price;       -- 房型優惠價格
DROP TABLE IF EXISTS promotion;                  -- 優惠專案清單
DROP TABLE IF EXISTS room;                       -- 房間
DROP TABLE IF EXISTS room_type_photo;            -- 房型圖片
DROP TABLE IF EXISTS room_type_availability;     -- 各房型空房庫存量
DROP TABLE IF EXISTS room_type;                  -- 房型
-- 會員
DROP TABLE IF EXISTS members;                    -- 會員


/***************************************************************************************************
********************************************* 會員相關 *********************************************
***************************************************************************************************/


-- 會員 members （賴彥儒）
CREATE TABLE members (
	member_id              INT           AUTO_INCREMENT,                                           -- 會員編號 (PK)
    member_email           VARCHAR(100)  NOT NULL  UNIQUE,                                         -- 會員信箱 (UNIQLE)
    member_password_hash   VARCHAR(128)  NOT NULL,                                                 -- 會員雜湊密碼
    member_name            VARCHAR(30)   NOT NULL,                                                 -- 會員姓名
    member_birthdate       DATE          NOT NULL,                                                 -- 會員生日
    member_gender          TINYINT       NOT NULL  COMMENT '0:男生 1:女生 2:其它',                 -- 會員性別
    member_phone           VARCHAR(20)   NOT NULL  UNIQUE,                                         -- 會員電話
    member_address         VARCHAR(200)  NOT NULL,                                                 -- 會員地址
    member_photo           LONGBLOB,                                                               -- 會員照片
    member_created_at      DATETIME      NOT NULL,                                                 -- 會員建立日期
    member_updated_at      DATETIME      NOT NULL,                                                 -- 會員更新日期
    member_last_login_time DATETIME      NOT NULL,                                                 -- 會員最後登入時間
    member_status          TINYINT       NOT NULL  DEFAULT 0  COMMENT '0:未驗證 1:已驗證 2:停用',  -- 會員狀態
    CONSTRAINT pk_member_id PRIMARY KEY (member_id)
);

INSERT INTO members (member_email, member_password_hash, member_name,  member_birthdate,  member_gender,
					 member_phone, member_address,       member_photo, member_created_at, member_updated_at, member_last_login_time, member_status)
VALUES ('member01@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '王小明', '1997-03-05', 0, '0900000001', '台北市信義路100號3樓之一', NULL, '2023-07-01 10:15:30', '2025-04-10 14:20:00', '2025-05-19 18:45:10', 1),
	   ('member02@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '林小華', '1998-03-05', 1, '0900000002', '台中市西屯路88號',         NULL, '2023-07-15 08:30:00', '2025-04-11 09:15:00', '2025-05-19 12:00:00', 1),
	   ('member03@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '陳大文', '1999-03-05', 0, '0900000003', '高雄市中山路99號',         NULL, '2023-08-01 16:45:00', '2025-04-12 19:30:00', '2025-05-19 21:10:00', 1),
	   ('member04@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '張怡君', '2000-03-05', 1, '0900000004', '新竹市經國路66號',         NULL, '2023-08-15 11:00:00', '2025-04-13 13:20:00', '2024-05-19 17:55:00', 1),
	   ('member05@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '周志強', '2001-03-05', 0, '0900000005', '桃園市復興路10號',         NULL, '2023-09-01 09:00:00', '2025-04-14 10:30:00', '2025-05-19 15:40:00', 1),
	   ('member06@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '蔡美麗', '2002-03-05', 0, '0900000006', '台南市永康區中正街5號',    NULL, '2023-09-15 07:50:00', '2025-04-15 08:30:00', '2025-05-19 20:00:00', 1),
	   ('member07@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '何玉芬', '2003-03-05', 0, '0900000007', '台北市大安區光復南路11號', NULL, '2023-10-01 14:10:00', '2025-04-16 16:45:00', '2025-05-19 18:15:00', 1),
	   ('member08@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '黃金龍', '2004-03-05', 1, '0900000008', '嘉義市博愛路3段77號',      NULL, '2023-10-15 18:00:00', '2025-04-17 19:20:00', '2025-05-19 22:30:00', 1),
	   ('member09@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '劉阿成', '2005-03-05', 1, '0900000009', '台中市南區建國南路20號',   NULL, '2023-11-01 12:15:00', '2025-04-18 13:10:00', '2025-05-19 14:55:00', 0),
	   ('member10@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '楊淑惠', '2006-03-05', 1, '0900000010', '基隆市安樂區文德路88號',   NULL, '2023-11-15 09:05:00', '2024-04-19 11:30:00', '2025-05-19 16:45:00', 0),
	   ('member11@gmail.com', '$argon2i$v=19$m=65536,t=3,p=1$Zi9GtJ8A1kAbFRInjhIofg$1p4djCm5PXCC650dGk+z0vKc7JX/LlhY0dRJ79FDMHo', -- 密碼：asd123456
        '測試用', '1912-01-01', 0, '0900000011', '台北市信義路100號3樓之二', NULL, '2023-07-01 10:15:30', '2025-04-20 14:30:00', '2025-05-19 18:46:10', 1);

-- DROP TABLE IF EXISTS members;


/***************************************************************************************************
********************************************* 訂房相關 *********************************************
***************************************************************************************************/


-- 優惠專案清單 promotion （曾宸瑩）
CREATE TABLE promotion (
	room_promotion_id     INT           AUTO_INCREMENT  PRIMARY KEY,  -- 優惠專案編號 (PK)
	room_promotion_title  VARCHAR(20)   NOT NULL,                     -- 優惠專案名稱
	promotion_start_date  DATE          NOT NULL,                     -- 專案開始日期
	promotion_end_date    DATE          NOT NULL,                     -- 專案結束日期
	promotion_remark      VARCHAR(50),                                -- 專案備註
	CONSTRAINT promotion_date_check CHECK (promotion_end_date >= promotion_start_date)  -- 檢查日期
);

INSERT INTO promotion (room_promotion_title, promotion_start_date, promotion_end_date, promotion_remark)
VALUES ('春假專案',     '2025-03-10', '2025-03-20', '適合學生出遊'),
       ('暑期優惠',     '2025-07-01', '2025-08-31', '指定房型享 85 折優惠'),
       ('週末快閃特惠', '2025-09-06', '2025-09-08', '僅限週末住房'),
       ('限時快閃',     '2025-09-15', '2025-09-17', '限時兩天，先搶先贏'),
       ('早鳥優惠',     '2025-10-01', '2025-10-15', '提前預訂享雙人房 75 折'),
       ('中秋促銷',     '2025-10-03', '2025-10-06', '歡慶中秋佳節海景房型優惠中'),
       ('秋季放鬆行程', '2025-10-20', '2025-10-31', '享受秋天悠閒時光'),
       ('週年活動',     '2025-11-01', '2025-11-10', '慶祝微嶼週年'),
       ('聖誕禮物',     '2025-12-24', '2025-12-26', '聖誕節高價位房型回饋價'),
       ('元旦開年優惠', '2026-01-01', '2026-01-07', '開年住宿折扣');

-- DROP TABLE IF EXISTS promotion;


-- 訂房訂單 room_reservation_order （陳薇淨）
CREATE TABLE room_reservation_order (
	room_reservation_id  INT           AUTO_INCREMENT,                                                       		-- 訂房編號 (PK) (AI)
    member_id            INT           NOT NULL,                                                             		-- 會員編號 (FK)
    room_order_date      DATETIME      NOT NULL,                                                             		-- 訂單日期
    room_order_status    TINYINT       NOT NULL  DEFAULT 0  COMMENT '0:成立 1:已付款 2:已完成 3:申請取消 4:確認取消',	-- 訂單狀態
    check_in_date        DATE          NOT NULL,                                                             		-- 入住日期
    check_out_date       DATE          NOT NULL,                                                             		-- 退房日期
    room_promotion_id    INT,                                                                                		-- 優惠專案編號 (FK)
    rv_remark            VARCHAR(100),                                                                       		-- 訂房備註
    room_total_amount    INT           NOT NULL,                                                             		-- 訂房總金額
    rv_discount_amount   INT           NOT NULL,                                                             		-- 折扣金額
    rv_paid_amount       INT           NOT NULL,                                                             		-- 實際付款金額
    CONSTRAINT pk_room_reservation_id PRIMARY KEY(room_reservation_id),
    CONSTRAINT fk_rro_member_id FOREIGN KEY(member_id) REFERENCES members(member_id),
    CONSTRAINT fk_rro_room_promotion_id FOREIGN KEY(room_promotion_id) REFERENCES promotion(room_promotion_id)
);

INSERT INTO room_reservation_order (member_id, room_order_date, room_order_status, check_in_date, check_out_date, room_promotion_id, rv_remark, room_total_amount, rv_discount_amount, rv_paid_amount)
VALUES (1,  '2024-01-01 09:24:18', 2, '2024-03-10', '2024-03-15', 1,    '備註', 3600*3,                  (3600-2160)*3, 2160*3),                  -- 3天 -- 優惠1
       (4,  '2024-05-09 01:52:22', 1, '2025-05-17', '2025-05-18', NULL, '備註', (3600+4800)*1,           0,             (3600+4800)*1),           -- 1天 
       (8,  '2024-09-12 21:03:18', 1, '2025-01-26', '2025-01-31', NULL, '備註', (6000+3600+6000+8400)*5, 0,             (6000+3600+6000+8400)*5), -- 5天
       (7,  '2025-05-11 17:34:59', 1, '2025-05-21', '2025-05-22', NULL, '備註', 8400*1,                  0,             8400*1),                  -- 1天
       (2,  '2025-07-24 15:37:12', 3, '2025-07-25', '2025-07-29', 2,    '備註', (7200+9600)*4,           (7200-6120)*4, (6120+9600)*4);           -- 4天 -- 優惠2

-- DROP TABLE IF EXISTS room_reservation_order;


-- 房型 room_type （詹力臻）
CREATE TABLE room_type (
	room_type_id           INT            NOT NULL  PRIMARY KEY,                            -- 房型編號 (PK)
	room_type_code         CHAR(3)        NOT NULL,                                         -- 房型代碼
	room_type_name         VARCHAR(10)    NOT NULL,                                         -- 房型名稱
	room_type_quantity     INT            NOT NULL,                                         -- 房間數量
	room_type_capacity     INT            NOT NULL,                                         -- 房間容納人數
	room_type_content      VARCHAR(1000)  NOT NULL,                                         -- 房間說明
	room_type_price        INT            NOT NULL,                                         -- 房間原價
	room_type_sale_status  TINYINT        NOT NULL  DEFAULT 0  COMMENT '0:下架中 1:上架中'  -- 房型上下架
);

INSERT INTO  room_type (room_type_id, room_type_code, room_type_name, room_type_quantity, room_type_capacity, room_type_content, room_type_price, room_type_sale_status)
VALUES (1, 'A01', '標準雙人房', 10, 2, '
		床型：雙人床
		坪數：9坪
		衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
		娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
		舒適設備｜Morning call、空調、室內拖鞋
		餐飲服務｜冰箱、快煮壺、即溶咖啡、茶包
		衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
		安全特色｜晶片式安全感應門鎖', 3600, 1),
	   (2, 'B02', '豪華海景雙人房', 5, 2, '
	    床型：雙人床
	    坪數：12坪
	    衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
	    娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
	    舒適設備｜Morning call、空調、室內拖鞋
	    餐飲服務｜冰箱、即溶咖啡、茶包、快煮壺
	    衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
	    安全特色｜晶片式安全感應門鎖', 4800, 1),
	   (3, 'C03', '豪華家庭房', 8, 4, '
	    床型：雙人床
	    坪數：9坪
	    衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
	    娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
	    舒適設備｜Morning call、空調、室內拖鞋
	    餐飲服務｜冰箱、快煮壺、即溶咖啡、茶包
	    衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
	    安全特色｜晶片式安全感應門鎖', 6000, 1),
	   (4, 'D04', '豪華海景家庭房', 8, 4, '
	    床型：雙人床
	    坪數：12坪
	    衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
	    娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
	    舒適設備｜Morning call、空調、室內拖鞋
	    餐飲服務｜冰箱、即溶咖啡、茶包、快煮壺
	    衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
	    安全特色｜晶片式安全感應門鎖', 7200, 1),
	   (5, 'E05', '海景樓中樓家庭房', 2, 6, '
	    床型：雙人床
	    坪數：18坪
	    衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
	    娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
	    舒適設備｜Morning call、空調、室內拖鞋
	    餐飲服務｜冰箱、即溶咖啡、茶包、快煮壺
	    衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
	    安全特色｜晶片式安全感應門鎖', 8400, 0),
	   (6, 'F06', '行政豪華月牙套房', 3, 2, '
	    床型：雙人床
	    坪數：18坪
	    衛浴設備｜分離式淋浴、浴缸、吹風機、盥洗用品（洗髮乳、沐浴乳、洗手乳)、免治馬桶
	    娛樂｜Wi-Fi、液晶電視、有線頻道、國際直撥電話服務
	    舒適設備｜Morning call、空調、室內拖鞋
	    餐飲服務｜冰箱、即溶咖啡、茶包、快煮壺
	    衣物與洗衣設備｜衣櫃衣架、洗衣乾洗服務
	    安全特色｜晶片式安全感應門鎖', 9600, 0);

-- DROP TABLE IF EXISTS room_type;


-- 房間 room （詹力臻）
CREATE TABLE room (
	room_id       INT      NOT NULL,                                                          -- 房號 (PK)
	room_type_id  INT      NOT NULL,                                                          -- 房型編號 (FK)
	room_status   TINYINT  NOT NULL  DEFAULT 0  COMMENT '0:空房 1:入住中 2:待維修 3:待清潔',  -- 房間狀態
    CONSTRAINT pk_room_id PRIMARY KEY (room_id),
	CONSTRAINT fk_room_room_type_id FOREIGN KEY (room_type_id) REFERENCES room_type(room_type_id)
);

INSERT INTO room (room_id, room_type_id, room_status)
VALUES (101,  1, 0),
       (201,  1, 0),
       (501,  1, 0),
       (502,  1, 0),
       (507,  1, 0),
       (508,  1, 0),
       (509,  1, 0),
       (602,  1, 0),
       (603,  1, 0),
       (607,  1, 2),
       (703,  1, 0),
       -- 房型1 共11間（空房10間，待維修1間）
       (301,  2, 0),
       (503,  2, 0),
       (505,  2, 0),
       (608,  2, 0),
       (609,  2, 0),
       -- 房型2 共5間（空房5間，待維修0間）
       (401,  3, 0),
	   (506,  3, 0),
       (601,  3, 0),
       (605,  3, 0),
       (606,  3, 0),
       (702,  3, 0),
       (705,  3, 0),
       (706,  3, 0),
       (707,  3, 2),
       (708,  3, 0),
       -- 房型3 共10間（空房9間，待維修1間）
       (701,  4, 0),
       (802,  4, 0),
       (803,  4, 0),
       (804,  4, 0),
       (805,  4, 0),
       (806,  4, 0),
       (902,  4, 0),
       -- 房型4 共7間（空房7間，待維修0間）
       (801,  5, 0),
       (901,  5, 0),
       (903,  5, 0),
       (905,  5, 0),
       -- 房型5 共4間（空房4間，待維修0間）
       (1001, 6, 0),
       (1002, 6, 0),
       (1003, 6, 0);
       -- 房型6 共3間（空房3間，待維修0間）
       
-- DROP TABLE IF EXISTS room;


-- 訂房明細 room_reservation_detail （陳薇淨）
CREATE TABLE room_reservation_detail(
	room_reservation_detail_id  INT  AUTO_INCREMENT,  -- 訂房明細編號 (PK)
	room_reservation_id         INT  NOT NULL,        -- 訂房編號 (FK)
    room_id                     INT  NOT NULL,        -- 房號 (FK)
    room_type_id                INT  NOT NULL,        -- 房型編號 (FK)
    guest_count                 INT  NOT NULL,        -- 入住人數
    room_price                  INT  NOT NULL,        -- 購買時單價
    rv_discount_amount          INT  NOT NULL,        -- 折扣金額
    rv_paid_amount              INT  NOT NULL,        -- 實際付款金額
    CONSTRAINT pk_room_reservation_detail_id PRIMARY KEY(room_reservation_detail_id),
    CONSTRAINT fk_rrd_room_reservation_id FOREIGN KEY(room_reservation_id) REFERENCES room_reservation_order(room_reservation_id),
    CONSTRAINT fk_rrd_room_id FOREIGN KEY(room_id) REFERENCES room(room_id),
    CONSTRAINT fk_rrd_room_type_id FOREIGN KEY(room_type_id) REFERENCES room_type(room_type_id)
);

INSERT INTO room_reservation_detail (room_reservation_id, room_id, room_type_id, guest_count, room_price, rv_discount_amount, rv_paid_amount)
VALUES (1, 101,  1, 2, 3600*3, (3600-2160)*3, 2160*3), -- 訂房訂單1 房號101  房型1 2人 原單價:3600 -- 3天 -- 優惠1
	   (2, 201,  1, 2, 3600*1, 0,             3600*1), -- 訂房訂單2 房號201  房型1 2人 原單價:3600 -- 1天
	   (2, 301,  2, 2, 4800*1, 0,             4800*1), -- 訂房訂單2 房號301  房型2 2人 原單價:4800 -- 1天
       (3, 401,  3, 4, 6000*5, 0,             6000*5), -- 訂房訂單3 房號401  房型3 4人 原單價:6000 -- 5天
       (3, 501,  1, 2, 3600*5, 0,             3600*5), -- 訂房訂單3 房號501  房型1 2人 原單價:3600 -- 5天
       (3, 601,  3, 1, 6000*5, 0,             6000*5), -- 訂房訂單3 房號601  房型3 1人 原單價:6000 -- 5天
       (3, 901,  5, 2, 8400*5, 0,             8400*5), -- 訂房訂單3 房號901  房型5 2人 原單價:8400 -- 5天
       (4, 801,  5, 3, 8400*1, 0,             8400*1), -- 訂房訂單4 房號801  房型5 3人 原單價:8400 -- 4天
       (5, 701,  4, 2, 7200*4, (7200-6120)*4, 6120*4), -- 訂房訂單5 房號701  房型4 2人 原單價:7200 -- 4天 -- 優惠2
       (5, 1001, 6, 2, 9600*4, 0,             9600*4); -- 訂房訂單5 房號1001 房型6 2人 原單價:9600 -- 4天 -- 優惠2

-- DROP TABLE IF EXISTS room_reservation_detail;


-- 房型優惠價格 room_promotion_price （曾宸瑩）
CREATE TABLE room_promotion_price (
	room_promotion_id    INT  NOT NULL,  -- 優惠專案編號 (PK, FK)
	room_type_id         INT  NOT NULL,  -- 房型編號 (PK, FK)
	room_discount_price  INT  NOT NULL,  -- 房間折扣後價格
	CONSTRAINT pk_room_promotion_id_room_type_id PRIMARY KEY (room_promotion_id, room_type_id),
	FOREIGN KEY (room_promotion_id) REFERENCES promotion (room_promotion_id) ,
	FOREIGN KEY (room_type_id) REFERENCES room_type (room_type_id)
);

INSERT INTO room_promotion_price (room_promotion_id, room_type_id, room_discount_price)
VALUES -- 1. 春假專案：針對雙人房 6 折吸引學生族群
       (1, 1, 2160),  -- 標準雙人房 6 折
       (1, 2, 2880),  -- 豪華海景雙人房 6 折
       -- 2. 暑期優惠：指定房型 85 折
       (2, 1, 3060),  -- 標準雙人房 85 折
       (2, 2, 4080),  -- 豪華海景雙人房 85 折
       (2, 3, 5100),  -- 豪華家庭房 85 折
       (2, 4, 6120),  -- 豪華海景家庭房 85 折
       -- 3. 週末快閃特惠：僅雙人房（含海景）享 9 折
       (3, 1, 3240),  -- 標準雙人房 9 折
       (3, 2, 4320),  -- 豪華海景雙人房 9 折
       -- 4. 限時快閃：便宜房型超低 7 折
       (4, 1, 2520),  -- 標準雙人房 7 折
       -- 5. 早鳥優惠：雙人房 75 折
       (5, 1, 2700),  -- 標準雙人房 75 折
       (5, 2, 3600),  -- 豪華海景雙人房 75 折
       -- 6. 中秋促銷：海景房型中秋優惠
       (6, 2, 4080),  -- 豪華海景雙人房 85 折
       (6, 4, 6120),  -- 豪華海景家庭房 85 折
       (6, 5, 7480),  -- 海景樓中樓家庭房 85 折
       -- 7. 秋季放鬆：中價房型舒壓優惠
       (7, 2, 4080),  -- 豪華海景雙人房 85 折
       (7, 3, 5100),  -- 豪華家庭房 85 折
       -- 8. 週年活動：超大優惠回饋
       (8, 1, 2700),  -- 標準雙人房 75 折
       (8, 2, 3600),  -- 豪華海景雙人房 75 折
       (8, 3, 4500),  -- 豪華家庭房 75 折
       -- 9. 聖誕禮物：最高價房型專屬折扣
       (9, 5, 7040),  -- 海景樓中樓家庭房 8 折
       (9, 6, 7680),  -- 行政豪華月牙套房 8 折
       -- 10. 元旦開年優惠：全館限量 8 折
       (10, 1, 2880),  -- 標準雙人房 8 折
       (10, 2, 3840),  -- 豪華海景雙人房 8 折
       (10, 3, 4800),  -- 豪華家庭房 8 折
       (10, 4, 5760);  -- 豪華海景家庭房 8 折

-- DROP TABLE IF EXISTS room_promotion_price;


-- 房型圖片 room_type_photo （詹力臻）
CREATE TABLE room_type_photo (
	room_type_photo_id  INT       AUTO_INCREMENT,  -- 房型圖片編號 (PK) (AI)
	room_type_id        INT       NOT NULL,        -- 房型編號 (FK)
	room_type_photo     LONGBLOB,                  -- 房型圖片
    CONSTRAINT pk_room_type_photo_id PRIMARY KEY (room_type_photo_id),
	CONSTRAINT fk_room_type_id FOREIGN KEY (room_type_id) REFERENCES room_type(room_type_id)
);

INSERT INTO room_type_photo (room_type_id)
VALUES (1),
       (1),
       (1),
       (2),
       (2),
       (2),
       (3),
       (3),
       (3),
       (4),
       (4),
       (4),
       (5),
       (5),
       (5),
       (6),
       (6),
       (6);

-- DROP TABLE IF EXISTS room_type_photo;


-- 各房型空房庫存量 room_type_availability （詹力臻）
CREATE TABLE room_type_availability(
	room_type_id                  INT   NOT NULL,
	room_type_availability_date   DATE  NOT NULL,
	room_type_availability_count  INT   NOT NULL,
	CONSTRAINT pk_room_type_id_room_type_availability_date PRIMARY KEY (room_type_id, room_type_availability_date),
	CONSTRAINT fk_room_type_availability_room_type_id FOREIGN KEY (room_type_id) REFERENCES room_type(room_type_id)
);

INSERT INTO room_type_availability (room_type_id, room_type_availability_date, room_type_availability_count)
VALUES (1, '2024-03-10', 9),
       (1, '2024-03-11', 9),
       (1, '2024-03-12', 9),
       (1, '2024-03-13', 9),
       (1, '2024-03-14', 9),
       (1, '2024-03-15', 10),
       (1, '2025-01-26', 9),
       (3, '2025-01-26', 7),
       (5, '2025-01-26', 3),
       (1, '2025-01-27', 9),
       (3, '2025-01-27', 7),
       (5, '2025-01-27', 3),
       (1, '2025-01-28', 9),
       (3, '2025-01-28', 7),
       (5, '2025-01-28', 3),
       (1, '2025-01-29', 9),
       (3, '2025-01-29', 7),
       (5, '2025-01-29', 3),
       (1, '2025-01-30', 9),
       (3, '2025-01-30', 7),
       (5, '2025-01-30', 3),
       (1, '2025-01-31', 10),
       (3, '2025-01-31', 9),
       (5, '2025-01-31', 4),
       (1, '2025-05-17', 9),
       (2, '2025-05-17', 4),
       (1, '2025-05-18', 10),
       (2, '2025-05-18', 5),
       (5, '2025-05-21', 3),
       (5, '2025-05-22', 4),
       (4, '2025-07-25', 6),
       (6, '2025-07-25', 2),
       (4, '2025-07-26', 6),
       (6, '2025-07-26', 2),
       (4, '2025-07-27', 6),
       (6, '2025-07-27', 2),
       (4, '2025-07-28', 6),
       (6, '2025-07-28', 2),
       (4, '2025-07-29', 7),
       (6, '2025-07-29', 3);
-- DROP TABLE IF EXISTS room_type_availability;


/***************************************************************************************************
********************************************* 接駁相關 *********************************************
***************************************************************************************************/


-- 接駁班次 shuttle_schedule （王人慶）
CREATE TABLE shuttle_schedule (
	shuttle_schedule_id     INT       AUTO_INCREMENT  PRIMARY KEY,       -- 接駁班次編號 
	shuttle_direction       TINYINT   NOT NULL  COMMENT '0:去程 1:回程',  -- 去回程
	shuttle_departure_time  TIME      NOT NULL,                          -- 出發時間
	shuttle_arrival_time    TIME      NOT NULL                           -- 抵達時間
);

INSERT INTO shuttle_schedule (shuttle_direction, shuttle_departure_time, shuttle_arrival_time) 
VALUES (0, '08:00:00', '09:00:00'),
	   (1, '09:00:00', '10:00:00'),
       (0, '10:00:00', '11:00:00'),
       (1, '11:00:00', '12:00:00'),
       (0, '14:00:00', '15:00:00'),
       (1, '15:00:00', '16:00:00'),
       (0, '16:00:00', '17:00:00'),
       (1, '17:00:00', '18:00:00');

-- DROP TABLE IF EXISTS shuttle_schedule;


-- 接駁預約 shuttle_reservation （賴彥儒）
CREATE TABLE shuttle_reservation (
	shuttle_reservation_id     INT      AUTO_INCREMENT  PRIMARY KEY,       -- 接駁預約編號 (PK) (AI)
    member_id                  INT      NOT NULL,                          -- 會員編號 (FK)
    room_reservation_id        INT      NOT NULL,                          -- 訂房編號 (FK)
    shuttle_date               DATE     NOT NULL,                          -- 接駁日期
    shuttle_schedule_id        INT      NOT NULL,                          -- 接駁班次編號 (FK)
    shuttle_direction          TINYINT  NOT NULL  COMMENT '0:去程 1:回程',  -- 接駁去回程
    shuttle_number             INT      NOT NULL,                          -- 接駁人數
    shuttle_reservation_status TINYINT  NOT NULL  COMMENT '0:取消 1:正常',  -- 接駁預約狀態
    FOREIGN KEY(member_id) REFERENCES members(member_id),
    FOREIGN KEY(room_reservation_id) REFERENCES room_reservation_order(room_reservation_id),
    FOREIGN KEY(shuttle_schedule_id) REFERENCES shuttle_schedule(shuttle_schedule_id)
);

INSERT INTO shuttle_reservation (member_id, room_reservation_id, shuttle_date, shuttle_schedule_id, shuttle_direction, shuttle_number, shuttle_reservation_status)
VALUES (1, 1, '2024-03-10', 1, 0, 2, 1), -- 訂單1 2人 去程
	   (1, 1, '2024-03-14', 2, 1, 2, 0), -- 訂單1 2人 回程 取消
	   (1, 1, '2024-03-15', 2, 1, 2, 1), -- 訂單1 2人 回程
       (4, 2, '2025-05-17', 3, 0, 4, 1), -- 訂單2 4人 去程
       (4, 2, '2025-05-18', 4, 1, 4, 1), -- 訂單2 4人 回程
       (8, 3, '2025-01-26', 5, 0, 9, 1), -- 訂單3 9人 去程
       (8, 3, '2025-01-31', 6, 1, 9, 1), -- 訂單3 9人 回程
       (7, 4, '2025-05-20', 7, 0, 3, 0), -- 訂單4 3人 去程 取消
       (7, 4, '2025-05-21', 7, 0, 3, 1), -- 訂單4 3人 去程
       (7, 4, '2025-05-22', 8, 1, 3, 1), -- 訂單4 3人 回程
       (2, 5, '2025-07-25', 1, 0, 4, 1), -- 訂單5 4人 去程
       (2, 5, '2025-07-29', 2, 1, 4, 1); -- 訂單5 4人 回程

-- DROP TABLE IF EXISTS shuttle_reservation;


-- 座位 seat （王人慶）
CREATE TABLE seat (
	seat_id      INT          AUTO_INCREMENT  PRIMARY KEY,      -- 座位編號 (PK)
	seat_number  VARCHAR(10)  NOT NULL,                         -- 座號
	seat_status  TINYINT      NOT NULL  COMMENT '0:故障 1:正常'  -- 座位狀態
);

INSERT INTO seat (seat_number, seat_status)
VALUES ('A1', 1), ('A2', 1), ('A3', 1), ('A4', 0), ('A5', 1), ('A6', 1), ('A7', 1), ('A8', 1), ('A9', 1), ('A10', 1), -- 01~10
	   ('B1', 1), ('B2', 1), ('B3', 1), ('B4', 1), ('B5', 1), ('B6', 1), ('B7', 1), ('B8', 1), ('B9', 1), ('B10', 1), -- 11~20
	   ('C1', 1), ('C2', 1), ('C3', 1), ('C4', 1), ('C5', 1), ('C6', 1), ('C7', 1), ('C8', 1), ('C9', 1), ('C10', 1), -- 21~30
       ('D1', 1), ('D2', 1), ('D3', 1), ('D4', 1), ('D5', 1), ('D6', 1), ('D7', 1), ('D8', 1), ('D9', 1), ('D10', 1), -- 31~40
       ('E1', 1), ('E2', 1), ('E3', 1), ('E4', 1), ('E5', 1), ('E6', 1), ('E7', 1), ('E8', 1), ('E9', 1), ('E10', 1), -- 41~50
       ('F1', 1), ('F2', 1), ('F3', 1), ('F4', 1), ('F5', 1), ('F6', 1), ('F7', 1), ('F8', 1), ('F9', 1), ('F10', 1), -- 51~60
       ('G1', 1), ('G2', 1), ('G3', 1), ('G4', 1), ('G5', 1), ('G6', 1), ('G7', 1), ('G8', 0), ('G9', 1), ('G10', 1), -- 61~70
       ('H1', 1), ('H2', 1), ('H3', 1), ('H4', 1), ('H5', 1), ('H6', 1), ('H7', 1), ('H8', 1), ('H9', 1), ('H10', 1), -- 71~80
       ('I1', 1), ('I2', 1), ('I3', 1), ('I4', 1), ('I5', 1), ('I6', 1), ('I7', 1), ('I8', 1), ('I9', 1), ('I10', 1), -- 81~90
       ('J1', 1), ('J2', 1), ('J3', 1), ('J4', 1), ('J5', 1), ('J6', 1), ('J7', 1), ('J8', 0), ('J9', 1), ('J10', 1); -- 91~100

-- DROP TABLE IF EXISTS seat_availability;


-- 接駁預約座位對應 shuttle_reservation_seat （賴彥儒）
CREATE TABLE shuttle_reservation_seat (
	shuttle_reservation_id  INT  NOT NULL,  -- 接駁預約編號 (PK, FK)
    seat_id                 INT  NOT NULL,  -- 座位編號 (PK, FK)
    CONSTRAINT pk_shuttle_reservation_seat_id PRIMARY KEY(shuttle_reservation_id, seat_id),
    CONSTRAINT fk_shuttle_reservation_id FOREIGN KEY(shuttle_reservation_id) REFERENCES shuttle_reservation(shuttle_reservation_id),
    CONSTRAINT fk_seat_id FOREIGN KEY(seat_id) REFERENCES seat(seat_id)
);

INSERT INTO shuttle_reservation_seat (shuttle_reservation_id, seat_id)
VALUES (1,  1),   (1,  2),   -- 預約編號1 2人
       (3,  100), (3,  99),  -- 預約編號3 2人
       (4,  3),   (4,  5),  (4,  7),  (4, 9),  -- 預約編號4 4人
       (5,  2),   (5,  3),  (5,  5),  (5, 7),  -- 預約編號5 4人
       (6,  11),  (6,  13), (6,  17), (6, 19), (6, 23), (6, 29), (6, 31), (6, 37), (6, 41),  -- 預約編號6 9人
       (7,  43),  (7,  47), (7,  53), (7, 59), (7, 61), (7, 67), (7, 71), (7, 73), (7, 79),  -- 預約編號7 9人
       (9,  83),  (9,  89), (9,  97),  -- 預約編號9  3人
       (10, 27),  (10, 18), (10, 28),  -- 預約編號10 3人
       (11, 31),  (11, 41), (11, 59), (11, 26),  -- 預約編號11 4人
       (12, 5),   (12, 10), (12, 15), (12, 20);  -- 預約編號11 4人

-- DROP TABLE IF EXISTS shuttle_reservation_seat;


-- 各接駁班次座位剩餘量 shuttle_seat_availability（王人慶）
CREATE TABLE shuttle_seat_availability (
	shuttle_schedule_id  INT       NOT NULL,                 -- 接駁班次編號 (PK, FK)
	shuttle_date         DATE      NOT NULL,                 -- 日期 (PK)
	seat_quantity        INT       NOT NULL  DEFAULT '100',  -- 座位剩餘數量
	seat_updated_at      DATETIME  NOT NULL,                 -- 座位最後更新時間
	CONSTRAINT pk_shuttle_seat_availability PRIMARY KEY(shuttle_schedule_id, shuttle_date),
	CONSTRAINT fk_shuttle_seat_availability_shuttle_schedule FOREIGN KEY(shuttle_schedule_id) REFERENCES shuttle_schedule (shuttle_schedule_id)
);

INSERT INTO shuttle_seat_availability (shuttle_schedule_id, shuttle_date, seat_quantity, seat_updated_at)
VALUES (1, '2024-03-10', 98, '2024-03-10 07:30:00'),
       (2, '2024-03-14', 100,'2024-03-14 08:30:00'),
       (2, '2024-03-15', 98, '2024-03-15 08:30:00'),
       (3, '2025-05-17', 96, '2025-05-17 09:30:00'),
       (4, '2025-05-18', 96, '2025-05-18 10:30:00'),
       (5, '2025-01-26', 91, '2025-01-26 13:30:00'),
       (6, '2025-01-31', 91, '2025-01-31 14:30:00'),
       (7, '2025-05-20', 100,'2025-05-20 15:30:00'),
       (7, '2025-05-21', 97, '2025-05-21 15:30:00'),
       (8, '2025-05-22', 97, '2025-05-22 16:30:00'),
       (1, '2025-07-25', 96, '2025-07-25 07:30:00'),
       (2, '2025-07-29', 96, '2025-07-29 08:30:00');

-- DROP TABLE IF EXISTS shuttle_seat_availability;

-- 臨時接駁預約請求表（用於多步驟預約流程）（賴彥儒）
CREATE TABLE temp_shuttle_reservation_request (
    id                    INT AUTO_INCREMENT PRIMARY KEY,
    member_id             INT NOT NULL,
    room_reservation_id   INT NOT NULL,
    shuttle_date          DATE NOT NULL,
    shuttle_number        INT NOT NULL,
    shuttle_direction     INT NOT NULL COMMENT '0:去程 1:回程',
    selected_schedule_id  INT,
    selected_seat_ids     VARCHAR(255) COMMENT '座位ID列表，以逗號分隔',
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_member_date (member_id, shuttle_date),
    INDEX idx_created_at (created_at)
);

-- DROP TABLE IF EXISTS temp_shuttle_reservation_request;

/***************************************************************************************************
********************************************* 購物相關 *********************************************
***************************************************************************************************/


-- 商品種類 product_category （陳吟瑄）
CREATE TABLE product_category (
	product_category_id    INT          AUTO_INCREMENT  PRIMARY KEY,  -- 商品種類編號 (PK) (AI)
	product_category_name  VARCHAR(20)  NOT NULL                      -- 商品種類名稱
);

INSERT INTO product_category (product_category_name)
VALUES ('食物禮盒'),
	   ('配件飾品'),
       ('香氛系列'),
       ('精選小物');

-- DROP TABLE IF EXISTS product_category;


-- 商品 product （陳吟瑄）
CREATE TABLE product (
    product_id           INT           AUTO_INCREMENT  PRIMARY KEY,        -- 商品編號 (PK) (AI)
    product_category_id  INT           NOT NULL,                           -- 商品種類編號 (FK)
    product_name         VARCHAR(20)   NOT NULL,                           -- 商品名稱
    product_price        INT           NOT NULL,                           -- 商品價格
    product_description  VARCHAR(100)  NOT NULL,                           -- 商品描述
    product_quantity     INT           NOT NULL,                           -- 商品數量
    product_status       TINYINT       NOT NULL  COMMENT '0:下架 1:上架',  -- 商品狀態
    FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id)
);

INSERT INTO product (product_category_id, product_name, product_price, product_description, product_quantity, product_status)
VALUES (1, '咖啡禮盒',     1990, '精選咖啡豆，送禮自用兩相宜。', 124, 1),
	   (4, '造型吊飾',     350,  '可愛小巧，隨身裝飾首選。',     52,  0),
       (3, '海島香氛片',   150,  '清新香氣，車內居家皆適用。',   75,  1),
       (3, '海島香水',     2580, '熱帶氣息，清新淡雅持久香。',   37,  1),
       (4, '造型擺件',     1250, '經典造型，收藏擺飾首選。',     69,  1),
       (4, '風景拼圖',     799,  '放鬆益智，展現美景細節。',     94,  1),
       (2, '椰子樹項鍊',   1380, '熱帶風情，穿搭亮點配件。',     72,  0),
       (1, '海島名產禮盒', 2599, '集結當地特色，一盒品味海島。', 188, 1),
       (2, '波浪耳飾',     999,  '時尚設計，簡約中帶有亮點。',   57,  1),
       (4, '造型環保杯',   1200, '輕巧便攜，環保又有型。',       10,  0);

-- DROP TABLE IF EXISTS product;


-- 優惠券 coupon （楊捷）
CREATE TABLE coupon (
	coupon_id       INT          AUTO_INCREMENT  PRIMARY KEY,  -- 優惠券編號 (PK)
    coupon_code     VARCHAR(10)  NOT NULL,                     -- 優惠代碼
    discount_value  INT          NOT NULL,                     -- 折扣金額
    min_spend       INT          NOT NULL,                     -- 最低消費金額限制
    start_date      DATE         NOT NULL,                     -- 啟用日期
    end_date        DATE         NOT NULL                      -- 結束日期
);

INSERT INTO coupon (coupon_code, discount_value, min_spend, start_date, end_date)
VALUES ('Tibame', 50, 500, '2025-01-01', '2025-12-31'),
	   ('David', 100, 1000, '2025-05-01', '2025-08-31'),
       ('Summer123', 66, 200, '2025-07-01', '2025-08-31'),
       ('MOM520', 520, 4000, '2025-05-01', '2025-05-31');

-- DROP TABLE IF EXISTS coupon;


-- 商品訂單 product_order （楊捷）
CREATE TABLE product_order (
    product_order_id      INT           AUTO_INCREMENT  PRIMARY KEY,                	-- 訂單編號 (PK)
    member_id             INT           NOT NULL,                                  		-- 會員編號 (FK)
    coupon_id             INT,                                                     		-- 優惠券編號 (FK)
    product_order_amount  INT           NOT NULL,                                  		-- 訂單總金額
    discount_amount       INT           NOT NULL,                                  		-- 折扣金額
    product_paid_amount   INT           NOT NULL,                                  		-- 實際付款金額
    payment_method        TINYINT       NOT NULL  COMMENT '0:信用卡 1:現金付款',     		-- 付款方式
    shipping_method       TINYINT       NOT NULL  COMMENT '0:宅配 1:飯店自取',       		-- 取貨方式
    contact_address       VARCHAR(200)  NOT NULL,                                  		-- 聯絡人地址
    contact_name          VARCHAR(30)   NOT NULL,                                  		-- 聯絡人姓名
    contact_phone         VARCHAR(20)   NOT NULL,                                  		-- 聯絡人電話
    note                  VARCHAR(50)   COMMENT '備註',                           	    -- 訂單備註
    product_order_status  TINYINT       NOT NULL  COMMENT  '0:訂單成立 1:配送中 2:已完成',	-- 商品訂單狀態
    product_order_time    DATETIME      NOT NULL,                                  		-- 商品訂單成立時間

    -- 外來鍵設定
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id)
);

INSERT INTO product_order (
    member_id, coupon_id, product_order_amount, discount_amount, product_paid_amount,
    payment_method, shipping_method, contact_address, contact_name, contact_phone,
    note, product_order_status, product_order_time
) VALUES
    (1, 1,     1990, 50,  1940, 0, 0, '台北市信義路100號3樓之一', '王小明', '0900000001', '平日下午五點前不在家，請宅配五點後送到謝謝!', 0, '2025-01-11 10:00:00'), -- 買產品1 1個
    (2, 1,     1400, 50,  1350, 1, 0, '台中市西屯路88號',         '林小華', '0900000002', null, 1, '2025-01-31 11:30:00'), -- 買產品2 4個
    (3, NULL,  150,  0,   2580, 0, 0, '高雄市中山路99號',         '陳大文', '0900000003', '星期三、四、五固定不在家，包裹送達時間請避開', 2, '2025-02-03 09:15:00'), -- 買產品3 1個
    (4, 1,     999,  50,  949,  1, 0, '新竹市經國路66號',         '張怡君', '0900000004', null, 0, '2025-03-04 14:00:00'), -- 買產品9 1個
    (5, 1,     1200, 50,  1150, 0, 0, '桃園市復興路10號',         '周志強', '0900000005', null, 1, '2025-04-05 16:45:00'), -- 買產品10 1個
    (6, 4,     6858, 520, 6338, 1, 0, '台南市永康區中正街5號',    '蔡美麗', '0900000006', '飯店服務很周到!讚!', 2, '2025-05-01 13:20:00'), -- 買產品5、6、7各2個
    (7, 4,     4100, 520, 3580, 0, 1, '台北市大安區光復南路11號', '何玉芬', '0900000007', null, 0, '2025-05-21 17:10:00'), -- 買產品10 3個、產品2 1個、產品3 1個
    (8, 4,     4500, 520, 3980, 1, 0, '嘉義市博愛路3段77號',      '黃金龍', '0900000008', null, 1, '2025-05-22 12:00:00'), -- 買產品3 30個
    (9, 1,     700,  50,  650,  0, 0, '台中市南區建國南路20號',   '劉阿成', '0900000009', null, 2, '2025-05-24 09:30:00'), -- 買產品3 2個
    (10, NULL, 350,  0,   350,  1, 0, '基隆市安樂區文德路88號',   '楊淑惠', '0900000010', null, 0, '2025-05-25 18:45:00'); -- 買產品2 1個

-- DROP TABLE IF EXISTS product_order;


-- 商品訂單明細 product_order_detail （楊捷）
CREATE TABLE product_order_detail (
    product_order_id        INT           NOT NULL,  -- 訂單編號 (PK, FK)
    product_id              INT           NOT NULL,  -- 商品編號 (PK, FK)
    product_order_name      VARCHAR(20)   NOT NULL,  -- 商品名稱
    product_order_price     INT           NOT NULL,  -- 商品價格
    product_order_quantity  INT           NOT NULL,  -- 商品訂購數量
    CONSTRAINT pk_product_order_id_product_id PRIMARY KEY (product_order_id, product_id),
    -- 外來鍵設定
	FOREIGN KEY (product_order_id) REFERENCES product_order(product_order_id),
	FOREIGN KEY (product_id) REFERENCES product(product_id)
);

INSERT INTO product_order_detail (product_order_id, product_id, product_order_name, product_order_price, product_order_quantity)
VALUES (1,  1, '咖啡禮盒',    1990, 1),
       (2,  2, '造型吊飾',    350,  4),
       (3,  3, '海島香氛片',  150,  1),
       (4,  9, '波浪耳飾',    999,  1),
       (5,  10, '造型環保杯', 1200, 1),
       (6,  5, '造型擺件',    1250, 2),
       (6,  6, '風景拼圖',    799,  2),
       (6,  7, '椰子樹項鍊',  1380, 2),
       (7,  10, '造型環保杯', 1200, 3),
       (7,  2, '造型吊飾',    350,  1),
       (7,  3, '海島香氛片',  150,  1),
       (8,  3, '海島香氛片',  150,  30),
       (9,  3, '海島香氛片',  150,  2),
       (10, 2, '造型吊飾',    350,  1);

-- DROP TABLE IF EXISTS product_order_detail;


-- 會員優惠券對應 member_coupon （楊捷）
CREATE TABLE member_coupon (
    member_id  INT  NOT NULL,                                  -- 會員編號 (PK, FK)
    coupon_id  INT  NOT NULL,                                  -- 優惠券編號 (PK, FK)
    used_date  DATE ,                                          -- 使用日期
    CONSTRAINT pk_member_id_coupon_id PRIMARY KEY (member_id, coupon_id),
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id)
);

INSERT INTO member_coupon (member_id, coupon_id, used_date)
VALUES (1, 1, '2025-01-11'),
       (2, 1, '2025-01-31'),
       (4, 1, '2025-03-04'),
       (5, 1, '2025-04-05'),
       (6, 4, '2025-05-01'),
       (7, 4, '2025-05-21'),
       (8, 4, '2025-05-22'),
       (9, 1, '2025-05-24');

-- DROP TABLE IF EXISTS member_coupon;


-- 商品圖片 product_photo （陳吟瑄）
CREATE TABLE product_photo (
	product_photo_id  INT       AUTO_INCREMENT  PRIMARY KEY,  -- 商品圖片編號 (PK) (AI)
    product_id        INT       NOT NULL,                     -- 商品編號 (FK)
    product_image     LONGBLOB,                               -- 商品圖片
	FOREIGN KEY (product_id) REFERENCES product(product_id)
);

INSERT INTO product_photo (product_id)
VALUES (1),
	   (2),
       (3),
       (4),
       (5),
       (6),
       (7),
       (8),
       (9),
       (10),
       (1),
       (2),
       (3),
       (4),
       (5);

-- DROP TABLE IF EXISTS product_photo;


-- 購物車 cart （陳吟瑄）
CREATE TABLE cart (
	member_id                INT       NOT NULL,  -- 會員編號 (PK, FK)
	product_id               INT       NOT NULL,  -- 商品編號 (PK, FK)
	cart_product_quantity    INT       NOT NULL,  -- 購物車內商品數量
	cart_product_price       INT       NOT NULL,  -- 商品當時價格
	cart_exist_time          DATETIME  NOT NULL,  -- 加入購物車時間
	CONSTRAINT pk_member_id_product_id PRIMARY KEY (member_id, product_id),
	FOREIGN KEY (member_id) REFERENCES members(member_id),
	FOREIGN KEY (product_id) REFERENCES product(product_id)
);

INSERT INTO cart (member_id, product_id, cart_product_quantity, cart_product_price, cart_exist_time)
VALUES (7, 1, 5, 1990, '2025-01-08 12:04:57'),
	   (6, 4, 3, 2580, '2025-01-16 13:59:03'),
       (7, 6, 5, 799,  '2025-02-15 17:31:03'),
       (4, 9, 5, 999,  '2025-02-23 12:32:53'),
       (2, 5, 5, 1250, '2025-03-17 23:34:18'),
       (5, 8, 1, 188,  '2025-04-12 07:43:05'),
       (2, 9, 3, 799,  '2025-04-12 20:41:30'),
       (3, 9, 4, 999,  '2025-04-15 23:22:41'),
       (2, 6, 5, 799,  '2025-05-13 17:20:00'),
       (4, 3, 2, 150,  '2025-05-16 19:04:49');

-- DROP TABLE IF EXISTS cart;


/***************************************************************************************************
********************************************* 員工相關 *********************************************
***************************************************************************************************/


-- 部門 department （陳吟瑄）
CREATE TABLE department (
	department_id    INT          AUTO_INCREMENT  PRIMARY KEY,  -- 部門編號 (PK) (AI)
    department_name  VARCHAR(30)  NOT NULL                      -- 部門名稱
);

INSERT INTO department (department_name)
VALUES ('營運部'),
	   ('客服部'),
	   ('接駁部'),
	   ('房務部'),
       ('產品部');
       
-- DROP TABLE IF EXISTS department;


-- 員工 employee （陳吟瑄）
CREATE TABLE employee (
    employee_id				INT				AUTO_INCREMENT	PRIMARY KEY,				-- 員工編號 (PK) (AI)
    department_id			INT				NOT NULL,									-- 部門編號 (FK)
    employee_name			VARCHAR(30)		NOT NULL,									-- 員工姓名
    employee_email			VARCHAR(100)	NOT NULL	UNIQUE,							-- 員工信箱
    employee_address		VARCHAR(200)	NOT NULL,									-- 員工地址
    employee_mobile			VARCHAR(20)		NOT NULL,									-- 員工手機
	employee_password_hash	VARCHAR(100)	NOT NULL,									-- 員工密碼
    employee_gender			TINYINT			NOT NULL	COMMENT '0:男 1:女 2:其它',		-- 員工性別
    employee_birthdate		DATE			NOT NULL,									-- 員工生日
    employee_photo			LONGBLOB,													-- 員工照片
    employee_hiredate		DATE			NOT NULL,									-- 員工到職日
    employee_leavedate		DATE,														-- 員工離職日
    employee_status			TINYINT			NOT NULL	COMMENT '0:離職 1:在職 2:停職',	-- 員工狀態
    FOREIGN KEY (department_id) REFERENCES department(department_id)
);

INSERT INTO employee (department_id, employee_name, employee_email, employee_address, employee_mobile, employee_password_hash, employee_gender, employee_birthdate, employee_hiredate, employee_leavedate, employee_status)
VALUES (1, '管理員',		'admin@tibame.com',			'60413 新營士林路4號9樓',		'0900000101',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '1990-12-30', '2023-01-01', NULL, 1), -- 密碼：asd123456
	   (5, '楊捷',		'jay@tibame.com',			'20869 彰化市育英街12號之0',	'0900000102',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 2, '1978-03-20', '2023-01-15', NULL, 1),
	   (4, '陳薇淨',		'vivian@tibame.com',		'57698 中壢縣龍山寺巷61號8樓',	'0900000103',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '1985-06-28', '2023-02-01', NULL, 1),
	   (5, '陳吟瑄',		'blaire@tibame.com',		'769 白沙民富巷8號4樓',		'0900000104',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '1998-01-11', '2023-02-15', NULL, 1),
	   (4, '詹力臻',		'patty@tibame.com',			'23008 屏東大橋頭街8號之1',	'0900000105',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 2, '1983-04-27', '2023-03-01', NULL, 1),
	   (4, '曾宸瑩',		'sandy@tibame.com',			'965 臺東自由街3號之5',		'0900000106',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 1, '1988-12-07', '2023-03-15', NULL, 1),
	   (3, '賴彥儒',		'lai@tibame.com',			'754 桃園天母街8號7樓',		'0900000107',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 2, '1968-11-08', '2023-04-01', NULL, 1),
	   (3, '王人慶',		'nick@tibame.com',			'479 雲林縣中山巷63號1樓',	'0900000108',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 1, '1978-06-23', '2023-04-15', NULL, 1),
	   (2, '客服員工',	'service@tibame.com',		'729 古坑縣頂福州巷193號之5', 	'0900000109',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '2004-01-17', '2023-05-01', NULL, 1),
	   (2, '停職員工',	'suspension@xiang.com',		'729 古坑縣頂福州巷193號之5',	'0900000110',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '2004-01-17', '2023-05-01', NULL, 2),
	   (2, '離職員工',	'resignation@xiang.com',	'729 古坑縣頂福州巷193號之5',	'0900000111',	'$2b$12$Qn0bN5vspTLVv4LOUtfla.Jm2kLI0vXRngb7817UDSfwHa1W3TqnW', 0, '2004-01-17', '2023-05-01', NULL, 0);

-- DROP TABLE IF EXISTS employee;


-- 權限 permission （陳吟瑄）
CREATE TABLE permission (
	permission_id           INT           AUTO_INCREMENT  PRIMARY KEY,  -- 權限編號 (PK) (AI)
    permission_name         VARCHAR(30)   NOT NULL,                     -- 權限名稱
    permission_description  VARCHAR(100)  NOT NULL                      -- 權限描述
);

INSERT INTO permission (permission_name, permission_description)
VALUES ('operation',	'營運部門權限'),
	   ('service',		'客服部門權限'),
	   ('shuttle',		'接駁部門權限'),
	   ('room',			'房務部門權限'),
	   ('product',		'產品部門權限');
   
-- DROP TABLE IF EXISTS permission;


-- 員工權限對應 employee_permission （陳吟瑄）
CREATE TABLE employee_permission (
	permission_id  INT  NOT NULL,  -- 權限編號 (PK, FK)
	employee_id    INT  NOT NULL,  -- 員工編號 (PK, FK)
    CONSTRAINT pk_permission_id_employee_id PRIMARY KEY (permission_id, employee_id),
    FOREIGN KEY (permission_id) REFERENCES permission(permission_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- 根據員工的部門自動分配對應權限
INSERT INTO employee_permission (permission_id, employee_id)
SELECT p.permission_id, e.employee_id
FROM employee e
JOIN permission p ON p.permission_name = 
    CASE e.department_id
        WHEN 1 THEN 'operation'
        WHEN 2 THEN 'service'
        WHEN 3 THEN 'shuttle'
        WHEN 4 THEN 'room'
        WHEN 5 THEN 'product'
    END
WHERE e.employee_status = 1;

-- 為員工編號01（管理員）添加全部權限
-- 先清除該員工現有的權限
DELETE FROM employee_permission WHERE employee_id = 1;

-- 為員工編號01添加所有權限
INSERT INTO employee_permission (permission_id, employee_id)
SELECT p.permission_id, 1
FROM permission p;

-- DROP TABLE IF EXISTS employee_permission;

-- 操作日誌 operation_log （楊捷）
CREATE TABLE operation_log (
	operation_log_id  INT           AUTO_INCREMENT  PRIMARY KEY,  -- 操作日誌編號 (PK)
    employee_id       INT           NOT NULL,                     -- 員工編號 (FK)
    operation_time    DATETIME      NOT NULL,                     -- 操作日誌時間
    log_description   VARCHAR(100)  NOT NULL,                     -- 操作日誌描述
    FOREIGN KEY (employee_id) REFERENCES employee (employee_id) 
);

INSERT INTO operation_log (employee_id, operation_time, log_description)
VALUES (2, '2025-05-01 09:15:00', '登入系統'),
       (8, '2025-05-01 09:22:00', '登入系統'),
	   (8, '2025-05-01 09:30:00', '新增商品'),
       (3, '2025-05-01 09:55:00', '登入系統'),
       (3, '2025-05-01 10:00:00', '瀏覽客服內容'),
       (3, '2025-05-01 10:15:00', '登出系統'),
       (8, '2025-05-01 10:30:00', '新增商品'),
       (8, '2025-05-01 11:00:00', '刪除商品'),
       (8, '2025-05-01 11:30:00', '登出系統'),
       (2, '2025-05-01 11:30:00', '登出系統'),
       (7, '2025-05-01 12:50:00', '登入系統'),
       (7, '2025-05-01 13:00:00', '瀏覽房務'),
       (5, '2025-05-01 13:25:00', '登入系統'),
       (6, '2025-05-01 13:27:00', '登入系統'),
       (5, '2025-05-01 13:30:00', '瀏覽接駁紀錄'),
       (5, '2025-05-01 13:41:00', '登出系統'),
       (6, '2025-05-01 14:00:00', '瀏覽接駁紀錄'),
       (6, '2025-05-01 14:13:00', '登出系統'),
       (7, '2025-05-01 14:30:00', '新增房型'),
       (7, '2025-05-01 14:53:00', '登出系統');

-- DROP TABLE IF EXISTS operation_log;


/***************************************************************************************************
********************************************* 其他相關 *********************************************
***************************************************************************************************/


-- 問卷紀錄 feedback （陳薇淨）
CREATE TABLE feedback (
	feedback_id    INT          AUTO_INCREMENT,                                                           -- 問卷編號 (PK) (AI)
    member_id      INT          NOT NULL,                                                                 -- 會員編號 (FK)
    feedback_time  DATETIME     NOT NULL,                                                                 -- 問卷填寫時間
    answer1        VARCHAR(50)  NOT NULL,                                                                 -- 回答一
    answer2        VARCHAR(50)  NOT NULL,                                                                 -- 回答二
    answer3        VARCHAR(50)  NOT NULL,                                                                 -- 回答三
    answer4        VARCHAR(50)  NOT NULL,                                                                 -- 回答四
    answer5        VARCHAR(50)  NOT NULL,                                                                 -- 回答五
    rating         TINYINT      NOT NULL  DEFAULT 3  COMMENT '1:一顆星 2:2顆星 3:3顆星 4:4顆星 5:5顆星',  -- 評論星數
    CONSTRAINT pk_feedback_id PRIMARY KEY(feedback_id),
    CONSTRAINT fk_f_member_id FOREIGN KEY(member_id) REFERENCES members(member_id)
);

INSERT INTO feedback (member_id, feedback_time, answer1, answer2, answer3, answer4, answer5, rating) -- feedback要有房型欄位？目前先設定成一張訂單不論幾間房都只有一份問卷
VALUES (8, '2025-01-31 10:00:00', '非常不滿意', '房間有味道', '服務冷漠',   '隔音差',     '不推薦',     1),
       (1, '2025-03-15 14:30:00', '非常滿意',   '乾淨整潔',   '服務親切',   '環境安靜',   '價格合理',   5),
       (4, '2025-05-18 16:15:00', '普通',       '位置方便',   '設備老舊',   '服務中等',   '價格偏高',   3),
       (7, '2025-05-22 18:10:00', '滿意',       '整潔乾淨',   '交通方便',   '服務良好',   '價格可接受', 4),
       (2, '2025-07-29 13:20:00', '普通',       '還可以接受', '無特別亮點', '冷氣有點吵', '價格可接受', 3);

-- DROP TABLE IF EXISTS feedback;


-- 最新消息 news （陳薇淨）
CREATE TABLE news(
    news_id				INT				AUTO_INCREMENT,											-- 最新消息編號 (PK) (AI)
    news_title			VARCHAR(20)		NOT NULL,												-- 最新消息標題
    news_content		VARCHAR(1000)	NOT NULL,												-- 最新消息內容
    news_image			LONGBLOB,																-- 最新消息圖片
    news_time			DATETIME		NOT NULL,												-- 最新消息上傳時間
    news_status			TINYINT			NOT NULL		DEFAULT 1	COMMENT '0:下架, 1:上架',	-- 最新消息狀態
    room_promotion_id	INT,            														-- 對應優惠專案
    CONSTRAINT pk_news_id PRIMARY KEY(news_id),
    CONSTRAINT fk_n_room_promotion_id FOREIGN KEY(room_promotion_id) REFERENCES promotion(room_promotion_id)
);

INSERT INTO news (news_title, news_content, news_time, news_status, room_promotion_id)
VALUES 
('春假出遊推薦', '適合學生與家庭出遊的春假專案', '2025-03-03 09:00:00', 1, 1),
('房客評價破千', '感謝支持，我們已累積超過1000則五星好評！', '2025-04-01 18:00:00', 1, NULL),
('免費早餐延長至10點', '應顧客建議，即日起早餐供應時間延長至上午10點。', '2025-04-05 07:00:00', 1, NULL),
('兒童遊樂區升級完成', '兒童遊樂區已完成升級，歡迎親子同遊！', '2025-04-15 12:00:00', 1, NULL),
('泳池維修公告', '5/10~5/15泳池進行維修，造成不便敬請見諒。', '2025-04-25 11:00:00', 1, NULL),
('網站改版通知', '全新官方網站上線，提供更便捷的訂房體驗！', '2025-05-10 10:30:00', 1, NULL),
('暑期優惠開跑', '指定房型享 85 折優惠', '2025-06-25 00:00:00', 1, 2),
('秋季旅行計畫推薦', '歡慶中秋佳節，景房限時優惠中', '2025-09-26 00:00:00', 1, 7),
('週末快閃特惠', '僅限週末訂房，2025/09/06 至 2025/09/08 優惠開跑！', '2025-09-05 00:00:00', 1, 3),
('限時快閃活動！', '限時兩天，先搶先贏！', '2025-09-14 00:00:00', 1, 4),
('早鳥優惠開跑', '提前預訂享雙人房 75 折折扣，優惠至 2025/10/01。', '2025-09-24 00:00:00', 1, 5),
('中秋促銷限定', '歡慶中秋佳節，景房型優惠中，快來預訂！', '2025-10-03 00:00:00', 1, 6),
('週年慶活動', '慶祝開館週年，推出多項感恩回饋', '2025-11-01 00:00:00', 1, 8),
('聖誕禮物大放送', '聖誕節首夜位房型回饋價！', '2025-12-17 00:00:00', 1, 9),
('元旦住宿優惠開跑', '2026 開年住房折扣，優惠期間：2026/01/01 至 2026/01/07，快來搶訂！', '2025-12-24 00:00:00', 1, 10);

-- DROP TABLE IF EXISTS news;


-- 輪播圖片 carousel_photo （陳薇淨）
CREATE TABLE carousel_photo(
	carousel_photo_id  INT       AUTO_INCREMENT,  -- 輪播圖片編號 (PK) (AI)
    carousel_image     LONGBLOB,                  -- 輪播圖片
    CONSTRAINT pk_carousel_photo_id PRIMARY KEY(carousel_photo_id)
);

INSERT INTO carousel_photo ()
VALUES (),
       (),
       (),
       (),
       (),
       (),
       (),
       (),
       (),
       ();

-- DROP TABLE IF EXISTS carousel_photo;

























