package ws.particularFunctionServer.utils.codeHelper.tabToJava;

import ws.common.table.data.TableData;
import ws.common.table.table.implement.table._TableHeader;
import ws.common.table.table.interfaces.table.TableHeader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

/**
 * 生成配置行访问器代码
 */
public class GenTableRowAccesserCodeFile {

    public static StringBuffer printCode(TableData tableDataTxt, String packagePathm, String fileName) {
        TableHeader tableHeader = new _TableHeader();
        tableHeader.parse(tableDataTxt.getTableName(), tableDataTxt.getHeaderDatas());
        String idColumnName = tableHeader.getIdColumnName();

        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packagePathm).append(";").append("\n");
        sb.append("public class ").append(fileName).append(" extends AbstractRow {\n");
        sb.append("private static final long serialVersionUID = 1L;").append("\n");

        Map<String, String> columnNameMaptoDesc = tableHeader.getColumnNameToDesc();
        Map<String, String> columnNameMaptoType = tableHeader.getColumnNameToType();

        for (String columnName : tableHeader.getAllColumnNames()) {
            if (columnName.equals(idColumnName)) {
                continue;
            }
            String columnDesc = columnNameMaptoDesc.get(columnName);
            String columnType = columnNameMaptoType.get(columnName);
            String fullJavaType = getFullJavaType(columnType);
            sb.append("            /**").append("\n").append(" * " + columnType + " " + columnDesc).append("\n").append(" */").append("\n").append("private " + fullJavaType + " " + lowerCaseFirstLetter(columnName) + ";").append("\n");

        }

        sb.append("@Override").append("\n");
        sb.append("public void parseRow(Map<String, String> map) throws CellParseFailedException {").append("\n");
        sb.append("                           // id column = {columnName:\"" + idColumnName + "\", columnDesc:\"" + columnNameMaptoDesc.get(idColumnName) + "\"}").append("\n");

        for (String columnName : tableHeader.getAllColumnNames()) {
            if (columnName.equals(idColumnName)) {
                continue;
            }
            String columnType = columnNameMaptoType.get(columnName);
            String parseMethodName = getParseMethodName(columnType);
            String javaType = getJavaTypeTxt(columnType);
            sb.append(lowerCaseFirstLetter(columnName) + " = " + parseMethodName + "(\"" + columnName + "\", map, " + javaType + ".class); //" + columnType).append("\n");
        }

        sb.append("\n}").append("\n");
        sb.append("\n}").append("\n");
        return sb;
    }

    private static String lowerCaseFirstLetter(String string) {
        return String.valueOf(string.charAt(0)).toLowerCase() + string.substring(1, string.length());
    }

    private static boolean isArray(String columnType) {
        return columnType.matches("^array.*$");
    }

    private static boolean isTuple(String columnType) {
        return columnType.matches("^.*\\d+$");
    }

    private static String getJavaTypeMysql(String columnType) {
        if (columnType.contains("java.lang.Integer")) {
            return "Integer";
        } else if (columnType.contains("java.lang.Float")) {
            return "Double";
        } else if (columnType.contains("java.lang.Boolean")) {
            return "Boolean";
        } else if (columnType.contains("java.lang.String")) {
            return "String";
        } else {
            return "null";
        }
    }

    private static String getJavaTypeTxt(String columnType) {
        if (columnType.contains("int")) {
            return "Integer";
        } else if (columnType.contains("float")) {
            return "Double";
        } else if (columnType.contains("bool")) {
            return "Boolean";
        } else if (columnType.contains("string")) {
            return "String";
        } else {
            return "null";
        }
    }

    private static String getFullJavaType(String columnType) {
        boolean isArray = isArray(columnType);
        boolean isTuple = isTuple(columnType);
        String javaType = getJavaTypeTxt(columnType);
        if (isArray && isTuple) {
            return "TupleListCell<" + javaType + ">";
        } else if (isArray) {
            return "ListCell<" + javaType + ">";
        } else if (isTuple) {
            return "TupleCell<" + javaType + ">";
        } else {
            return javaType;
        }
    }

    private static String getParseMethodName(String columnType) {
        boolean isArray = isArray(columnType);
        boolean isTuple = isTuple(columnType);
        if (isArray && isTuple) {
            return "CellParser.parseTupleListCell";
        } else if (isArray) {
            return "CellParser.parseListCell";
        } else if (isTuple) {
            return "CellParser.parseTupleCell";
        } else {
            return "CellParser.parseSimpleCell";
        }
    }

    public static void writeToFile(File file, String writeContent, boolean append) {
        OutputStream outStream = null;
        Writer write = null;
        BufferedWriter bufWrite = null;
        try {
            outStream = new FileOutputStream(file, append);
            write = new OutputStreamWriter(outStream, "UTF-8");
            bufWrite = new BufferedWriter(write);
            bufWrite.write(writeContent);
            bufWrite.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufWrite != null) {
                    bufWrite.close();
                }
                bufWrite = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearFile(File file) {
        if (file.exists() && file.isFile()) {
            writeToFile(file, "", false);
        }
    }

}
