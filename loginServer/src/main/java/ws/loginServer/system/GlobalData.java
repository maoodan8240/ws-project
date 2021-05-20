package ws.loginServer.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.loginServer.system.config.AppConfig;
import ws.loginServer.system.config.AppConfig.Key;

import java.util.Locale;

/**
 * Created by lee on 17-6-7.
 */
public class GlobalData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalData.class);
    private static Locale locale = null;


    private static void initLocale() {
        String lang = AppConfig.getString(Key.WS_Common_Config_lang);
        String country = AppConfig.getString(Key.WS_Common_Config_country);
        locale = new Locale(lang, country);
    }


    public static Locale getLocale() {
        if (locale == null) {
            initLocale();
        }
        LOGGER.debug("当前语言环境:lang={} country={}", locale.getLanguage(), locale.getCountry());
        return locale;
    }
}