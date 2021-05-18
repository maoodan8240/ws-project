package ws.gameServer.features.actor.register;

import ws.common.utils.classProcess.ClassFinder;
import ws.gameServer.features.actor.register.utils.create.ext._999_PaymentExtIniter;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtensionIniterClassHolder {
    private static List<Class<? extends ExtensionIniter>> extensionIniterClasses = null;

    static {
        extensionIniterClasses = new ArrayList<>(ClassFinder.getAllAssignedClass(ExtensionIniter.class, _999_PaymentExtIniter.class));
        sortByName();
    }

    public static List<Class<? extends ExtensionIniter>> getExtensionIniterClasses() {
        return extensionIniterClasses;
    }

    public static void sortByName() {
        Collections.sort(extensionIniterClasses, (class1, class2) -> {
            String name1 = class1.getSimpleName();
            String name2 = class2.getSimpleName();
            return name1.compareTo(name2);
        });
    }
}
