package ws.gameServer.system.date.dayChanged;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.fileHandler.FileReadCharacters;
import ws.common.utils.fileHandler._FileReadWritArgs;

import java.io.BufferedReader;
import java.io.IOException;

public class TasklistCron4jReader extends _FileReadWritArgs {
    private static final Logger LOGGER = LoggerFactory.getLogger(TasklistCron4jReader.class);

    public TasklistCron4jReader(FileReadCharacters fileCharacters) {
        super(fileCharacters);
    }

    @Override
    public Object callContentRead(BufferedReader bufRead, Object... args) {
        try {
            String str = bufRead.readLine();
            while (str != null) {
                if (str.contains("#tellDayChanged")) {
                    return str;
                }
                str = bufRead.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("读取tasklist.cron4j文件内容异常！", e);
        }
        return null;
    }
}
