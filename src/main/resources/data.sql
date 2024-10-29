-- ROLE TABLE
INSERT INTO public.role (role_name)
VALUES ('ADMIN');
INSERT INTO public.role (role_name)
VALUES ('USER');
INSERT INTO public.role (role_name)
VALUES ('LANDLORD');

-- USER TABLE
INSERT INTO public.users (id, email, password, created_at, created_by, full_name, role, status)
VALUES ('9e477280-942f-43cd-a181-db482c9be95f', 'admin@gmail.com',
        '$2a$10$Z75d4vzIBCAotcC/4se50OINWCEuGFA5pe0z30ZZfGmWuV3JBcVL2', now(), 'Server', 'Admin Server', 'ADMIN',
        'ACTIVE');
INSERT INTO public.users (id, email, password, created_at, created_by, full_name, role, status)
VALUES ('9f477280-942f-43cd-a181-db484c9bd95f', 'landlord@gmail.com',
        '$2a$10$Z75d4vzIBCAotcC/4se50OINWCEuGFA5pe0z30ZZfGmWuV3JBcVL2', now(), 'Server', 'Landlord Server', 'LANDLORD',
        'ACTIVE');

-- PERMISSION TABLE
INSERT INTO public.permission (permission, role_name)
VALUES ('USER:READ_PROFILE', 'USER');
INSERT INTO public.permission (permission, role_name)
VALUES ('USER:CREATE', 'USER');
INSERT INTO public.permission (permission, role_name)
VALUES ('USER:UPDATE_PROFILE', 'USER');
INSERT INTO public.permission (permission, role_name)
VALUES ('USER:DELETE', 'USER');
INSERT INTO public.permission (permission, role_name)
VALUES ('USER:UPDATE_FAVORITE', 'USER');
INSERT INTO public.permission (permission, role_name)
VALUES ('ADMIN:READ_USER', 'ADMIN');
INSERT INTO public.permission (permission, role_name)
VALUES ('ADMIN:READ_ALL_HOMESTAY', 'ADMIN');
INSERT INTO public.permission (permission, role_name)
VALUES ('ADMIN:CREATE_USER', 'ADMIN');
INSERT INTO public.permission (permission, role_name)
VALUES ('ADMIN:UPDATE_USER', 'ADMIN');
INSERT INTO public.permission (permission, role_name)
VALUES ('ADMIN:DELETE_USER', 'ADMIN');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:READ_OWN_HOMESTAY', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:CREATE_HOMESTAY', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_HOMESTAY', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:DELETE_HOMESTAY', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:READ_PROFILE', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_PROFILE', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_ROOM', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:CREATE_ROOM', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:DELETE_ROOM', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_FAVORITE', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_PRICE_HOMESTAY', 'LANDLORD');
INSERT INTO public.permission (permission, role_name)
VALUES ('LANDLORD:UPDATE_DISCOUNT_HOMESTAY', 'LANDLORD');

-- CITY TABLE
INSERT INTO public.city (id, "name")
VALUES (1, 'An Giang'),
       (2, 'Bà Rịa – Vũng Tàu'),
       (3, 'Bắc Giang'),
       (4, 'Bắc Kạn'),
       (5, 'Bạc Liêu'),
       (6, 'Bắc Ninh'),
       (7, 'Bến Tre'),
       (8, 'Bình Định'),
       (9, 'Bình Dương'),
       (10, 'Bình Phước');
INSERT INTO public.city (id, "name")
VALUES (11, 'Bình Thuận'),
       (12, 'Cà Mau'),
       (13, 'Cần Thơ'),
       (14, 'Cao Bằng'),
       (15, 'Đà Nẵng'),
       (16, 'Đắk Lắk'),
       (17, 'Đắk Nông'),
       (18, 'Điện Biên'),
       (19, 'Đồng Nai'),
       (20, 'Đồng Tháp');
INSERT INTO public.city (id, "name")
VALUES (21, 'Gia Lai'),
       (22, 'Hà Giang'),
       (23, 'Hà Nam'),
       (24, 'Hà Nội'),
       (25, 'Hà Tĩnh'),
       (26, 'Hải Dương'),
       (27, 'Hải Phòng'),
       (28, 'Hậu Giang'),
       (29, 'Hòa Bình'),
       (30, 'Hưng Yên');
INSERT INTO public.city (id, "name")
VALUES (31, 'Khánh Hòa'),
       (32, 'Kiên Giang'),
       (33, 'Kon Tum'),
       (34, 'Lai Châu'),
       (35, 'Lâm Đồng'),
       (36, 'Lạng Sơn'),
       (37, 'Lào Cai'),
       (38, 'Long An'),
       (39, 'Nam Định'),
       (40, 'Nghệ An');
INSERT INTO public.city (id, "name")
VALUES (41, 'Ninh Bình'),
       (42, 'Ninh Thuận'),
       (43, 'Phú Thọ'),
       (44, 'Phú Yên'),
       (45, 'Quảng Bình'),
       (46, 'Quảng Nam'),
       (47, 'Quảng Ngãi'),
       (48, 'Quảng Ninh'),
       (49, 'Quảng Trị'),
       (50, 'Sóc Trăng');
INSERT INTO public.city (id, "name")
VALUES (51, 'Sơn La'),
       (52, 'Tây Ninh'),
       (53, 'Thái Bình'),
       (54, 'Thái Nguyên'),
       (55, 'Thanh Hóa'),
       (56, 'Thành phố Hồ Chí Minh'),
       (57, 'Thừa Thiên Huế'),
       (58, 'Tiền Giang'),
       (59, 'Trà Vinh'),
       (60, 'Tuyên Quang');
INSERT INTO public.city (id, "name")
VALUES (61, 'Vĩnh Long'),
       (62, 'Vĩnh Phúc'),
       (63, 'Yên Bái');


