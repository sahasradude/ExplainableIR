import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class makeCorpus {

    public static void main(String[] args) {
        String dirpath = "/Users/dhruva/Desktop/ISR/final_project/explainable-results";
        String idxpath = dirpath + "/index_lucene_robust04_krovetz";
        String field_docno = "docno";
        String field_search = "content";
        Set<String> fieldset = new HashSet<>();
        fieldset.add( "docno" );
        try {
            Directory dirLucene = FSDirectory.open((new File(idxpath)).toPath());
            IndexReader index = DirectoryReader.open(dirLucene);

            for(int docid = 0; docid < index.maxDoc(); docid++) {
                String docno = index.document( docid, fieldset).get( "docno" );
                int doclen = 0;
                TermsEnum termsEnum = index.getTermVector( docid, field_search ).iterator();
                while ( termsEnum.next() != null ) {
                    doclen += termsEnum.totalTermFreq();
                }

//                String [] termarr = new String[doclen];
                ArrayList<String> termslist = new ArrayList<>();
                Terms vector = index.getTermVector(docid, field_search); // Read the document's document vector.
                TermsEnum iterator = vector.iterator();
                BytesRef term;
                PostingsEnum positions = null;
                long len = 0;
                while ((term = iterator.next()) != null) {
                    long freq = iterator.totalTermFreq();
                    String termstr = term.utf8ToString();
//                    positions = iterator.postings( positions, PostingsEnum.POSITIONS );
//                    positions.nextDoc();
                    while (freq > 0){
                        freq--;
//                        int p = positions.nextPosition();
//                        System.out.println(p);
//                        termarr[p] = termstr;
                        termslist.add(termstr);
//                        len++;
                    }
                }
                System.out.println(len+" "+doclen);
                System.out.println(termslist);
                if (docid > 10){
                    break;
                }

            }
            index.close();
            dirLucene.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}