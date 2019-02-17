package com.anluy.admin.web.neo4j;

import com.alibaba.fastjson.JSONObject;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.impl.util.DefaultValueMapper;
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
import java.io.IOException;
import java.util.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/16.
 */
@RestController
@RequestMapping("/api/admin/neo4j")
@Api(value = "/api/admin/neo4j", description = "neo4j操作")
public class Neo4jController {
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jController.class);
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;
    @Resource
    private GraphDatabaseService graphDatabaseService;

    /**
     * 人员信息查询
     *
     * @return
     */
    @ApiOperation(value = "人员信息查询", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息查询失败")})//错误码说明
    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test(HttpServletRequest request, String p, String cypher) {
        try {
            if (StringUtils.isBlank(cypher)) {
                LOGGER.error("人员信息查询失败:类型为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
            }
            Map map = "1".equals(p) ? query2(cypher) : query(cypher);
            ;
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息查询完成").setData(map).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:" + exception.getMessage()));
        }
    }

    /**
     * 人员信息查询
     *
     * @return
     */
    @ApiOperation(value = "人员信息查询", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息查询失败")})//错误码说明
    @RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
    public Object query(HttpServletRequest request,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String type2,
                        @RequestParam(required = false) String keyword2
    ) {
        try {
            if (StringUtils.isBlank(type)) {
                LOGGER.error("人员信息查询失败:类型为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
            }
            if (StringUtils.isBlank(keyword)) {
                LOGGER.error("人员信息查询失败:关键词为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:关键词为空"));
            }
            String cypher = null;
            Map map = null;
            //关键词2不为空时校验类型2
            if (StringUtils.isNotBlank(keyword2)) {
                if (StringUtils.isBlank(type2)) {
                    LOGGER.error("人员信息查询失败:类型为空");
                    return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:类型为空"));
                }
                cypher = createCypher2(type, keyword, type2, keyword2);
                map = query2(cypher);
            } else {
                cypher = createCypher(type, keyword);
                map = query(cypher);
            }
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息查询完成").setData(map).setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息查询失败:" + exception.getMessage()));
        }
    }

    /**
     * 创建查询节点的cypher语句的方法
     * @param type
     * @param keyword
     * @return
     */
    private String createCypher(String type, String keyword) {
        StringBuffer sb = new StringBuffer();
        //MATCH p= (n:PERSON)<-[*]->(m)  WHERE (n.id='张样品' OR n.name='张样品') RETURN p
        //sb.append("MATCH (n:").append(type).append(")-[r *1..2]-(m)");
        sb.append("MATCH (n:").append(type).append(")-[r]-(m)");
        sb.append(" WHERE (n.id='").append(keyword).append("' OR n.name='").append(keyword).append("')");
        sb.append(" RETURN n,r,m");
        return sb.toString();
    }
    /**
     * 创建查询路径的cypher语句的方法
     * @param type
     * @param keyword
     * @return
     */
    private String createCypher2(String type, String keyword, String type2, String keyword2) {
        StringBuffer sb = new StringBuffer();
        //MATCH p= (n:PERSON)<-[*]->(m)  WHERE (n.id='张样品' OR n.name='张样品') RETURN p
        sb.append("MATCH p = (n:").append(type).append(")-[r *]-(");
        if (StringUtils.isNotBlank(keyword2) && StringUtils.isNotBlank(type2)) {
            sb.append("m:").append(type2).append(") ");
        } else {
            sb.append("m) ");
        }
        sb.append(" WHERE (n.id='").append(keyword).append("' OR n.name='").append(keyword).append("')");
        if (StringUtils.isNotBlank(keyword2) && StringUtils.isNotBlank(type2)) {
            sb.append(" AND (m.id='").append(keyword2).append("' OR m.name='").append(keyword2).append("')");
        }
        sb.append(" RETURN p");

        return sb.toString();
    }

    /**
     * 查询节点的方法
     * @param cypher
     * @return
     */
    private Map query(String cypher) {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            LOGGER.info("查询图的CYPHER语句" + cypher);
            org.neo4j.graphdb.Result result = graphDatabaseService.execute(cypher);
            List<Map> nodeList = new ArrayList<>();
            Map<Long, Map> nodeMap = new HashMap<>();
            List<Map> relationshipList = new ArrayList<>();
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    Object o = column.getValue();
                    if (o instanceof Node) {
                        Node n = (Node) o;
                        if (nodeMap.containsKey(n.getId())) {
                            continue;
                        }
                        JSONObject jo = new JSONObject();
                        for (String key : n.getAllProperties().keySet()) {
                            jo.put(key, n.getProperty(key));
                        }
                        Label label = n.getLabels().iterator().next();
                        nodeMap.put(n.getId(), ImmutableMap.of("id", n.getId(), "name", jo.getString("name"), "label", label.name(), "attr", jo));
                    } else if (o instanceof Relationship) {
                        Relationship n = (Relationship) o;
                        JSONObject jo = new JSONObject();
                        jo.put("id", n.getId());
                        jo.put("type", n.getType().name());
                        jo.put("start", n.getStartNodeId());
                        jo.put("end", n.getEndNodeId());
                        jo.put("source", n.getStartNodeId());
                        jo.put("target", n.getEndNodeId());
                        relationshipList.add(jo);
                    } else if (o instanceof List) {
                        List<Relationship> ll = (List) o;
                        ll.forEach(n -> {
                            JSONObject jo = new JSONObject();
                            jo.put("id", n.getId());
                            jo.put("type", n.getType().name());
                            jo.put("start", n.getStartNodeId());
                            jo.put("end", n.getEndNodeId());
                            jo.put("source", n.getStartNodeId());
                            jo.put("target", n.getEndNodeId());
                            relationshipList.add(jo);
                        });
                    }
                }
            }
            nodeMap.forEach((k, v) -> nodeList.add(v));
            Map map = ImmutableMap.of("nodes", nodeList, "links", relationshipList, "cypher", cypher);
            transaction.success();
            return map;
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return null;
        }
    }

    /**
     * 查询路径P的方法
     * @param cypher
     * @return
     */
    private Map query2(String cypher) {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            LOGGER.info("查询图的CYPHER语句" + cypher);
            org.neo4j.graphdb.Result result = graphDatabaseService.execute(cypher);
            List<Map> nodeList = new ArrayList<>();
            Map<Long, Map> nodeMap = new HashMap<>();
            Map<Long, Map> relationshipMap = new HashMap<>();
            List<Map> relationshipList = new ArrayList<>();
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                Path path = (Path) row.get("p");
                path.nodes().forEach(n -> {
                    if (nodeMap.containsKey(n.getId())) {
                        return;
                    }
                    JSONObject jo = new JSONObject();
                    for (String key : n.getAllProperties().keySet()) {
                        jo.put(key, n.getProperty(key));
                    }
                    Label label = n.getLabels().iterator().next();
                    nodeMap.put(n.getId(), ImmutableMap.of("id", n.getId(), "name", jo.getString("name"), "label", label.name(), "attr", jo));
                });
                path.relationships().forEach(n -> {
                    if (relationshipMap.containsKey(n.getId())) {
                        return;
                    }
                    JSONObject jo = new JSONObject();
                    jo.put("id", n.getId());
                    jo.put("type", n.getType().name());
                    jo.put("start", n.getStartNodeId());
                    jo.put("end", n.getEndNodeId());
                    jo.put("source", n.getStartNodeId());
                    jo.put("target", n.getEndNodeId());
                    relationshipMap.put(n.getId(), jo);
                });
            }
            nodeMap.forEach((k, v) -> nodeList.add(v));
            relationshipMap.forEach((k, v) -> relationshipList.add(v));
            Map map = ImmutableMap.of("nodes", nodeList, "links", relationshipList, "cypher", cypher);
            transaction.success();
            return map;
        } catch (Exception exception) {
            LOGGER.error("人员信息查询失败:" + exception.getMessage(), exception);
            return null;
        }
    }

    /**
     * 人员信息入图
     *
     * @return
     */
    @ApiOperation(value = "人员信息入图", response = Result.class)
    @ApiResponses(value = {@ApiResponse(code = 500, message = "人员信息入图失败")})//错误码说明
    @RequestMapping(value = "/susp2neo4j", method = {RequestMethod.GET, RequestMethod.POST})
    public Object susp2neo4j(HttpServletRequest request, @RequestParam(required = false) String id) {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            if (StringUtils.isBlank(id)) {
                LOGGER.error("人员信息入图失败:人员编号为空");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息入图失败:人员编号为空"));
            }
            SuspiciousNode suspiciousNode = this.createSuspiciousNode(id);
            if (suspiciousNode == null) {
                LOGGER.error("人员信息入图失败:人员不存在");
                return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息入图失败:人员不存在"));
            }
            this.createGmsfzh(suspiciousNode);
            this.createRelationship(suspiciousNode.getInfo().get("qq"),Labels.QQ,suspiciousNode.getQqNode());
            this.createRelationship(suspiciousNode.getInfo().get("weixin"),Labels.WEIXIN,suspiciousNode.getWeixinNode());
            this.createRelationship(suspiciousNode.getInfo().get("cft"),Labels.TENPLAY,suspiciousNode.getTenplayNode());
            this.createRelationship(suspiciousNode.getInfo().get("zfb"),Labels.ALIPLAY,suspiciousNode.getAliplayNode());
            this.createRelationship(suspiciousNode.getInfo().get("yhzh"),Labels.YHZH,suspiciousNode.getYhzhNode());
            this.createRelationship(suspiciousNode.getInfo().get("phone"),Labels.PHONE,suspiciousNode.getPhoneNode());
            this.createRelationship(suspiciousNode.getInfo().get("ip"),Labels.IP,suspiciousNode.getIpNode());
            this.createRelationship(suspiciousNode.getInfo().get("email"),Labels.EMAIL,suspiciousNode.getEmailNode());

            transaction.success();
            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("人员信息入图完成").setData("").setPath(request.getRequestURI()));
        } catch (Exception exception) {
            LOGGER.error("人员信息入图失败:" + exception.getMessage(), exception);
            return ResponseEntity.ok().body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "人员信息入图失败:" + exception.getMessage()));
        }
    }

    /**
     * 构建人员信息节点
     *
     * @param id
     * @return
     */
    private SuspiciousNode createSuspiciousNode(String id) {
        Map suspInfo = elasticsearchRestClient.get("suspicious", id, null, null);
        if (suspInfo == null) {
            return null;
        }
        SuspiciousNode suspiciousNode = new SuspiciousNode();
        suspiciousNode.setInfo(suspInfo);
        Node suspNode = graphDatabaseService.findNode(Labels.PERSON, "id", id);
        if (suspNode == null) {
            suspNode = graphDatabaseService.createNode(Labels.PERSON);
            suspNode.setProperty("id", id);
            suspNode.setProperty("name", suspInfo.get("name"));
            suspiciousNode.setNode(suspNode);
            Node idcardNode = graphDatabaseService.createNode(Labels.IDCARD_NODE);
            idcardNode.setProperty("id", Labels.IDCARD_NODE.name() + "-" + id);
            idcardNode.setProperty("name", "证件");
            suspiciousNode.setIdcardNode(idcardNode);
            Node qqNode = graphDatabaseService.createNode(Labels.QQ_NODE);
            suspiciousNode.setQqNode(qqNode);
            qqNode.setProperty("id", Labels.QQ_NODE.name() + "-" + id);
            qqNode.setProperty("name", "QQ");
            Node weixinNode = graphDatabaseService.createNode(Labels.WEIXIN_NODE);
            suspiciousNode.setWeixinNode(weixinNode);
            weixinNode.setProperty("id", Labels.WEIXIN_NODE.name() + "-" + id);
            weixinNode.setProperty("name", "微信");
            Node tenplayNode = graphDatabaseService.createNode(Labels.TENPLAY_NODE);
            suspiciousNode.setTenplayNode(tenplayNode);
            tenplayNode.setProperty("id", Labels.TENPLAY_NODE.name() + "-" + id);
            tenplayNode.setProperty("name", "财付通");
            Node aliplayNode = graphDatabaseService.createNode(Labels.ALIPLAY_NODE);
            suspiciousNode.setAliplayNode(aliplayNode);
            aliplayNode.setProperty("id", Labels.ALIPLAY_NODE.name() + "-" + id);
            aliplayNode.setProperty("name", "支付宝");
            Node yhzhNode = graphDatabaseService.createNode(Labels.YHZH_NODE);
            suspiciousNode.setYhzhNode(yhzhNode);
            yhzhNode.setProperty("id", Labels.YHZH_NODE.name() + "-" + id);
            yhzhNode.setProperty("name", "银行账户");
            Node phoneNode = graphDatabaseService.createNode(Labels.PHONE_NODE);
            suspiciousNode.setPhoneNode(phoneNode);
            phoneNode.setProperty("id", Labels.PHONE_NODE.name() + "-" + id);
            phoneNode.setProperty("name", "手机");
            Node ipNode = graphDatabaseService.createNode(Labels.IP_NODE);
            suspiciousNode.setIpNode(ipNode);
            ipNode.setProperty("id", Labels.IP_NODE.name() + "-" + id);
            ipNode.setProperty("name", "IP");
            Node emailNode = graphDatabaseService.createNode(Labels.EMAIL_NODE);
            suspiciousNode.setEmailNode(emailNode);
            emailNode.setProperty("id", Labels.EMAIL_NODE.name() + "-" + id);
            emailNode.setProperty("name", "EMAIL");
            //创建关系
            idcardNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            qqNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            weixinNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            tenplayNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            aliplayNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            yhzhNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            phoneNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            ipNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
            emailNode.createRelationshipTo(suspNode, RelationshipTypes.HAVE);
        } else {
            suspiciousNode.setNode(suspNode);
            Node idcardNode = graphDatabaseService.findNode(Labels.IDCARD_NODE, "id", Labels.IDCARD_NODE.name() + "-" + id);
            suspiciousNode.setIdcardNode(idcardNode);
            Node qqNode = graphDatabaseService.findNode(Labels.QQ_NODE, "id", Labels.QQ_NODE.name() + "-" + id);
            suspiciousNode.setQqNode(qqNode);
            Node weixinNode = graphDatabaseService.findNode(Labels.WEIXIN_NODE, "id", Labels.WEIXIN_NODE.name() + "-" + id);
            suspiciousNode.setWeixinNode(weixinNode);
            Node tenplayNode = graphDatabaseService.findNode(Labels.TENPLAY_NODE, "id", Labels.TENPLAY_NODE.name() + "-" + id);
            suspiciousNode.setTenplayNode(tenplayNode);
            Node aliplayNode = graphDatabaseService.findNode(Labels.ALIPLAY_NODE, "id", Labels.ALIPLAY_NODE.name() + "-" + id);
            suspiciousNode.setAliplayNode(aliplayNode);
            Node yhzhNode = graphDatabaseService.findNode(Labels.YHZH_NODE, "id", Labels.YHZH_NODE.name() + "-" + id);
            suspiciousNode.setYhzhNode(yhzhNode);
            Node phoneNode = graphDatabaseService.findNode(Labels.PHONE_NODE, "id", Labels.PHONE_NODE.name() + "-" + id);
            suspiciousNode.setPhoneNode(phoneNode);
            Node ipNode = graphDatabaseService.findNode(Labels.IP_NODE, "id", Labels.IP_NODE.name() + "-" + id);
            suspiciousNode.setIpNode(ipNode);
            Node emailNode = graphDatabaseService.findNode(Labels.EMAIL_NODE, "id", Labels.EMAIL_NODE.name() + "-" + id);
            suspiciousNode.setEmailNode(emailNode);
        }
        return suspiciousNode;
    }

    /**
     * 创建身份证关联节点
     *
     * @param suspiciousNode
     */
    private void createGmsfzh(SuspiciousNode suspiciousNode) {
        String gmsfzh = (String) suspiciousNode.getInfo().get("gmsfzh");
        if (StringUtils.isNotBlank(gmsfzh)) {
            String[] gmsfzhs = gmsfzh.split(" |,|，|、|  |；|;");
            for (String sfzh : gmsfzhs) {
                if (StringUtils.isBlank(sfzh)) {
                    continue;
                }
                sfzh = sfzh.trim().toUpperCase();
                Node sfzhNode = graphDatabaseService.findNode(Labels.IDCARD, "id", sfzh);
                if (sfzhNode == null) {
                    sfzhNode = graphDatabaseService.createNode(Labels.IDCARD);
                    sfzhNode.setProperty("id", sfzh);
                    sfzhNode.setProperty("name", sfzh);
                    sfzhNode.createRelationshipTo(suspiciousNode.getIdcardNode(), RelationshipTypes.HAVE);
                }
            }
        }
    }

    /**
     *  创建关联节点
     * @param field
     * @param label
     * @param node
     */
    private void createRelationship( Object field,Label label,Node node) {
        if (field == null) {
            return;
        }
        String[] fields;
        if (field instanceof List) {
            List<String> l = (List) field;
            fields = l.toArray(new String[l.size()]);
        } else {
            if (StringUtils.isBlank((String) field)) {
                return;
            }
            fields = field.toString().split(" |,|，|、|  |；|;");
        }
        if (fields.length > 0) {
            for (String sfzh : fields) {
                if (StringUtils.isBlank(sfzh)) {
                    continue;
                }
                sfzh = sfzh.trim().toUpperCase();
                Node sfzhNode = graphDatabaseService.findNode(label, "id", sfzh);
                if (sfzhNode == null) {
                    sfzhNode = graphDatabaseService.createNode(label);
                    sfzhNode.setProperty("id", sfzh);
                    sfzhNode.setProperty("name", sfzh);
                    sfzhNode.createRelationshipTo(node, RelationshipTypes.HAVE);
                } else {
                    Iterator<Relationship> iterator = sfzhNode.getRelationships().iterator();
                    boolean have = false;
                    while (iterator.hasNext()) {
                        Relationship r = iterator.next();
                        if (r.getEndNodeId() == node.getId()) {
                            have = true;
                            break;
                        }
                    }
                    //不存在边指向节点,创建新的边
                    if (!have) {
                        sfzhNode.createRelationshipTo(node, RelationshipTypes.HAVE);
                    }
                }
            }
        }
    }

