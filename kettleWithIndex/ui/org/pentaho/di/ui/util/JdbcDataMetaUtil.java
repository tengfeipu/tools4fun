package org.pentaho.di.ui.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;

public class JdbcDataMetaUtil {
    /**
     * 获取源数据库的源表的主键信息，可能存在多个用户下有相同表，故需要做排除
     */
    public static String exportPkAndIndex(DatabaseMeta sourceDbInfo,
                                          Connection sourceCon, String sourceTable,
                                          DatabaseMeta targetDbInfo, Connection targetCon, String targetTable) {
        String result = "";
        StringBuffer sb = new StringBuffer();

        sourceTable = sourceDbInfo.quoteField(sourceTable);
        Map<String, String> sourceKeys = getSourcePkKeyMap(targetDbInfo,
                sourceCon, sourceTable, targetTable);

        if (targetPkExist(targetCon, targetTable)
                || targetPkExist(targetCon, targetTable.toLowerCase())
                || targetPkExist(targetCon, targetTable.toUpperCase())) {
        } else {
            if (sourceKeys != null && sourceKeys.size() > 0) {
                String str = "ALTER TABLE ${TABLE} ADD CONSTRAINT  ${PKID}  PRIMARY KEY (${ID});";
                for (Entry<String, String> m : sourceKeys.entrySet()) {
                    str = str.replace("${PKID}", m.getKey())
                            .replace("${TABLE}", sourceTable)
                            .replace("${ID}", m.getValue());// 目标表中关键字
                    sb.append(str).append(Const.CR);
                }
                result += sb.toString();
            }
        }
        result += exportIndexes(sourceCon, sourceTable, targetDbInfo,
                targetCon, targetTable);
        return result;
    }

    public static String exportPrimaryKey(DatabaseMeta sourceDbInfo,
                                          Connection sourceCon, String sourceTable,
                                          DatabaseMeta targetDbInfo, Connection targetCon, String targetTable) {
        StringBuffer sb = new StringBuffer();

        sourceTable = sourceDbInfo.quoteField(sourceTable);
        Map<String, String> sourceKeys = getSourcePkKeyMap(targetDbInfo,
                sourceCon, sourceTable, targetTable);

        if (sourceKeys == null || sourceKeys.size() <= 0) {
            return "";
        }

        if (targetPkExist(targetCon, targetTable)
                || targetPkExist(targetCon, targetTable.toLowerCase())
                || targetPkExist(targetCon, targetTable.toUpperCase())) {
            return "";
        }

        String str = "ALTER TABLE ${TABLE} ADD CONSTRAINT  ${PKID}  PRIMARY KEY (${ID});";
        for (Entry<String, String> m : sourceKeys.entrySet()) {
            str = str.replace("${PKID}", m.getKey())
                    .replace("${TABLE}", sourceTable)
                    .replace("${ID}", m.getValue());// 目标表中关键字
            sb.append(str).append(Const.CR);
        }
        return sb.toString();

    }

