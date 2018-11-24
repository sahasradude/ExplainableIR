import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class CorpusMaker {

    public static void main(String[] args) {
        String dirpath = "/Users/dhruva/Desktop/ISR/final_project/explainable-results";
        String idxpath = dirpath + "/index_lucene_robust04_krovetz";
        String pathqrels = dirpath+"/qrels_robust04";
        String pathqueries = dirpath+"/queries_robust04";
        String field_docno = "docno";
        String field_search = "content";
        String filename = "corpus_bow.txt";
        String outfilename = "matchzoo_input.txt";
//        getTextfromIndex(idxpath, field_docno, field_search, filename);
        makeMatchZooFile(pathqrels, pathqueries, filename, outfilename);
    }

    public static void makeMatchZooFile(String pathqrels, String pathqueries, String filename, String outfilename){
        try {
            Map<String, Map<String, Integer>> qrels = EvalUtils.loadQrels(pathqrels);
            Map<String, String> queries = EvalUtils.loadQueries(pathqueries);
            FileWriter fw = new FileWriter(outfilename);
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( new File(filename) ), "UTF-8" ) );
            String line;
            String text = "";
            int count = 0;
            while ( ( line = reader.readLine() ) != null ) {
                String[] splits = line.split("\\s+");
                String docno = splits[0];
                for (int i = 1; i < splits.length; i++){
                    text += (splits[i]+" ");

                }
                for (String qid: qrels.keySet()) {
                   String query = queries.get(qid);
                   int relevance = 0;
                   if (qrels.get(qid).containsKey(docno)){
                       relevance = qrels.get(qid).get(docno);
                   }

                    fw.write(relevance+"\t"+query+"\t"+text+"\n");
                }
                count++;
                if (count == 1000){
                    break;
                }
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
    public static void getTextfromIndex(String idxpath, String field_search, String field_docno, String filename) {

        try {
            FileWriter fw = new FileWriter(filename);
            Set<String> fieldset = new HashSet<>();
            fieldset.add("docno");

            Directory dirLucene = FSDirectory.open((new File(idxpath)).toPath());
            IndexReader index = DirectoryReader.open(dirLucene);

            for (int docid = 0; docid < index.maxDoc(); docid++) {
                String docno = index.document(docid, fieldset).get("docno");
                fw.write(docno + "\t");
                int doclen = 0;
                /* if the index has positions, then uncomment certain lines */

//                TermsEnum termsEnum = index.getTermVector( docid, field_search ).iterator();
//                while ( termsEnum.next() != null ) {
//                    doclen += termsEnum.totalTermFreq();
//                }

//                String [] termarr = new String[doclen];
//                ArrayList<String> termslist = new ArrayList<>();
                Terms vector = index.getTermVector(docid, field_search); // Read the document's document vector.
                TermsEnum iterator = vector.iterator();
                BytesRef term;
//                PostingsEnum positions = null;
                while ((term = iterator.next()) != null) {
                    long freq = iterator.totalTermFreq();
                    String termstr = term.utf8ToString();
//                    positions = iterator.postings( positions, PostingsEnum.POSITIONS );
//                    positions.nextDoc();
                    while (freq > 0) {
                        freq--;
//                        int p = positions.nextPosition();
//                        System.out.println(p);
//                        termarr[p] = termstr;
//                        termslist.add(termstr);
                        fw.write(termstr + " ");
                    }
                }
                fw.write("\n");
//                if (docid > 10){
//                    break;
//                }

            }
            fw.close();
            index.close();
            dirLucene.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
