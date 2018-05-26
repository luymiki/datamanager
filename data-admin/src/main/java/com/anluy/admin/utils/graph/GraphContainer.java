package com.anluy.admin.utils.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/17.
 */
public class GraphContainer {
    private AtomicInteger atomicInteger = new AtomicInteger(10000);

    private Set<BaseNode> allNodes = new HashSet<>();
    private Set<Node> nodes = new HashSet<>();
//    private Map<String,Node> nodesMap = new HashMap<>();
    private Set<Link> links = new HashSet<>();

    private Node createNode(String name){
        Node node = new Node();
        node.setId(atomicInteger.addAndGet(1));
        node.setName(name);
        this.addNode(node);
        return node;
    }
    private Link createLink(String name,int startId,int endId){
        Link node = new Link();
        node.setId(atomicInteger.addAndGet(1));
        node.setName(name);
        node.setStartId(startId);
        node.setEndId(endId);
        this.addNode(node);
        return node;
    }
    public GraphContainer addNode(BaseNode node){
        allNodes.add(node);
        if(node instanceof Node){
            nodes.add((Node)node);
        }else if(node instanceof Link){
            links.add((Link)node);
        }
        return this;
    }

    private Node findNode(String key,Object value){
        for (Node node : nodes ) {
            Object val = node.getProperty(key);
            if(val == null){
                return null;
            }
            if(val.equals(value)){
                return node;
            }
        }
        return null;
    }

    public void getNode(int edge){

    }
}
