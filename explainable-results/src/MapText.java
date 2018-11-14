import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MapText {
    public static void main(String[] args) {
        try {
            int LIMIT = 1000;
            String pathCorpus = "/Users/dhruva/Desktop/ISR/final_project/explainable-results/acm_corpus.gz";
            String pathResults = "/Users/dhruva/Desktop/ISR/final_project/explainable-results/results_TFIDF";
            String outFile = "/Users/dhruva/Desktop/ISR/final_project/explainable-results/file_text.txt";
            Pattern pattern = Pattern.compile(
                    "<DOC>.+?<DOCNO>(.+?)</DOCNO>.+?<TEXT>(.+?)</TEXT>.+?</DOC>",
                    Pattern.CASE_INSENSITIVE + Pattern.MULTILINE + Pattern.DOTALL
            );
            InputStream is = new GZIPInputStream(new FileInputStream(pathCorpus));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BufferedReader br_results = new BufferedReader(new InputStreamReader(new FileInputStream(pathResults)));
            BufferedWriter bw_out = new BufferedWriter(new FileWriter(outFile));
            HashMap<String, String> docmap = new HashMap<>();
            String docstr = "";
            String l = br.readLine();
            while (l != null) {
                docstr += l + " ";
                if (l.contains("</DOC>")) {
                    Matcher matcher = pattern.matcher(docstr);
                    matcher.find();
                    String docno = matcher.group(1).trim();
                    String text = matcher.group(2).trim();

//                    System.out.println(docno);
//                    System.out.println(text);
                    docmap.put(docno, text);
                    docstr = "";
                }

                l = br.readLine();

            }
            String res = br_results.readLine();
            int count = 0;
            double topscore;
            while (res != null && count < LIMIT){
                String [] result = res.split(" ");
                String result_docno = result[2];
                String result_score = result[4];
                String result_text = docmap.get(result_docno);
//                System.out.println(result_docno+" "+result_text);
                bw_out.write(result_docno+" "+result_text+" "+result_score+"\n");
                res = br_results.readLine();
                count++;
            }
            br.close();
            br_results.close();
            bw_out.close();

            // remember to close both the index writer and the directory

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
