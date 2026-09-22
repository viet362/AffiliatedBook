-- Active: 1785914295113@@mysql-22176-buivietbacn01-1ff7.a.aivencloud.com@10055@affiliate-book
-- ==========================================================
-- DỰ ÁN AFFILIATED BOOK - FILE TẠO DATABASE & DỮ LIỆU DEMO
-- ==========================================================

-- Tạo database (nếu chưa có) và sử dụng database affiliate-book
CREATE DATABASE IF NOT EXISTS `affiliate-book` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `affiliate-book`;

SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------
-- 1. BẢNG NHÀ XUẤT BẢN / THƯƠNG HIỆU (brand)
-- ----------------------------------------------------------
DROP TABLE IF EXISTS `user_favorite`;

DROP TABLE IF EXISTS `product_category`;

DROP TABLE IF EXISTS `product`;

DROP TABLE IF EXISTS `user`;

DROP TABLE IF EXISTS `category`;

DROP TABLE IF EXISTS `brand`;

CREATE TABLE `brand` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- 2. BẢNG THỂ LOẠI (category)
-- ----------------------------------------------------------
CREATE TABLE `category` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- 3. BẢNG NGƯỜI DÙNG (user)
-- roleID = 1: Admin, roleID = 2: User thường
-- ----------------------------------------------------------
CREATE TABLE `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `roleID` INT NOT NULL DEFAULT 2
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- 4. BẢNG SẢN PHẨM / SÁCH (product)
-- ----------------------------------------------------------
CREATE TABLE `product` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `price` DOUBLE NOT NULL,
    `brandID` INT NOT NULL,
    `image` TEXT NOT NULL,
    `summary` TEXT,
    `alink` TEXT,
    CONSTRAINT `fk_product_brand` FOREIGN KEY (`brandID`) REFERENCES `brand` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- 5. BẢNG QUAN HỆ SẢN PHẨM - THỂ LOẠI (product_category)
-- ----------------------------------------------------------
CREATE TABLE `product_category` (
    `productID` INT NOT NULL,
    `categoryID` INT NOT NULL,
    PRIMARY KEY (`productID`, `categoryID`),
    CONSTRAINT `fk_pc_product` FOREIGN KEY (`productID`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_pc_category` FOREIGN KEY (`categoryID`) REFERENCES `category` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- 6. BẢNG YÊU THÍCH (user_favorite)
