package ws.gameServer.system.jmx;

import ws.relationship.exception.JmxMBeanManagerInitException;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JmxMBeanManager {
    /**
     * 创建AppDebuggerMBean并注册，Jconsole中无法显示该MBean
     */
    public static void init() {
        MBeanServer server = MBeanServerFactory.newMBeanServer("Http");
        try {
            ObjectName appDebuggerMBeanName = new ObjectName("ws.gameServer:name=AppDebugger");
            server.createMBean("AppDebugger", appDebuggerMBeanName);
        } catch (Exception e) {
            throw new JmxMBeanManagerInitException("JMX MBean 创建失败！", e);
        }
    }

    /**
     * 创建AppDebuggerMBean并注册，可以在Jconsole中显示该MBean
     */
    public static void initNew() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName appDebuggerMBeanName = new ObjectName("ws.gameServer:name=AppDebugger");
            // 或者 直接 AppDebugger appDebugger = new AppDebugger(); server.registerMBean(appDebugger, appDebuggerMBeanName);
            server.createMBean("AppDebugger", appDebuggerMBeanName);
        } catch (Exception e) {
            throw new JmxMBeanManagerInitException("JMX MBean 创建失败！", e);
        }
    }
}