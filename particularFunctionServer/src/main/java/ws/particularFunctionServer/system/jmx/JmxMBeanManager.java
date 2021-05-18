package ws.particularFunctionServer.system.jmx;

import ws.relationship.exception.JmxMBeanManagerInitException;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class JmxMBeanManager {
    public static void init() {
        MBeanServer server = MBeanServerFactory.createMBeanServer("Http");
        try {
            ObjectName appDebugger = new ObjectName("PiecesFunctionServerRuntimeInfo:name=AppDebugger");
            server.createMBean("ws.particularFunctionServer.features.standalone.jmxManager.AppDebugger", appDebugger);
        } catch (Exception e) {
            throw new JmxMBeanManagerInitException("JMX MBean 创建失败！", e);
        }
    }
}