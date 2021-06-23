package com.anluy.admin.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/10/13.
 */
public class ClassUtils {

    public static Map<Integer, ClassFieldInfo> loadIndexMapping(Class clazz,List<String> titleList) {
        return loadIndexMapping(clazz,titleList,null);
    }
    public static Map<Integer, ClassFieldInfo> loadIndexMapping(Class clazz,List<String> titleList,Map<Integer, ClassFieldInfo> indexMapping) {
        if(clazz == null){
            return new LinkedHashMap<>();
        }
        Field[] fields = clazz.getDeclaredFields();
        if(indexMapping == null){
            indexMapping = new LinkedHashMap<>();
        }
        boolean isFind;
        //遍历标题字段
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            isFind = false;
            //如果标题已经存在了字段映射关系，跳过
            if(indexMapping.containsKey(i)){
                continue;
            }
            //遍历类的字段列表
            for (Field field : fields) {
                com.anluy.admin.entity.Field fid = field.getAnnotation(com.anluy.admin.entity.Field.class);
                if (fid != null) {
                    //如果有field注解，获取注解的内容，并按|分割
                    String vt = fid.value();
                    String[] vts = vt.split("\\|");
                    //遍历注解内容，
                    for (int j = 0; j < vts.length; j++) {
                        //如果注解内容能对应标题，记录该注解字段对应的下标
                        String v = vts[j];
                        if (v.equals(title) || title.startsWith(v)) {
                            field.setAccessible(true);
                            indexMapping.put(i, new ClassFieldInfo(i,v,field));
                            isFind = true;
                            break;
                        }
                    }
                }
                if(isFind){
                    break;
                }
            }
        }
        loadIndexMapping(clazz.getSuperclass(),titleList,indexMapping);
        return indexMapping;
    }

    public static void setDataToObject(Object obj, List<String> dataList, Map<Integer, ClassFieldInfo> mapping,Map<String,Function> functionMap) throws IllegalAccessException {
        //List<String> list
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            ClassFieldInfo classFieldInfo = mapping.get(i);
            if (classFieldInfo != null) {
                if(functionMap!=null&& functionMap.containsKey(classFieldInfo.getName())){
                    Function function = functionMap.get(classFieldInfo.getName());
                    classFieldInfo.getField().set(obj,function.apply(dataList.get(i)));
                }else {
                    classFieldInfo.getField().set(obj,dataList.get(i));
                }
            }
        }
    }

    public static class ClassFieldInfo {
        private int index;
        private String name;
        private  Field field;

        public ClassFieldInfo(int index, String name, Field field) {
            this.index = index;
            this.name = name;
            this.field = field;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }
    }

    public static Function FUNCTION_JDLX = new Function<String,String>() {
        @Override
        public String apply(String o) {
            if(StringUtils.isNotBlank(o) && !"null".equals(o)){
                Integer jdlx = Integer.valueOf(o.endsWith(".00")?o.substring(0,o.length()-3):o);
                if(jdlx == 1){
                    return "出";
                }
                if(jdlx == 2){
                    return "进";
                }
                return "-";
            }
            return null;
        }
    };
    public static Function FUNCTION_DOUBLE = new Function<String,Double>() {
        @Override
        public Double apply(String o) {
            if(StringUtils.isNotBlank(o) && !"null".equals(o)){
                return Double.valueOf(o);
            }
            return null;
        }
    };

    public static Function FUNCTION_DATE_YYYYMMDDHHMMSS = new Function<String,Date>() {
        @Override
        public Date apply(String o) {
            if(StringUtils.isNotBlank(o)){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    return sdf1.parse(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };
    public static Function FUNCTION_DATE_YYYYMMDDHHMMSS_GG = new Function<String,Date>() {
        @Override
        public Date apply(String o) {
            if(StringUtils.isNotBlank(o)){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return sdf1.parse(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };
    public static Function FUNCTION_DATE_YYYYMMDDHHMMSS_GG_X = new Function<String,Date>() {
        @Override
        public Date apply(String o) {
            if(StringUtils.isNotBlank(o)){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                try {
                    if (o.indexOf("/") > 0) {
                        return sdf2.parse(o);
                    } else {
                        return sdf1.parse(o);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };
    public static Function FUNCTION_DATE_YYYYMMDD = new Function<String,Date>() {
        @Override
        public Date apply(String o) {
            if(StringUtils.isNotBlank(o)){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                try {
                    return sdf1.parse(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };
    public static Function FUNCTION_DATE_YYYY_MM_DD = new Function<String,Date>() {
        @Override
        public Date apply(String o) {
            if(StringUtils.isNotBlank(o)){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return sdf1.parse(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    };
}
