package com.omc.service.zookeeper;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.registration.OmcServiceRegistry;

public class OmcZookeeperServiceRegistry implements OmcServiceRegistry, OmcServiceDiscovery {

	private final CuratorFramework curatorFramework;
	private String servicePath;

	public OmcZookeeperServiceRegistry(String host, String port) {
        this.curatorFramework = CuratorFrameworkFactory.newClient(host + ":" + port, new RetryNTimes(5, 1000));
        this.curatorFramework.start();
    }

	@Override
	public void registerService(String znode, String uri) {
		this.servicePath = znode + "/" + UUID.randomUUID().toString();
		try {
			if (curatorFramework.checkExists().forPath(this.servicePath) != null) {
				curatorFramework.delete().forPath(this.servicePath);
			}
			curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(this.servicePath, uri.getBytes());
		} catch (Exception ex) {
			throw new RuntimeException("Could not register service \"" + this.servicePath + "\", with URI \"" + uri + "\": " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void unregisterService(String name, String uri) {
		try {
			if (curatorFramework.checkExists().forPath(this.servicePath) != null) {
				curatorFramework.delete().forPath(this.servicePath);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not unregister service \"" + name + "\", with URI \"" + uri + "\": " + ex.getLocalizedMessage());
		}
	}

	@Override
	public String discoverServiceURI(String znode) {
		try {
			List<String> nodeList = curatorFramework.getChildren().forPath(znode);
			if(nodeList.isEmpty()) {
				throw new Exception("No instance available for service: " + znode);
			}
			String node = nodeList.size() == 1 ? nodeList.get(0) : nodeList.get(new Random().nextInt(nodeList.size()));
			return new String(curatorFramework.getData().forPath(znode + "/" + node));
		} catch (Exception ex) {
			throw new RuntimeException("Service \"" + znode + "\" not found: " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void close() {
		this.curatorFramework.close();
	}
}
