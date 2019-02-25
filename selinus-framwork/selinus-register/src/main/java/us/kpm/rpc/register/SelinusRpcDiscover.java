package us.kpm.rpc.register;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sun.tools.javac.util.StringUtils;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kpm.rpc.enums.Constant;
import us.kpm.rpc.exception.RpcDiscoverException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: SelinusRpcDiscover  服务发现
 * PackageName: us.kpm.rpc.register
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcDiscover.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
@Data
public class SelinusRpcDiscover {
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SelinusRpcDiscover.class);


    /**
     * zk注册地址
     */
    private String registryAddress;

    /**
     * 获取到的所有提供服务的服务器列表
     */
    private List<String> rpcServers = Lists.newArrayList();

    /**
     * zk客户端
     */
    private ZooKeeper zooKeeper;

    public SelinusRpcDiscover(String registryAddress) throws Exception {

        this.registryAddress = registryAddress;
        this.zooKeeper = new ZooKeeper(registryAddress, Constant.DEFAULT_SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event == null || event.getType() == null){
                    return;
                }
                //如果zookeeper提供服务的服务器发生改变，更新服务器列表
                if(event.getType().equals(Event.EventType.NodeChildrenChanged)){
                    nodeListener();
                }
            }
        });
        //监听获取节点数据
        nodeListener();
    }
    /**
     * 监听服务端的服务列表信息
     */
    public void nodeListener(){
        List<String> serverList = Lists.newArrayList();
        try {
             serverList = zooKeeper.getChildren(Constant.REGISTRY_PATH, true);
             LOGGER.info("nodeListener getServerList info,servers:{}",JSON.toJSONString(serverList));
            if(CollectionUtils.isNotEmpty(serverList)){
                this.rpcServers = serverList.stream().map(s -> getNodeData(s)).collect(Collectors.toList());
            }
        }  catch (Exception e) {
           LOGGER.error("nodeListener listen error,serverList:{}", JSON.toJSONString(serverList));
        }

    }

    /**
     * 随机获取一个服务发现地址提供给客户端
     *
     */
    public String discover() throws RpcDiscoverException {
        if(CollectionUtils.isEmpty(rpcServers)){
            throw new RpcDiscoverException();
        }
        Collections.shuffle(rpcServers);
        return rpcServers.get(0);
    }

    private String getNodeData(String node) {
        byte[] data = new byte[0];
        try {
            data = zooKeeper.getData(Constant.REGISTRY_PATH.concat("/").concat(node), false, null);
        } catch (Exception e) {
           LOGGER.error("getNodeData getData error,node:{}",node);
        }
        return new String(data);
    }





}
