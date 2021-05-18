package ws.thirdPartyServer.features.utils;

import ws.thirdPartyServer.features.actor.Placeholder;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform.LoginCheckPlatform;
import ws.thirdPartyServer.features.actor.payment.payment.platform.PaymentPlatform;

import java.util.ArrayList;
import java.util.List;

public class AssignedClassHolder {
    private static List<Class<? extends LoginCheckPlatform>> loginCheckPlatformClasses = null;
    private static List<Class<? extends PaymentPlatform>> paymentPlatformClasses = null;

    static {
        loginCheckPlatformClasses = ws.common.utils.classProcess.ClassFinder.getAllAssignedClass(LoginCheckPlatform.class, Placeholder.class);
        paymentPlatformClasses = ws.common.utils.classProcess.ClassFinder.getAllAssignedClass(PaymentPlatform.class, Placeholder.class);
    }

    public static List<Class<? extends LoginCheckPlatform>> getLoginCheckPlatformClasses() {
        return new ArrayList<>(loginCheckPlatformClasses);
    }

    public static List<Class<? extends PaymentPlatform>> getPaymentPlatformClasses() {
        return new ArrayList<>(paymentPlatformClasses);
    }
}