-- DISTRICT TABLE
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (1, '', 'Lục Yên', 63),
       (2, '', 'Mù Cang Chải', 63),
       (3, 'thị xã', 'Nghĩa Lộ', 63),
       (4, '', 'Trạm Tấu', 63),
       (5, '', 'Trấn Yên', 63),
       (6, '', 'Văn Chấn', 63),
       (7, '', 'Văn Yên', 63),
       (8, 'thành phố', 'Yên Bái', 63),
       (9, '', 'Yên Bình', 63),
       (10, '', 'Bình Xuyên', 62);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (11, '', 'Lập Thạch', 62),
       (12, 'thành phố', 'Phúc Yên', 62),
       (13, '', 'Sông Lô', 62),
       (14, '', 'Tam Đảo', 62),
       (15, '', 'Tam Dương', 62),
       (16, '', 'Vĩnh Tường', 62),
       (17, 'thành phố', 'Vĩnh Yên', 62),
       (18, '', 'Yên Lạc', 62),
       (19, 'thị xã', 'Bình Minh', 61),
       (20, '', 'Bình Tân', 61);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (21, '', 'Long Hồ', 61),
       (22, '', 'Mang Thít', 61),
       (23, '', 'Tam Bình', 61),
       (24, '', 'Trà Ôn', 61),
       (25, 'thành phố', 'Vĩnh Long', 61),
       (26, '', 'Vũng Liêm', 61),
       (27, '', 'Chiêm Hóa', 60),
       (28, '', 'Hàm Yên', 60),
       (29, '', 'Lâm Bình', 60),
       (30, '', 'Na Hang', 60);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (31, '', 'Sơn Dương', 60),
       (32, 'thành phố', 'Tuyên Quang', 60),
       (33, '', 'Yên Sơn', 60),
       (34, '', 'Càng Long', 59),
       (35, '', 'Cầu Kè', 59),
       (36, '', 'Cầu Ngang', 59),
       (37, '', 'Châu Thành', 59),
       (38, 'thị xã', 'Duyên Hải', 59),
       (39, '', 'Duyên Hải', 59),
       (40, '', 'Tiểu Cần', 59);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (41, '', 'Trà Cú', 59),
       (42, 'thành phố', 'Trà Vinh', 59),
       (43, '', 'Cái Bè', 58),
       (44, 'thị xã', 'Cai Lậy', 58),
       (45, '', 'Cai Lậy', 58),
       (46, '', 'Châu Thành', 58),
       (47, '', 'Chợ Gạo', 58),
       (48, 'thành phố', 'Gò Công', 58),
       (49, '', 'Gò Công Đông', 58),
       (50, '', 'Gò Công Tây', 58);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (51, 'thành phố', 'Mỹ Tho', 58),
       (52, '', 'Tân Phú Đông', 58),
       (53, '', 'Tân Phước', 58),
       (54, '', 'A Lưới', 57),
       (55, 'thành phố', 'Huế', 57),
       (56, 'thị xã', 'Hương Thủy', 57),
       (57, 'thị xã', 'Hương Trà', 57),
       (58, '', 'Nam Đông', 57),
       (59, '', 'Phong Điền', 57),
       (60, '', 'Phú Lộc', 57);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (61, '', 'Phú Vang', 57),
       (62, '', 'Quảng Điền', 57),
       (63, '', 'Bình Chánh', 56),
       (64, 'quận', 'Bình Tân', 56),
       (65, 'quận', 'Bình Thạnh', 56),
       (66, '', 'Cần Giờ', 56),
       (67, '', 'Củ Chi', 56),
       (68, 'quận', 'Gò Vấp', 56),
       (69, '', 'Hóc Môn', 56),
       (70, '', 'Nhà Bè', 56);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (71, 'quận', 'Phú Nhuận', 56),
       (72, 'quận', 'Quận 1', 56),
       (73, 'quận', 'Quận 3', 56),
       (74, 'quận', 'Quận 4', 56),
       (75, 'quận', 'Quận 5', 56),
       (76, 'quận', 'Quận 6', 56),
       (77, 'quận', 'Quận 7', 56),
       (78, 'quận', 'Quận 8', 56),
       (79, 'quận', 'Quận 10', 56),
       (80, 'quận', 'Quận 11', 56);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (81, 'quận', 'Quận 12', 56),
       (82, 'quận', 'Tân Bình', 56),
       (83, 'quận', 'Tân Phú', 56),
       (84, 'thành phố', 'Thủ Đức', 56),
       (85, '', 'Bá Thước', 55),
       (86, 'thị xã', 'Bỉm Sơn', 55),
       (87, '', 'Cẩm Thủy', 55),
       (88, '', 'Đông Sơn', 55),
       (89, '', 'Hà Trung', 55),
       (90, '', 'Hậu Lộc', 55);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (91, '', 'Hoằng Hóa', 55),
       (92, '', 'Lang Chánh', 55),
       (93, '', 'Mường Lát', 55),
       (94, '', 'Nga Sơn', 55),
       (95, 'thị xã', 'Nghi Sơn', 55),
       (96, '', 'Ngọc Lặc', 55),
       (97, '', 'Như Thanh', 55),
       (98, '', 'Như Xuân', 55),
       (99, '', 'Nông Cống', 55),
       (100, '', 'Quan Hóa', 55);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (101, '', 'Quan Sơn', 55),
       (102, '', 'Quảng Xương', 55),
       (103, 'thành phố', 'Sầm Sơn', 55),
       (104, '', 'Thạch Thành', 55),
       (105, 'thành phố', 'Thanh Hóa', 55),
       (106, '', 'Thiệu Hóa', 55),
       (107, '', 'Thọ Xuân', 55),
       (108, '', 'Thường Xuân', 55),
       (109, '', 'Triệu Sơn', 55),
       (110, '', 'Vĩnh Lộc', 55);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (111, '', 'Yên Định', 55),
       (112, '', 'Đại Từ', 54),
       (113, '', 'Định Hóa', 54),
       (114, '', 'Đồng Hỷ', 54),
       (115, 'thành phố', 'Phổ Yên', 54),
       (116, '', 'Phú Bình', 54),
       (117, '', 'Phú Lương', 54),
       (118, 'thành phố', 'Sông Công', 54),
       (119, 'thành phố', 'Thái Nguyên', 54),
       (120, '', 'Võ Nhai', 54);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (121, '', 'Đông Hưng', 53),
       (122, '', 'Hưng Hà', 53),
       (123, '', 'Kiến Xương', 53),
       (124, '', 'Quỳnh Phụ', 53),
       (125, 'thành phố', 'Thái Bình', 53),
       (126, '', 'Thái Thụy', 53),
       (127, '', 'Tiền Hải', 53),
       (128, '', 'Vũ Thư', 53),
       (129, '', 'Bến Cầu', 52),
       (130, '', 'Châu Thành', 52);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (131, '', 'Dương Minh Châu', 52),
       (132, '', 'Gò Dầu', 52),
       (133, 'thị xã', 'Hòa Thành', 52),
       (134, '', 'Tân Biên', 52),
       (135, '', 'Tân Châu', 52),
       (136, 'thành phố', 'Tây Ninh', 52),
       (137, 'thị xã', 'Trảng Bàng', 52),
       (138, '', 'Bắc Yên', 51),
       (139, '', 'Mai Sơn', 51),
       (140, '', 'Mộc Châu', 51);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (141, '', 'Mường La', 51),
       (142, '', 'Phù Yên', 51),
       (143, '', 'Quỳnh Nhai', 51),
       (144, 'thành phố', 'Sơn La', 51),
       (145, '', 'Sông Mã', 51),
       (146, '', 'Sốp Cộp', 51),
       (147, '', 'Thuận Châu', 51),
       (148, '', 'Vân Hồ', 51),
       (149, '', 'Yên Châu', 51),
       (150, '', 'Châu Thành', 50);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (151, '', 'Cù Lao Dung', 50),
       (152, '', 'Kế Sách', 50),
       (153, '', 'Long Phú', 50),
       (154, '', 'Mỹ Tú', 50),
       (155, '', 'Mỹ Xuyên', 50),
       (156, 'thị xã', 'Ngã Năm', 50),
       (157, 'thành phố', 'Sóc Trăng', 50),
       (158, '', 'Thạnh Trị', 50),
       (159, '', 'Trần Đề', 50),
       (160, 'thị xã', 'Vĩnh Châu', 50);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (161, '', 'Cam Lộ', 49),
       (162, 'huyện đảo', 'Cồn Cỏ', 49),
       (163, '', 'Đakrông', 49),
       (164, 'thành phố', 'Đông Hà', 49),
       (165, '', 'Gio Linh', 49),
       (166, '', 'Hải Lăng', 49),
       (167, '', 'Hướng Hóa', 49),
       (168, 'thị xã', 'Quảng Trị', 49),
       (169, '', 'Triệu Phong', 49),
       (170, '', 'Vĩnh Linh', 49);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (171, '', 'Ba Chẽ', 48),
       (172, '', 'Bình Liêu', 48),
       (173, 'thành phố', 'Cẩm Phả', 48),
       (174, 'huyện đảo', 'Cô Tô', 48),
       (175, '', 'Đầm Hà', 48),
       (176, 'thị xã', 'Đông Triều', 48),
       (177, 'thành phố', 'Hạ Long', 48),
       (178, '', 'Hải Hà', 48),
       (179, 'thành phố', 'Móng Cái', 48),
       (180, 'thị xã', 'Quảng Yên', 48);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (181, '', 'Tiên Yên', 48),
       (182, 'thành phố', 'Uông Bí', 48),
       (183, 'huyện đảo', 'Vân Đồn', 48),
       (184, '', 'Ba Tơ', 47),
       (185, '', 'Bình Sơn', 47),
       (186, 'thị xã', 'Đức Phổ', 47),
       (187, 'huyện đảo', 'Lý Sơn', 47),
       (188, '', 'Minh Long', 47),
       (189, '', 'Mộ Đức', 47),
       (190, '', 'Nghĩa Hành', 47);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (191, 'thành phố', 'Quảng Ngãi', 47),
       (192, '', 'Sơn Hà', 47),
       (193, '', 'Sơn Tây', 47),
       (194, '', 'Sơn Tịnh', 47),
       (195, '', 'Trà Bồng', 47),
       (196, '', 'Tư Nghĩa', 47),
       (197, '', 'Bắc Trà My', 46),
       (198, '', 'Đại Lộc', 46),
       (199, 'thị xã', 'Điện Bàn', 46),
       (200, '', 'Đông Giang', 46);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (201, '', 'Duy Xuyên', 46),
       (202, '', 'Hiệp Đức', 46),
       (203, 'thành phố', 'Hội An', 46),
       (204, '', 'Nam Giang', 46),
       (205, '', 'Nam Trà My', 46),
       (206, '', 'Nông Sơn', 46),
       (207, '', 'Núi Thành', 46),
       (208, '', 'Phú Ninh', 46),
       (209, '', 'Phước Sơn', 46),
       (210, '', 'Quế Sơn', 46);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (211, 'thành phố', 'Tam Kỳ', 46),
       (212, '', 'Tây Giang', 46),
       (213, '', 'Thăng Bình', 46),
       (214, '', 'Tiên Phước', 46),
       (215, 'thị xã', 'Ba Đồn', 45),
       (216, '', 'Bố Trạch', 45),
       (217, 'thành phố', 'Đồng Hới', 45),
       (218, '', 'Lệ Thủy', 45),
       (219, '', 'Minh Hóa', 45),
       (220, '', 'Quảng Ninh', 45);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (221, '', 'Quảng Trạch', 45),
       (222, '', 'Tuyên Hóa', 45),
       (223, 'thị xã', 'Đông Hòa', 44),
       (224, '', 'Đồng Xuân', 44),
       (225, '', 'Phú Hòa', 44),
       (226, '', 'Sơn Hòa', 44),
       (227, 'thị xã', 'Sông Cầu', 44),
       (228, '', 'Sông Hinh', 44),
       (229, '', 'Tây Hòa', 44),
       (230, '', 'Tuy An', 44);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (231, 'thành phố', 'Tuy Hòa', 44),
       (232, '', 'Cẩm Khê', 43),
       (233, '', 'Đoan Hùng', 43),
       (234, '', 'Hạ Hòa', 43),
       (235, '', 'Lâm Thao', 43),
       (236, '', 'Phù Ninh', 43),
       (237, 'thị xã', 'Phú Thọ', 43),
       (238, '', 'Tam Nông', 43),
       (239, '', 'Tân Sơn', 43),
       (240, '', 'Thanh Ba', 43);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (241, '', 'Thanh Sơn', 43),
       (242, '', 'Thanh Thủy', 43),
       (243, 'thành phố', 'Việt Trì', 43),
       (244, '', 'Yên Lập', 43),
       (245, '', 'Bác Ái', 42),
       (246, '', 'Ninh Hải', 42),
       (247, '', 'Ninh Phước', 42),
       (248, '', 'Ninh Sơn', 42),
       (249, 'thành phố', 'Phan Rang – Tháp Chàm', 42),
       (250, '', 'Thuận Bắc', 42);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (251, '', 'Thuận Nam', 42),
       (252, '', 'Gia Viễn', 41),
       (253, '', 'Hoa Lư', 41),
       (254, '', 'Kim Sơn', 41),
       (255, '', 'Nho Quan', 41),
       (256, 'thành phố', 'Ninh Bình', 41),
       (257, 'thành phố', 'Tam Điệp', 41),
       (258, '', 'Yên Khánh', 41),
       (259, '', 'Yên Mô', 41),
       (260, '', 'Anh Sơn', 40);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (261, '', 'Con Cuông', 40),
       (262, 'thị xã', 'Cửa Lò', 40),
       (263, '', 'Diễn Châu', 40),
       (264, '', 'Đô Lương', 40),
       (265, 'thị xã', 'Hoàng Mai', 40),
       (266, '', 'Hưng Nguyên', 40),
       (267, '', 'Kỳ Sơn', 40),
       (268, '', 'Nam Đàn', 40),
       (269, '', 'Nghi Lộc', 40),
       (270, '', 'Nghĩa Đàn', 40);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (271, '', 'Quế Phong', 40),
       (272, '', 'Quỳ Châu', 40),
       (273, '', 'Quỳ Hợp', 40),
       (274, '', 'Quỳnh Lưu', 40),
       (275, '', 'Tân Kỳ', 40),
       (276, 'thị xã', 'Thái Hòa', 40),
       (277, '', 'Thanh Chương', 40),
       (278, '', 'Tương Dương', 40),
       (279, 'thành phố', 'Vinh', 40),
       (280, '', 'Yên Thành', 40);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (281, '', 'Giao Thủy', 39),
       (282, '', 'Hải Hậu', 39),
       (283, 'thành phố', 'Nam Định', 39),
       (284, '', 'Nam Trực', 39),
       (285, '', 'Nghĩa Hưng', 39),
       (286, '', 'Trực Ninh', 39),
       (287, '', 'Vụ Bản', 39),
       (288, '', 'Xuân Trường', 39),
       (289, '', 'Ý Yên', 39),
       (290, '', 'Bến Lức', 38);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (291, '', 'Cần Đước', 38),
       (292, '', 'Cần Giuộc', 38),
       (293, '', 'Châu Thành', 38),
       (294, '', 'Đức Hòa', 38),
       (295, '', 'Đức Huệ', 38),
       (296, 'thị xã', 'Kiến Tường', 38),
       (297, '', 'Mộc Hóa', 38),
       (298, 'thành phố', 'Tân An', 38),
       (299, '', 'Tân Hưng', 38),
       (300, '', 'Tân Thạnh', 38);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (301, '', 'Tân Trụ', 38),
       (302, '', 'Thạnh Hóa', 38),
       (303, '', 'Thủ Thừa', 38),
       (304, '', 'Vĩnh Hưng', 38),
       (305, '', 'Bảo Lâm', 35),
       (306, 'thành phố', 'Bảo Lộc', 35),
       (307, '', 'Cát Tiên', 35),
       (308, '', 'Đạ Huoai', 35),
       (309, 'thành phố', 'Đà Lạt', 35),
       (310, '', 'Đạ Tẻh', 35);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (311, '', 'Đam Rông', 35),
       (312, '', 'Di Linh', 35),
       (313, '', 'Đơn Dương', 35),
       (314, '', 'Đức Trọng', 35),
       (315, '', 'Lạc Dương', 35),
       (316, '', 'Lâm Hà', 35),
       (317, '', 'Bắc Hà', 37),
       (318, '', 'Bảo Thắng', 37),
       (319, '', 'Bảo Yên', 37),
       (320, '', 'Bát Xát', 37);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (321, 'thành phố', 'Lào Cai', 37),
       (322, '', 'Mường Khương', 37),
       (323, 'thị xã', 'Sa Pa', 37),
       (324, '', 'Si Ma Cai', 37),
       (325, '', 'Văn Bàn', 37),
       (326, '', 'Bắc Sơn', 36),
       (327, '', 'Bình Gia', 36),
       (328, '', 'Cao Lộc', 36),
       (329, '', 'Chi Lăng', 36),
       (330, '', 'Đình Lập', 36);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (331, '', 'Hữu Lũng', 36),
       (332, 'thành phố', 'Lạng Sơn', 36),
       (333, '', 'Lộc Bình', 36),
       (334, '', 'Tràng Định', 36),
       (335, '', 'Văn Lãng', 36),
       (336, '', 'Văn Quan', 36),
       (337, 'thành phố', 'Lai Châu', 34),
       (338, '', 'Mường Tè', 34),
       (339, '', 'Nậm Nhùn', 34),
       (340, '', 'Phong Thổ', 34);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (341, '', 'Sìn Hồ', 34),
       (342, '', 'Tam Đường', 34),
       (343, '', 'Tân Uyên', 34),
       (344, '', 'Than Uyên', 34),
       (345, '', 'Đăk Glei', 33),
       (346, '', 'Đăk Hà', 33),
       (347, '', 'Đăk Tô', 33),
       (348, '', 'Ia H''Drai', 33),
       (349, '', 'Kon Plông', 33),
       (350, '', 'Kon Rẫy', 33);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (351, 'thành phố', 'Kon Tum', 33),
       (352, '', 'Ngọc Hồi', 33),
       (353, '', 'Sa Thầy', 33),
       (354, '', 'Tu Mơ Rông', 33),
       (355, '', 'An Biên', 32),
       (356, '', 'An Minh', 32),
       (357, '', 'Châu Thành', 32),
       (358, '', 'Giang Thành', 32),
       (359, '', 'Giồng Riềng', 32),
       (360, '', 'Gò Quao', 32);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (361, 'thành phố', 'Hà Tiên', 32),
       (362, '', 'Hòn Đất', 32),
       (363, 'huyện đảo', 'Kiên Hải', 32),
       (364, '', 'Kiên Lương', 32),
       (365, 'thành phố', 'Phú Quốc', 32),
       (366, 'thành phố', 'Rạch Giá', 32),
       (367, '', 'Tân Hiệp', 32),
       (368, '', 'U Minh Thượng', 32),
       (369, '', 'Vĩnh Thuận', 32),
       (370, '', 'Cam Lâm', 31);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (371, 'thành phố', 'Cam Ranh', 31),
       (372, '', 'Diên Khánh', 31),
       (373, '', 'Khánh Sơn', 31),
       (374, '', 'Khánh Vĩnh', 31),
       (375, 'thành phố', 'Nha Trang', 31),
       (376, 'thị xã', 'Ninh Hòa', 31),
       (377, 'huyện đảo', 'Trường Sa', 31),
       (378, '', 'Vạn Ninh', 31),
       (379, '', 'Ân Thi', 30),
       (380, 'thành phố', 'Hưng Yên', 30);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (381, '', 'Khoái Châu', 30),
       (382, '', 'Kim Động', 30),
       (383, 'thị xã', 'Mỹ Hào', 30),
       (384, '', 'Phù Cừ', 30),
       (385, '', 'Tiên Lữ', 30),
       (386, '', 'Văn Giang', 30),
       (387, '', 'Văn Lâm', 30),
       (388, '', 'Yên Mỹ', 30),
       (389, '', 'Cao Phong', 29),
       (390, '', 'Đà Bắc', 29);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (391, 'thành phố', 'Hòa Bình', 29),
       (392, '', 'Kim Bôi', 29),
       (393, '', 'Lạc Sơn', 29),
       (394, '', 'Lạc Thủy', 29),
       (395, '', 'Lương Sơn', 29),
       (396, '', 'Mai Châu', 29),
       (397, '', 'Tân Lạc', 29),
       (398, '', 'Yên Thủy', 29),
       (399, '', 'Châu Thành', 28),
       (400, '', 'Châu Thành A', 28);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (401, 'thị xã', 'Long Mỹ', 28),
       (402, '', 'Long Mỹ', 28),
       (403, 'thành phố', 'Ngã Bảy', 28),
       (404, '', 'Phụng Hiệp', 28),
       (405, 'thành phố', 'Vị Thanh', 28),
       (406, '', 'Vị Thủy', 28),
       (407, '', 'An Dương', 27),
       (408, '', 'An Lão', 27),
       (409, 'huyện đảo', 'Bạch Long Vĩ', 27),
       (410, 'huyện đảo', 'Cát Hải', 27);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (411, 'quận', 'Đồ Sơn', 27),
       (412, 'quận', 'Dương Kinh', 27),
       (413, 'quận', 'Hải An', 27),
       (414, 'quận', 'Hồng Bàng', 27),
       (415, 'quận', 'Kiến An', 27),
       (416, '', 'Kiến Thụy', 27),
       (417, 'quận', 'Lê Chân', 27),
       (418, 'quận', 'Ngô Quyền', 27),
       (419, '', 'Thủy Nguyên', 27),
       (420, '', 'Tiên Lãng', 27);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (421, '', 'Vĩnh Bảo', 27),
       (422, '', 'Bình Giang', 26),
       (423, '', 'Cẩm Giàng', 26),
       (424, 'thành phố', 'Chí Linh', 26),
       (425, '', 'Gia Lộc', 26),
       (426, 'thành phố', 'Hải Dương', 26),
       (427, '', 'Kim Thành', 26),
       (428, 'thị xã', 'Kinh Môn', 26),
       (429, '', 'Nam Sách', 26),
       (430, '', 'Ninh Giang', 26);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (431, '', 'Thanh Hà', 26),
       (432, '', 'Thanh Miện', 26),
       (433, '', 'Tứ Kỳ', 26),
       (434, '', 'Cẩm Xuyên', 25),
       (435, '', 'Can Lộc', 25),
       (436, '', 'Đức Thọ', 25),
       (437, 'thành phố', 'Hà Tĩnh', 25),
       (438, 'thị xã', 'Hồng Lĩnh', 25),
       (439, '', 'Hương Khê', 25),
       (440, '', 'Hương Sơn', 25);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (441, 'thị xã', 'Kỳ Anh', 25),
       (442, '', 'Kỳ Anh', 25),
       (443, '', 'Lộc Hà', 25),
       (444, '', 'Nghi Xuân', 25),
       (445, '', 'Thạch Hà', 25),
       (446, '', 'Vũ Quang', 25),
       (447, 'quận', 'Ba Đình', 24),
       (448, '', 'Ba Vì', 24),
       (449, 'quận', 'Bắc Từ Liêm', 24),
       (450, 'quận', 'Cầu Giấy', 24);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (451, '', 'Chương Mỹ', 24),
       (452, '', 'Đan Phượng', 24),
       (453, '', 'Đông Anh', 24),
       (454, 'quận', 'Đống Đa', 24),
       (455, '', 'Gia Lâm', 24),
       (456, 'quận', 'Hà Đông', 24),
       (457, 'quận', 'Hai Bà Trưng', 24),
       (458, '', 'Hoài Đức', 24),
       (459, 'quận', 'Hoàn Kiếm', 24),
       (460, 'quận', 'Hoàng Mai', 24);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (461, 'quận', 'Long Biên', 24),
       (462, '', 'Mê Linh', 24),
       (463, '', 'Mỹ Đức', 24),
       (464, 'quận', 'Nam Từ Liêm', 24),
       (465, '', 'Phú Xuyên', 24),
       (466, '', 'Phúc Thọ', 24),
       (467, '', 'Quốc Oai', 24),
       (468, '', 'Sóc Sơn', 24),
       (469, 'thị xã', 'Sơn Tây', 24),
       (470, 'quận', 'Tây Hồ', 24);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (471, '', 'Thạch Thất', 24),
       (472, '', 'Thanh Oai', 24),
       (473, '', 'Thanh Trì', 24),
       (474, 'quận', 'Thanh Xuân', 24),
       (475, '', 'Thường Tín', 24),
       (476, '', 'Ứng Hòa', 24),
       (477, '', 'Bình Lục', 23),
       (478, 'thị xã', 'Duy Tiên', 23),
       (479, '', 'Kim Bảng', 23),
       (480, '', 'Lý Nhân', 23);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (481, 'thành phố', 'Phủ Lý', 23),
       (482, '', 'Thanh Liêm', 23),
       (483, '', 'Bắc Mê', 22),
       (484, '', 'Bắc Quang', 22),
       (485, '', 'Đồng Văn', 22),
       (486, 'thành phố', 'Hà Giang', 22),
       (487, '', 'Hoàng Su Phì', 22),
       (488, '', 'Mèo Vạc', 22),
       (489, '', 'Quản Bạ', 22),
       (490, '', 'Quang Bình', 22);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (491, '', 'Vị Xuyên', 22),
       (492, '', 'Xín Mần', 22),
       (493, '', 'Yên Minh', 22),
       (494, 'thị xã', 'An Khê', 21),
       (495, 'thị xã', 'Ayun Pa', 21),
       (496, '', 'Chư Păh', 21),
       (497, '', 'Chư Prông', 21),
       (498, '', 'Chư Pưh', 21),
       (499, '', 'Chư Sê', 21),
       (500, '', 'Đak Đoa', 21);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (501, '', 'Đak Pơ', 21),
       (502, '', 'Đức Cơ', 21),
       (503, '', 'Ia Grai', 21),
       (504, '', 'Ia Pa', 21),
       (505, '', 'Kbang', 21),
       (506, '', 'Kông Chro', 21),
       (507, '', 'Krông Pa', 21),
       (508, '', 'Mang Yang', 21),
       (509, '', 'Phú Thiện', 21),
       (510, 'thành phố', 'Pleiku', 21);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (511, 'thành phố', 'Cao Lãnh', 20),
       (512, '', 'Cao Lãnh', 20),
       (513, '', 'Châu Thành', 20),
       (514, 'thành phố', 'Hồng Ngự', 20),
       (515, '', 'Hồng Ngự', 20),
       (516, '', 'Lai Vung', 20),
       (517, '', 'Lấp Vò', 20),
       (518, 'thành phố', 'Sa Đéc', 20),
       (519, '', 'Tam Nông', 20),
       (520, '', 'Tân Hồng', 20);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (521, '', 'Thanh Bình', 20),
       (522, '', 'Tháp Mười', 20),
       (523, 'thành phố', 'Biên Hòa', 19),
       (524, '', 'Cẩm Mỹ', 19),
       (525, '', 'Định Quán', 19),
       (526, 'thành phố', 'Long Khánh', 19),
       (527, '', 'Long Thành', 19),
       (528, '', 'Nhơn Trạch', 19),
       (529, '', 'Tân Phú', 19),
       (530, '', 'Thống Nhất', 19);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (531, '', 'Trảng Bom', 19),
       (532, '', 'Vĩnh Cửu', 19),
       (533, '', 'Xuân Lộc', 19),
       (534, '', 'Điện Biên', 18),
       (535, '', '18 Đông', 18),
       (536, 'thành phố', '18 Phủ', 18),
       (537, '', 'Mường Ảng', 18),
       (538, '', 'Mường Chà', 18),
       (539, 'thị xã', 'Mường Lay', 18),
       (540, '', 'Mường Nhé', 18);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (541, '', 'Nậm Pồ', 18),
       (542, '', 'Tủa Chùa', 18),
       (543, '', 'Tuần Giáo', 18),
       (544, '', 'Cư Jút', 17),
       (545, '', 'Đắk Glong', 17),
       (546, '', 'Đắk Mil', 17),
       (547, '', 'Đắk R''lấp', 17),
       (548, '', 'Đắk Song', 17),
       (549, 'thành phố', 'Gia Nghĩa', 17),
       (550, '', 'Krông Nô', 17);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (551, '', 'Tuy Đức', 17),
       (552, '', 'Buôn Đôn', 16),
       (553, 'thị xã', 'Buôn Hồ', 16),
       (554, 'thành phố', 'Buôn Ma Thuột', 16),
       (555, '', 'Cư Kuin', 16),
       (556, '', 'Cư M''gar', 16),
       (557, '', 'Ea H''leo', 16),
       (558, '', 'Ea Kar', 16),
       (559, '', 'Ea Súp', 16),
       (560, '', 'Krông Ana', 16);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (561, '', 'Krông Bông', 16),
       (562, '', 'Krông Búk', 16),
       (563, '', 'Krông Năng', 16),
       (564, '', 'Krông Pắc', 16),
       (565, '', 'Lắk', 16),
       (566, '', 'M''Drắk', 16),
       (567, 'quận', 'Cẩm Lệ', 15),
       (568, 'quận', 'Hải Châu', 15),
       (569, '', 'Hòa Vang', 15),
       (570, 'huyện đảo', 'Hoàng Sa', 15);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (571, 'quận', 'Liên Chiểu', 15),
       (572, 'quận', 'Ngũ Hành Sơn', 15),
       (573, 'quận', 'Sơn Trà', 15),
       (574, 'quận', 'Thanh Khê', 15),
       (575, 'quận', 'Bình Thủy', 13),
       (576, 'quận', 'Cái Răng', 13),
       (577, '', 'Cờ Đỏ', 13),
       (578, 'quận', 'Ninh Kiều', 13),
       (579, 'quận', 'Ô Môn', 13),
       (580, '', 'Phong Điền', 13);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (581, '', 'Thới Lai', 13),
       (582, 'quận', 'Thốt Nốt', 13),
       (583, '', 'Vĩnh Thạnh', 13),
       (584, '', 'Bảo Lạc', 14),
       (585, '', 'Bảo Lâm', 14),
       (586, 'thành phố', 'Cao Bằng', 14),
       (587, '', 'Hạ Lang', 14),
       (588, '', 'Hà Quảng', 14),
       (589, '', 'Hòa An', 14),
       (590, '', 'Nguyên Bình', 14);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (591, '', 'Quảng Hòa', 14),
       (592, '', 'Thạch An', 14),
       (593, '', 'Trùng Khánh', 14),
       (594, 'thành phố', 'Cà Mau', 12),
       (595, '', 'Cái Nước', 12),
       (596, '', 'Đầm Dơi', 12),
       (597, '', 'Năm Căn', 12),
       (598, '', 'Ngọc Hiển', 12),
       (599, '', 'Phú Tân', 12),
       (600, '', 'Thới Bình', 12);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (601, '', 'Trần Văn Thời', 12),
       (602, '', 'U Minh', 12),
       (603, '', 'Bắc Bình', 11),
       (604, '', 'Đức Linh', 11),
       (605, '', 'Hàm Tân', 11),
       (606, '', 'Hàm Thuận Bắc', 11),
       (607, '', 'Hàm Thuận Nam', 11),
       (608, 'thị xã', 'La Gi', 11),
       (609, 'thành phố', 'Phan Thiết', 11),
       (610, 'huyện đảo', 'Phú Quý', 11);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (611, '', 'Tánh Linh', 11),
       (612, '', 'Tuy Phong', 11),
       (613, 'thị xã', 'Bình Long', 10),
       (614, '', 'Bù Đăng', 10),
       (615, '', 'Bù Đốp', 10),
       (616, '', 'Bù Gia Mập', 10),
       (617, 'thị xã', 'Chơn Thành', 10),
       (618, '', 'Đồng Phú', 10),
       (619, 'thành phố', 'Đồng Xoài', 10),
       (620, '', 'Hớn Quản', 10);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (621, '', 'Lộc Ninh', 10),
       (622, '', 'Phú Riềng', 10),
       (623, 'thị xã', 'Phước Long', 10),
       (624, '', 'An Lão', 8),
       (625, 'thị xã', 'An Nhơn', 8),
       (626, '', 'Hoài Ân', 8),
       (627, 'thị xã', 'Hoài Nhơn', 8),
       (628, '', 'Phù Cát', 8),
       (629, '', 'Phù Mỹ', 8),
       (630, 'thành phố', 'Quy Nhơn', 8);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (631, '', 'Tây Sơn', 8),
       (632, '', 'Tuy Phước', 8),
       (633, '', 'Vân Canh', 8),
       (634, '', 'Vĩnh Thạnh', 8),
       (635, '', 'Bắc Tân Uyên', 9),
       (636, '', 'Bàu Bàng', 9),
       (637, 'thành phố', 'Bến Cát', 9),
       (638, '', 'Dầu Tiếng', 9),
       (639, 'thành phố', 'Dĩ An', 9),
       (640, '', 'Phú Giáo', 9);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (641, 'thành phố', 'Tân Uyên', 9),
       (642, 'thành phố', 'Thủ Dầu Một', 9),
       (643, 'thành phố', 'Thuận An', 9),
       (644, '', 'Ba Tri', 7),
       (645, 'thành phố', 'Bến Tre', 7),
       (646, '', 'Bình Đại', 7),
       (647, '', 'Châu Thành', 7),
       (648, '', 'Chợ Lách', 7),
       (649, '', 'Giồng Trôm', 7),
       (650, '', 'Mỏ Cày Bắc', 7);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (651, '', 'Mỏ Cày Nam', 7),
       (652, '', 'Thạnh Phú', 7),
       (653, 'thành phố', 'Bắc Ninh', 6),
       (654, '', 'Gia Bình', 6),
       (655, '', 'Lương Tài', 6),
       (656, 'thị xã', 'Quế Võ', 6),
       (657, 'thị xã', 'Thuận Thành', 6),
       (658, '', 'Tiên Du', 6),
       (659, 'thành phố', 'Từ Sơn', 6),
       (660, '', 'Yên Phong', 6);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (661, '', 'Ba Bể', 4),
       (662, 'thành phố', 'Bắc Kạn', 4),
       (663, '', 'Bạch Thông', 4),
       (664, '', 'Chợ Đồn', 4),
       (665, '', 'Chợ Mới', 4),
       (666, '', 'Na Rì', 4),
       (667, '', 'Ngân Sơn', 4),
       (668, '', 'Pác Nặm', 4),
       (669, 'thành phố', 'Bắc Giang', 3),
       (670, '', 'Hiệp Hòa', 3);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (671, '', 'Lạng Giang', 3),
       (672, '', 'Lục Nam', 3),
       (673, '', 'Lục Ngạn', 3),
       (674, '', 'Sơn Động', 3),
       (675, '', 'Tân Yên', 3),
       (676, 'thị xã', 'Việt Yên', 3),
       (677, '', 'Yên Dũng', 3),
       (678, '', 'Yên Thế', 3),
       (679, 'thành phố', 'Bạc Liêu', 5),
       (680, '', 'Đông Hải', 5);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (681, 'thị xã', 'Giá Rai', 5),
       (682, '', 'Hòa Bình', 5),
       (683, '', 'Hồng Dân', 5),
       (684, '', 'Phước Long', 5),
       (685, '', 'Vĩnh Lợi', 5),
       (686, 'thành phố', 'Bà Rịa', 2),
       (687, '', 'Châu Đức', 2),
       (688, 'huyện đảo', 'Côn Đảo', 2),
       (689, '', 'Đất Đỏ', 2),
       (690, '', 'Long Điền', 2);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (691, 'thị xã', 'Phú Mỹ', 2),
       (692, 'thành phố', 'Vũng Tàu', 2),
       (693, '', 'Xuyên Mộc', 2),
       (694, '', 'An Phú', 1),
       (695, 'thành phố', 'Châu Đốc', 1),
       (696, '', 'Châu Phú', 1),
       (697, '', 'Châu Thành', 1),
       (698, '', 'Chợ Mới', 1),
       (699, 'thành phố', 'Long Xuyên', 1),
       (700, '', 'Phú Tân', 1);
