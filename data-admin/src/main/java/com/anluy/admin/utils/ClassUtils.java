package com.anluy.admin.utils;

import com.anluy.admin.entity.YhzhJylsInfo;
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
        Field[] fields = clazz.getDeclaredFields();
        Map<Integer, ClassFieldInfo> indexMapping = new LinkedHashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            for (Field field : fields) {
                com.anluy.admin.entity.Field fid = field.getAnnotation(com.anluy.admin.entity.Field.class);
                if (fid != null) {
                    String vt = fid.value();
                    //如果能字段对应的字段，记录该字段对应的下标
                    if (vt.equals(title)) {
                        field.setAccessible(true);
                        indexMapping.put(i, new ClassFieldInfo(i,vt,field));
                        break;
                    }
                }
            }
        }
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
                if(functionMap.containsKey(classFieldInfo.getName())){
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

    public static Function FUNCTION_DOUBLE = new Function<String,Double>() {
        @Override
        public Double apply(String o) {
            if(StringUtils.isNotBlank(o)){
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
}