-- ----------------------------------------------------------
CREATE TABLE `user_favorite` (
    `userID` INT NOT NULL,
    `productID` INT NOT NULL,
    PRIMARY KEY (`userID`, `productID`),
    CONSTRAINT `fk_uf_user` FOREIGN KEY (`userID`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_uf_product` FOREIGN KEY (`productID`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- CHÈN DỮ LIỆU MẪU (SEED DATA)
-- ==========================================================

-- 1. Thêm Nhà Xuất Bản
INSERT INTO
    `brand` (`id`, `name`)
VALUES (1, 'NXB Trẻ'),
    (2, 'Alpha Books'),
    (3, 'First News - Trí Việt'),
    (4, 'Thái Hà Books'),
    (5, 'Nhã Nam'),
    (6, 'NXB Kim Đồng');

-- 2. Thêm Thể Loại Sách
INSERT INTO
    `category` (`id`, `name`)
VALUES (1, 'Lập trình & CNTT'),
    (
        2,
        'Kỹ năng & Phát triển bản thân'
    ),
    (3, 'Kinh tế & Khởi nghiệp'),
    (4, 'Văn học & Tiểu thuyết'),
    (5, 'Khoa học & Công nghệ'),
    (6, 'Tâm lý học');

-- 3. Thêm Tài khoản Demo
-- admin / admin123 (Quyền quản trị viên: Thêm, Sửa, Xóa sách)
-- demo_user / 123456 (Quyền người dùng: Xem, Tìm kiếm, Yêu thích)
INSERT INTO
    `user` (
        `id`,
        `username`,
        `password`,
        `roleID`
    )
VALUES (1, 'admin', 'admin123', 1),
    (2, 'demo_user', '123456', 2),
    (3, 'steven', '123456', 2);

-- 4. Thêm Sản phẩm (Sách)
INSERT INTO
    `product` (
        `id`,
        `name`,
        `price`,
        `brandID`,
        `image`,
        `summary`,
        `alink`
    )
VALUES (
        1,
        'Clean Code - Mã Sạch',
        195000,
        1,
        'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=600&auto=format&fit=crop&q=80',
        'Cuốn sách kinh điển của Robert C. Martin (Uncle Bob) hướng dẫn các nguyên tắc, mẫu thiết kế và phương pháp viết code sạch sẽ, dễ đọc, dễ bảo trì và mở rộng cho lập trình viên.',
        'https://shopee.vn'
    ),
    (
        2,
        'Thiết Kế Hệ Thống - System Design',
        260000,
        2,
        'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80',
        'Cẩm nang chuyên sâu về kiến trúc phần mềm phân tán, microservices, cân bằng tải, cơ chế caching và database scaling giúp chuẩn bị cho các kỳ phỏng vấn kỹ thuật cấp cao.',
        'https://tiki.vn'
    ),
    (
        3,
        'Đắc Nhân Tâm - Dale Carnegie',
        86000,
        3,
        'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop&q=80',
        'Tác phẩm bất hủ về nghệ thuật đối nhân xử thế, thu phục lòng người và xây dựng mối quan hệ chân thành, tích cực trong công việc và cuộc sống hàng ngày.',
        'https://shopee.vn'
    ),
    (
        4,
        'Atomic Habits - Thói Quen Nguyên Tử',
        145000,
        4,
        'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600&auto=format&fit=crop&q=80',
        'Thay đổi tí hon mang lại kết quả phi thường. James Clear cung cấp các chiến lược tâm lý thực tế để loại bỏ thói quen xấu và xây dựng hệ thống thói quen tích cực bền vững.',
        'https://tiki.vn'
    ),
    (
        5,
        'Nhà Giả Kim - Paulo Coelho',
        79000,
        5,
        'https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=600&auto=format&fit=crop&q=80',
        'Câu chuyện ngụ ngôn giàu chất thơ về hành trình đi tìm kho báu của chàng chăn cừu Santiago, truyền cảm hứng mạnh mẽ về việc kiên định theo đuổi ước mơ và lắng nghe số mệnh.',
        'https://shopee.vn'
    ),
    (
        6,
        'Cha Giàu Cha Nghèo - Robert Kiyosaki',
        115000,
        1,
        'https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=600&auto=format&fit=crop&q=80',
        'Khai mở tư duy quản lý tài chính thông minh, giải thích sự khác biệt giữa tài sản và tiêu sản, hướng dẫn cách tạo dòng tiền thụ động để tiến tới tự do tài chính.',
        'https://tiki.vn'
    ),
    (
        7,
        'Tư Duy Nhanh Và Chậm - Daniel Kahneman',
        185000,
        2,
        'https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=600&auto=format&fit=crop&q=80',
        'Khám phá hai cơ chế vận hành tư duy của con người: Hệ thống 1 cảm tính trực giác và Hệ thống 2 logic thận trọng, giúp đưa ra những quyết định sáng suốt hơn.',
        'https://shopee.vn'
    ),
    (
        8,
        'Súng, Vi Trùng Và Thép - Jared Diamond',
        220000,
        6,
        'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop&q=80',
        'Công trình nghiên cứu đồ sộ đạt giải Pulitzer lý giải các yếu tố địa lý, môi trường và công nghệ đã định hình sự thống trị và phát triển của các nền văn minh nhân loại.',
        'https://tiki.vn'
    ),
    (
        9,
        'Tâm Lý Học Về Tiền - Morgan Housel',
        139000,
        4,
        'https://images.unsplash.com/photo-1565372195458-9de0b320ef04?w=600&auto=format&fit=crop&q=80',
        'Những bài học vượt thời gian về tâm lý đầu tư, cách con người suy nghĩ về sự giàu có, lòng tham và hạnh phúc. Thành công tài chính đến từ hành vi hơn là chỉ số IQ.',
        'https://shopee.vn'
    ),
    (
        10,
        'Bắt Trẻ Đồng Xanh - J.D. Salinger',
        75000,
        5,
        'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=600&auto=format&fit=crop&q=80',
        'Góc nhìn độc đáo và đầy trăn trở của chàng trai trẻ Holden Caulfield về sự trưởng thành, nỗi cô đơn và khát khao giữ lại sự trong sáng giữa thế giới người lớn giả tạo.',
        'https://tiki.vn'
    ),
    (
        11,
        'Khởi Nghiệp Tinh Gọn - Eric Ries',
        165000,
        3,
        'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600&auto=format&fit=crop&q=80',
        'Phương pháp Lean Startup giúp các startup phát triển sản phẩm với tốc độ nhanh nhất, kiểm chứng giả thuyết liên tục và giảm thiểu tối đa rủi ro thất bại.',
        'https://shopee.vn'
    ),
    (
        12,
        'Cấu Trúc Dữ Liệu & Giải Thuật',
        199000,
        1,
        'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop&q=80',
        'Tài liệu nền tảng giúp nắm vững các giải thuật tìm kiếm, sắp xếp, đồ thị, cây nhị phân và tối ưu độ phức tạp tính toán, trang bị tư duy giải quyết bài toán hóc búa.',
        'https://tiki.vn'
    );

-- 5. Thêm Liên kết Sản phẩm - Thể loại
INSERT INTO
    `product_category` (`productID`, `categoryID`)
VALUES (1, 1), -- Clean Code -> Lập trình
    (2, 1), -- System Design -> Lập trình
    (2, 5), -- System Design -> Khoa học & Công nghệ
    (3, 2), -- Đắc Nhân Tâm -> Kỹ năng
    (3, 6), -- Đắc Nhân Tâm -> Tâm lý học
    (4, 2), -- Atomic Habits -> Kỹ năng
    (5, 4), -- Nhà Giả Kim -> Văn học
    (6, 3), -- Cha Giàu Cha Nghèo -> Kinh tế
    (7, 5), -- Tư Duy Nhanh Chậm -> Khoa học
    (7, 6), -- Tư Duy Nhanh Chậm -> Tâm lý học
    (8, 4), -- Súng Vi Trùng Thép -> Văn học
    (8, 5), -- Súng Vi Trùng Thép -> Khoa học
    (9, 3), -- Tâm Lý Học Về Tiền -> Kinh tế
    (9, 6), -- Tâm Lý Học Về Tiền -> Tâm lý học
    (10, 4), -- Bắt Trẻ Đồng Xanh -> Văn học
    (11, 3), -- Khởi Nghiệp Tinh Gọn -> Kinh tế
    (12, 1), -- Giải thuật -> Lập trình
    (12, 5);
-- Giải thuật -> Khoa học

-- 6. Thêm Danh sách Yêu thích Demo
INSERT INTO
    `user_favorite` (`userID`, `productID`)
VALUES (2, 1),
    (2, 4),
    (2, 6);