INSERT INTO public.district (id, detail, "name", city_id)
VALUES (701, 'thị xã', 'Tân Châu', 1),
       (702, '', 'Thoại Sơn', 1),
       (703, 'thị xã', 'Tịnh Biên', 1),
       (704, '', 'Tri Tôn', 1);

-- TYPE HOMESTAY TABLE
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Được ưa chuộng', 'https://a0.muscache.com/pictures/3726d94b-534a-42b8-bca0-a0304d912260.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Mới', 'https://a0.muscache.com/pictures/c0fa9598-4e37-40f3-b734-4bd0e2377add.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Thật ấn tượng', 'https://a0.muscache.com/pictures/c5a4f6fc-c92c-4ae8-87dd-57f1ff1b89a6.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà nghỉ dưỡng', 'https://a0.muscache.com/pictures/3b1eb541-46d9-4bef-abc4-c37d77e3c21b.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Chung cư', 'https://a0.muscache.com/pictures/aaa02c2d-9f0d-4c41-878a-68c12ec6c6bd.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà trên núi', 'https://a0.muscache.com/pictures/248f85bf-e35e-4dc3-a9a1-e1dbff9a3db4.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Cắm trại', 'https://a0.muscache.com/pictures/ca25c7f3-0d1f-432b-9efa-b9f5dc6d8770.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Lướt sóng', 'https://a0.muscache.com/pictures/957f8022-dfd7-426c-99fd-77ed792f6d7a.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà sàn', 'https://a0.muscache.com/pictures/677a041d-7264-4c45-bb72-52bff21eb6e8.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà riêng', 'https://a0.muscache.com/pictures/eb7ba4c0-ea38-4cbb-9db6-bdcc8baad585.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Hồ bơi', 'https://a0.muscache.com/pictures/3fb523a0-b622-4368-8142-b5e03df7549b.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Hướng biển', 'https://a0.muscache.com/pictures/bcd1adc0-5cee-4d7a-85ec-f6730b0f8d0c.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Biệt thự', 'https://a0.muscache.com/pictures/33dd714a-7b4a-4654-aaf0-f58ea887a688.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà trên đảo', 'https://a0.muscache.com/pictures/ee9e2a40-ffac-4db9-9080-b351efc3cfc4.jpg');
INSERT INTO public.type_homestay (name, url_image)
VALUES ('Nhà trên cây', 'https://a0.muscache.com/pictures/4d4a4eba-c7e4-43eb-9ce2-95e1d200d10e.jpg');

