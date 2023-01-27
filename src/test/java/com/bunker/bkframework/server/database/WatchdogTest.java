package com.bunker.bkframework.server.database;

import org.junit.Test;

public class WatchdogTest extends TestBase {
	@Test
	public void test() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
