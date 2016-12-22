package com.db.rpc.service.registry;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import com.db.rpc.constant.Constant;

/**
 * 服务注册中心
 * @author dengbo
 */
public class ServiceRegistry {
	private static final Logger logger = Logger.getLogger(ServiceRegistry.class);	
	
	private CountDownLatch latch = new CountDownLatch(1); //线程计数闭锁器
	
	private String registryAddress;
	
	public ServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	/**
	 * 注册
	 * @param data
	 */
	public void registry(String data){
		if(data!=null){
			ZooKeeper zk = connectZookeeperServer();
			if(zk!=null){
				createNode(zk,data);
			}
		}
	}
	
	/**
	 * 创建zookeeper连接
	 * @return
	 */
	private ZooKeeper connectZookeeperServer(){
		ZooKeeper zk = null;
		try{
			zk = new ZooKeeper(registryAddress,Constant.ZK_SESSION_TIMEOUT,new Watcher(){

				public void process(WatchedEvent watchedEvent) {
					if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
            latch.await();
		}catch(Exception e){
			logger.error("", e);
		}
		return zk;
	}
	
	/**
	 * 创建节点
	 * @param zk
	 * @param data
	 */
	private void createNode(ZooKeeper zk,String data){
		if(zk==null||data==null){
			return;
		}
		try {
			zk.create(Constant.ZK_DATA_PATH, data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("create zookeeper nodes success... node:"+data);
		} catch (Exception e) {
			logger.error("create zookeeper nodes fail...",e);
		} 
	}
	

}
