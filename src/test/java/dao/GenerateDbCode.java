package dao;

import fun.johntaylor.kunkka.entity.user.User;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class GenerateDbCode {

    private static String camelNameToUnderString(String name) {
        return name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    private static <T> void printSql(Class<T> clz, String... keys) {
        String tableName = "t_" + camelNameToUnderString(clz.getSimpleName());
        String objName = clz.getSimpleName().substring(0, 1).toLowerCase() + clz.getSimpleName().substring(1);

        StringBuilder insertCol = new StringBuilder();
        StringBuilder insertFn = new StringBuilder();
        StringBuilder update = new StringBuilder();
        StringBuilder select = new StringBuilder();
        StringBuilder updateIdempotentKey = new StringBuilder();
        StringBuilder updateIdempotentWhere = new StringBuilder();

        Map<String, String> keyMap = new LinkedHashMap<>();
        Map<String, String> typeMap = new LinkedHashMap<>();

        Set<String> keyColSet = new HashSet<>(Arrays.asList(keys));

        String fn;
        String col;
        StringBuilder sqlCode = new StringBuilder();
        int i = 0;
        for (Field field : clz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getType().getSimpleName().equals("List")) {
                continue;
            }
            fn = field.getName();
            col = camelNameToUnderString(fn);
            String typeName = field.getType().getSimpleName();
            if (typeName.equals("Integer") || typeName.equals("Long") || typeName.equals("Short")) {
                String defaultStr = "default";
                String autoStr = "";
                if (keyColSet.contains(field.getName())) {
                    defaultStr = "not";
                    autoStr = "auto_increment ";
                }
                sqlCode.append(String.format("\t`%s` bigint(20) %s null %scomment '',\n", col, defaultStr, autoStr));
            } else if (typeName.equals("String") || typeName.equals("Character")) {
                sqlCode.append(String.format("\t`%s` varchar(50) default null comment '',\n", col));
            } else if (typeName.equals("Float") || typeName.equals("Double")) {
                sqlCode.append(String.format("\t`%s` decimal(20,2) default '0.00' comment '',\n", col));
            } else if (typeName.equals("Boolean")) {
                sqlCode.append(String.format("\t`%s` tinyint(2) default '0' comment '',\n", col));
            }

            if (i != 0) {
                select.append(", ");
                insertCol.append(", ");
                insertFn.append(", ");
            }
            if (!fn.equals(col)) {
                select.append(String.format("%s as %s", col, fn));
            } else {
                select.append(fn);
            }
            fn = String.format("#{%s}", fn);
            insertCol.append(col);
            insertFn.append(fn);

            if (keyColSet.contains(field.getName())) {
                keyMap.put(field.getName(), camelNameToUnderString(field.getName()));
                typeMap.put(field.getName(), field.getType().getSimpleName());
            } else {
                update.append(String.format("\t\t<if test=\"%s!=null\">%s = #{%s},</if>\n", field.getName(), camelNameToUnderString(field.getName()), field.getName()));
                updateIdempotentKey.append(String.format("\t\t<if test=\"new.%s!=null\">%s = #{new.%s},</if>\n", field.getName(), camelNameToUnderString(field.getName()), field.getName()));
                updateIdempotentWhere.append(String.format("\t\t<if test=\"new.%s!=null and old.%s!=null\">and %s = #{old.%s}</if>\n", field.getName(), field.getName(), camelNameToUnderString(field.getName()), field.getName()));
            }

            i++;
        }

        System.out.println("===========================================================");
        StringBuilder sql = new StringBuilder();
        sql.append(String.format("String COLUMNS = \"%s\";", select));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        StringBuilder keyPairs = new StringBuilder();
        StringBuilder keyPairsNew = new StringBuilder();
        i = 0;
        for (String k : keyMap.keySet()) {
            if (i != 0) {
                keyPairs.append(" and ");
                keyPairsNew.append(" and ");
            }
            keyPairs.append(String.format("%s=#{%s}", keyMap.get(k), k));
            keyPairsNew.append(String.format("%s=#{old.%s}", keyMap.get(k), k));
            i++;
        }
        sql.append(String.format("@Select(\"select \" + COLUMNS + \" from %s where %s\")", tableName, keyPairs.toString()));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        String selectWhere = keyPairs.toString();
        keyPairs.delete(0, keyPairs.length());
        i = 0;
        for (String k : keyMap.keySet()) {
            if (i != 0) {
                keyPairs.append(", ");
            }
            keyPairs.append(String.format("%s %s", typeMap.get(k), k));
            i++;
        }
        sql.append(String.format("%s select(%s);", clz.getSimpleName(), keyPairs.toString()));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("@Insert(\"insert into %s(%s) values(%s)\")", tableName, insertCol, insertFn));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        if (keys.length == 1) {
            sql.append(String.format("@SelectKey(statement = \"SELECT LAST_INSERT_ID()\", keyProperty = \"%s\", before = false, resultType = Long.class)", keys[0]));
            System.out.println(sql.toString());
            sql.delete(0, sql.length());
        }
        sql.append(String.format("int insert(%s %s);", clz.getSimpleName(), objName));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("int update(%s %s);", clz.getSimpleName(), objName));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("int updateIdempotent(Map<String, %s> params);", clz.getSimpleName()));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("@Delete(\"delete from %s where %s\")", clz.getSimpleName(), selectWhere));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        sql.append(String.format("int delete(%s);", keyPairs.toString()));
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("<update id=\"update\" parameterType=\"%s\">\n", clz.getName()));
        sql.append(String.format("\tupdate %s\n", tableName));
        if (update.length() > 0) {
            sql.append("\t<set>\n");
            sql.append(update);
            sql.append("\t</set>\n");
        }
        sql.append(String.format("\twhere %s\n", selectWhere));
        sql.append("</update>");
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append("<update id=\"updateIdempotent\" parameterType=\"Map\">\n");
        sql.append(String.format("\tupdate %s\n", tableName));
        if (updateIdempotentKey.length() > 0) {
            sql.append("\t<set>\n");
            sql.append(updateIdempotentKey);
            sql.append("\t</set>\n");
        }
        if (updateIdempotentWhere.length() > 0) {
            sql.append("\t<where>\n");
            sql.append(String.format("\t\twhere %s\n", keyPairsNew.toString()));
            sql.append(updateIdempotentWhere);
            sql.append("\t</where>\n");
        }
        sql.append("</update>");
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();

        sql.append(String.format("drop table if exists `%s`;\n", tableName));
        sql.append(String.format("create table `%s` (\n", tableName));
        sql.append(sqlCode);
        if (keys.length == 1) {
            sql.append(String.format("\tprimary key(`%s`)\n", keys[0]));
        } else {
            StringBuilder keyStr = new StringBuilder();
            i = 0;
            for (String key : keys) {
                if (i != 0) {
                    keyStr.append(",");
                }
                keyStr.append(String.format("`%s`", key));
                i++;
            }
            sql.append(String.format("\tprimary key(%s)\n", keyStr));
        }
        sql.append(")engine=InnoDB auto_increment=1;");
        System.out.println(sql.toString());
        sql.delete(0, sql.length());
        System.out.println();
        System.out.println("===========================================================");
    }

    public static void main(String[] args) {
        printSql(User.class, "id");
    }
}
