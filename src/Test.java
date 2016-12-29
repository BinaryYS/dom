import java.io.File;

/**
 * Created by song.yang on 2016/12/19 15:41.
 */
public class Test {
    public static void main(String[] args) {
        TestDom testDom = new TestDom();
        String path = "E:\\orign";
        File file = new File(path);
        if (file.isDirectory()){
            File [] files = file.listFiles();
            for (int i = 0; i < files.length; i++){
                File cFile = files[i];
                String childPath = cFile.getAbsolutePath();
                childPath = childPath.replaceAll("\\\\", "\\\\\\\\");
                System.out.println(childPath);
                Loan loan = new Loan();
                String val = testDom.read(loan, childPath);
                testDom.write2txt(loan.getLoanCode(),val);
            }
        }
    }
}
