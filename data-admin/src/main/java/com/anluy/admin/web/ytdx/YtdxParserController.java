package com.anluy.admin.web.ytdx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.utils.MD5;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import com.google.common.collect.ImmutableSet;
import io.swagger.annotations.*;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能说明：亚太电信数据文件导入操作
 * <p>
 * Created by hc.zeng on 2019/11/30.
 */
@RestController
@RequestMapping("/api/admin/ytdx")
@Api(value = "/api/admin/ytdx", description = "亚太电信数据文件导入操作")
public class YtdxParserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(YtdxParserController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    /**
     * 亚太电信数据解析保存文件
     *
     * @return
     */
    @ApiOperation(value = "解析亚太电信文件", response = Result.class)
    @RequestMapping(value = "/parser", method = RequestMethod.POST)
    public Object parser(HttpServletRequest request,
                         @RequestParam @ApiParam(value = "文件路径")String path,
                         @RequestParam @ApiParam(defaultValue = "customer,contract,sms,voice",value = "数据文件分类") String type,
                         @RequestParam(required = false) @ApiParam(defaultValue = "UTF-8",value = "文件编码格式") String encoding,
                         @RequestParam(required = false) @ApiParam(defaultValue = "ytdx_customerinfo,ytdx_contractinfo,ytdx_smsinfo,ytdx_voiceinfo",value = "表名，选其中一个，和type的选择对应；分表直接在后面加数字，如：ytdx_smsinfo2，ytdx_smsinfo3") String indexName,
                         @RequestParam(required = false) @ApiParam(defaultValue = "10000",value = "单次批量入库记录条数")String batchSize) {
        JSONObject rt = new JSONObject();
        ThreadPoolExecutor executor = null;
        try {
            if (StringUtils.isBlank(path)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件路径为空,rt=" + rt));
            }
            if (StringUtils.isBlank(type)) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件类型为空,rt=" + rt));
            }
            Integer BS = 10000;
            if (StringUtils.isNotBlank(batchSize)) {
                BS = Integer.valueOf(batchSize);
            }

            File file = new File(path);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(1001, "文件不存在,rt=" + rt));
            }

            List<File> fileList = new ArrayList<>();
            if (file.isDirectory()) {
                fileList.addAll(FileUtils.listFiles(file, null, true));
            } else {
                fileList.add(file);
            }
            YtdxParse parse = YtdxParseFctory.getParse(type);
            executor = new ThreadPoolExecutor(5, 10, 60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
            System.out.println("开始时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

            for (File f : fileList) {
                if (!f.getName().endsWith(".txt") || f.getName().endsWith("rows.txt")) {
                    continue;
                }
                int ri = 0;
                if (StringUtils.isBlank(indexName)) {
                    indexName = parse.getIndex(f.getName());
                }
                if (StringUtils.isBlank(encoding)) {
                    encoding = com.anluy.admin.utils.FileUtils.detectorCharset(f);
                }
                if (StringUtils.isBlank(indexName)) {
                    continue;
                }
                LOGGER.info("解析文件：" + f.getAbsolutePath());
                LOGGER.info("文件编码：" + encoding);
                LineIterator lineIterator = new LineIterator(new BufferedReader(new InputStreamReader(FileUtils.openInputStream(f), Charsets.toCharset(encoding)), 10 * 1024 * 1024));
                List<Map> data = new ArrayList<>();
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    ri++;
                    JSONObject o = parse.parse(type, line, f.getName(), elasticsearchRestClient);
                    if (o == null || o.isEmpty()) {
                        continue;
                    }
                    data.add(o);
                    if (data.size() > BS) {
                        executor.execute(new SaveRun(data, indexName));
                        data = new ArrayList<>();
                    }
                }
                if (!data.isEmpty()) {
                    executor.execute(new SaveRun(data, indexName));
                }
                rt.put(f.getAbsolutePath(), ri);
                lineIterator.close();
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("保存成功,rt=" + rt).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("保存失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "rt=" + rt + "\n" + exception.getMessage()));
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    private class SaveRun implements Runnable {
        List<Map> data;
        String indexName;

        public SaveRun(List<Map> data, String indexName) {
            this.data = data;
            this.indexName = indexName;
        }

        @Override
        public void run() {
            try {
                elasticsearchRestClient.batchSave(data, indexName, false);
            } catch (Exception e) {
                LOGGER.error(JSONArray.toJSONString(data));
                LOGGER.error(e.toString(), e);
            }
        }
    }

    private interface YtdxParse {
        JSONObject _parse(String type, String line, String fileName);

        String getIndex(String fileName);

        Map<String, Integer> _mapping();

        default JSONObject mapping(String[] items) {
            JSONObject jsonObject = new JSONObject();
            _mapping().forEach((it, inx) ->{
                if(inx < items.length){
                    jsonObject.put(it, quotaItem(items[inx]));
                }
            });
            return jsonObject;
        }

        default JSONObject parse(String type, String line, String fileName, ElasticsearchRestClient elasticsearchRestClient) {
            JSONObject d = _parse(type, line, fileName);
            if (d == null || d.isEmpty()) {
                return null;
            }
            String id = MD5.encode(d.toJSONString());
            d.put("create_time", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            d.put("id", id);
            d.put("_id", id);

            return d;
        }
    }

    private static class YtdxParseFctory {
        public static YtdxParse getParse(String type) {
            switch (type) {
                case "customer": {
                    return new CustomerParse();
                }
                case "contract": {
                    return new ContractParse();
                }
                case "sms": {
                    return new SmsParse();
                }
                case "voice": {
                    return new VoiceParse();
                }
            }
            return null;
        }
    }

    private static class CustomerParse implements YtdxParse {

        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\\|");
            return foreignmigrate(items);
        }

        protected JSONObject foreignmigrate(String[] items) {
            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("customer_id", 0);
            MAPPING.put("oldcustomer_id", 1);
            MAPPING.put("customer_flag", 2);
            MAPPING.put("customer_type", 3);
            MAPPING.put("directory_category", 4);
            MAPPING.put("customer_c_name", 5);
            MAPPING.put("customer_e_name", 6);
            MAPPING.put("customer_birthday", 7);
            MAPPING.put("id_type", 8);
            MAPPING.put("id_card_id", 9);
            MAPPING.put("company_id", 10);
            MAPPING.put("company_owner_name", 11);
            MAPPING.put("company_owner_e_name", 12);
            MAPPING.put("company_owner_id", 13);
            MAPPING.put("non_profit_business", 14);
            MAPPING.put("contact_name", 15);
            MAPPING.put("contact_e_name", 16);
            MAPPING.put("contact_phone", 17);
            MAPPING.put("customer_contact_phone", 18);
            MAPPING.put("customer_email", 19);
            MAPPING.put("customer_zip_code", 20);
            MAPPING.put("customer_city", 21);
            MAPPING.put("customer_town", 22);
            MAPPING.put("customer_address", 23);
            MAPPING.put("customer_e_address", 24);
            MAPPING.put("data_create_csr", 25);
            MAPPING.put("customer_create_date", 26);
            MAPPING.put("customer_create_csr", 27);
            MAPPING.put("data_create_date", 28);
            MAPPING.put("data_modify_csr", 29);
            MAPPING.put("data_modify_date", 30);
            MAPPING.put("customer_contact_mobile", 31);
            MAPPING.put("billingflag", 32);
            MAPPING.put("payman_id", 33);
            MAPPING.put("agent_name", 34);
            MAPPING.put("agent_phone", 35);
            MAPPING.put("agent_id", 36);
            MAPPING.put("agent_zip_code", 37);
            MAPPING.put("agent_city", 38);
            MAPPING.put("agent_town", 39);
            MAPPING.put("agent_address", 40);
            MAPPING.put("modifytime", 41);
            MAPPING.put("customer_fraud", 42);
            MAPPING.put("customer_collection", 43);
            MAPPING.put("customer_rootaccount", 44);
            MAPPING.put("customer_payperiod", 45);
            MAPPING.put("customer_longdistance1", 46);
            MAPPING.put("customer_longdistance2", 47);
            MAPPING.put("customer_longdistance3", 48);
            MAPPING.put("customer_international1", 49);
            MAPPING.put("customer_international2", 50);
            MAPPING.put("customer_international3", 51);
            MAPPING.put("customer_bonustotal", 52);
            MAPPING.put("customer_fax", 53);
            MAPPING.put("root_cs_id", 54);
            MAPPING.put("root_cs_code", 55);
            MAPPING.put("bscs_cs_id", 56);
            MAPPING.put("bscs_cs_code", 57);
            MAPPING.put("bscs_date", 58);
            MAPPING.put("pid", 59);
            MAPPING.put("large10", 60);
            MAPPING.put("status", 61);
            MAPPING.put("reason", 62);
            MAPPING.put("vip_flag", 63);
            MAPPING.put("promotion", 64);
            MAPPING.put("pcs_surplus_piece", 65);
            MAPPING.put("pcs_pan_master", 66);
            MAPPING.put("pcs_card_no_type", 67);
            MAPPING.put("em_pid", 68);
            MAPPING.put("em_status", 69);
            MAPPING.put("em_reason", 70);
            MAPPING.put("em_bscs_date", 71);
            MAPPING.put("em_bscs_csid", 72);
            MAPPING.put("em_bscs_cscode", 73);
            MAPPING.put("customer_vip", 74);
            MAPPING.put("sales_id", 75);
            MAPPING.put("system_date", 76);
            MAPPING.put("info", 77);
            MAPPING.put("nuskin_status", 78);
            MAPPING.put("account_function", 79);
            MAPPING.put("potential_customer_id", 80);
            MAPPING.put("customer_vip_update_date", 81);
            MAPPING.put("customer_vip_update_csr", 82);
            MAPPING.put("eaccount_mail_remark", 83);
            MAPPING.put("oss_sub_id", 84);
            MAPPING.put("extension", 85);
            MAPPING.put("receivedm", 86);
            MAPPING.put("remark", 87);
            MAPPING.put("mrtgpassword", 88);
            MAPPING.put("customer_floor", 89);
            MAPPING.put("gender", 90);
            MAPPING.put("company_owner_id_rep_date", 91);
            MAPPING.put("id_card_replacement_date", 92);
            MAPPING.put("fagent_zip_code", 93);
            MAPPING.put("fagent_city", 94);
            MAPPING.put("fagent_town", 95);
            MAPPING.put("fagent_address", 96);
            MAPPING.put("fagent_name", 97);
            MAPPING.put("fagent_id", 98);
            MAPPING.put("fagent_phone", 99);
            MAPPING.put("guarantor_company", 100);
            MAPPING.put("guarantor_company_id", 101);
            MAPPING.put("guarantor_name", 102);
            MAPPING.put("guarantor_id", 103);
            MAPPING.put("guarantor_contact_phone", 104);
            MAPPING.put("guarantor_zip_code", 105);
            MAPPING.put("guarantor_city", 106);
            MAPPING.put("guarantor_town", 107);
            MAPPING.put("guarantor_address", 108);
            MAPPING.put("product_type", 109);
            MAPPING.put("data_create_csr_dept", 110);
            MAPPING.put("data_modify_csr_dept", 111);
            MAPPING.put("customer_vip_update_csr_dept", 112);
            MAPPING.put("contact_phone_ext", 113);
            MAPPING.put("black_flag", 114);
            MAPPING.put("postbill", 115);
            MAPPING.put("company_owner_id_type", 116);
            MAPPING.put("fagent_id_type", 117);
            MAPPING.put("country_code", 118);
            MAPPING.put("third_id_no", 119);
            MAPPING.put("bad_debt", 120);
            MAPPING.put("disability", 121);
            MAPPING.put("bundle_billing", 122);
            MAPPING.put("bundle_billing_mdn", 123);
            MAPPING.put("cust_point_of_sale_id", 124);
            MAPPING.put("abnormal_notice", 125);
            MAPPING.put("agent_id_type", 126);
            MAPPING.put("guarantor_id_type", 127);
            MAPPING.put("invoicetype", 128);
            MAPPING.put("invoicemdn", 129);
            MAPPING.put("invoiceemail", 130);
            MAPPING.put("invoicedonate", 131);
            MAPPING.put("invoicedonationunit", 132);
            MAPPING.put("brm_cs_id", 133);
            MAPPING.put("brm_cs_code", 134);
            MAPPING.put("brm_date", 135);
            MAPPING.put("customer_category", 136);
            MAPPING.put("carriertype", 137);
            MAPPING.put("carrierinfo", 138);
            MAPPING.put("transfer_accounts", 139);
            MAPPING.put("capital", 140);

        }

        @Override
        public String getIndex(String fileName) {
            return "ytdx_customerinfo";
        }

    }

    private static class ContractParse implements YtdxParse {

        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\\|");
            return foreignmigrate(items);
        }

        protected JSONObject foreignmigrate(String[] items) {
            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("contract_serial_num", 0);
            MAPPING.put("form_id", 1);
            MAPPING.put("precontract_id", 2);
            MAPPING.put("isoriginal", 3);
            MAPPING.put("customer_id", 4);
            MAPPING.put("product_type", 5);
            MAPPING.put("contract_status", 6);
            MAPPING.put("receive_date", 7);
            MAPPING.put("termination_date", 8);
            MAPPING.put("promotion_id", 9);
            MAPPING.put("promotion_open_date", 10);
            MAPPING.put("dealer_type", 11);
            MAPPING.put("agent_id", 12);
            MAPPING.put("other_id", 13);
            MAPPING.put("activate_csr", 14);
            MAPPING.put("activate_date", 15);
            MAPPING.put("approve_csr", 16);
            MAPPING.put("approve_date", 17);
            MAPPING.put("contract_remark", 18);
            MAPPING.put("contract_create_csr", 19);
            MAPPING.put("contract_create_date", 20);
            MAPPING.put("data_create_csr", 21);
            MAPPING.put("data_create_date", 22);
            MAPPING.put("data_modify_csr", 23);
            MAPPING.put("data_modify_date", 24);
            MAPPING.put("rate_plan_id", 25);
            MAPPING.put("sales_remark", 26);
            MAPPING.put("modifytime", 27);
            MAPPING.put("bscs_contract_id", 28);
            MAPPING.put("bscs_date", 29);
            MAPPING.put("pid", 30);
            MAPPING.put("lock_flag", 31);
            MAPPING.put("cpending_date", 32);
            MAPPING.put("restart_date", 33);
            MAPPING.put("oldcontract_status", 34);
            MAPPING.put("try_date", 35);
            MAPPING.put("reason", 36);
            MAPPING.put("support_state", 37);
            MAPPING.put("act_date", 38);
            MAPPING.put("category", 39);
            MAPPING.put("pro_comment", 40);
            MAPPING.put("agenttosales_id", 41);
            MAPPING.put("suspend_type", 42);
            MAPPING.put("old_contract_serial_num", 43);
            MAPPING.put("em_flag", 44);
            MAPPING.put("em_pid", 45);
            MAPPING.put("em_status", 46);
            MAPPING.put("em_reason", 47);
            MAPPING.put("em_bscs_date", 48);
            MAPPING.put("em_bscs_coid", 49);
            MAPPING.put("migrate_date", 50);
            MAPPING.put("relation_id", 51);
            MAPPING.put("pre_contract_create_date", 52);
            MAPPING.put("pre_data_create_date", 53);
            MAPPING.put("termination_reason", 54);
            MAPPING.put("promotion_gift_no", 55);
            MAPPING.put("slocation", 56);
            MAPPING.put("prepaid_flag", 57);
            MAPPING.put("gt_flag", 58);
            MAPPING.put("loyal_promotion_id", 59);
            MAPPING.put("loyal_act_date", 60);
            MAPPING.put("loyal_mature_date", 61);
            MAPPING.put("loyal_replace_date", 62);
            MAPPING.put("loyal_other_id", 63);
            MAPPING.put("loyal_dealer_type", 64);
            MAPPING.put("d_scde", 65);
            MAPPING.put("loyal_id_card_id", 66);
            MAPPING.put("email", 67);
            MAPPING.put("contactperson", 68);
            MAPPING.put("point_of_sale_id", 69);
            MAPPING.put("sales_id", 70);
            MAPPING.put("sales_dep_id", 71);
            MAPPING.put("sales_dep", 72);
            MAPPING.put("insertdb_status", 73);
            MAPPING.put("mdn_first", 74);
            MAPPING.put("cash_deposit", 75);
            MAPPING.put("workflowid", 76);
            MAPPING.put("source_sys", 77);
            MAPPING.put("promotion_expire_date", 78);
            MAPPING.put("ldap_status", 79);
            MAPPING.put("act_daemon_count", 80);
            MAPPING.put("trg_err_code", 81);
            MAPPING.put("no_temp_paid", 82);
            MAPPING.put("ext_sales_id", 83);
            MAPPING.put("point_of_sales_phone", 84);
            MAPPING.put("first_call_flag", 85);
            MAPPING.put("ap_form_flag", 86);
            MAPPING.put("grantor", 87);
            MAPPING.put("second_id_type", 88);
            MAPPING.put("second_id_no", 89);
            MAPPING.put("reserve_date", 90);
            MAPPING.put("rate_plan_first", 91);
            MAPPING.put("id_return_flag", 92);
            MAPPING.put("pay_type", 93);
            MAPPING.put("termination_type", 94);
            MAPPING.put("data_create_csr_dept", 95);
            MAPPING.put("data_modify_csr_dept", 96);
            MAPPING.put("fraud_flag", 97);
            MAPPING.put("ap_form_reason", 98);
            MAPPING.put("np_ch_coid", 99);
            MAPPING.put("np_ch_mdn", 100);
            MAPPING.put("servicelimit_flag", 101);
            MAPPING.put("termination_balance", 102);
            MAPPING.put("reopen_date", 103);
            MAPPING.put("password", 104);
            MAPPING.put("inv_no", 105);
            MAPPING.put("inactive_flag", 106);
            MAPPING.put("promotion_gift2_no", 107);
            MAPPING.put("trial3g_flag", 108);
            MAPPING.put("voice_balance", 109);
            MAPPING.put("data_balance", 110);
            MAPPING.put("brm_contract_id", 111);
            MAPPING.put("brm_date", 112);
            MAPPING.put("group_id", 113);
            MAPPING.put("contract_vip", 114);

        }

        @Override
        public String getIndex(String fileName) {
            return "ytdx_contractinfo";
        }

    }

    private static class SmsParse implements YtdxParse {
        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\\|");
            return foreignmigrate(items);
        }

        protected JSONObject foreignmigrate(String[] items) {

            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("sd", 0);
            MAPPING.put("an", 1);
            MAPPING.put("bn", 2);
            MAPPING.put("odt", 3);
            MAPPING.put("st", 4);
            MAPPING.put("dt", 5);
            MAPPING.put("ss", 6);
            MAPPING.put("le", 7);
            MAPPING.put("sm", 8);
            MAPPING.put("dm", 9);
            MAPPING.put("at", 10);
            MAPPING.put("cc", 11);

        }

        @Override
        public String getIndex(String fileName) {
            return "ytdx_smsinfo";
        }
    }

    private static class VoiceParse implements YtdxParse {
        @Override
        public JSONObject _parse(String type, String line, String fileName) {
            String[] items = line.split("\\|");
            return foreignmigrate(items);
        }

        protected JSONObject foreignmigrate(String[] items) {
            return mapping(items);
        }

        @Override
        public Map<String, Integer> _mapping() {
            return MAPPING;
        }

        private static final Map<String, Integer> MAPPING = new HashMap<>();

        static {
            MAPPING.put("time_and_date", 0);
            MAPPING.put("cause_of_release", 1);
            MAPPING.put("calling_number", 2);
            MAPPING.put("called_number", 3);
            MAPPING.put("dialed_number", 4);
            MAPPING.put("answer_time", 5);
            MAPPING.put("release_time", 6);
            MAPPING.put("duration", 7);
            MAPPING.put("incoming_trunk_group_cic_ip", 8);
            MAPPING.put("outcoming_trunk_group_cic_ip", 9);
            MAPPING.put("service_id", 10);
            MAPPING.put("charging_id", 11);
            MAPPING.put("record_type", 12);
            MAPPING.put("role_of_node", 13);
            MAPPING.put("caller_type", 14);
            MAPPING.put("caller_ip", 15);
            MAPPING.put("called_ip", 16);
            MAPPING.put("diversion_reason", 17);
            MAPPING.put("accounting_record_type", 18);
            MAPPING.put("session_priority", 19);
            MAPPING.put("carrier_identification_code", 20);
            MAPPING.put("origin_caller_party_address", 21);
            MAPPING.put("sdp_media_identifier", 22);
            MAPPING.put("caller_centrex_number", 23);
            MAPPING.put("called_centrex_number", 24);
            MAPPING.put("caller_short_number", 25);
            MAPPING.put("called_short_number", 26);
            MAPPING.put("caller_cust_groupid", 27);
            MAPPING.put("called_cust_groupid", 28);
            MAPPING.put("np_number", 29);
            MAPPING.put("rec_seq_number", 30);
            MAPPING.put("calling_party_noa", 31);
            MAPPING.put("called_party_noa", 32);
            MAPPING.put("redirecting_number_noa", 33);
            MAPPING.put("original_called_party_noa", 34);
            MAPPING.put("modified_terminate_numb_noa", 35);
            MAPPING.put("dialed_digits_noa", 36);
            MAPPING.put("redirecting_number", 37);
            MAPPING.put("caller_location_group_id", 38);
            MAPPING.put("called_location_group_id", 39);
            MAPPING.put("incoming_trunk_name", 40);
            MAPPING.put("outgoing_trunk_name", 41);
            MAPPING.put("inc_trunk_name", 42);
            MAPPING.put("out_trunk_name", 43);
            MAPPING.put("filename", 44);
            MAPPING.put("timestamp", 45);

        }

        @Override
        public String getIndex(String fileName) {
            return "ytdx_voiceinfo";
        }
    }


    private static String quotaItem(String item) {
        return item.trim();
    }
}