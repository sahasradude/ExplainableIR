import java.io.*;
import java.util.*;

public class EvalUtils {
    /**
     * Load a TREC-format relevance judgment (qrels) file (such as "qrels_robust04" in HW2).
     *
     * @param f A qrels file.
     * @return A map storing the set of relevant documents for each qid.
     * @throws IOException
     */
    public static Map<String, Set<String>> loadQrels( String f ) throws IOException {
        return loadQrels( new File( f ) );
    }

    /**
     * Load a TREC-format relevance judgment (qrels) file (such as "qrels_robust04" in HW2).
     *
     * @param f A qrels file.
     * @return A map storing the set of relevant documents for each qid.
     * @throws IOException
     */
    public static Map<String, Set<String>> loadQrels( File f ) throws IOException {
        Map<String, Set<String>> qrels = new TreeMap<>();
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            String[] splits = line.split( "\\s+" );
            String qid = splits[ 0 ];
            String docno = splits[ 2 ];
            qrels.putIfAbsent( qid, new TreeSet<>() );
            if ( Integer.parseInt( splits[ 3 ] ) > 0 ) {
                qrels.get( qid ).add( docno );
            }
        }
        reader.close();
        return qrels;
    }

    /**
     * Load a TREC-format relevance judgment (qrels) file (such as "qrels_robust04" in HW2).
     *
     * @param f A qrels file.
     * @return A map storing the set of judged non-relevant documents for each qid.
     * @throws IOException
     */
    public static Map<String, Set<String>> loadNrels( File f ) throws IOException {
        Map<String, Set<String>> qrels = new TreeMap<>();
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            String[] splits = line.split( "\\s+" );
            String qid = splits[ 0 ];
            String docno = splits[ 2 ];
            qrels.putIfAbsent( qid, new TreeSet<>() );
            if ( Integer.parseInt( splits[ 3 ] ) <= 0 ) {
                qrels.get( qid ).add( docno );
            }
        }
        reader.close();
        return qrels;
    }

    public static Map<String, Set<String>> loadNrels( String f ) throws IOException {
        return loadNrels( new File( f ) );
    }

    /**
     * Load a query file (such as "queries_robust04" in HW2).
     *
     * @param f A query file.
     * @return A map storing the text query for each qid.
     * @throws IOException
     */
    public static Map<String, String> loadQueries( String f ) throws IOException {
        return loadQueries( new File( f ) );
    }

    /**
     * Load a query file (such as "queries_robust04" in HW2).
     *
     * @param f A query file.
     * @return A map storing the text query for each qid.
     * @throws IOException
     */
    public static Map<String, String> loadQueries( File f ) throws IOException {
        Map<String, String> queries = new TreeMap<>();
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            String[] splits = line.split( "\t" );
            String qid = splits[ 0 ];
            String query = splits[ 1 ];
            queries.put( qid, query );
        }
        reader.close();
        return queries;
    }

}
