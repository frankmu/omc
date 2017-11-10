package com.omc.service.zookeeper;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import com.omc.service.registration.OmcServiceRegistry;

public class OmcZookeeperServiceRegistry implements OmcServiceRegistry {

	private final CuratorFramework curatorFramework;
	private final ConcurrentHashMap<String, String> uriToZnodePath;

	public OmcZookeeperServiceRegistry(String host, String port) {
        this.curatorFramework = CuratorFrameworkFactory.newClient(host + ":" + port, new RetryNTimes(5, 1000));
        this.curatorFramework.start();
        this.uriToZnodePath = new ConcurrentHashMap<>();
    }

	@Override
	public void registerService(String znode, String uri) {
		try {
			if (curatorFramework.checkExists().forPath(znode) == null) {
				curatorFramework.create().creatingParentsIfNeeded().forPath(znode);
			}

			String znodePath = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(znode, uri.getBytes());

			uriToZnodePath.put(uri, znodePath);
		} catch (Exception ex) {
			throw new RuntimeException("Could not register service \"" + znode + "\", with URI \"" + uri + "\": " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void unregisterService(String name, String uri) {
		try {
			if (uriToZnodePath.contains(uri)) {
				curatorFramework.delete().forPath(uriToZnodePath.get(uri));
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not unregister service \"" + name + "\", with URI \"" + uri + "\": " + ex.getLocalizedMessage());
		}
	}

	@Override
	public String discoverServiceURI(String znode) {
		try {
			List<String> uris = curatorFramework.getChildren().forPath(znode);
			return new String(curatorFramework.getData().forPath(ZKPaths.makePath(znode, uris.get(0))));
		} catch (Exception ex) {
			throw new RuntimeException("Service \"" + znode + "\" not found: " + ex.getLocalizedMessage());
		}
	}
}