    /**
     * FIXME 源表可能存在多个表有相同主键
     *
     * @param sourceCon
     * @param sourceTable
     * @param targetTable
     * @return
     */
    private static Map<String, String> getSourcePkKeyMap(
            DatabaseMeta targetDbInfo, Connection sourceCon,
            String sourceTable, String targetTable) {
        Map<String, String> map = new HashMap<String, String>();

        Map<String, String> columnMap = new HashMap<String, String>();
        DatabaseMetaData meta;
        ResultSet rs = null;
        ResultSet rs2 = null;
        String columnName = null;

        try {
            meta = sourceCon.getMetaData();
            boolean flag = false;

            rs = meta.getPrimaryKeys(null, null, sourceTable);
            while (rs.next()) {
                columnName = rs.getObject("COLUMN_NAME").toString();
                rs2 = meta.getColumns(null, null, sourceTable, columnName);

                /**
                 * 去重复，key为：targetTable + columnName，源表名称可能不同但是目标表名称应该相同
                 */
                if (targetTable != null) {
                    String key = targetTable + columnName;
                    if (columnMap.containsKey(key)) {
                        continue;
                    }

                    columnMap.put(key, targetDbInfo.quoteField(columnName));
                }

                Integer dataType;
                Integer columnSize;
                while (rs2.next()) {
                    dataType = Integer.parseInt(rs2.getObject("DATA_TYPE")
                            .toString());
                    columnSize = Integer.parseInt(rs2.getObject("COLUMN_SIZE")
                            .toString());

                    if (dataType.intValue() == java.sql.Types.LONGNVARCHAR
                            || dataType.intValue() == java.sql.Types.LONGVARCHAR
                            || dataType.intValue() == java.sql.Types.NVARCHAR
                            || dataType.intValue() == java.sql.Types.VARCHAR) {
                        if (columnSize.intValue() >= 256) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    continue;
                }

                if (map.size() >= 1) {
                    /**
                     * 联合主键，其中有一个为字符串，则返回空
                     */
                    if (flag) {
                        map.clear();
                        return map;
                    }
                    String val = map.get(sourceTable + "_PK_ID");
                    val += "," + targetDbInfo.quoteField(columnName);
                    map.remove(sourceTable + "_PK_ID");
                    map.put(sourceTable + "_PK_ID", val);
                } else {
                    map.put(sourceTable + "_PK_ID",
                            targetDbInfo.quoteField(columnName));
                }
            }
        } catch (SQLException e) {
            // 源表不存在的时候
            System.err.println("源表 "+sourceTable+"查询主键异常:" + e.getMessage());
            map = null;
        } finally {
            columnMap.clear();
            columnMap = null;
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (SQLException e) {
            }
        }
        return map;
    }

    private static boolean targetPkExist(Connection targetCon,
                                         String targetTable) {
        DatabaseMetaData meta;
        ResultSet rs = null;

        boolean flag = false;
        try {
            meta = targetCon.getMetaData();

            rs = meta.getPrimaryKeys(null, null, targetTable);
            while (rs.next()) {
                flag = true;
                break;
            }
        } catch (SQLException e) {
            // 目标表不存在的时候
            System.err.println("目标表 "+targetTable+"查询主键异常:" + e.getMessage());
            flag = false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return flag;
    }

    private static boolean targetIndexExist(Connection targetCon,
                                            String targetTable) {
        DatabaseMetaData meta;
        ResultSet rs = null;

        boolean flag = false;
        try {
            meta = targetCon.getMetaData();

            rs = meta.getIndexInfo(null, null, targetTable, false, false);
            while (rs.next()) {
                flag = true;
                break;
            }
        } catch (SQLException e) {
            // 目标表不存在的时候
            System.err.println("目标表 "+targetTable+"查询索引异常:" + e.getMessage());
            flag = false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return flag;
    }

    public static String exportIndexes(Connection sourceCon,
                                       String sourceTable, DatabaseMeta targetDbInfo,
                                       Connection targetCon, String targetTable) {
        StringBuilder sb = new StringBuilder();
        String index = null;
        ResultSet rs = null;
        boolean unique = false;
        if (targetIndexExist(targetCon, targetTable)
                || targetIndexExist(targetCon, targetTable.toLowerCase())
                || targetIndexExist(targetCon, targetTable.toUpperCase())) {
            return "";
        }

        try {
            ArrayList columns = new ArrayList();
            DatabaseMetaData meta = sourceCon.getMetaData();
            rs = meta.getIndexInfo(null, null, sourceTable, false, false);

            while (rs.next()) {
                String newIndex = rs.getString("INDEX_NAME");
                if (newIndex == null || newIndex.equals("PRIMARY_KEY") || newIndex.contains("SYS") || newIndex.contains("PK")) {
                    continue;
                }
                if (index != null && !index.equals(newIndex)) {
                    exportIndex(sb, sourceTable, index, unique, columns);
                    index = null;
                }
                if (index == null) {
                    index = newIndex;
                    columns.clear();
                }
                unique = !rs.getBoolean("NON_UNIQUE");
                String column = targetDbInfo.quoteField(rs
                        .getString("COLUMN_NAME"));
                if (!columns.contains(column)) {
                    columns.add(column);
                }

            }
            if (index != null) {
                exportIndex(sb, sourceTable, index, unique, columns);
            }
        } catch (SQLException e) {
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
        }
        return sb.toString();
    }

    private static void exportIndex(StringBuilder sb, String table,
                                    String index, boolean unique, ArrayList columns) {
        String s = "CREATE ";
        if (unique) {
            s += "UNIQUE ";
        }
        s += "INDEX " + index + " ON " + table + "(";
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                s += ",";
            }
            s += (String) columns.get(i);
        }
        sb.append(s + ");").append(Const.CR);
    }

    public static void main(String[] args) throws ClassNotFoundException,
            SQLException {
        Class.forName("org.postgresql.Driver");
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection sourceCon = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:orcl", "kettle", "kettle");
        // Connection targetCon = DriverManager.getConnection(
        // "jdbc:postgresql://localhost:5432/postgres", "postgres",
        // "redhat");
        //
        // System.out.println("添加主键字符串： "
        // + exportPrimaryKey(sourceCon, "TSSH_RBAC_ROLERESOURCE",
        // targetCon, "TSSH_RBAC_ROLERESOURCE"));

        // System.out.println("获取索引： "
        // + exportIndexes(sourceCon, "TSSH_RBAC_ROLERESOURCE"));
    }
}