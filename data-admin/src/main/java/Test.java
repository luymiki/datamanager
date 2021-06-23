import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2021/3/19.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        List<String> list = FileUtils.readLines(new File("C:\\Users\\Administrator\\Desktop\\80D178E8-B95C-48ce-8858-49F9F469C982.txt"),"UTF-8");
        list.forEach(line->{
            if(line.indexOf("|")>0){
                String[] str = line.split("\\|");
                for (int i = 0; i < str.length; i++) {
                    System.out.print(str[i].trim()+"\t");
                }
            }else {
                System.out.print(line);
            }
            System.out.println();
        });
    }
}
