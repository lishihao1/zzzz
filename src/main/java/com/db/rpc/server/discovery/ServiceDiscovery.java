package com.db.rpc.server.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.rpc.constant.Constant;

/**
 * 服务发现
 * 
 * @author dengbo
 */
public class ServiceDiscovery {
	private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	private CountDownLatch latch = new CountDownLatch(1);

	private volatile List<String> dataList = new ArrayList<String>();

	private String registryAddress;

	public ServiceDiscovery(String registryAddress) {
		this.registryAddress = registryAddress;
		ZooKeeper zk = connectZookeeperServer();
		if(zk!=null){
			watchNode(zk);
		}
	}

	/**
	 * 发现服务
	 * @return
	 */
	public String discovery(){
		String data = null;
		int size = dataList.size();
		if(size>0){
			if(size==1){
				data = dataList.get(0);
			}else{
				data = dataList.get(ThreadLocalRandom.current().nextInt(size));//并发随机数
			}
		}
		return data;
	}
	
	/**
	 * 创建zookeeper 连接
	 * @return
	 */
	private ZooKeeper connectZookeeperServer() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
				public void process(WatchedEvent watchedEvent) {
					if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			logger.error("", e);
		}
		return zk;
	}

	/**
	 * 观察节点
	 * @param zk
	 */
	private void watchNode(final ZooKeeper zk){
		try{
			List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher(){
				public void process(WatchedEvent event) {
					if(event.getType() ==  Event.EventType.NodeChildrenChanged){
						watchNode(zk);//子节点更改时重新获取服务列表
					}
				}
			});
			List<String> dataList = new ArrayList<String>();
			for(String node : nodeList){
				byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH+"/"+node, false, null);
				dataList.add(new String(bytes));
			}
			this.dataList = dataList;
		}catch(Exception e){
			logger.error("watch node error...",e);
		}
	}
	
}
