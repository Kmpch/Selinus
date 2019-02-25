package us.kpm.rpc.register;

import lombok.Data;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.kpm.rpc.enums.Constant;

import java.io.IOException;

/**
 * Description: SelinusRpcRegistry 服务注册
 * PackageName: us.kpm.rpc.register
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcRegistry.java
 * Create at:  2019/2/24
 * Copyright:   Copyright (c)2019
 */
@Data
public class SelinusRpcRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SelinusRpcRegistry.class);


    /**
     * 服务端注册地址
     */
    private String registryAddress;

    /**
     * zk客户端程序
     */
    private ZooKeeper zooKeeper;


    /**
     * 创建节点
     */
    public void createNode(String data) throws Exception {

        zooKeeper = new ZooKeeper(registryAddress, Constant.DEFAULT_SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });

        if(zooKeeper == null){
            LOGGER.error("zookeeper connect is null");
            return;
        }

        //判断当前目录是否注册
        Stat exists = zooKeeper.exists(registryAddress, false);
        if(exists == null){
            //如果不存在，则创建一个持久节点目录
            zooKeeper.create(Constant.REGISTRY_PATH,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //创建一个临时的序列节点,并且保存数据信息
        zooKeeper.create(Constant.REGISTRY_PATH,data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);

    }
}
