import java.io.*;

public class zhuanhuan {
    public static void main(String[] args) throws java.io.FileNotFoundException,java.io.IOException{
        String fileName = "C:\\Users\\ASUS\\Desktop\\100008348542.txt";
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            // 一行一行地处理...
            System.out.println(line);
        }//原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/java/java-read-text-file.html


    }
}
