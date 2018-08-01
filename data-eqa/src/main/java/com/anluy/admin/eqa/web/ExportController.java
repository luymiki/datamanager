package com.anluy.admin.eqa.web;

import com.alibaba.fastjson.JSONArray;
import com.anluy.admin.eqa.core.ElasticsearchQueryAnalyzeEngine;
import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/4.
 */
@RestController
@RequestMapping("/api/eqa")
@Api(value = "/api/eqa", description = "数据导出接口")
public class ExportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportController.class);
    @Resource
    private ElasticsearchQueryAnalyzeEngine elasticsearchQueryAnalyzeEngine;

    /**
     * 查询接口
     *
     * @return
     */
    @ApiOperation(value = "数据导出接口", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询失败"),
            @ApiResponse(code = 501, message = "查询参数异常")})//错误码说明
    @RequestMapping(value = "/exportEXcel", method = {RequestMethod.GET, RequestMethod.POST})
    public Object exportEXcel(HttpServletResponse response, Integer pageNum, Integer pageSize, String paramsStr) {
        try {
            if (pageSize == null) {
                pageSize = 1000;
            }
            if (pageNum == null) {
                pageNum = 1;
            }
            if (StringUtils.isBlank(paramsStr)) {
                LOGGER.error("查询失败，查询条件为空");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(500, "查询失败，查询条件为空"));
            }

            ElasticsearchQueryAnalyzeEngine.DSLPOJO dslpojo = elasticsearchQueryAnalyzeEngine.parseQueryDsl(paramsStr, pageNum, pageSize);
            this.exprotByDsl(response,dslpojo);
            return null;
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(500, "查询失败").setData(exception.getMessage()));
        }
    }
    /**
     * 查询接口
     *
     * @return
     */
    @ApiOperation(value = "数据导出接口，全文搜索", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "查询失败"),
            @ApiResponse(code = 501, message = "查询参数异常")})//错误码说明
    @RequestMapping(value = "/exportEXcelFullText", method = {RequestMethod.GET, RequestMethod.POST})
    public Object exportEXcel(HttpServletResponse response, Integer pageNum, Integer pageSize,String keyword,String indexName,String sort) {
        try {
            if (pageSize == null) {
                pageSize = 1000;
            }
            if (pageNum == null) {
                pageNum = 1;
            }
            if (StringUtils.isBlank(keyword)) {
                LOGGER.error("查询失败，查询条件为空");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(500, "查询失败，查询条件为空"));
            }
            if (StringUtils.isBlank(indexName)) {
                LOGGER.error("查询失败，查询的数据源为空");
                return ResponseEntity.status(HttpStatus.OK).body(Result.error(500, "查询失败，查询数据源为空"));
            }

            ElasticsearchQueryAnalyzeEngine.DSLPOJO dslpojo = elasticsearchQueryAnalyzeEngine.parseFulltextDsl(keyword, pageNum, pageSize,indexName,sort);
            this.exprotByDsl(response,dslpojo);
            return null;
        } catch (Exception exception) {
            LOGGER.error("查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.OK).body(Result.error(500, "查询失败").setData(exception.getMessage()));
        }
    }

    /**
     * 数据导出公共接口
     * @param response
     * @param dslpojo
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    private Object exprotByDsl(HttpServletResponse response,ElasticsearchQueryAnalyzeEngine.DSLPOJO dslpojo) throws IOException, InvalidFormatException {
        List<Map> result = new ArrayList<>();
        elasticsearchQueryAnalyzeEngine.getElasticsearchRestClient().scroll(dslpojo.getDsl(), null, new ElasticsearchRestClient.TimeWindowCallBack() {
            @Override
            public void process(List<Map> var1) {
                if (var1 != null && !var1.isEmpty()) {
                    var1.forEach(map -> {
                        map.forEach((k, v) -> {
                            if (v instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) v;
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i <jsonArray.size() ; i++) {
                                    if(i>0){
                                        sb.append("\r\n");
                                    }
                                    sb.append(jsonArray.getString(i));
                                }
                                map.put(k, sb.toString());
                            }
                        });
                    });
                    result.addAll(var1);
                }
            }
        }, dslpojo.getIndexName(), null, null);
        EqaIndex eqaIndex = elasticsearchQueryAnalyzeEngine.getEqaMetaMap().getEqaMetaList(dslpojo.getIndexName());
        if (eqaIndex == null) {
            return null;
        }
        List<EqaMeta> metaList = eqaIndex.getEqaMetas();
        //准备导出的数据
        //如果没有元数据,跳过
        if (metaList == null) {
            return null;
        }
        List title = new ArrayList<>();
        metaList.forEach(eqaMeta -> {
            title.add(eqaMeta.getFieldName());
        });
        List<List> dataList = new ArrayList<>();
        dataList.add(title);
        result.forEach(dataMap -> {
            List data = new ArrayList<>();
            metaList.forEach(eqaMeta -> {
                data.add(dataMap.get(eqaMeta.getFieldCode()));
            });
            dataList.add(data);
        });
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
        String fileName = "数据导出-" + eqaIndex.getIndexNameCn() + ".xls";
        fileName = new String(fileName.getBytes(),"ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> beans = new HashMap<>();
        beans.put("dataList", dataList);

        XLSTransformer transformer = new XLSTransformer();
        Workbook wb = transformer.transformXLS(ExportController.class.getResourceAsStream("/template/export.xls"), beans);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        return null;
    }

}