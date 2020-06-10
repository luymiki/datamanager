package com.anluy.admin.web.kdyd.parser;

import com.anluy.admin.entity.Attachment;
import com.anluy.admin.entity.KdydxxInfo;
import com.anluy.admin.entity.YhzhKhxxInfo;
import com.anluy.admin.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明：银行账号开户信息文件解析
 * <p>
 * Created by hc.zeng on 2018/6/25.
 */
public class KdydxxParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(KdydxxParser.class);
    private final Attachment attachment;

    public KdydxxParser(Attachment attachment) {
        this.attachment = attachment;
    }

    /**
     * 解析文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public List<KdydxxInfo> parser(File file) throws Exception {
        List<List<String>> list = ExcelUtils.read(file);
        return parse(list);
    }

    /**
     * 解析文件
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<KdydxxInfo> parser(String path) throws Exception {
        return parser(new File(path));
    }

    private List<KdydxxInfo> parse(List<List<String>> txtContent) throws Exception {
        if (txtContent.size() < 2) {
            throw new RuntimeException("文件格式不正确，不能解析");
        }
        List<String> row1 = txtContent.get(0);
        int type = 1;
        for (String col : row1) {
            //新的数据类型，20191202新增
            if ("快递公司代码".equals(col)) {
                type = 2;
                break;
            }
        }
        List<KdydxxInfo> dataList;
        if (type == 2) {
            dataList = _parse2(txtContent);
        } else {
            dataList = _parse1(txtContent);
        }
        return dataList;
    }

    /**
     * 序号	单号	品名	重量	收件人	电话	地址	代收款	发货单号	重量	日期	货品分类	fsdfd	sfd	退款1
     *
     * @param txtContent
     * @return
     */
    private List<KdydxxInfo> _parse1(List<List<String>> txtContent) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<KdydxxInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 3) {
                continue;
            }

            KdydxxInfo khxx = new KdydxxInfo();
            khxx.setFileId(attachment.getId());
            khxx.setTags(attachment.getTags());
            khxx.setYdh(list.size() > 1 ? list.get(1) : null);
            khxx.setPm(list.size() > 2 ? list.get(2) : null);
            if (list.size() > 3) {
                String data = list.get(3);
                if (StringUtils.isNotBlank(data)) {
                    khxx.setZl(Double.valueOf(data));
                }
            }
            khxx.setSjr(list.size() > 4 ? list.get(4) : null);
            khxx.setDh(list.size() > 5 ? list.get(5) : null);
            khxx.setDz(list.size() > 6 ? list.get(6) : null);
            if (list.size() > 7) {
                String data = list.get(7);
                if (StringUtils.isNotBlank(data)) {
                    khxx.setDsk(Double.valueOf(data));
                }
            }
            khxx.setFhdh(list.size() > 8 ? list.get(8).replace(".00", "") : null);
            if (list.size() > 9) {
                String data = list.get(9);
                if (StringUtils.isNotBlank(data)) {
                    khxx.setFhzl(Double.valueOf(data));
                }
            }
            if (list.size() > 10) {
                String date = list.get(10);
                if (StringUtils.isNotBlank(date)) {
                    try {
                        khxx.setFhrq(sdf1.parse(list.get(10)));
                    } catch (ParseException e) {
                        LOGGER.error("时间转换错误！" + e.toString(), e);
                    }
                }
            }

            khxx.setHpfl(list.size() > 11 ? list.get(11) : null);
            khxx.setNoname(list.size() > 12 ? list.get(12) : null);
            khxx.setQszt(list.size() > 13 ? list.get(13) : null);
            khxx.setTk(list.size() > 14 ? list.get(14) : null);
            dataList.add(khxx);
        }
        return dataList;
    }

    /**
     * 序号	快递公司代码	寄件时间	运单号	寄件国家	寄件网点省	监控号码	寄件网点市	寄件人地址	寄件人姓名	寄件人电话	寄件人手机	寄件公司	收件国家	收件网点省	收件网点市	收件人地址	收件人姓名	收件公司	收件人电话	收件人手机	签收人姓名	物品数量	物品重量	物品尺寸	物品名称	附言或备注	真迹情况	积累情况	数据类型	收件网点	寄件网点	寄件网点代码	收件网点代码	RESERVE3	RESERVE4	RESERVE5	RESERVE6	RESERVE7	RESERVE8
     *
     * @param txtContent
     * @return
     */
    private List<KdydxxInfo> _parse2(List<List<String>> txtContent) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        List<KdydxxInfo> dataList = new ArrayList<>();
        for (int i = 1; i < txtContent.size(); i++) {
            List<String> list = txtContent.get(i);
            if (list.size() < 14) {
                continue;
            }

            KdydxxInfo khxx = new KdydxxInfo();
            khxx.setFileId(attachment.getId());
            khxx.setTags(attachment.getTags());
            khxx.setKdgs(list.size() > 1 ? list.get(1) : null);
            khxx.setYdh(list.size() > 3 ? list.get(3) : null);
            khxx.setPm(list.size() > 25 ? list.get(25) : null);
            if (list.size() > 22) {
                String data = list.get(22);
                if (StringUtils.isNotBlank(data)) {
                    khxx.setSl(Double.valueOf(data));
                }
            }
            if (list.size() > 23) {
                String data = list.get(23);
                if (StringUtils.isNotBlank(data)) {
                    khxx.setZl(Double.valueOf(data));
                }
            }

            khxx.setJjr(list.size() > 9 ? list.get(9) : null);
            if(StringUtils.isBlank(khxx.getJjr())){
                khxx.setJjr(list.size() > 12 ? list.get(12) : null);
            }
            khxx.setJjdh(list.size() > 10 ? list.get(10) : null);
            if(StringUtils.isBlank(khxx.getJjdh())){
                khxx.setJjdh(list.size() > 11 ? list.get(11) : null);
            }
            String jjdz = (list.size() > 4 ? list.get(4) : "")+(list.size() > 5 ? list.get(5) : "")+(list.size() > 7 ? list.get(7) : "")+(list.size() > 8 ? list.get(8) : "");
            khxx.setJjdz(jjdz);


            khxx.setSjr(list.size() > 17 ? list.get(17) : null);
            if(StringUtils.isBlank(khxx.getSjr())){
                khxx.setSjr(list.size() > 18 ? list.get(18) : null);
            }
            khxx.setDh(list.size() > 19 ? list.get(19) : null);
            if(StringUtils.isBlank(khxx.getDh())){
                khxx.setDh(list.size() > 20 ? list.get(20) : null);
            }
            String sjdz = (list.size() > 13 ? list.get(13) : "")+(list.size() > 14 ? list.get(14) : "")+(list.size() > 15 ? list.get(15) : "")+(list.size() > 16 ? list.get(16) : "");
            khxx.setDz(sjdz);

            if (list.size() > 2) {
                String date = list.get(2);
                if (StringUtils.isNotBlank(date)) {
                    try {
                        khxx.setFhrq(sdf1.parse(list.get(2)));
                    } catch (ParseException e) {
                        LOGGER.error("时间转换错误！" + e.toString(), e);
                    }
                }
            }
            dataList.add(khxx);
        }
        return dataList;
    }

    public static void main(String[] args) {
        KdydxxParser zfbRegParser = new KdydxxParser(new Attachment());
        try {
            List<KdydxxInfo> info = zfbRegParser.parser("H:\\数据管理系统\\数据导入20180629\\213.xlsx");
            System.out.println(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
