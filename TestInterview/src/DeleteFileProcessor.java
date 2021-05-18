import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DeleteFileProcessor {
    /**
     * @param localDate 指定日期
     * @param path      文件目录
     * @return 返回已经删除的文件列表
     */
    public static List<Path> deleteFile(LocalDate localDate, Path path) {
        // TODO
        LocalDate deleteDate = localDate.plusDays(-30);
        File file = new File(path.toString());
        List<Path> deleteList = new ArrayList();
        delete(file, deleteDate, deleteList);
        for (Path p : deleteList) {
            System.out.println(p.toString());
        }
        return deleteList;
    }

    public static List<Path> delete(File file, LocalDate deleteDate, List<Path> pathList) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    delete(file1, deleteDate, pathList);
                }
            } else {
                String fileName = file.getName();
                //文件,判断名字
                if (checkFileName(fileName)) {
                    LocalDate fileDate = getFileDate(fileName);
                    //判断日期
                    if (fileDate.isBefore(deleteDate)) {
                        pathList.add(file.toPath());
                        file.delete();
                    }
                }
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length == 0) {
                    //是空文件夹,直接删除
                    pathList.add(file.toPath());
                    file.delete();
                }
            }
        }
        return pathList;
    }

    public static void main(String[] args) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy_MM_dd");
        String date = "2021_10_12";
        LocalDate d1 = LocalDate.parse(date, formatter);
        Path path = Paths.get("D:\\work_test\\test");
        deleteFile(d1, path);

    }


    public static boolean checkFileName(String fileName) {
        LocalDate fileDate = getFileDate(fileName);
        return fileDate != null;
    }

    public static LocalDate getFileDate(String fileName) {
        LocalDate fileDate = null;
        if (fileName.contains(".log") && fileName.contains("_")) {
            fileName = fileName.replace(".log", "");
            String[] arr = fileName.split("_");
            fileName = "";
            for (int i = 0; i < arr.length; i++) {
                if (isInt(arr[i])) {
                    fileName += arr[i];
                    if (i < arr.length - 1) {
                        fileName += "_";
                    }
                }
            }
            if (fileName != "" && isDate(fileName)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy_MM_dd");
                try {
                    fileDate = LocalDate.parse(fileName, formatter);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileDate;
    }

    private static boolean isInt(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDate(String fileDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy_MM_dd");
        try {
            LocalDate.parse(fileDate, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
