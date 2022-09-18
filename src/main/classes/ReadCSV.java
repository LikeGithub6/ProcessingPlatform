import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
public class ReadCSV {
    public List<List<String>> CSVToExcel(String fileP){
        List<List<String>> bigList=new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(fileP))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {

                List<String> l=new ArrayList<>();
                for(int i=0;i<record.size();i++){
                    String str=record.get(i).replaceAll("'","''");
                    l.add(str);
                }
                bigList.add(l);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("biglist的第一行数量："+bigList.get(0).size());
        return bigList;

    }
}