-- AMENITY TABLE
INSERT INTO public.amenity (name, type)
VALUES ('Hướng nhìn ra sân trong', 'Hướng nhìn đẹp mắt');
INSERT INTO public.amenity (name, type)
VALUES ('Hướng nhìn ra biển', 'Hướng nhìn đẹp mắt');
INSERT INTO public.amenity (name, type)
VALUES ('Hướng nhìn ra núi', 'Hướng nhìn đẹp mắt');
INSERT INTO public.amenity (name, type)
VALUES ('Máy sấy tóc', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Dầu gội đầu', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Dầu xả', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Khăn tắm', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Khăn mặt', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Xà phòng tắm', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Nước nóng', 'Phòng tắm');
INSERT INTO public.amenity (name, type)
VALUES ('Máy giặt – Trong tòa nhà', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Máy sấy – Trong tòa nhà', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Móc treo quần áo', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Bộ chăn ga gối', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Mành chắn sáng cho phòng', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Bàn là', 'Phòng ngủ và giặt ủi');
INSERT INTO public.amenity (name, type)
VALUES ('Điều hòa nhiệt đội', 'Hệ thống sưởi và làm mát');
INSERT INTO public.amenity (name, type)
VALUES ('Hệ thống sưởi', 'Hệ thống sưởi và làm mát');
INSERT INTO public.amenity (name, type)
VALUES ('Máy báo khói', 'An toàn nhà ở');
INSERT INTO public.amenity (name, type)
VALUES ('Bình chữa cháy', 'An toàn nhà ở');
INSERT INTO public.amenity (name, type)
VALUES ('Bảng hướng dẫn cứu trợ', 'An toàn nhà ở');
INSERT INTO public.amenity (name, type)
VALUES ('Wi-fi nhanh – 602 Mbps', 'Internet và văn phòng');
INSERT INTO public.amenity (name, type)
VALUES ('Bàn làm việc', 'Internet và văn phòng');
INSERT INTO public.amenity (name, type)
VALUES ('Bếp', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Lò vi sóng', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Tủ lạnh', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Bát đĩa và đồ bạc', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Đồ nấu ăn cơ bản', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Bếp gas', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Máy pha cà phê', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Nồi cơm điện', 'Đồ dùng nấu bếp và ăn uống');
INSERT INTO public.amenity (name, type)
VALUES ('Sân hiên riêng hoặc ban công riêng', 'Ngoài trời');
INSERT INTO public.amenity (name, type)
VALUES ('Sân sau chung – Có hàng rào kín', 'Ngoài trời');
INSERT INTO public.amenity (name, type)
VALUES ('Khóa ở cửa phòng ngủ', 'Quyền riêng tư và an toàn');
INSERT INTO public.amenity (name, type)
VALUES ('Có camera an ninh ở ngoài nhà hoặc ở lối vào', 'Quyền riêng tư và an toàn');