/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50559
Source Host           : 127.0.0.1:3306
Source Database       : dataadmin

Target Server Type    : MYSQL
Target Server Version : 50559
File Encoding         : 65001

Date: 2018-05-27 21:21:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for al_user_loc_infoa
-- ----------------------------
DROP TABLE IF EXISTS `al_user_loc_infoa`;
CREATE TABLE `al_user_loc_infoa` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `real_lat_e6` int(11) DEFAULT NULL COMMENT '真实纬度',
  `real_lon_e6` int(11) DEFAULT NULL COMMENT '真实经度',
  `real_addr` varchar(255) DEFAULT NULL COMMENT '真实地址',
  `mock_info` text COMMENT '虚拟位置信息',
  `imei` varchar(255) DEFAULT NULL COMMENT '手机串号',
  `imsi` varchar(255) DEFAULT NULL COMMENT '国际移动用户识别码',
  `model_type` varchar(255) DEFAULT NULL COMMENT '设备类型',
  `ip` varchar(255) DEFAULT NULL COMMENT 'ip',
  `phone_num` varchar(255) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `contacts` text COMMENT '运营商',
  `sms` text COMMENT 'sms',
  `call_log` text,
  `lang` varchar(255) DEFAULT NULL COMMENT '语言',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='神行虚拟定位数据';

