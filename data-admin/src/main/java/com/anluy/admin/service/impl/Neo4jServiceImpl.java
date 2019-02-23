package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.anluy.admin.EqaConfig;
import com.anluy.admin.service.Neo4jService;
import com.anluy.admin.utils.Neo4jException;
import com.anluy.admin.web.AuthorizationController;
import com.anluy.admin.web.neo4j.Labels;
import com.anluy.admin.web.neo4j.RelationshipTypes;
import com.anluy.admin.web.neo4j.SuspiciousNode;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.web.Result;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/18.
 */
@Service
public class Neo4jServiceImpl implements Neo4jService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jServiceImpl.class);
    private static final String ID = "id";
    private static final String NAME = "name";
    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    @Resource
    private GraphDatabaseService graphDatabaseService;

    @Resource
    private EqaConfig eqaConfig;

    private String queryDsl2Param;

    private String queryDsl;

    @Override
    public void createNeo4j(String suspId, String token) throws Exception {
        try (Transaction transaction = graphDatabaseService.beginTx()) {
            if (StringUtils.isBlank(suspId)) {
                LOGGER.error("人员信息入图失败:人员编号为空");
                throw new Neo4jException("人员信息入图失败:人员编号为空");
            }
            SuspiciousNode suspiciousNode = this.createSuspiciousNode(suspId);
            if (suspiciousNode == null) {
                LOGGER.error("人员信息入图失败:人员不存在");
                throw new Neo4jException("人员信息入图失败:人员不存在");
            }

            this.createGmsfzh(suspiciousNode);
            this.createRelationship(suspiciousNode.getInfo().get("qq"), Labels.QQ, suspiciousNode.getQqNode());
            this.createRelationship(suspiciousNode.getInfo().get("weixin"), Labels.WEIXIN, suspiciousNode.getWeixinNode());
            this.createRelationship(suspiciousNode.getInfo().get("cft"), Labels.TENPLAY, suspiciousNode.getTenplayNode());
            this.createRelationship(suspiciousNode.getInfo().get("zfb"), Labels.ALIPLAY, suspiciousNode.getAliplayNode());
            this.createRelationship(suspiciousNode.getInfo().get("yhzh"), Labels.YHZH, suspiciousNode.getYhzhNode());
            this.createRelationship(suspiciousNode.getInfo().get("phone"), Labels.PHONE, suspiciousNode.getPhoneNode());
            this.createRelationship(suspiciousNode.getInfo().get("ip"), Labels.IP, suspiciousNode.getIpNode());
            this.createRelationship(suspiciousNode.getInfo().get("email"), Labels.EMAIL, suspiciousNode.getEmailNode());

            //dsl查询语句
            if (StringUtils.isBlank(queryDsl)) {
                queryDsl = IOUtils.toString(SuspiciousServiceImpl.class.getResourceAsStream("/dsl/eqa-query-field.json"));
            }
            if (StringUtils.isBlank(queryDsl2Param)) {
                queryDsl2Param = IOUtils.toString(SuspiciousServiceImpl.class.getResourceAsStream("/dsl/eqa-query-field-2param.json"));
            }

            //查询QQ信息，提取好友和群信息
            this.createQQ(suspiciousNode.getInfo().get("qq"), suspId, token);

            //查询微信信息，提取好友和群信息
            this.createWeChat(suspiciousNode.getInfo().get("weixin"), suspId, token);
            //查询财付通
            this.createTenPlay(suspiciousNode.getInfo().get("cft"), suspId, token);
            //查询支付宝
            this.createAilPlay(suspiciousNode.getInfo().get("zfb"), token);
            //查询银行账号
            this.createTrades(suspiciousNode.getInfo().get("yhzh"), token);


            transaction.success();
        } catch (Exception exception) {
            LOGGER.error("人员信息入图失败:" + exception.getMessage(), exception);
            throw new Neo4jException("人员信息入图失败:" + exception.getMessage(), exception);
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
        Node suspNode = graphDatabaseService.findNode(Labels.PERSON, ID, id);
        if (suspNode == null) {
            suspNode = graphDatabaseService.createNode(Labels.PERSON);
            suspNode.setProperty(ID, id);
            suspNode.setProperty(NAME, suspInfo.get(NAME));
            suspiciousNode.setNode(suspNode);

            Node idcardNode = createNode(Labels.IDCARD_NODE,id,"证件");
            suspiciousNode.setIdcardNode(idcardNode);
            Node qqNode = createNode(Labels.QQ_NODE,id,"QQ");
            suspiciousNode.setQqNode(qqNode);
            Node weixinNode = createNode(Labels.WEIXIN_NODE,id,"微信");
            suspiciousNode.setWeixinNode(weixinNode);
            Node tenplayNode = createNode(Labels.TENPLAY_NODE,id,"财付通");
            suspiciousNode.setTenplayNode(tenplayNode);
            Node aliplayNode =createNode(Labels.ALIPLAY_NODE,id,"支付宝");
            suspiciousNode.setAliplayNode(aliplayNode);
            Node yhzhNode = createNode(Labels.YHZH_NODE,id,"银行账户");
            suspiciousNode.setYhzhNode(yhzhNode);
            Node phoneNode = createNode(Labels.PHONE_NODE,id,"手机");
            suspiciousNode.setPhoneNode(phoneNode);
            Node ipNode = createNode(Labels.IP_NODE,id,"IP");
            suspiciousNode.setIpNode(ipNode);
            Node emailNode = createNode(Labels.EMAIL_NODE,id,"EMAIL");
            suspiciousNode.setEmailNode(emailNode);
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
            Node idcardNode = graphDatabaseService.findNode(Labels.IDCARD_NODE, ID, Labels.IDCARD_NODE.name() + "-" + id);
            suspiciousNode.setIdcardNode(idcardNode);
            Node qqNode = graphDatabaseService.findNode(Labels.QQ_NODE, ID, Labels.QQ_NODE.name() + "-" + id);
            suspiciousNode.setQqNode(qqNode);
            Node weixinNode = graphDatabaseService.findNode(Labels.WEIXIN_NODE, ID, Labels.WEIXIN_NODE.name() + "-" + id);
            suspiciousNode.setWeixinNode(weixinNode);
            Node tenplayNode = graphDatabaseService.findNode(Labels.TENPLAY_NODE, ID, Labels.TENPLAY_NODE.name() + "-" + id);
            suspiciousNode.setTenplayNode(tenplayNode);
            Node aliplayNode = graphDatabaseService.findNode(Labels.ALIPLAY_NODE, ID, Labels.ALIPLAY_NODE.name() + "-" + id);
            suspiciousNode.setAliplayNode(aliplayNode);
            Node yhzhNode = graphDatabaseService.findNode(Labels.YHZH_NODE, ID, Labels.YHZH_NODE.name() + "-" + id);
            suspiciousNode.setYhzhNode(yhzhNode);
            Node phoneNode = graphDatabaseService.findNode(Labels.PHONE_NODE, ID, Labels.PHONE_NODE.name() + "-" + id);
            suspiciousNode.setPhoneNode(phoneNode);
            Node ipNode = graphDatabaseService.findNode(Labels.IP_NODE, ID, Labels.IP_NODE.name() + "-" + id);
            suspiciousNode.setIpNode(ipNode);
            Node emailNode = graphDatabaseService.findNode(Labels.EMAIL_NODE, ID, Labels.EMAIL_NODE.name() + "-" + id);
            suspiciousNode.setEmailNode(emailNode);
        }
        return suspiciousNode;
    }

    private Node createNode(Label label,String id,String name){
        String key = label.name() + "-" + id;
        Node node = graphDatabaseService.findNode(label, ID, key);
        if(node==null){
            node = graphDatabaseService.createNode(label);
            node.setProperty(ID,key);
            node.setProperty(NAME, name);
        }
        return node;
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
                Node sfzhNode = graphDatabaseService.findNode(Labels.IDCARD, ID, sfzh);
                if (sfzhNode == null) {
                    sfzhNode = graphDatabaseService.createNode(Labels.IDCARD);
                    sfzhNode.setProperty(ID, sfzh);
                    sfzhNode.setProperty(NAME, sfzh);
                    sfzhNode.createRelationshipTo(suspiciousNode.getIdcardNode(), RelationshipTypes.HAVE);
                }
            }
        }
    }

    /**
     * 创建关联节点
     *
     * @param field
     * @param label
     * @param node
     */
    private void createRelationship(Object field, Label label, Node node) {
        String[] fields = this.toArray(field);
        if (fields != null && fields.length > 0) {
            for (String sfzh : fields) {
                if (StringUtils.isBlank(sfzh)) {
                    continue;
                }
                sfzh = sfzh.trim();
                Node sfzhNode = graphDatabaseService.findNode(label, ID, sfzh);
                if (sfzhNode == null) {
                    sfzhNode = graphDatabaseService.createNode(label);
                    sfzhNode.setProperty(ID, sfzh);
                    sfzhNode.setProperty(NAME, sfzh);
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

    /**
     * 转换为数组
     *
     * @param field
     * @return
     */
    private String[] toArray(Object field) {
        if (field == null) {
            return null;
        }
        String[] fields;
        if (field instanceof List) {
            List<String> l = (List) field;
            Set<String> set = new HashSet<>();
            l.forEach(s -> {
                if (StringUtils.isNotBlank(s)) {
                    set.add(s);
                }
            });
            fields = set.toArray(new String[l.size()]);
        } else {
            if (StringUtils.isBlank((String) field)) {
                return null;
            }
            fields = field.toString().split(" |,|，|、|  |；|;");
        }
        return fields;
    }

    private String arryToString(String[] field) {
        if (field == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < field.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(field[i]).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 创建查询节点的cypher语句的方法
     *
     * @param type
     * @param keyword
     * @return
     */
    public String createCypher(String type, String keyword) {
        keyword = keyword.trim();
        StringBuffer sb = new StringBuffer();
        //MATCH p= (n:PERSON)<-[*]->(m)  WHERE (n.id='张样品' OR n.name='张样品') RETURN p
        //sb.append("MATCH (n:").append(type).append(")-[r *1..2]-(m)");
        sb.append("MATCH (n:").append(type).append(")-[r *1..2]-(m)");
        sb.append(" WHERE (n.id='").append(keyword).append("' OR n.name='").append(keyword).append("')");
        sb.append(" RETURN n,r,m");
        return sb.toString();
    }

    /**
     * 创建查询路径的cypher语句的方法
     *
     * @param type
     * @param keyword
     * @return
     */
    public String createCypherPath(String type, String keyword, String type2, String keyword2) {
        keyword = keyword.trim();
        StringBuffer sb = new StringBuffer();
        //MATCH p= (n:PERSON)<-[*]->(m)  WHERE (n.id='张样品' OR n.name='张样品') RETURN p
        sb.append("MATCH p = shortestPath((n:").append(type).append(")-[r *]-(");
        if (StringUtils.isNotBlank(keyword2) && StringUtils.isNotBlank(type2)) {
            sb.append("m:").append(type2).append(") ");
        } else {
            sb.append("m) ");
        }
        sb.append(") WHERE (n.id='").append(keyword).append("' OR n.name='").append(keyword).append("')");
        if (StringUtils.isNotBlank(keyword2) && StringUtils.isNotBlank(type2)) {
            keyword2 = keyword2.trim();
            sb.append(" AND (m.id='").append(keyword2).append("' OR m.name='").append(keyword2).append("')");
        }
        sb.append(" RETURN p");

        return sb.toString();
    }

    /**
     * 查询节点的方法
     *
     * @param cypher
     * @return
     */
    public Map queryCypher(String cypher) {
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
                        nodeMap.put(n.getId(), ImmutableMap.of(ID, n.getId(), NAME, jo.getString(NAME), "label", label.name(), "attr", jo));
                    } else if (o instanceof Relationship) {
                        Relationship n = (Relationship) o;
                        JSONObject jo = new JSONObject();
                        jo.put(ID, n.getId());
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
                            jo.put(ID, n.getId());
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
     *
     * @param cypher
     * @return
     */
    public Map queryCypherPath(String cypher) {
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
                    nodeMap.put(n.getId(), ImmutableMap.of(ID, n.getId(), NAME, jo.getString(NAME), "label", label.name(), "attr", jo));
                });
                path.relationships().forEach(n -> {
                    if (relationshipMap.containsKey(n.getId())) {
                        return;
                    }
                    JSONObject jo = new JSONObject();
                    jo.put(ID, n.getId());
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
     * 查询es
     *
     * @return
     */
    private JSONObject get(String dsl, String token) {
        return getByDsl(eqaConfig.getQueryUrl(), dsl, token);
    }

    /**
     * 获取数据
     *
     * @param resultJson
     * @param arry
     * @param function
     * @return
     */
    private Map<String, Set> functionData(JSONObject resultJson, String[] arry, Function<Map<String, Set>, JSONObject> function) {
        if (resultJson != null && resultJson.getJSONObject("data") != null && resultJson.getJSONObject("data").getJSONArray("data") != null) {
            JSONArray dataList = resultJson.getJSONObject("data").getJSONArray("data");
            Map<String, Set> dataMap = new HashMap<>();
            for (String qq : arry) {
                if (StringUtils.isNotBlank(qq)) {
                    dataMap.put(qq, new HashSet<>());
                }
            }
            dataList.forEach(o -> {
                JSONObject jo = (JSONObject) o;
                function.apply(dataMap, jo);
            });
            return dataMap;
        }
        return null;
    }

    public interface Function<V, W> {
        void apply(V v, W w);
    }

    /**
     * 给关外围节点添加关系
     *
     * @param dataMap
     * @param groupLabel
     * @param groupName
     */
    private void createNodeRelationship(Map<String, Set> dataMap, Label mainLabel, Label groupLabel, String groupName, Label nodeLabel) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }
        for (String id : dataMap.keySet()) {
            Node mainNode = graphDatabaseService.findNode(mainLabel, ID, id);
            if (mainNode == null) {
                continue;
            }
            Node gropuNode = graphDatabaseService.findNode(groupLabel, ID, groupLabel.name() + "-" + id);
            //如果外围节点组没有找到，新建
            if (gropuNode == null) {
                gropuNode = graphDatabaseService.createNode(groupLabel);
                gropuNode.setProperty(ID, groupLabel.name() + "-" + id);
                gropuNode.setProperty(NAME, groupName);
                gropuNode.createRelationshipTo(mainNode, RelationshipTypes.HAVE);
            }
            Set<String> data = dataMap.get(id);
            for (String da : data) {
                if(StringUtils.isBlank(da)){
                    continue;
                }
                if("-".equals(da)){
                    continue;
                }
                da = da.trim();
                Node subNode = graphDatabaseService.findNode(nodeLabel, ID,  da);
                //不存在节点
                if (subNode == null) {
                    subNode = graphDatabaseService.createNode(nodeLabel);
                    subNode.setProperty(ID, da);
                    subNode.setProperty(NAME, da);
                    //创建边
                    gropuNode.createRelationshipTo(subNode, RelationshipTypes.HAVE);
                } else {
                    //如果存在，查询边是否存在
                    Iterator<Relationship> iterator = subNode.getRelationships().iterator();
                    boolean have = false;
                    while (iterator.hasNext()) {
                        Relationship r = iterator.next();
                        if (r.getEndNodeId() == gropuNode.getId()) {
                            have = true;
                            break;
                        }
                    }
                    if (!have) {
                        //创建边
                        gropuNode.createRelationshipTo(subNode, RelationshipTypes.HAVE);
                    }
                }
            }
        }
    }

    /**
     * 创建QQ相关的节点
     *
     * @param qq
     * @param suspId
     * @param token
     */
    private void createQQ(Object qq, String suspId, String token) {
        //查询QQ信息，提取好友和群信息
        String[] qqArry = toArray(qq);
        if (qqArry != null && qqArry.length > 0) {
            String json = String.format(queryDsl2Param, "qqreginfo", "qq", this.arryToString(qqArry), "susp_id", suspId);
            JSONObject resultJson = get(json, token);
            Map<String, Set> groupMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("qq");
                    String[] jrqh = toArray(jsonObject.get("jrqh"));
                    String[] cjqh = toArray(jsonObject.get("cjqh"));
                    if (jrqh != null) {
                        for (String q : jrqh) {
                            stringSetMap.get(qq).add(q);
                        }
                    }
                    if (cjqh != null) {
                        for (String q : cjqh) {
                            stringSetMap.get(qq).add(q);
                        }
                    }
                }
            });
            Map<String, Set> firendMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("qq");
                    String[] qqhy = toArray(jsonObject.get("qqhy"));
                    if (qqhy != null) {
                        for (String q : qqhy) {
                            stringSetMap.get(qq).add(q);
                        }
                    }
                }
            });
            this.createNodeRelationship(groupMap, Labels.QQ, Labels.GROUP_NODE, "QQ群", Labels.GROUP);
            this.createNodeRelationship(firendMap, Labels.QQ, Labels.FRIEND_NODE, "QQ好友", Labels.QQ);
        }
    }

    /**
     * 创建微信相关的节点
     *
     * @param weixin
     * @param suspId
     * @param token
     */
    private void createWeChat(Object weixin, String suspId, String token) {
        //查询微信信息，提取好友和群信息
        String[] qqArry = toArray(weixin);
        if (qqArry != null && qqArry.length > 0) {
            String json = String.format(queryDsl2Param, "wxlxr", "weixin", this.arryToString(qqArry), "susp_id", suspId);
            JSONObject resultJson = get(json, token);
            Map<String, Set> firendMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("weixin");
                    String zh = jsonObject.getString("zh");
                    stringSetMap.get(qq).add(zh);
                }
            });
            this.createNodeRelationship(firendMap, Labels.WEIXIN, Labels.FRIEND_NODE, "微信好友", Labels.WEIXIN);

            //微信群
            String wxqunjson = String.format(queryDsl2Param, "wxqun", "weixin", this.arryToString(qqArry), "susp_id", suspId);
            JSONObject wxqunResultJson = get(wxqunjson, token);
            Map<String, Set> groupMap = this.functionData(wxqunResultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("weixin");
                    String zh = jsonObject.getString("zh");
                    stringSetMap.get(qq).add(zh);
                }
            });
            this.createNodeRelationship(groupMap, Labels.WEIXIN, Labels.GROUP_NODE, "微信群", Labels.GROUP);
        }
    }

    /**
     * 创建财付通相关的节点
     *
     * @param cft
     * @param suspId
     * @param token
     */
    private void createTenPlay(Object cft, String suspId, String token) {
        //查询财付通信息，提取交易对手信息
        String[] qqArry = toArray(cft);
        if (qqArry != null && qqArry.length > 0) {
            String json = String.format(queryDsl2Param, "cfttrades", "zh", this.arryToString(qqArry), "susp_id", suspId);
            JSONObject resultJson = get(json, token);
            Map<String, Set> firendMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("zh");
                    String zh = jsonObject.getString("ds_id");
                    stringSetMap.get(qq).add(zh);
                }
            });
            this.createNodeRelationship(firendMap, Labels.TENPLAY, Labels.JIAOYI_NODE, "交易", Labels.TENPLAY);
        }
    }
    /**
     * 创建支付宝相关的节点
     *
     * @param zfb
     * @param token
     */
    private void createAilPlay(Object zfb,String token) {
        //查询支付宝信息，提取交易对手信息
        String[] qqArry = toArray(zfb);
        if (qqArry != null && qqArry.length > 0) {
            String json = String.format(queryDsl, "zfbzhinfo,zfbzzinfo,zfbtxinfo,zfbjyjlinfo", "user_id", this.arryToString(qqArry));
            JSONObject resultJson = get(json, token);
            Map<String, Set> firendMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("user_id");
                    String zh = jsonObject.getString("ds_id");
                    stringSetMap.get(qq).add(zh);
                }
            });
            this.createNodeRelationship(firendMap, Labels.ALIPLAY, Labels.JIAOYI_NODE, "交易", Labels.ALIPLAY);
        }
    }

    /**
     * 创建交易流水相关的节点
     *
     * @param yhzh
     * @param token
     */
    private void createTrades(Object yhzh,String token) {
        //查询银行交易流水信息，提取交易对手信息
        String[] qqArry = toArray(yhzh);
        if (qqArry != null && qqArry.length > 0) {
            String json = String.format(queryDsl, "yhzh_jyls", "kh", this.arryToString(qqArry));
            JSONObject resultJson = get(json, token);
            Map<String, Set> firendMap = this.functionData(resultJson, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("kh");
                    String zh = jsonObject.getString("ds_id");
                    stringSetMap.get(qq).add(zh);
                }
            });
            String jsonZh = String.format(queryDsl, "yhzh_jyls", "zh", this.arryToString(qqArry));
            JSONObject resultJsonZh = get(jsonZh, token);
            Map<String, Set> firendMap2 = this.functionData(resultJsonZh, qqArry, new Function<Map<String, Set>, JSONObject>() {
                @Override
                public void apply(Map<String, Set> stringSetMap, JSONObject jsonObject) {
                    String qq = (String) jsonObject.get("zh");
                    String zh = jsonObject.getString("ds_id");
                    stringSetMap.get(qq).add(zh);
                }
            });
            //合并两份数据
            firendMap.forEach((kh,set)->{
                Set<String> set2 = firendMap2.get(kh);
                if(set2!=null && !set2.isEmpty()){
                    set.addAll(set2);
                }
            });

            this.createNodeRelationship(firendMap, Labels.YHZH, Labels.JIAOYI_NODE, "交易", Labels.YHZH);
        }
    }
}
