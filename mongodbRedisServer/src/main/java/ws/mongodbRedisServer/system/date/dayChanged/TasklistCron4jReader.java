package ws.mongodbRedisServer.system.date.dayChanged;

import ws.common.utils.fileHandler.FileReadCharacters;
import ws.common.utils.fileHandler._FileReadWritArgs;

import java.io.BufferedReader;
import java.io.IOException;

public class TasklistCron4jReader extends _FileReadWritArgs {
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
        }
        return null;
    }
}