//
//    /**
//     * 设置人员类型
//     *
//     * @return
//     */
//    @ApiOperation(value = "设置人员类型", response = Result.class)
//    @ApiResponses(value = {@ApiResponse(code = 500, message = "设置人员类型失败")})//错误码说明
//    @RequestMapping(value = "/setsusptype", method = {RequestMethod.GET, RequestMethod.POST})
//    public Object setSuspType(HttpServletRequest request) {
//        try {
//            elasticsearchRestClient.scroll("{\"size\":1000}",null,new ElasticsearchRestClient.TimeWindowCallBack(){
//                @Override
//                public void process(List<Map> var1) {
//                    List<Map> listmap = new ArrayList<>();
//                    var1.forEach(map->{
//                        String type = (String) map.get("type");
//                        if("2".equals(type)){
//                            map.put("type","关系人");
//                        }else{
//                            map.put("type","可疑人");
//                        }
//                        map.put("_id",map.get("id"));
//                        listmap.add(map);
//                    });
//                    if(!listmap.isEmpty()){
//                        elasticsearchRestClient.batchUpdate(listmap,"suspicious");
//                    }
//                }
//            },"suspicious",null,null);
//            return ResponseEntity.status(HttpStatus.OK).body(Result.seuccess("设置完成").setData("").setPath(request.getRequestURI()));
//        } catch (Exception exception) {
//            LOGGER.error("查询失败:" + exception.getMessage(), exception);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
//        }
//    }
}