-- ----------------------------
-- Records of al_user_loc_infoa
-- ----------------------------
INSERT INTO `al_user_loc_infoa` VALUES ('13733', '2016-09-08 08:43:00', '0', '0', '[Angola]', '[{\"latE6\":28206066,\"lonE6\":116430289,\"addr\":\"江西省抚州市东乡县X961\"},{\"latE6\":26072916,\"lonE6\":119294921,\"addr\":\"福建省福州市鼓楼区乌山路65号\"},{\"latE6\":26072916,\"lonE6\":119294921,\"addr\":\"福建省福州市鼓楼区乌山路65号\"}]', '867376021484817 ', '631024036711060 ', 'HUAWEI MT7-TL00', '105.168.49.103', '', '', '', '', '', '(NULL)');
INSERT INTO `al_user_loc_infoa` VALUES ('18482', '2016-10-01 00:41:00', '0', '0', '[Costa Rica]', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"},{\"latE6\":28152095,\"lonE6\":112880253,\"addr\":\"湖南省长沙市望城区G0401(长沙绕城高速)\"},{\"latE6\":28152095,\"lonE6\":112880253,\"addr\":\"湖南省长沙市望城区G0401(长沙绕城高速)\"}]', '359658067511867 ', '712012007269030 ', 'SM-G530H', '186.64.146.254', '506-61077247', '', '', '', '', '(NULL)');
INSERT INTO `al_user_loc_infoa` VALUES ('20472', '2016-10-08 01:37:00', '0', '0', '[Mexico]Mexico CityRepublica de Bolivia', '[{\"latE6\":28206086,\"lonE6\":112906904,\"addr\":\"湖南省长沙市岳麓区海天街27号\"}]', '860736034267698 ', '334050058682481 ', 'vivo X6D', '201.103.93.149', '525566751533', '', '', '', '', '(NULL)');
INSERT INTO `al_user_loc_infoa` VALUES ('22835', '2016-10-15 23:40:00', '22275302', '114191004', '[中国]香港特别行政区香港特别行政区湾仔区恩平道3号', '[{\"latE6\":28205603,\"lonE6\":112861553,\"addr\":\"湖南省长沙市岳麓区东方红中路\"},{\"latE6\":30941774,\"lonE6\":113811381,\"addr\":\"湖北省孝感市云梦县\"},{\"latE6\":35325993,\"lonE6\":116496425,\"addr\":\"山东省济宁市任城区G105(京珠线)\"},{\"latE6\":36188661,\"lonE6\":117122081,\"addr\":\"山东省泰安市泰山区河东路\"}]', '865863021480046 ', '460016026515686 ', 'MX4 Pro', '112.96.124.128', '8618566025888', '', '', '', '', '(NULL)');
INSERT INTO `al_user_loc_infoa` VALUES ('40525', '2016-12-29 09:30:00', '22311338', '113939890', '[China]暢順路 Cheong Shun Road', '[{\"latE6\":28206467,\"lonE6\":116905155,\"addr\":\"江西省鹰潭市余江县G320(沪瑞线)\"},{\"latE6\":28206467,\"lonE6\":116905155,\"addr\":\"江西省鹰潭市余江县G320(沪瑞线)\"}]', '359627050149175 ', '460023521140233 ', 'SM-G7108', '121.225.236.46', '', '', '', '', '', '');
INSERT INTO `al_user_loc_infoa` VALUES ('41254', '2016-12-31 13:30:00', '22311338', '113939890', '[China]暢順路 Cheong Shun Road', '[{\"latE6\":28206467,\"lonE6\":116905155,\"addr\":\"江西省鹰潭市余江县G320(沪瑞线)\"}]', '359627050149175 ', '460023521140233 ', 'SM-G7108', '117.89.187.240', '', '', '', '', '', '');
INSERT INTO `al_user_loc_infoa` VALUES ('41579', '2016-12-31 21:53:00', '0', '0', '[United States]New YorkAllen St', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"},{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"}]', '862018038953179 ', '310260847882407 ', 'OPPO A37m', '158.222.243.217', '19177741156', '', '', '', '', 'zh-cn');
INSERT INTO `al_user_loc_infoa` VALUES ('58845', '2017-03-08 18:22:00', '0', '0', '[Spain]Avinguda del Carme', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"},{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"}]', '358267055337771 ', '214019818412555 ', 'SM-G3815', '83.43.89.123', '', '', '', '', '', 'zh-cn');
INSERT INTO `al_user_loc_infoa` VALUES ('59243', '2017-03-10 04:18:00', '0', '0', '[Spain]Carrer d\'en Blanch', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"},{\"latE6\":25256821,\"lonE6\":110254433,\"addr\":\"广西壮族自治区桂林市秀峰区G321(翠竹路)\"}]', '358504079979976 ', '214075541820979 ', 'SM-J510FN', '176.83.127.7', '', '', '', '', '', 'zh-cn');
INSERT INTO `al_user_loc_infoa` VALUES ('59469', '2017-03-11 07:58:00', '0', '0', '[Spain]Avinguda del Carme', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"}]', '358267055337771 ', '214019818412555 ', 'SM-G3815', '31.4.189.17', '', '', '', '', '', 'zh-cn');
INSERT INTO `al_user_loc_infoa` VALUES ('63018', '2017-03-28 19:16:00', '0', '0', '[Australia]ChatswoodMcIntosh Street', '[{\"latE6\":28206130,\"lonE6\":112939501,\"addr\":\"湖南省长沙市岳麓区枫林一路\"}]', '869239024658449 ', '505025304461573 ', 'HUAWEI KII-L22', '58.104.2.47', '', '', '', '', '', 'en-us');
INSERT INTO `al_user_loc_infoa` VALUES ('63226', '2017-03-29 14:53:00', '0', '0', '[Australia]ChatswoodMcIntosh Street', '[{\"latE6\":28206130,\"lonE6\":112939501,\"addr\":\"湖南省长沙市岳麓区枫林一路\"},{\"latE6\":25069138,\"lonE6\":117028882,\"addr\":\"福建省龙岩市新罗区G319(曹溪北路)\"},{\"latE6\":25079189,\"lonE6\":117018451,\"addr\":\"福建省龙岩市新罗区龙岩大道\"},{\"latE6\":25079189,\"lonE6\":117018451,\"addr\":\"福建省龙岩市新罗区龙岩大道\"}]', '869239024658449 ', '505025304461573 ', 'HUAWEI KII-L22', '58.104.2.67', '', '', '', '', '', 'en-us');
INSERT INTO `al_user_loc_infoa` VALUES ('70805', '2017-05-11 06:51:00', '0', '0', '[Spain]Carrer de l\'Horta d\'en Pla', '[{\"latE6\":28207785,\"lonE6\":112972757,\"addr\":\"湖南省长沙市开福区湘江中路195号\"},{\"latE6\":22806881,\"lonE6\":108405721,\"addr\":\"广西壮族自治区南宁市青秀区朱槿路\"},{\"latE6\":27689542,\"lonE6\":111714255,\"addr\":\"湖南省娄底市涟源市人民东路\"},{\"latE6\":34505383,\"lonE6\":101549809,\"addr\":\"青海省黄南藏族自治州河南蒙古族自治县\"},{\"latE6\":39138026,\"lonE6\":117204263,\"addr\":\"天津市河北区民生路52号\"},{\"latE6\":39923694,\"lonE6\":116389254,\"addr\":\"北京市西城区文津街\"},{\"latE6\":26041360,\"lonE6\":119323659,\"addr\":\"福建省福州市仓山区下藤路158号\"},{\"latE6\":25915199,\"lonE6\":119558365,\"addr\":\"福建省福州市长乐市\"},{\"latE6\":41802983,\"lonE6\":123426148,\"addr\":\"辽宁省沈阳市和平区二纬路14号\"},{\"latE6\":30253185,\"lonE6\":120212873,\"addr\":\"浙江省杭州市江干区钱江路\"},{\"latE6\":31311711,\"lonE6\":120613479,\"addr\":\"江苏省苏州市姑苏区西百花巷16号\"}]', '358267055337771 ', '214019818412555 ', 'SM-G3815', '31.4.185.230', '', '', '', '', '', 'zh-cn');

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` varchar(255) NOT NULL,
  `folder` varchar(255) DEFAULT NULL COMMENT '文件夹',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签',
  `path` varchar(255) NOT NULL COMMENT '文件路径',
  `name` varchar(255) NOT NULL COMMENT '文件名称',
  `size` decimal(10,0) NOT NULL COMMENT '文件大小',
  `suffix` varchar(255) NOT NULL COMMENT '文件后缀',
  `type` varchar(255) NOT NULL COMMENT '文件类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `susp_name` varchar(255) DEFAULT NULL COMMENT '嫌疑人的姓名',
  `susp_id` varchar(255) DEFAULT NULL COMMENT '嫌疑人的id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of attachment
-- ----------------------------
INSERT INTO `attachment` VALUES ('00712ddd-ed5c-487b-9827-18d54b958748', '文件', '话单', '/bf256071-7539-4da4-835a-acf1b715744d/0503af51-29b3-45d5-98b6-3f71ef5eb21c-13622388955-222.xls', '13622388955-222.xls', '134656', 'xls', '文件', '2018-04-07 18:57:56', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('019035e6-a15c-479a-868e-a7fee67614ee', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/f4610dfb-dd02-42ad-a01c-e5de29bb90cf-1023724.jpg', '1023724.jpg', '218733', 'jpg', '图片', '2018-03-30 22:00:41', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('027ae9ba-131f-4150-9e36-9f445837f5fb', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/3c79997d-f721-4327-a3ba-6ab714422bcc-1023729.jpg', '1023729.jpg', '258599', 'jpg', '图片', '2018-03-30 22:00:41', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('088f8e46-0ec4-4b3a-8a90-d00663fc4569', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 23:09:58', null, null);
INSERT INTO `attachment` VALUES ('0abd4197-3b52-4e1c-a0d4-c5bcebb16f13', 'SQL文件', '神行', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/5239c18e-c738-437b-bd54-c534973a5ec7-al_user_loc_infoa.sql', 'al_user_loc_infoa.sql', '6506', 'sql', 'SQL文件', '2018-04-07 19:34:55', '虚拟定位数据公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('0ce7f1da-1a72-42a0-9ac7-682b652ca9b1', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/37fd02c2-d9fd-43b3-bd1b-67f8024d2e16-2015_11_12_05_20_50.eml', '2015_11_12_05_20_50.eml', '50724', 'eml', '文件', '2018-04-24 15:23:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('0cee8120-d562-4626-bf8f-a47843bf9c8d', '文件', '网易', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/50a73423-2aad-407f-bae5-a58c58823872-1tbiKBlIKVWBVoUMeQAAsi.eml', '1tbiKBlIKVWBVoUMeQAAsi.eml', '28118', 'eml', '文件', '2018-04-04 00:11:12', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('0e2a6756-27f8-4ce6-97a4-0121cad2e124', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/ba7444fd-20f1-44ad-b627-052632ff5de2-1023724.jpg', '1023724.jpg', '218733', 'jpg', '图片', '2018-03-30 22:01:38', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('100773e9-1a9a-4f47-a04a-b52aa3f5bebd', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/3bd460e8-2087-4028-9968-1fce505444f2-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-04-24 14:48:16', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('10a08d46-56c6-4849-92df-183dbb94fdc5', '文件', '流水', '/bf256071-7539-4da4-835a-acf1b715744d/b02c0b12-2ef0-46a1-9411-7e212f8e3736-tp_pygongyi_trades.txt', 'tp_pygongyi_trades.txt', '59725', 'txt', '文件', '2018-05-18 23:17:50', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('110ad584-afe2-4da6-8162-7bf353882da7', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/2758f3e1-b179-4bf2-a92b-23b8e26bc335-1023725.jpg', '1023725.jpg', '331483', 'jpg', '图片', '2018-03-30 22:01:38', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('15c0c664-134b-4213-880b-6212fd1ce3b4', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38919', 'eml', '文件', '2018-03-25 14:38:18', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('1679df1e-7b68-4e92-a09b-18b2aa62ce70', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/3d99c966-0146-4702-bfd9-e766865046b7-885303.jpg', '885303.jpg', '115396', 'jpg', '图片', '2018-03-30 21:54:23', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('1868b9c3-4ff9-4607-a181-27d444a948f4', '文件', 'QQ登录', '/bf256071-7539-4da4-835a-acf1b715744d/2d02dc10-7ca4-4024-81d1-1ead5cad7b3a-QQLogins.txt', 'QQLogins.txt', '95835', 'txt', '文件', '2018-04-09 22:30:45', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('19151a4f-3f50-457c-884e-b479e7ed288d', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/26de1eba-fdcc-40a4-977c-f6729435e1ef-2015_10_27_10_07_28.eml', '2015_10_27_10_07_28.eml', '5005', 'eml', '文件', '2018-04-24 16:46:19', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('192fb556-07bd-4789-ac6c-16f4e0ec4461', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 22:56:34', null, null);
INSERT INTO `attachment` VALUES ('1a2d0e68-00ac-4c03-a1b5-d853564dbcae', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/76134898-fa0b-4cea-ac58-6ecee102a1c0-2015_12_31_09_39_29.eml', '2015_12_31_09_39_29.eml', '4236', 'eml', '文件', '2018-05-27 18:07:18', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('1b228641-886e-42e3-992f-5a92a2214c1e', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/77f27487-2f91-4c7b-9e3c-3ebe2d65cbd6-1023725.jpg', '1023725.jpg', '331483', 'jpg', '图片', '2018-03-30 21:54:45', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('1ba6127c-81a3-4b78-928c-69881ac7c42f', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:08:22', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('1c330905-4f70-4c26-aa13-59e6699e300f', 'SQL文件', 'WSK', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/0a12dee4-1ba0-4590-a1a9-daffb83cc0d5-WSK表（网页版数据库拷贝成EXCEL） - 副本.xlsx', 'WSK表（网页版数据库拷贝成EXCEL） - 副本.xlsx', '16152', 'xlsx', '文件', '2018-04-09 00:12:23', '虚拟定位数据公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('1faf81d5-cbc8-4f08-a048-36fa24de30b7', '文件', '话单', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/05ff315f-635e-4dd5-97f8-50b9852028d2-13025461418.xls', '13025461418.xls', '161792', 'xls', '文件', '2018-04-07 18:58:29', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('200a18e9-8c14-468a-818f-e61ecdf9de29', '文件', '测试', '/2016_02_05_16_28_50 -3333.eml', '2016_02_05_16_28_50 -3333.eml', '115224', 'eml', '文件', '2018-03-25 15:16:36', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('205b5ae6-efff-4014-b915-2934f4058241', '文件', '交易记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/d7f5866d-8660-4c46-8860-f79fd40a632c-交易记录.csv', '交易记录.csv', '33508', 'csv', '文件', '2018-04-22 11:34:22', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('24989f86-8aa3-4f07-a3b0-562b288d293b', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:13:04', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('2891c7ec-5ea5-4db9-8dcb-2ea106d02d6e', '文件', '交易记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/9d8cfc53-2e58-41f2-9e39-b50e0b739d8a-交易记录.csv', '交易记录.csv', '33508', 'csv', '文件', '2018-04-22 11:37:35', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('29cbff77-9e70-4b59-82a2-f3031b18fc50', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/37081701-7c47-4b03-aafd-e8bdbccd798d-1023727.jpg', '1023727.jpg', '264271', 'jpg', '图片', '2018-03-30 22:01:38', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('2a360e29-0902-486a-80cd-4735b42c91f3', '文件', '注册', '/62a50696-12b0-4774-acd9-14c1f5c977c0/d1b42173-3fdf-4bea-afc7-1d5d6db64698-reg_982389608.txt', 'reg_982389608.txt', '2803', 'txt', '文件', '2018-04-02 22:56:46', '张样品', '62a50696-12b0-4774-acd9-14c1f5c977c0');
INSERT INTO `attachment` VALUES ('2b957f1a-794a-4d04-8552-dbe1a33c66b8', '文件', '注册', '/eb94748f-9de5-4d63-9fcd-93290c27312b/46d24767-c9bc-4507-b141-5712869a8f5a-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-28 00:11:26', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('2e1d5be9-695f-4e5d-bdaa-839c8096996a', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/1c350fde-49ff-4e73-a47e-52e9b032e13b-1023723.jpg', '1023723.jpg', '269369', 'jpg', '图片', '2018-03-30 21:59:52', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('2e3c1fbb-d604-4bfb-9d38-11158f110cc8', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38919', 'eml', '文件', '2018-03-25 14:39:55', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('3008ca6e-a056-4a5b-a483-a25f1b499c1e', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:30:59', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('322bb5a1-c960-47c4-8761-84c9922d1e16', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/4cbcf768-f3d1-4bbb-92ad-426732a309ad-现在开始试用Wijmo Enterprise产品- (1).eml', '现在开始试用Wijmo Enterprise产品- (1).eml', '11129', 'eml', '文件', '2018-03-25 18:52:58', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('33367a13-4a20-4788-9963-c4442a76f94d', '文件', '登录日志', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/966bfbe9-10d4-42ec-94c7-58ca91a41d7d-登陆日志.csv', '登陆日志.csv', '170', 'csv', '文件', '2018-04-21 22:51:26', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('367053b1-b97f-4fe4-8a8d-809299cc1fdf', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/abac7ff7-bbdb-43aa-983a-8ba7e7c9d057-【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 18:30:37', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('3720e76f-9ff8-4ef7-b331-bf64c82e66fe', '文件', '登录日志', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/d6a0fa70-92c8-40ae-8fe4-a614be3c1c08-登陆日志.csv', '登陆日志.csv', '170', 'csv', '文件', '2018-04-21 22:49:42', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('37601b14-faa4-4634-b71f-502eb70d7a46', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:31:23', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('3b9cb0df-84ff-4bf3-8569-b841a31ae797', '文件', '微信', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/9f34a954-7aa8-4537-afc3-0557e61db67b-info_1149078363.txt', 'info_1149078363.txt', '7884', 'txt', '文件', '2018-04-03 22:58:05', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('3c5532c2-62dd-4d78-a182-7ac3a76a1a5e', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/494ad998-6d30-4d74-9f0c-07d704fe44ee-1023723.jpg', '1023723.jpg', '269369', 'jpg', '图片', '2018-03-30 21:54:24', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('3d3b7207-d57a-4f4d-8789-1be3e7006886', '文件', '测试', '/style.css', 'style.css', '30697', 'css', '文件', '2018-03-24 21:07:54', null, null);
INSERT INTO `attachment` VALUES ('3da367e8-47a2-4615-9af5-db652c7af4e3', '文件', '注册', '/eb94748f-9de5-4d63-9fcd-93290c27312b/53886529-408c-450c-8b16-111edae3d478-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-28 00:38:09', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('3e592567-1a95-47cd-ac55-348889f516b2', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 23:03:22', null, null);
INSERT INTO `attachment` VALUES ('45feacd4-0d28-4aba-91b8-be6dfdaef0a6', '文件', '测试,word', '/系统设计方案.docx', '系统设计方案.docx', '31508', 'docx', '文件', '2018-03-20 00:06:00', null, null);
INSERT INTO `attachment` VALUES ('47e70a30-73d9-41da-91c7-1cd012f01a3d', '文件', '话单', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/b81b006e-cb23-41c2-b336-3332525ac010-18924625123.xls', '18924625123.xls', '1249792', 'xls', '文件', '2018-04-07 18:58:47', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('47e87ab5-5347-4146-840f-4f234fea559d', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38727', 'eml', '文件', '2018-03-25 14:45:33', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('4820a4b8-f2cf-4264-b5c9-4871356bebe0', '文件', '测试', '/2016_03_09_12_37_33.eml', '2016_03_09_12_37_33.eml', '36576', 'eml', '文件', '2018-03-25 14:37:08', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('484d4cd0-1727-440d-b921-a7ddead21aa5', '文件', '交易记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/21d25b0c-1fde-4f4c-a6ae-9af066122631-交易记录.csv', '交易记录.csv', '33508', 'csv', '文件', '2018-04-22 11:35:52', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('4aad703d-e2b9-444a-a131-5117f118d86c', '文件', '测试', '/2016_03_09_12_37_33.eml', '2016_03_09_12_37_33.eml', '37060', 'eml', '文件', '2018-03-25 14:36:04', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('4ad95790-40f2-4609-b523-96c0bf052191', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 17:11:04', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('5052847c-8794-454e-87b5-3e3623609ab4', '文件', '流水', '/bf256071-7539-4da4-835a-acf1b715744d/8aa23dfd-08f9-4d88-9f30-fb7e57ee811b-tp_1440299297_info.txt', 'tp_1440299297_info.txt', '233', 'txt', '文件', '2018-04-06 00:01:26', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('55063f3b-1c65-44ed-bb8a-39a20290aa55', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/2fbf98e8-4261-4a2c-a031-06f5c04be494-1023725.jpg', '1023725.jpg', '331483', 'jpg', '图片', '2018-03-30 22:00:41', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('55272f88-8cf3-4d2f-9529-8d4cec1838a7', '文件', '测试', '/2016_02_05_16_28_50 -3333.eml', '2016_02_05_16_28_50 -3333.eml', '115224', 'eml', '文件', '2018-03-25 15:40:53', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('563cbcf3-b713-4858-bd8c-6b75a14c9b0c', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/97538352-042a-4127-91d9-2c3a0f4e69d9-2015_12_01_03_53_36.eml', '2015_12_01_03_53_36.eml', '9074', 'eml', '文件', '2018-04-24 15:23:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('56e2591b-4ea0-4f0f-b895-f719cbcd00b9', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/0418a858-69e6-4aaf-9fde-386f0e13b2d1-【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 18:52:58', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('590a7e3a-4b47-4efe-85ff-6a63a6057c3f', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/aa81f5af-91ef-4464-bd94-6e8a62b78369-1023727.jpg', '1023727.jpg', '264271', 'jpg', '图片', '2018-03-30 22:00:41', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('5abbb1bc-2b35-4a71-afc1-18771829315b', '文件', '邮件', '/eb94748f-9de5-4d63-9fcd-93290c27312b/877aba00-bdb4-4197-a5ff-c5f306295845-【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-04-24 14:43:35', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('5b46dca1-10c4-4eda-b9ea-477bd9409e88', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/358a55a8-a89b-487e-b861-2d9a118e7f27-1023724.jpg', '1023724.jpg', '218733', 'jpg', '图片', '2018-03-30 21:59:53', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('5c6baf0c-8f1c-42de-ade6-bb9b79ee71ab', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/05330e9c-4d3f-4d89-a3c3-62ad2647aed2-iaX17CWdQiudi5EyQJ1U.png', 'iaX17CWdQiudi5EyQJ1U.png', '5382', 'png', '图片', '2018-03-30 00:29:38', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('5d900f6c-eff9-4fa9-933a-3a920bff3c81', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/50b6ca02-ba8a-40ca-a5ec-a6cbcf206835-2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38729', 'eml', '文件', '2018-03-25 20:32:16', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('6008293c-77a2-4f55-9708-6438039f33dd', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/d0e105e1-3f07-47ad-87f6-807e5af99b24-1023729.jpg', '1023729.jpg', '258599', 'jpg', '图片', '2018-03-30 22:01:39', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('610b0b6e-64b4-425e-95cc-4b05ee474eb7', '图片', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/7cdaa07c-8ddf-412b-8495-baeb408a3c9e-iaX17CWdQiudi5EyQJ1U.png', 'iaX17CWdQiudi5EyQJ1U.png', '5382', 'png', '图片', '2018-04-24 14:52:53', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('6466c969-4c47-4eeb-8f23-6be7cae296b8', '文件', '测试', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/2499bead-d04c-4c9d-ad75-eae7e4d0e338-2016_02_05_16_28_50 -3333.eml', '2016_02_05_16_28_50 -3333.eml', '115224', 'eml', '文件', '2018-03-26 23:14:21', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('6570472f-d91a-4d7f-98a7-e02b1cee3438', '文件', '邮件', '/eb94748f-9de5-4d63-9fcd-93290c27312b/542ce93b-aa5a-4994-9985-4d0b76cd8069-现在开始试用Wijmo Enterprise产品- (1).eml', '现在开始试用Wijmo Enterprise产品- (1).eml', '11129', 'eml', '文件', '2018-04-24 14:43:35', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('67899dec-32b8-45a3-9cc6-197f7b7d3f20', '文件', '登录', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/d68f6952-38ca-4a5a-af51-aff515c779b6-QQLogins.txt', 'QQLogins.txt', '11967679', 'txt', '文件', '2018-04-01 22:33:14', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('67e3fae1-6bee-41f0-b48c-37ecb9d1d0aa', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/c2ab1e23-15dd-44e3-b7d9-a6f96788d59b-2015_12_05_21_40_16.eml', '2015_12_05_21_40_16.eml', '3540', 'eml', '文件', '2018-04-24 15:23:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('6869d710-b011-4038-a3c6-4aa33d90fa64', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/be195115-93bb-42a8-a275-1d0216c9be2c-1023729.jpg', '1023729.jpg', '258599', 'jpg', '图片', '2018-03-30 21:54:45', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('68ecbef9-3425-47fe-afe6-9de0a4f9b3be', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38730', 'eml', '文件', '2018-03-25 14:44:42', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('69048397-a3f2-44ff-91b4-43f2b5a4b3ee', '文件', '注册', '/eb94748f-9de5-4d63-9fcd-93290c27312b/cece1e59-3ad1-45fe-b527-ee475b29fb34-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-27 22:03:02', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('690990eb-009e-45be-9318-7c7c7259e0ac', '文件', '登录', '/eb94748f-9de5-4d63-9fcd-93290c27312b/04512803-bd68-4f6a-8698-2fda6d312a6c-QQLogins.txt', 'QQLogins.txt', '11967679', 'txt', '文件', '2018-04-01 22:35:55', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('69353333-6046-4d99-9502-b5805d00955a', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/4f9b7ffa-699b-4be7-b505-72728e9359e3-2015_11_28_00_16_59.eml', '2015_11_28_00_16_59.eml', '9724', 'eml', '文件', '2018-04-24 15:23:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('6a1fbf32-cc4a-489e-b119-5d8933adf18f', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38919', 'eml', '文件', '2018-03-25 14:39:18', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('6b53fdf8-eee6-43e3-b056-45c2c8de6e0c', '文件', '提现记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/53eba985-4105-4aca-aa4a-92c14466b9e0-提现记录.csv', '提现记录.csv', '3872', 'csv', '文件', '2018-04-22 00:57:26', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('6de1a7c2-f84a-419c-91e2-2ecd98f14a6e', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/bd376b42-b565-49c8-92b8-b39660f344e8-2015_12_31_09_39_29.eml', '2015_12_31_09_39_29.eml', '4160', 'eml', '文件', '2018-05-27 18:09:35', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('713a5883-71f4-471f-9742-b6c40689ed24', '文件', '微信', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/82a652eb-aad9-4f38-938a-ac38ec8d1d74-info_1149078363.txt', 'info_1149078363.txt', '7884', 'txt', '文件', '2018-04-03 22:06:45', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('7276d346-84e8-4b10-814d-45a288eeccb0', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/1a785661-aca1-455d-8caa-683985cd61a2-2015_10_29_20_51_49.eml', '2015_10_29_20_51_49.eml', '2780', 'eml', '文件', '2018-04-24 15:23:38', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('747a5439-6d71-45c5-98d8-2538caab0130', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 15:19:55', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('7662ce42-2250-4313-9afc-8b26a6d7bfe2', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/7e7e9a38-496b-4e7d-8f7d-10cf7fbc6aac-2015_10_10_17_16_25.eml', '2015_10_10_17_16_25.eml', '7066328', 'eml', '文件', '2018-04-24 16:46:19', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('7900c5ce-0d04-499b-b3ef-58c69dc6d072', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/50d19778-907f-469c-bf8f-c0c8a5045ce5-323001.jpg', '323001.jpg', '235359', 'jpg', '图片', '2018-03-30 21:59:52', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('79d65fd9-1f35-44e3-a30e-7896ed6c9b10', '文件', '测试', '/2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38727', 'eml', '文件', '2018-03-25 14:46:59', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('7a8950a5-4a44-4f54-8e04-d340d5d4b0aa', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/51a1b054-5b7e-41de-a41b-790996f2fc58-18924625123.xls', '18924625123.xls', '1249792', 'xls', '文件', '2018-04-24 15:42:04', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('7a952872-9fd9-4237-a84b-aaa2a050f133', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/69ee7448-680c-4f7d-9cb0-ed3c5cd04a52-1023724.jpg', '1023724.jpg', '218733', 'jpg', '图片', '2018-03-30 21:54:25', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('7c59c217-ca6f-4985-9b66-467ec3865f52', '文件', '微信', '/bf256071-7539-4da4-835a-acf1b715744d/bea0861e-0aee-4706-b576-2d6aaa44fb31-tp_1440299297_info.txt', 'tp_1440299297_info.txt', '233', 'txt', '文件', '2018-04-05 12:27:32', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('7f69be4e-8dc1-4e7b-aef6-65d45694adbb', '文件', '登录', '/62a50696-12b0-4774-acd9-14c1f5c977c0/306bf5a9-cbcb-4ca8-b128-9fe09b847ac3-QQLogins.txt', 'QQLogins.txt', '11967679', 'txt', '文件', '2018-04-01 23:10:27', '张样品', '62a50696-12b0-4774-acd9-14c1f5c977c0');
INSERT INTO `attachment` VALUES ('7f9466e4-6950-44d7-8714-efe44260020b', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:06:54', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('84b41c76-e2e5-4bed-b057-3a85f767ab3b', '文件', '注册', '/62a50696-12b0-4774-acd9-14c1f5c977c0/b187c133-d11b-4951-9058-e2038f43db01-reg_12344335 - 副本.txt', 'reg_12344335 - 副本.txt', '3616', 'txt', '文件', '2018-03-28 00:52:35', '张样品', '62a50696-12b0-4774-acd9-14c1f5c977c0');
INSERT INTO `attachment` VALUES ('8b288dc3-6234-48db-b30c-473be87e1351', '文件', '登录日志', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/6b67dc35-d90c-4958-8965-49ff588a5e13-登陆日志.csv', '登陆日志.csv', '170', 'csv', '文件', '2018-04-22 01:18:42', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('8faa573d-cb13-46ea-bc32-87f970157224', '文件', '附件', '/dsl.txt', 'dsl.txt', '2087', 'txt', '文件', '2018-03-20 23:20:35', null, null);
INSERT INTO `attachment` VALUES ('9127e292-7948-401b-9f56-4155ef899d76', '文件', '转账明细', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/fa43b5c9-7cab-4adc-83d0-7211f6a2c77c-转账明细.csv', '转账明细.csv', '7423', 'csv', '文件', '2018-04-22 12:37:11', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('916d88f8-bca0-4f74-8e47-1439703138bf', '文件', '测试', '/2016_03_09_12_37_33.eml', '2016_03_09_12_37_33.eml', '37060', 'eml', '文件', '2018-03-25 14:35:39', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('92991469-d0be-4533-b15c-7a097629bf0a', '文件', '测试', '/2016_03_09_12_37_33.eml', '2016_03_09_12_37_33.eml', '37060', 'eml', '文件', '2018-03-25 14:34:53', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('944ad4f5-c4f9-47cc-a31b-44c96be8d250', '文件', '财付通', '/bf256071-7539-4da4-835a-acf1b715744d/4a61d832-e291-4034-9664-febd1554192a-tp_pygongyi_info.txt', 'tp_pygongyi_info.txt', '305', 'txt', '文件', '2018-04-05 11:27:08', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('94992255-d0d9-48bb-abab-952be6d72365', '文件', '网易', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/8bb8012f-9fe2-4e79-bc73-0e20488e7ef1-1tbiuBB9KVQG5dYlFwAAsv.eml', '1tbiuBB9KVQG5dYlFwAAsv.eml', '28421', 'eml', '文件', '2018-04-04 00:11:12', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('95c3a29f-a155-436d-b581-303704b04d28', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/3823cb99-0f59-4b94-9601-6011543f23d0-现在开始试用Wijmo Enterprise产品-.eml', '现在开始试用Wijmo Enterprise产品-.eml', '11129', 'eml', '文件', '2018-03-25 18:30:47', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('964108f9-da1f-4c81-a88c-b6b04faef4d5', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:11:15', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('99adf664-7ff4-49df-a78d-fb52fbf9f9cd', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:07:32', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('9d8f60c7-5caa-4b1b-9fbb-2a918c1001a3', '文件', '登录', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/357cbeb7-1599-48c9-99cd-0e0088c5e398-QQLogins.txt', 'QQLogins.txt', '95835', 'txt', '文件', '2018-04-02 22:03:22', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('9f769fa2-d5b4-4b52-80cd-790832b01649', '文件', '邮件', '/eb94748f-9de5-4d63-9fcd-93290c27312b/1d39a1d5-4624-43e0-89bf-3169aebd1026-现在开始试用Wijmo Enterprise产品-.eml', '现在开始试用Wijmo Enterprise产品-.eml', '11129', 'eml', '文件', '2018-04-24 14:43:35', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('a53496e0-2e09-4f9f-8622-7474938eebfc', '文件', '测试', '/现在开始试用Wijmo Enterprise产品-.eml', '现在开始试用Wijmo Enterprise产品-.eml', '11129', 'eml', '文件', '2018-03-24 23:02:41', null, null);
INSERT INTO `attachment` VALUES ('a7287671-e2d1-4442-8b04-92929749ec8d', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/29b66872-9148-4de8-b471-3d0fef05a0e8-tp_1440299297_info.txt', 'tp_1440299297_info.txt', '233', 'txt', '文件', '2018-04-24 15:24:53', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('a76272cb-858f-4e8e-b4f7-0b4adccb6831', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/044031700111_08207151.pdf', '044031700111_08207151.pdf', '37267', 'pdf', '文件', '2018-03-25 16:36:37', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('a7699b3b-ed0e-4cb9-97c7-a2009b977f0e', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/cb489fbc-2016-4598-b5f2-f6956f0a3f92-487666.jpg', '487666.jpg', '209826', 'jpg', '图片', '2018-03-30 21:54:23', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('a9e01e5f-f677-478e-9a9a-f6065a3c7ef8', '文件', '测试', '/现在开始试用Wijmo Enterprise产品- (1).eml', '现在开始试用Wijmo Enterprise产品- (1).eml', '11129', 'eml', '文件', '2018-03-24 23:08:40', null, null);
INSERT INTO `attachment` VALUES ('aa9fc189-11bf-421a-915c-61032fdd13fb', '文件', '财付通', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/760b7746-0cd0-4f32-8776-965d88fff585-tp_1440299297_info.txt', 'tp_1440299297_info.txt', '233', 'txt', '文件', '2018-04-05 11:50:40', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('ab0e0f96-5339-46f7-b237-89677bcab3c6', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/现在开始试用Wijmo Enterprise产品-.eml', '现在开始试用Wijmo Enterprise产品-.eml', '11129', 'eml', '文件', '2018-03-25 17:09:19', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('ac41fa2b-bd8d-42c6-99ab-077b94528396', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/399c4658-4a5c-43fb-b88d-0db2d5aa0369-885303.jpg', '885303.jpg', '115396', 'jpg', '图片', '2018-03-30 21:59:52', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('ad057b22-b4da-4d45-875c-c92e29f65367', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/ced956e6-ff2b-4afe-9265-511af6630552-2015_12_03_07_49_57.eml', '2015_12_03_07_49_57.eml', '54588', 'eml', '文件', '2018-04-24 15:23:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('ae011e29-ebbb-4a35-a679-3fba4a540c61', '文件', '提现记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/f48ee12d-ec20-4c08-80df-5b84fb7dd4cd-提现记录.csv', '提现记录.csv', '3872', 'csv', '文件', '2018-04-22 01:19:27', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('ae0f40ac-c3a2-4321-be07-4aefaf6d4b38', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/1891f67f-45ad-40d7-9798-c24377674bd2-487666.jpg', '487666.jpg', '209826', 'jpg', '图片', '2018-03-30 21:59:52', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('aee34c9a-0b11-452b-90f8-4713bacd34ce', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/ba174ec7-e72b-4123-953d-0ce46f6257c2-323001.jpg', '323001.jpg', '235359', 'jpg', '图片', '2018-03-30 21:54:23', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('b11816d3-2559-4d58-8311-91de796e46ba', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/7e0dd2c2-56ca-4e06-b707-bd0caf1cd509-1023727.jpg', '1023727.jpg', '264271', 'jpg', '图片', '2018-03-30 21:54:45', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('b13afa88-a475-41bb-8ceb-52dd300e3925', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 17:09:38', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('b1481f99-6d28-4719-93d9-650bb7d03b07', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:10:00', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('b733b216-8b04-4c29-a056-f65c4ae55868', '文件', '微信', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/92b0f2ac-7a61-4688-b833-9bc9fe41f111-info_1149078363.txt', 'info_1149078363.txt', '7884', 'txt', '文件', '2018-04-03 23:04:18', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('b98f2389-32a0-4972-ae05-b62febd3834e', '文件', '测试,word', '/系统设计方案.docx', '系统设计方案.docx', '31508', 'docx', '文件', '2018-03-20 00:12:38', null, null);
INSERT INTO `attachment` VALUES ('bbe26aa3-d307-4648-8d1e-a72e6725c78f', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/3949ce2e-46b7-4450-b87c-1920d04916c9-【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 17:33:12', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('bd418090-5d9c-4541-856f-7a0eb71388ca', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:24:29', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('be412d93-313c-45b0-8fe2-081242ee1184', '文件', '注册', '/eb94748f-9de5-4d63-9fcd-93290c27312b/ace4a31e-801b-4f92-9c7b-4610b3c5fc0a-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-27 23:33:28', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('c134bbbe-6a59-47f1-a0b9-b3e40ba07d7c', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/e2ce20c0-7cd9-4c53-a41e-0cac1c67d4e4-2016_03_09_12_37_33.eml', '2016_03_09_12_37_33.eml', '36576', 'eml', '文件', '2018-04-24 14:53:39', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('c678f714-1956-4c14-b0ce-8dfa573a46d4', '图片', '测试', '/person1.jpg', 'person1.jpg', '96608', 'jpg', '图片', '2018-03-24 22:13:31', null, null);
INSERT INTO `attachment` VALUES ('c69b2a59-b589-490d-9bde-6989af021088', '文件', '注册', '/62a50696-12b0-4774-acd9-14c1f5c977c0/763c226f-d8f1-4f21-baa4-bb8a7c30bd05-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-28 00:52:35', '张样品', '62a50696-12b0-4774-acd9-14c1f5c977c0');
INSERT INTO `attachment` VALUES ('c8aef348-2083-4108-b387-9faefb51cd1c', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/5129f16f-d533-45b7-a52b-465ecd828983-2015_11_02_15_35_41.eml', '2015_11_02_15_35_41.eml', '11764', 'eml', '文件', '2018-04-24 15:23:38', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('ca03541b-2435-404b-88cd-8031826d1af0', '文件', '流水', '/bf256071-7539-4da4-835a-acf1b715744d/465573fa-add7-430a-ab52-0712f457a80b-tp_pygongyi_trades.txt', 'tp_pygongyi_trades.txt', '59725', 'txt', '文件', '2018-04-06 10:32:52', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('ca1f0ec8-f290-42cd-822b-2c402cc0c40f', '文件', '提现记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/17b8f305-0ea4-49c8-85c1-0373a79da316-提现记录.csv', '提现记录.csv', '3872', 'csv', '文件', '2018-04-22 01:04:09', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('ccf472c0-10c3-436a-a89c-66d1289eef48', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 23:04:08', null, null);
INSERT INTO `attachment` VALUES ('ce77475a-cb12-418f-99f3-29de3f32048d', '文件', '注册', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/3b485074-91f5-4b87-8247-a047ee523cd5-reg_12344335.txt', 'reg_12344335.txt', '3616', 'txt', '文件', '2018-03-28 23:38:59', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('cf911202-0121-4a9b-baf5-cb5afcd11c6e', '文件', '账户明细', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/b6daa659-a54c-42f7-8b42-1870e082bab4-账户明细.csv', '账户明细.csv', '58353', 'csv', '文件', '2018-04-22 00:19:03', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('d0065e6a-7cd6-440f-ac34-50113038aad7', '文件', '邮件', '/eb94748f-9de5-4d63-9fcd-93290c27312b/c308f518-3bef-4c2f-a5a5-8b386375a7b5-现在开始试用Wijmo Enterprise产品- (1).eml', '现在开始试用Wijmo Enterprise产品- (1).eml', '11129', 'eml', '文件', '2018-04-24 14:42:51', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('d294ca78-921a-41f0-a650-0c45a1f0423c', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/f5592c57-bf6d-4c50-9fe6-0b291a87ad7a-QQLogins.txt', 'QQLogins.txt', '11967679', 'txt', '文件', '2018-04-24 14:49:27', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('d49a2ef4-8219-4c59-a771-d50333f9dc4c', '图片', '照片', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/831c5be9-a9f2-4d82-9d2e-ce3b02c6dfb9-jIF1FEryNvucagT6B4RS.png', 'jIF1FEryNvucagT6B4RS.png', '6330', 'png', '图片', '2018-03-30 00:35:29', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('d5c243d0-218d-4d20-a79d-2ce09915ce0e', '文件', '财付通', '/bf256071-7539-4da4-835a-acf1b715744d/b34b090e-19d4-4f9b-8b25-6b3af9510b39-tp_pygongyi_info.txt', 'tp_pygongyi_info.txt', '305', 'txt', '文件', '2018-04-05 11:23:23', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('d6aa555e-79e9-4585-85cf-e86302ede602', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/edcb0b26-4d5d-44d8-b339-2725a9e57f10-reg_982389608.txt', 'reg_982389608.txt', '2803', 'txt', '文件', '2018-04-04 00:15:22', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('d6b29295-9305-405f-ac50-8694457c6619', '文件', '登录', '/2716ffb9-52aa-4286-90e0-56d7d6f83617/14d40c48-25b4-4608-a535-2fd58a614177-QQLogins.txt', 'QQLogins.txt', '11967679', 'txt', '文件', '2018-04-01 22:28:28', '王五', '2716ffb9-52aa-4286-90e0-56d7d6f83617');
INSERT INTO `attachment` VALUES ('d78ce0f9-3fec-4570-9689-f763bab20bc3', '文件', '提现记录', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/bedd54c0-5357-4018-9d12-e196254634ea-提现记录.csv', '提现记录.csv', '3872', 'csv', '文件', '2018-04-22 00:52:24', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('d8f0d8be-2224-4819-826c-e714a73d4091', '文件', '微信', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/df766c63-4695-422b-9a00-b0113e35fc00-info_1149078363.txt', 'info_1149078363.txt', '7884', 'txt', '文件', '2018-05-05 13:12:46', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('d98223b9-5eb4-4174-9b0c-73c5f74db03c', '文件', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/c382068a-7b15-4668-8f45-660901f14eee-2015_10_10_17_16_25.eml', '2015_10_10_17_16_25.eml', '7066328', 'eml', '文件', '2018-04-24 15:23:38', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('d9ae533f-9444-4553-aae8-c8f69930a4d2', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/15482785-6da7-4ece-8a91-5e0ed2531d8e-2016_03_28_22_15_00.eml', '2016_03_28_22_15_00.eml', '38729', 'eml', '文件', '2018-03-25 20:33:18', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('da4b1212-9d51-4204-9ef5-93474f356148', '文件', '注册信息', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/b28f9a48-6ea0-41bd-b8ca-283178a7fc47-注册信息.csv', '注册信息.csv', '270', 'csv', '文件', '2018-04-22 01:18:19', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('db33aeb8-f8ea-4643-bd11-a73997ff5aa1', '图片', '测试', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/29318a81-9ef7-45dd-9bfd-e951fdaa0784-jIF1FEryNvucagT6B4RS.png', 'jIF1FEryNvucagT6B4RS.png', '6330', 'png', '图片', '2018-04-24 14:52:53', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('db52028c-dbbd-4533-b015-5ce7c2eecc67', '文件', '注册', '/eb94748f-9de5-4d63-9fcd-93290c27312b/6c4b19e8-9bf3-4d08-a708-a387fa5bf433-reg_12344335 - 副本.txt', 'reg_12344335 - 副本.txt', '3616', 'txt', '文件', '2018-03-28 00:26:34', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('e38f1fb6-7803-4e67-a962-6240c7a38096', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 23:04:43', null, null);
INSERT INTO `attachment` VALUES ('e51f22d4-cd15-44c0-b4c9-ecde359ae016', '文件', '测试', '/bf256071-7539-4da4-835a-acf1b715744d/33a61a5a-97c0-4f91-9e2f-26acab07d2dd-2016_01_05_13_50_17.eml', '2016_01_05_13_50_17.eml', '5460', 'eml', '文件', '2018-05-27 18:06:35', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('e5953dac-a049-44bc-bbca-d33889a52b54', '文件', '账户明细', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/58fc2b94-b5fa-47a9-af14-277ea1308ccc-账户明细.csv', '账户明细.csv', '58353', 'csv', '文件', '2018-04-22 00:05:12', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('e63db552-e3fe-45f9-ad57-18f9a6a6e64e', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 14:32:48', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('e8c2687b-8657-4bd9-bca8-aa0b5db7358f', '文件', '财付通', '/3f505207-bacc-4cbd-a58d-53fe4f2695a0/c4e99420-5188-402c-949f-4ff674bebb63-tp_1440299297_trades.txt', 'tp_1440299297_trades.txt', '10017', 'txt', '文件', '2018-05-05 13:46:50', '刘丽丽', '3f505207-bacc-4cbd-a58d-53fe4f2695a0');
INSERT INTO `attachment` VALUES ('ec0cb5ed-a0e2-4911-9ef1-d82083646ebe', '文件', '邮件', '/bf256071-7539-4da4-835a-acf1b715744d/a1b506b3-3d87-4504-bca9-f8595eaa16b7-1tbiKA5iKVWBS2IjfAABsO.eml', '1tbiKA5iKVWBS2IjfAABsO.eml', '2125', 'eml', '文件', '2018-04-09 22:19:43', '尼古拉斯。赵四', 'bf256071-7539-4da4-835a-acf1b715744d');
INSERT INTO `attachment` VALUES ('ec7e36d9-8428-4108-9199-9a708dd3baf4', '文件', '测试', '/2016_02_05_16_28_50 -3333.eml', '2016_02_05_16_28_50 -3333.eml', '115224', 'eml', '文件', '2018-03-25 15:17:56', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('f0a19ed1-2a11-42ca-a98c-acce17709b61', '文件', '测试', '/15bf33c0-8ec4-4161-ba0e-7887503066fd/4475f241-43ba-4297-b5bd-ea500b56f3d3-【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-25 17:41:32', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');
INSERT INTO `attachment` VALUES ('f6c0bf2c-3140-4679-8df8-f7152aca0029', '图片', '照片', '/eb94748f-9de5-4d63-9fcd-93290c27312b/444e7242-6c15-4f29-96ce-56bd913aeba7-1023725.jpg', '1023725.jpg', '331483', 'jpg', '图片', '2018-03-30 21:59:53', '王五', 'eb94748f-9de5-4d63-9fcd-93290c27312b');
INSERT INTO `attachment` VALUES ('fb989d24-5a0a-4bd7-9704-66a8de16ebf1', '文件', '测试', '/【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '【电子发票】您收到一张新的电子发票[发票号码：08207151]-.eml', '82260', 'eml', '文件', '2018-03-24 23:07:42', null, null);
INSERT INTO `attachment` VALUES ('fdbe42d3-ae6e-4358-ab04-060d1a0e5f8c', '文件', '账户明细', '/0155acf5-e2bc-472a-b7bc-01ac81cee56f/a94539be-ba3b-416d-ab59-ed5bb1068001-账户明细.csv', '账户明细.csv', '58353', 'csv', '文件', '2018-04-22 01:19:03', '公共人员', '0155acf5-e2bc-472a-b7bc-01ac81cee56f');
INSERT INTO `attachment` VALUES ('ff97ea24-c223-48f4-9ad0-6ff7d0c944cb', '文件', '测试', '/2016_02_05_16_28_50.eml', '2016_02_05_16_28_50.eml', '115406', 'eml', '文件', '2018-03-25 13:18:03', '张洋洋', '15bf33c0-8ec4-4161-ba0e-7887503066fd');

-- ----------------------------
-- Table structure for eqa_index
-- ----------------------------
DROP TABLE IF EXISTS `eqa_index`;
CREATE TABLE `eqa_index` (
  `index_Name` varchar(255) NOT NULL COMMENT '索引列表',
  `index_Name_Cn` varchar(255) DEFAULT NULL COMMENT '索引名称',
  `sort` int(255) DEFAULT NULL,
  PRIMARY KEY (`index_Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of eqa_index
-- ----------------------------
INSERT INTO `eqa_index` VALUES ('attachment', '文件信息', '0');
INSERT INTO `eqa_index` VALUES ('cftreginfo', '财付通注册信息', '14');
INSERT INTO `eqa_index` VALUES ('cfttrades', '财付通流水信息', '15');
INSERT INTO `eqa_index` VALUES ('comment', '批注信息', '24');
INSERT INTO `eqa_index` VALUES ('email', '邮件信息', '11');
INSERT INTO `eqa_index` VALUES ('huaduan', '话单基本信息', '12');
INSERT INTO `eqa_index` VALUES ('huaduan_list', '话单流水信息', '13');
INSERT INTO `eqa_index` VALUES ('qqloginip', 'QQ登录记录', '3');
INSERT INTO `eqa_index` VALUES ('qqloginip_list', 'QQ登录IP列表', '4');
INSERT INTO `eqa_index` VALUES ('qqreginfo', 'QQ注册信息', '2');
INSERT INTO `eqa_index` VALUES ('qqzone', 'QQ空间', '5');
INSERT INTO `eqa_index` VALUES ('suspicious', '可疑人信息', '1');
INSERT INTO `eqa_index` VALUES ('wxloginip', '微信登录信息', '10');
INSERT INTO `eqa_index` VALUES ('wxlxr', '微信联系人信息', '8');
INSERT INTO `eqa_index` VALUES ('wxqun', '微信群信息', '9');
INSERT INTO `eqa_index` VALUES ('wxreginfo', '微信注册信息', '7');
INSERT INTO `eqa_index` VALUES ('xndw_sx', '神行虚拟定位', '22');
INSERT INTO `eqa_index` VALUES ('xndw_wsk', 'WSK虚拟定位', '23');
INSERT INTO `eqa_index` VALUES ('zfbjyjlinfo', '支付宝交易记录', '21');
INSERT INTO `eqa_index` VALUES ('zfblogininfo', '支付宝登录日志', '18');
INSERT INTO `eqa_index` VALUES ('zfbreginfo', '支付宝注册信息', '16');
INSERT INTO `eqa_index` VALUES ('zfbtxinfo', '支付宝提现记录', '20');
INSERT INTO `eqa_index` VALUES ('zfbzhinfo', '支付宝账户明细', '17');
INSERT INTO `eqa_index` VALUES ('zfbzzinfo', '支付宝转账记录', '19');

-- ----------------------------
-- Table structure for eqa_meta
-- ----------------------------
DROP TABLE IF EXISTS `eqa_meta`;
CREATE TABLE `eqa_meta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `index_name` varchar(255) DEFAULT NULL COMMENT 'es 索引名称',
  `field_name` varchar(255) DEFAULT NULL COMMENT '字段名称',
  `field_code` varchar(255) DEFAULT NULL COMMENT '字段代码',
  `field_type` int(255) DEFAULT NULL COMMENT '字段类型：1：文本；2：标签；3：数字；4：日期；5：字典',
  `is_fx` int(255) NOT NULL DEFAULT '1' COMMENT '是否分析维度',
  `sort` int(255) NOT NULL DEFAULT '1' COMMENT '排序号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=351 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of eqa_meta
-- ----------------------------
INSERT INTO `eqa_meta` VALUES ('1', 'attachment', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('2', 'attachment', '文件名', 'name', '1', '1', '2');
INSERT INTO `eqa_meta` VALUES ('3', 'attachment', '文件夹', 'folder', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('4', 'attachment', '标签', 'tags', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('5', 'attachment', '文件类型', 'type', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('6', 'attachment', '文件后缀名', 'suffix', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('7', 'attachment', '文件大小', 'size', '3', '1', '7');
INSERT INTO `eqa_meta` VALUES ('8', 'attachment', '文件路径', 'path', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('9', 'attachment', '嫌疑人姓名', 'susp_name', '2', '0', '9');
INSERT INTO `eqa_meta` VALUES ('10', 'attachment', '嫌疑人id', 'susp_id', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('11', 'attachment', '创建时间', 'create_time', '4', '1', '11');
INSERT INTO `eqa_meta` VALUES ('12', 'suspicious', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('13', 'suspicious', '嫌疑人姓名', 'name', '1', '1', '2');
INSERT INTO `eqa_meta` VALUES ('14', 'suspicious', '人员类型', 'type', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('15', 'suspicious', '身份证号', 'gmsfzh', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('16', 'suspicious', 'QQ', 'qq', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('17', 'suspicious', '微信', 'weixin', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('18', 'suspicious', '财付通', 'cft', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('19', 'suspicious', '支付宝', 'zfb', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('20', 'suspicious', '银行账号', 'yhzh', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('21', 'suspicious', '手机号', 'phone', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('22', 'suspicious', 'IMEI', 'imei', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('23', 'suspicious', 'IMSI', 'imsi', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('24', 'suspicious', '电子邮箱', 'email', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('25', 'suspicious', '工作进度', 'gzjd', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('26', 'suspicious', '其他码值', 'other', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('27', 'suspicious', '情况简介', 'qkjj', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('28', 'suspicious', '创建时间', 'create_time', '4', '1', '16');
INSERT INTO `eqa_meta` VALUES ('29', 'suspicious', '修改时间', 'modify_time', '4', '0', '16');
INSERT INTO `eqa_meta` VALUES ('30', 'suspicious', '嫌疑人姓名(NA)', 'name_na', '2', '1', '17');
INSERT INTO `eqa_meta` VALUES ('31', 'suspicious', '嫌疑人id', 'susp_id', '2', '1', '18');
INSERT INTO `eqa_meta` VALUES ('32', 'suspicious', '嫌疑人姓名', 'susp_name', '2', '1', '19');
INSERT INTO `eqa_meta` VALUES ('33', 'qqreginfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('34', 'qqreginfo', 'QQ号码', 'qq', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('35', 'qqreginfo', '昵称', 'name', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('36', 'qqreginfo', '国家', 'gj', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('37', 'qqreginfo', '省份', 'sf', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('38', 'qqreginfo', '邮编', 'yzbm', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('39', 'qqreginfo', '地址', 'dz', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('40', 'qqreginfo', '电话', 'dh', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('41', 'qqreginfo', '生日', 'csrq', '4', '1', '9');
INSERT INTO `eqa_meta` VALUES ('42', 'qqreginfo', '性别', 'xb', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('43', 'qqreginfo', '真实姓名', 'xm', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('44', 'qqreginfo', 'Email', 'email', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('45', 'qqreginfo', '主页', 'home', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('46', 'qqreginfo', '城市', 'cs', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('47', 'qqreginfo', '毕业院校', 'bycs', '1', '1', '15');
INSERT INTO `eqa_meta` VALUES ('48', 'qqreginfo', '星座', 'xz', '1', '1', '16');
INSERT INTO `eqa_meta` VALUES ('49', 'qqreginfo', '注册时间', 'zcsj', '4', '1', '17');
INSERT INTO `eqa_meta` VALUES ('50', 'qqreginfo', '好友', 'qqhy', '1', '1', '18');
INSERT INTO `eqa_meta` VALUES ('51', 'qqreginfo', '创建的群', 'jrqh', '1', '1', '19');
INSERT INTO `eqa_meta` VALUES ('52', 'qqreginfo', '加入的群', 'cjqh', '1', '1', '20');
INSERT INTO `eqa_meta` VALUES ('53', 'qqreginfo', '嫌疑人id', 'susp_id', '2', '1', '21');
INSERT INTO `eqa_meta` VALUES ('54', 'qqreginfo', '嫌疑人姓名', 'susp_name', '2', '1', '22');
INSERT INTO `eqa_meta` VALUES ('55', 'qqreginfo', '文件id', 'file_id', '2', '1', '23');
INSERT INTO `eqa_meta` VALUES ('56', 'qqreginfo', '创建时间', 'create_time', '4', '1', '24');
INSERT INTO `eqa_meta` VALUES ('57', 'qqreginfo', '标签', 'tags', '2', '1', '25');
INSERT INTO `eqa_meta` VALUES ('58', 'qqloginip', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('59', 'qqloginip', '文件id', 'file_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('60', 'qqloginip', 'QQ号码', 'qq', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('61', 'qqloginip', '记录数', 'size', '3', '1', '4');
INSERT INTO `eqa_meta` VALUES ('62', 'qqloginip', 'ip列表', 'ip_list', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('63', 'qqloginip', '嫌疑人id', 'susp_id', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('64', 'qqloginip', '嫌疑人姓名', 'susp_name', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('65', 'qqloginip', '创建时间', 'create_time', '4', '1', '8');
INSERT INTO `eqa_meta` VALUES ('66', 'qqloginip', '标签', 'tags', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('67', 'qqloginip_list', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('68', 'qqloginip_list', 'ip', 'ip', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('69', 'qqloginip_list', '上线时间', 'login_time', '4', '1', '3');
INSERT INTO `eqa_meta` VALUES ('70', 'qqloginip_list', '离线时间', 'logout_time', '4', '1', '4');
INSERT INTO `eqa_meta` VALUES ('71', 'qqloginip_list', '登录方式', 'login_type', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('72', 'qqloginip_list', '登录记录id', 'login_id', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('73', 'qqloginip_list', '嫌疑人id', 'susp_id', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('74', 'qqloginip_list', '嫌疑人姓名', 'susp_name', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('75', 'qqloginip_list', '文件id', 'file_id', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('76', 'qqloginip_list', '创建时间', 'create_time', '4', '1', '10');
INSERT INTO `eqa_meta` VALUES ('77', 'qqzone', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('78', 'qqzone', 'QQ号码', 'qq', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('79', 'qqzone', '照片数量', 'pic', '3', '1', '3');
INSERT INTO `eqa_meta` VALUES ('80', 'qqzone', '嫌疑人id', 'susp_id', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('81', 'qqzone', '嫌疑人姓名', 'susp_name', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('82', 'qqzone', '文件id', 'file_id', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('83', 'qqzone', '创建时间', 'create_time', '4', '1', '7');
INSERT INTO `eqa_meta` VALUES ('84', 'qqzone', '标签', 'tags', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('85', 'wxreginfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('86', 'wxreginfo', '微信号', 'weixin', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('87', 'wxreginfo', 'QQ号码', 'qq', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('88', 'wxreginfo', '昵称', 'name', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('89', 'wxreginfo', 'Email', 'email', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('90', 'wxreginfo', '别名', 'bm', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('91', 'wxreginfo', '签名', 'qm', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('92', 'wxreginfo', '手机号', 'dh', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('93', 'wxreginfo', '性别', 'xb', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('94', 'wxreginfo', '省份', 'sf', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('95', 'wxreginfo', '城市', 'cs', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('96', 'wxreginfo', '嫌疑人id', 'susp_id', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('97', 'wxreginfo', '嫌疑人姓名', 'susp_name', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('98', 'wxreginfo', '文件id', 'file_id', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('99', 'wxreginfo', '创建时间', 'create_time', '4', '1', '15');
INSERT INTO `eqa_meta` VALUES ('100', 'wxreginfo', '标签', 'tags', '2', '1', '16');
INSERT INTO `eqa_meta` VALUES ('101', 'wxlxr', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('102', 'wxlxr', '微信号', 'weixin', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('103', 'wxlxr', '微信注册信息id', 'info_id', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('104', 'wxlxr', '好友微信号', 'zh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('105', 'wxlxr', '好友QQ号码', 'qq', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('106', 'wxlxr', '手机号', 'dh', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('107', 'wxlxr', 'Email', 'email', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('108', 'wxlxr', '别名', 'bm', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('109', 'wxlxr', '微博', 'wbo', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('110', 'wxlxr', '昵称', 'nc', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('111', 'wxlxr', '嫌疑人id', 'susp_id', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('112', 'wxlxr', '嫌疑人姓名', 'susp_name', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('113', 'wxlxr', '文件id', 'file_id', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('114', 'wxlxr', '创建时间', 'create_time', '4', '1', '14');
INSERT INTO `eqa_meta` VALUES ('115', 'wxqun', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('116', 'wxqun', '微信号', 'weixin', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('117', 'wxqun', '微信注册信息id', 'info_id', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('118', 'wxqun', '群号', 'zh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('119', 'wxqun', '群名称', 'mc', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('120', 'wxqun', '群创建时间', 'cjsj', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('121', 'wxqun', '嫌疑人id', 'susp_id', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('122', 'wxqun', '嫌疑人姓名', 'susp_name', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('123', 'wxqun', '文件id', 'file_id', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('124', 'wxqun', '创建时间', 'create_time', '4', '1', '10');
INSERT INTO `eqa_meta` VALUES ('125', 'wxloginip', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('126', 'wxloginip', '微信注册信息id', 'info_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('127', 'wxloginip', '微信号', 'weixin', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('128', 'wxloginip', 'ip', 'ip', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('129', 'wxloginip', '登录时间', 'cjsj', '4', '1', '5');
INSERT INTO `eqa_meta` VALUES ('130', 'wxloginip', '嫌疑人id', 'susp_id', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('131', 'wxloginip', '嫌疑人姓名', 'susp_name', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('132', 'wxloginip', '文件id', 'file_id', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('133', 'wxloginip', '创建时间', 'create_time', '4', '1', '9');
INSERT INTO `eqa_meta` VALUES ('134', 'email', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('135', 'email', '发送人', 'from', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('136', 'email', '发送地址', 'from_address', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('137', 'email', '接收人', 'to', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('138', 'email', '接收地址', 'to_address', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('139', 'email', '接收时间', 'received_date', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('140', 'email', '邮件主题', 'subject', '1', '1', '7');
INSERT INTO `eqa_meta` VALUES ('141', 'email', '邮件内容', 'content', '1', '1', '8');
INSERT INTO `eqa_meta` VALUES ('142', 'email', '邮件附件内容', 'file_list', '1', '1', '9');
INSERT INTO `eqa_meta` VALUES ('143', 'email', '创建时间', 'create_time', '4', '1', '10');
INSERT INTO `eqa_meta` VALUES ('144', 'email', '文件id', 'file_id', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('145', 'email', '嫌疑人姓名', 'susp_name', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('146', 'email', '嫌疑人id', 'susp_id', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('147', 'email', '标签', 'tags', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('148', 'huaduan', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('149', 'huaduan', '运营商', 'yys', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('150', 'huaduan', '手机号码', 'zjhm', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('151', 'huaduan', '话单记录数', 'size', '3', '1', '4');
INSERT INTO `eqa_meta` VALUES ('152', 'huaduan', '嫌疑人id', 'susp_id', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('153', 'huaduan', '嫌疑人姓名', 'susp_name', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('154', 'huaduan', '文件id', 'file_id', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('155', 'huaduan', '创建时间', 'create_time', '4', '1', '8');
INSERT INTO `eqa_meta` VALUES ('156', 'huaduan', '标签', 'tags', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('157', 'huaduan_list', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('158', 'huaduan_list', '话单基本信息id', 'hd_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('159', 'huaduan_list', '运营商', 'yys', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('160', 'huaduan_list', '通话类型', 'thlx', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('161', 'huaduan_list', '主机号码', 'zjhm', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('162', 'huaduan_list', '对端号码', 'ddhm', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('163', 'huaduan_list', '对端号码归属地', 'ddhmgsd', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('164', 'huaduan_list', '开始时间', 'kssj', '4', '1', '8');
INSERT INTO `eqa_meta` VALUES ('165', 'huaduan_list', '开始时间', 'jssj', '4', '1', '9');
INSERT INTO `eqa_meta` VALUES ('166', 'huaduan_list', '通话时长', 'thsc', '3', '1', '10');
INSERT INTO `eqa_meta` VALUES ('167', 'huaduan_list', '呼叫类型', 'hjlx', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('168', 'huaduan_list', '小区号', 'xqh', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('169', 'huaduan_list', '嫌疑人id', 'susp_id', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('170', 'huaduan_list', '嫌疑人姓名', 'susp_name', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('171', 'huaduan_list', '文件id', 'file_id', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('172', 'huaduan_list', '创建时间', 'create_time', '4', '1', '16');
INSERT INTO `eqa_meta` VALUES ('173', 'huaduan_list', '标签', 'tags', '2', '1', '17');
INSERT INTO `eqa_meta` VALUES ('174', 'cftreginfo', '主键', 'id', '2', '1', '1');
INSERT INTO `eqa_meta` VALUES ('175', 'cftreginfo', '账户状态', 'zhzt', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('176', 'cftreginfo', '账号', 'zh', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('177', 'cftreginfo', '注册姓名', 'name', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('178', 'cftreginfo', '注册身份证号', 'sfzh', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('179', 'cftreginfo', '注册时间', 'zcsj', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('180', 'cftreginfo', '手机号', 'dh', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('181', 'cftreginfo', '开户行信息', 'khxx_list', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('182', 'cftreginfo', '银行账号', 'yhzh_list', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('183', 'cftreginfo', '嫌疑人id', 'susp_id', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('184', 'cftreginfo', '嫌疑人姓名', 'susp_name', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('185', 'cftreginfo', '文件id', 'file_id', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('186', 'cftreginfo', '创建时间', 'create_time', '4', '1', '13');
INSERT INTO `eqa_meta` VALUES ('187', 'cftreginfo', '标签', 'tags', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('188', 'cfttrades', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('189', 'cfttrades', '财付通注册信息ID', 'cft_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('190', 'cfttrades', '用户ID', 'zh', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('191', 'cfttrades', '交易单号', 'jydh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('192', 'cfttrades', '借贷类型', 'jdlx', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('193', 'cfttrades', '交易类型', 'jylx', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('194', 'cfttrades', '交易金额', 'jyje', '3', '1', '7');
INSERT INTO `eqa_meta` VALUES ('195', 'cfttrades', '账户余额', 'jyye', '3', '1', '8');
INSERT INTO `eqa_meta` VALUES ('196', 'cfttrades', '交易时间', 'jysj', '4', '1', '9');
INSERT INTO `eqa_meta` VALUES ('197', 'cfttrades', '银行类型', 'yhlx', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('198', 'cfttrades', '交易说明', 'jysm', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('199', 'cfttrades', '商户名称', 'shmc', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('200', 'cfttrades', '发送方', 'fsf', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('201', 'cfttrades', '发送金额', 'fsje', '3', '1', '14');
INSERT INTO `eqa_meta` VALUES ('202', 'cfttrades', '接收方', 'jsf', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('203', 'cfttrades', '接收时间', 'jssj', '4', '1', '16');
INSERT INTO `eqa_meta` VALUES ('204', 'cfttrades', '接收金额', 'jsje', '3', '1', '17');
INSERT INTO `eqa_meta` VALUES ('205', 'cfttrades', '嫌疑人id', 'susp_id', '2', '1', '18');
INSERT INTO `eqa_meta` VALUES ('206', 'cfttrades', '嫌疑人姓名', 'susp_name', '2', '1', '19');
INSERT INTO `eqa_meta` VALUES ('207', 'cfttrades', '文件id', 'file_id', '2', '1', '20');
INSERT INTO `eqa_meta` VALUES ('208', 'cfttrades', '创建时间', 'create_time', '4', '1', '21');
INSERT INTO `eqa_meta` VALUES ('209', 'cfttrades', '标签', 'tags', '2', '1', '22');
INSERT INTO `eqa_meta` VALUES ('210', 'zfbreginfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('211', 'zfbreginfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('212', 'zfbreginfo', '登陆邮箱', 'email', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('213', 'zfbreginfo', '登陆手机', 'dlsj', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('214', 'zfbreginfo', '账户名称', 'name', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('215', 'zfbreginfo', '证件类型', 'zjlx', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('216', 'zfbreginfo', '注册身份证号', 'sfzh', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('217', 'zfbreginfo', '可用余额', 'ye', '3', '1', '8');
INSERT INTO `eqa_meta` VALUES ('218', 'zfbreginfo', '绑定手机', 'bdsj', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('219', 'zfbreginfo', '开户行信息', 'khxx_list', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('220', 'zfbreginfo', '银行账号', 'yhzh_list', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('221', 'zfbreginfo', '协查编号', 'xcbh', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('222', 'zfbreginfo', '文件id', 'file_id', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('223', 'zfbreginfo', '创建时间', 'create_time', '4', '1', '14');
INSERT INTO `eqa_meta` VALUES ('224', 'zfbreginfo', '标签', 'tags', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('225', 'zfbzhinfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('226', 'zfbzhinfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('227', 'zfbzhinfo', '用户名称', 'name', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('228', 'zfbzhinfo', '交易号', 'jyh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('229', 'zfbzhinfo', '商户订单号', 'shddh', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('230', 'zfbzhinfo', '交易创建时间', 'jycjsj', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('231', 'zfbzhinfo', '付款时间', 'fksj', '4', '1', '7');
INSERT INTO `eqa_meta` VALUES ('232', 'zfbzhinfo', '最近修改时间', 'zjxgsj', '4', '1', '8');
INSERT INTO `eqa_meta` VALUES ('233', 'zfbzhinfo', '交易来源地', 'jylyd', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('234', 'zfbzhinfo', '交易类型', 'jylx', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('235', 'zfbzhinfo', '交易对方ID', 'df_user_id', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('236', 'zfbzhinfo', '交易对方户名', 'df_name', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('237', 'zfbzhinfo', '消费名称', 'xfmc', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('238', 'zfbzhinfo', '转账金额（元）', 'je', '3', '1', '14');
INSERT INTO `eqa_meta` VALUES ('239', 'zfbzhinfo', '收/支标记', 'sjbj', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('240', 'zfbzhinfo', '交易状态', 'jyzt', '2', '1', '16');
INSERT INTO `eqa_meta` VALUES ('241', 'zfbzhinfo', '备注', 'bz', '2', '1', '17');
INSERT INTO `eqa_meta` VALUES ('242', 'zfbzhinfo', '协查编号', 'xcbh', '2', '1', '18');
INSERT INTO `eqa_meta` VALUES ('243', 'zfbzhinfo', '文件id', 'file_id', '2', '1', '19');
INSERT INTO `eqa_meta` VALUES ('244', 'zfbzhinfo', '创建时间', 'create_time', '4', '1', '20');
INSERT INTO `eqa_meta` VALUES ('245', 'zfbzhinfo', '标签', 'tags', '2', '1', '21');
INSERT INTO `eqa_meta` VALUES ('246', 'zfblogininfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('247', 'zfblogininfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('248', 'zfblogininfo', '登陆账号', 'dlzh', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('249', 'zfblogininfo', '账户名称', 'name', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('250', 'zfblogininfo', 'ip', 'ip', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('251', 'zfblogininfo', '登录时间', 'czsj', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('252', 'zfblogininfo', '协查编号', 'xcbh', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('253', 'zfblogininfo', '文件id', 'file_id', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('254', 'zfblogininfo', '创建时间', 'create_time', '4', '1', '9');
INSERT INTO `eqa_meta` VALUES ('255', 'zfblogininfo', '标签', 'tags', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('256', 'zfbzzinfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('257', 'zfbzzinfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('258', 'zfbzzinfo', '交易号', 'jyh', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('259', 'zfbzzinfo', '付款方支付宝账号', 'fkf_id', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('260', 'zfbzzinfo', '收款方支付宝账号', 'skf_id', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('261', 'zfbzzinfo', '收款机构信息', 'skjgmc', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('262', 'zfbzzinfo', '到账时间', 'dzsj', '4', '1', '7');
INSERT INTO `eqa_meta` VALUES ('263', 'zfbzzinfo', '转账金额（元）', 'je', '3', '1', '8');
INSERT INTO `eqa_meta` VALUES ('264', 'zfbzzinfo', '转账产品名称', 'zzcpmc', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('265', 'zfbzzinfo', '交易发生地', 'jyfsd', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('266', 'zfbzzinfo', '提现流水号', 'txlsh', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('267', 'zfbzzinfo', '协查编号', 'xcbh', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('268', 'zfbzzinfo', '文件id', 'file_id', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('269', 'zfbzzinfo', '创建时间', 'create_time', '4', '1', '14');
INSERT INTO `eqa_meta` VALUES ('270', 'zfbzzinfo', '标签', 'tags', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('271', 'zfbtxinfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('272', 'zfbtxinfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('273', 'zfbtxinfo', '提现类型', 'txlx', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('274', 'zfbtxinfo', '提现流水号', 'txlsh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('275', 'zfbtxinfo', '开户银行', 'khyh', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('276', 'zfbtxinfo', '银行账号', 'yhzh', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('277', 'zfbtxinfo', '申请时间', 'sqsj', '4', '1', '7');
INSERT INTO `eqa_meta` VALUES ('278', 'zfbtxinfo', '处理时间', 'clsj', '4', '1', '8');
INSERT INTO `eqa_meta` VALUES ('279', 'zfbtxinfo', '提现金额（元）', 'je', '3', '1', '9');
INSERT INTO `eqa_meta` VALUES ('280', 'zfbtxinfo', '状态', 'zt', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('281', 'zfbtxinfo', '交易状态', 'jyzt', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('282', 'zfbtxinfo', '失败原因', 'sbyy', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('283', 'zfbtxinfo', '协查编号', 'xcbh', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('284', 'zfbtxinfo', '文件id', 'file_id', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('285', 'zfbtxinfo', '创建时间', 'create_time', '4', '1', '15');
INSERT INTO `eqa_meta` VALUES ('286', 'zfbtxinfo', '标签', 'tags', '2', '1', '16');
INSERT INTO `eqa_meta` VALUES ('287', 'zfbjyjlinfo', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('288', 'zfbjyjlinfo', '用户Id', 'user_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('289', 'zfbjyjlinfo', '用户名称', 'name', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('290', 'zfbjyjlinfo', '交易号', 'jyh', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('291', 'zfbjyjlinfo', '外部交易号', 'wbjyh', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('292', 'zfbjyjlinfo', '交易状态', 'jyzt', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('293', 'zfbjyjlinfo', '合作伙伴ID', 'hzhb_id', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('294', 'zfbjyjlinfo', '买家用户id', 'mj_id', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('295', 'zfbjyjlinfo', '买家户名', 'mjxx', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('296', 'zfbjyjlinfo', '卖家用户id', 'maijia_id', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('297', 'zfbjyjlinfo', '卖家户名', 'maijiaxx', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('298', 'zfbjyjlinfo', '交易金额（元）', 'je', '3', '1', '12');
INSERT INTO `eqa_meta` VALUES ('299', 'zfbjyjlinfo', '收款时间', 'sksj', '4', '1', '13');
INSERT INTO `eqa_meta` VALUES ('300', 'zfbjyjlinfo', '最后修改时间', 'zhxgsj', '4', '1', '14');
INSERT INTO `eqa_meta` VALUES ('301', 'zfbjyjlinfo', '创建时间', 'cjsj', '4', '1', '15');
INSERT INTO `eqa_meta` VALUES ('302', 'zfbjyjlinfo', '交易类型', 'jylx', '2', '1', '16');
INSERT INTO `eqa_meta` VALUES ('303', 'zfbjyjlinfo', '来源地', 'lyd', '2', '1', '17');
INSERT INTO `eqa_meta` VALUES ('304', 'zfbjyjlinfo', '商品名称', 'spmc', '2', '1', '18');
INSERT INTO `eqa_meta` VALUES ('305', 'zfbjyjlinfo', '收货人地址', 'shrdz', '2', '1', '19');
INSERT INTO `eqa_meta` VALUES ('306', 'zfbjyjlinfo', '协查编号', 'xcbh', '2', '1', '20');
INSERT INTO `eqa_meta` VALUES ('307', 'zfbjyjlinfo', '文件id', 'file_id', '2', '1', '21');
INSERT INTO `eqa_meta` VALUES ('308', 'zfbjyjlinfo', '创建时间', 'create_time', '4', '1', '22');
INSERT INTO `eqa_meta` VALUES ('309', 'zfbjyjlinfo', '标签', 'tags', '2', '1', '23');
INSERT INTO `eqa_meta` VALUES ('310', 'xndw_sx', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('311', 'xndw_sx', '真实纬度', 'real_lat_e6', '3', '1', '2');
INSERT INTO `eqa_meta` VALUES ('312', 'xndw_sx', '真实经度', 'real_lon_e6', '3', '1', '3');
INSERT INTO `eqa_meta` VALUES ('313', 'xndw_sx', '真实地址', 'real_addr', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('314', 'xndw_sx', '虚拟位置信息', 'mock_info', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('315', 'xndw_sx', 'IMEI', 'imei', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('316', 'xndw_sx', 'IMSI', 'imsi', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('317', 'xndw_sx', '设备类型', 'model_type', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('318', 'xndw_sx', 'IP', 'ip', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('319', 'xndw_sx', '手机号码', 'phone_num', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('320', 'xndw_sx', '电子邮箱', 'email', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('321', 'xndw_sx', '运营商', 'contacts', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('322', 'xndw_sx', '短信', 'sms', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('323', 'xndw_sx', '通话记录', 'call_log', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('324', 'xndw_sx', '语言', 'lang', '2', '1', '15');
INSERT INTO `eqa_meta` VALUES ('325', 'xndw_sx', '创建时间', 'created_at', '4', '1', '16');
INSERT INTO `eqa_meta` VALUES ('326', 'xndw_sx', '标签', 'tags', '2', '1', '17');
INSERT INTO `eqa_meta` VALUES ('327', 'xndw_wsk', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('328', 'xndw_wsk', '微信号', 'wechat_id', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('329', 'xndw_wsk', '手机号', 'mobilephone', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('330', 'xndw_wsk', 'IMEI', 'imei', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('331', 'xndw_wsk', 'QQ号码', 'qq', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('332', 'xndw_wsk', '电子邮箱', 'email', '2', '1', '6');
INSERT INTO `eqa_meta` VALUES ('333', 'xndw_wsk', 'FACEBOOK', 'facebook', '2', '1', '7');
INSERT INTO `eqa_meta` VALUES ('334', 'xndw_wsk', 'TOKEN', 'token', '2', '1', '8');
INSERT INTO `eqa_meta` VALUES ('335', 'xndw_wsk', 'IP', 'ip', '2', '1', '9');
INSERT INTO `eqa_meta` VALUES ('336', 'xndw_wsk', '纬度', 'latitude', '2', '1', '10');
INSERT INTO `eqa_meta` VALUES ('337', 'xndw_wsk', '经度', 'longtude', '2', '1', '11');
INSERT INTO `eqa_meta` VALUES ('338', 'xndw_wsk', '虚拟地址', 'vaddr', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('339', 'xndw_wsk', '短信', 'adword', '2', '1', '13');
INSERT INTO `eqa_meta` VALUES ('340', 'xndw_wsk', '真实地址', 'address', '2', '1', '14');
INSERT INTO `eqa_meta` VALUES ('341', 'xndw_wsk', '创建时间', 'time', '4', '1', '15');
INSERT INTO `eqa_meta` VALUES ('342', 'xndw_wsk', '标签', 'tags', '2', '1', '16');
INSERT INTO `eqa_meta` VALUES ('343', 'suspicious', 'IP', 'ip', '2', '1', '12');
INSERT INTO `eqa_meta` VALUES ('344', 'comment', '主键', 'id', '2', '0', '1');
INSERT INTO `eqa_meta` VALUES ('345', 'comment', '标签', 'tags', '2', '1', '2');
INSERT INTO `eqa_meta` VALUES ('346', 'comment', '索引名称', 'index_name', '2', '1', '3');
INSERT INTO `eqa_meta` VALUES ('347', 'comment', '源数据id', 'source', '2', '1', '4');
INSERT INTO `eqa_meta` VALUES ('348', 'comment', '批注内容', 'comment', '2', '1', '5');
INSERT INTO `eqa_meta` VALUES ('349', 'comment', '创建时间', 'create_time', '4', '1', '6');
INSERT INTO `eqa_meta` VALUES ('350', 'huaduan_list', '通话开始时间_时', 'kssj_hh', '2', '1', '12');

-- ----------------------------
-- Table structure for suspicious
-- ----------------------------
DROP TABLE IF EXISTS `suspicious`;
CREATE TABLE `suspicious` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `gmsfzh` varchar(255) DEFAULT NULL COMMENT '身份证号',
  `qq` varchar(255) DEFAULT NULL COMMENT 'QQ',
  `weixin` varchar(255) DEFAULT NULL COMMENT '微信',
  `cft` varchar(255) DEFAULT NULL COMMENT '财付通',
  `zfb` varchar(255) DEFAULT NULL COMMENT '支付宝',
  `yhzh` varchar(255) DEFAULT NULL COMMENT '银行账号',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `imei` varchar(255) DEFAULT NULL COMMENT 'IMEI',
  `gzjd` varchar(255) DEFAULT NULL COMMENT '工作进度',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `imsi` varchar(255) DEFAULT '' COMMENT 'imsi',
  `email` varchar(255) DEFAULT NULL COMMENT '电子邮箱',
  `type` varchar(255) DEFAULT '1' COMMENT '人员类型 1：可疑人员；2：关系人',
  `susp_id` varchar(255) DEFAULT NULL COMMENT '可疑人员id',
  `susp_name` varchar(255) DEFAULT NULL COMMENT '可疑人员姓名',
  `qkjj` varchar(3000) DEFAULT NULL,
  `other` varchar(255) DEFAULT NULL COMMENT '其他特征',
  `kyr_id` varchar(255) DEFAULT NULL COMMENT '可疑人id',
  `ip` text COMMENT 'IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of suspicious
-- ----------------------------
INSERT INTO `suspicious` VALUES ('15bf33c0-8ec4-4161-ba0e-7887503066fd', '张洋洋', '66666666', '982389608,123456,1149078363', '', '', '', '', '18924625123', '', '新增', '2018-03-23 00:00:00', '2018-04-10 23:35:56', '', '', '1', '', '', null, '', null, null);
INSERT INTO `suspicious` VALUES ('2716ffb9-52aa-4286-90e0-56d7d6f83617', '王五', '4415222222222222', '982389608,1149078363', 'wxid_704ih1qydddo12,dfdsf,uuuuuuuuuu', '1440299299', '', '6217993000257323629', '13025461418,7,15165256950,13606276542', '', '新增', '2018-03-23 00:00:00', '2018-04-10 23:35:44', '', '', '1', '', '', null, '', null, null);
INSERT INTO `suspicious` VALUES ('3f505207-bacc-4cbd-a58d-53fe4f2695a0', '刘丽丽', '1231231232', '123456,1149078363', 'wxid_704ih1qydddo12', '1440299299', '', '6217993000257323629', '7,15165256950,18924625123,13606276542', '', '新增', '2018-04-24 00:00:00', '2018-05-17 09:34:43', '', 'qinai-ni@qq.com,1149078363@qq.com', '1', '', '', '范德萨范德萨发似懂非懂所发生的防守打法水电费是否收到佛挡杀佛水电费收到', '', null, '115.60.115.221,223.104.13.206,42.236.212.197,1.192.22.42,42.236.209.210,42.236.218.142,223.79.72.95,223.104.13.213,43.228.190.136,223.104.13.216,223.78.70.233,223.104.13.215,42.236.214.250,42.236.210.70,42.236.217.202,42.236.214.253,42.236.217.206,42.236.209.101,42.236.209.221,223.104.105.57,42.236.210.62,117.136.61.35,117.136.61.33,42.236.214.81,223.104.186.147,42.236.209.52,42.236.216.196,117.136.78.74,219.156.32.106,42.236.208.199,42.236.216.15,42.236.214.103,223.104.105.203,223.104.186.82,27.193.186.208,42.236.218.172,223.104.101.32,42.236.211.14,223.104.105.75,42.236.216.187,223.78.128.16,42.236.214.234,117.136.94.240,117.136.94.242,42.236.214.89,42.236.208.171,42.236.217.252,223.81.84.194,117.136.95.28,117.136.61.7,223.104.105.20,42.236.216.212,223.104.186.254,117.136.44.184,42.236.216.211,42.236.218.19,223.104.105.229,223.104.19.76,42.236.216.218,42.236.216.217,42.236.218.109,223.104.105.106,39.88.159.86,117.136.61.85,42.236.218.33,42.236.208.182,117.136.61.82,125.46.18.110,223.104.255.126,42.236.216.91,223.104.105.19,223.104.105.17,42.236.213.229,117.136.95.14,42.236.213.107,42.236.212.49,42.236.211.196,223.104.105.114,223.104.19.64,61.158.148.15,42.236.214.4,61.158.148.14,42.236.214.8,42.236.214.7,110.253.145.148,223.81.98.236,112.9.38.27,223.104.13.195,223.104.105.46,42.236.213.132,43.228.190.230,223.104.105.120,42.236.208.155,42.236.218.248,223.104.19.57,42.236.210.166,223.104.13.199,117.136.9.12,223.104.105.247,42.236.215.181,42.236.217.23,42.236.219.163,223.104.105.248,42.236.217.42,117.136.77.79,223.104.105.31,42.236.210.86,61.158.148.38,42.236.217.99,61.158.148.36,61.158.148.126,42.236.218.60,223.104.19.4,61.158.148.30,223.81.92.88,42.236.209.179,42.236.208.18,42.236.210.182,42.236.214.17,42.236.219.104,42.236.216.134,42.236.208.254,42.236.219.222,42.236.214.12,42.236.218.77,117.136.0.229,61.158.148.110,117.136.9.180,42.236.217.169,61.158.148.22,117.136.78.182,42.236.216.246,223.104.186.220,103.47.214.102,42.236.208.148,42.236.219.238,42.236.219.115,42.236.217.162,42.236.219.37,42.236.217.77,223.104.19.90,42.236.217.73,42.236.213.177,42.236.219.249,42.236.218.208,42.236.212.136,42.236.212.139,42.236.208.1,42.236.209.164,42.236.213.169,117.136.77.129,27.223.222.111,117.158.203.242,117.136.77.31,117.158.203.240,117.158.203.241,42.236.212.67,121.22.250.24,42.236.212.126,117.136.44.217,121.22.250.25,117.136.44.214,42.236.208.242,42.236.214.63,42.236.214.62,42.236.219.8,61.158.148.78,42.236.213.198,103.47.214.136,42.236.214.57,112.242.140.236,42.236.214.204,117.136.44.225,42.236.214.55,117.136.94.147,42.236.214.74,42.236.212.142,42.236.219.83,42.236.209.146,42.236.211.237,42.236.209.144,42.236.211.234,42.236.213.189,61.158.148.70,223.104.13.246,42.236.215.39,42.236.214.213,117.136.92.113,117.136.44.239,223.104.13.242,117.136.44.238,42.236.213.98,42.236.212.149,42.236.215.101,117.136.9.178,42.236.211.129,42.236.209.232,117.136.44.246,42.236.216.158,42.236.211.243,115.231.154.161,223.104.13.238,223.104.13.232,42.236.217.196,27.221.254.223,42.236.215.239,42.236.212.165,42.236.212.164,117.136.37.8,42.236.212.160,42.236.209.21,42.236.208.207,117.136.44.136,42.236.219.214,42.236.214.49,42.236.210.210,42.236.210.211,223.104.13.222');
INSERT INTO `suspicious` VALUES ('62a50696-12b0-4774-acd9-14c1f5c977c0', '张样品', '66666666', '982389608,1149078363,123213', '', '', '', '', '7', '', '新增', '2018-03-23 00:00:00', '2018-04-10 23:35:26', '', '', '1', '', '', null, '', null, null);
INSERT INTO `suspicious` VALUES ('915855fa-db27-47d7-b61e-7d77cae4d232', '关系人3', '12321322222222', '', '', '', '', '', '', '', null, '2018-04-04 00:00:00', '2018-04-10 23:50:45', '', '', '2', '', '', null, '', '2716ffb9-52aa-4286-90e0-56d7d6f83617', null);
INSERT INTO `suspicious` VALUES ('bf256071-7539-4da4-835a-acf1b715744d', '尼古拉斯。赵四', '544444444444444444', '982389608', '', '1440299299,pygongyi', '', '6217993000257323629,6222021712001966513,6217231712000287909', '13781385117,13622388955,13606276542', '', '新增', '2018-04-04 00:00:00', '2018-04-11 22:43:49', '', '13228778254@163.com', '1', '', '', null, '', null, '36.7.175.150,117.136.101.59,223.104.18.107,120.210.238.19,223.104.18.225,120.210.238.18,223.104.18.226,223.104.18.232,223.104.33.60,112.26.225.159,223.104.18.27,223.104.33.103,117.136.101.187,36.33.11.91,223.104.33.188,223.104.18.67,223.104.33.143,223.104.18.68,117.136.101.50,114.98.129.189,117.136.101.106,114.98.157.233,223.104.33.182,112.26.67.19,117.136.100.157,223.104.34.113,117.67.224.87,220.178.70.78,223.104.18.214,223.104.18.137,223.104.18.220,117.136.101.114,36.33.9.111,223.104.33.114,223.104.33.37,117.136.101.42,223.104.33.38,223.104.18.10,223.104.18.11,60.173.251.176,223.104.18.83,223.104.18.40,223.104.33.39,112.26.235.187,223.104.18.205,223.104.18.249,117.136.101.170,36.33.9.8,223.104.18.125,223.104.18.247,223.104.33.84,223.104.18.252,117.136.101.123,36.32.0.79,223.104.18.132,36.33.11.110,223.104.33.83,223.104.33.169,117.136.101.242,223.104.33.121,223.104.18.85,112.26.235.32,223.104.18.70,36.7.175.241,223.104.18.118,36.33.12.118,218.22.57.123,223.240.179.121,223.104.18.115,223.104.18.237,223.104.18.241,117.136.101.178,223.104.33.4,218.23.119.154,60.173.155.177,223.104.18.122,223.104.33.138,112.26.66.248,117.136.101.251,223.104.33.92,223.104.33.132,223.104.33.131,223.104.33.11,223.104.33.12,114.98.140.122,223.104.33.54,223.104.33.98,223.104.18.77');
INSERT INTO `suspicious` VALUES ('eb94748f-9de5-4d63-9fcd-93290c27312b', '王五', '4535345345', '1149078363', '', '', '', '', '7', '', '新增', '2018-03-23 00:00:00', '2018-04-11 22:43:39', '', 'qinai-ni@qq.com', '1', '', '', null, '', null, '115.60.115.221,223.104.13.206,42.236.212.197,1.192.22.42,42.236.209.210,42.236.218.142,223.79.72.95,223.104.13.213,43.228.190.136,223.104.13.216,223.78.70.233,223.104.13.215,42.236.214.250,42.236.210.70,42.236.217.202,42.236.214.253,42.236.217.206,42.236.209.101,42.236.209.221,223.104.105.57,42.236.210.62,117.136.61.35,117.136.61.33,42.236.214.81,223.104.186.147,42.236.209.52,42.236.216.196,117.136.78.74,219.156.32.106,42.236.208.199,42.236.216.15,42.236.214.103,223.104.105.203,223.104.186.82,27.193.186.208,42.236.218.172,223.104.101.32,42.236.211.14,223.104.105.75,42.236.216.187,223.78.128.16,42.236.214.234,117.136.94.240,117.136.94.242,42.236.214.89,42.236.208.171,42.236.217.252,223.81.84.194,117.136.95.28,117.136.61.7,223.104.105.20,42.236.216.212,223.104.186.254,117.136.44.184,42.236.216.211,42.236.218.19,223.104.105.229,223.104.19.76,42.236.216.218,42.236.216.217,42.236.218.109,223.104.105.106,39.88.159.86,117.136.61.85,42.236.218.33,42.236.208.182,117.136.61.82,125.46.18.110,223.104.255.126,42.236.216.91,223.104.105.19,223.104.105.17,42.236.213.229,117.136.95.14,42.236.213.107,42.236.212.49,42.236.211.196,223.104.105.114,223.104.19.64,61.158.148.15,42.236.214.4,61.158.148.14,42.236.214.8,42.236.214.7,110.253.145.148,223.81.98.236,112.9.38.27,223.104.13.195,223.104.105.46,42.236.213.132,43.228.190.230,223.104.105.120,42.236.208.155,42.236.218.248,223.104.19.57,42.236.210.166,223.104.13.199,117.136.9.12,223.104.105.247,42.236.215.181,42.236.217.23,42.236.219.163,223.104.105.248,42.236.217.42,117.136.77.79,223.104.105.31,42.236.210.86,61.158.148.38,42.236.217.99,61.158.148.36,61.158.148.126,42.236.218.60,223.104.19.4,61.158.148.30,223.81.92.88,42.236.209.179,42.236.208.18,42.236.210.182,42.236.214.17,42.236.219.104,42.236.216.134,42.236.208.254,42.236.219.222,42.236.214.12,42.236.218.77,117.136.0.229,61.158.148.110,117.136.9.180,42.236.217.169,61.158.148.22,117.136.78.182,42.236.216.246,223.104.186.220,103.47.214.102,42.236.208.148,42.236.219.238,42.236.219.115,42.236.217.162,42.236.219.37,42.236.217.77,223.104.19.90,42.236.217.73,42.236.213.177,42.236.219.249,42.236.218.208,42.236.212.136,42.236.212.139,42.236.208.1,42.236.209.164,42.236.213.169,117.136.77.129,27.223.222.111,117.158.203.242,117.136.77.31,117.158.203.240,117.158.203.241,42.236.212.67,121.22.250.24,42.236.212.126,117.136.44.217,121.22.250.25,117.136.44.214,42.236.208.242,42.236.214.63,42.236.214.62,42.236.219.8,61.158.148.78,42.236.213.198,103.47.214.136,42.236.214.57,112.242.140.236,42.236.214.204,117.136.44.225,42.236.214.55,117.136.94.147,42.236.214.74,42.236.212.142,42.236.219.83,42.236.209.146,42.236.211.237,42.236.209.144,42.236.211.234,42.236.213.189,61.158.148.70,223.104.13.246,42.236.215.39,42.236.214.213,117.136.92.113,117.136.44.239,223.104.13.242,117.136.44.238,42.236.213.98,42.236.212.149,42.236.215.101,117.136.9.178,42.236.211.129,42.236.209.232,117.136.44.246,42.236.216.158,42.236.211.243,115.231.154.161,223.104.13.238,223.104.13.232,42.236.217.196,27.221.254.223,42.236.215.239,42.236.212.165,42.236.212.164,117.136.37.8,42.236.212.160,42.236.209.21,42.236.208.207,117.136.44.136,42.236.219.214,42.236.214.49,42.236.210.210,42.236.210.211,223.104.13.222');
