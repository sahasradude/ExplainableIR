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
    public static Map<String, Map<String, Integer>> loadQrels( String f ) throws IOException {
        return loadQrels( new File( f ) );
    }

    /**
     * Load a TREC-format relevance judgment (qrels) file (such as "qrels_robust04" in HW2).
     *
     * @param f A qrels file.
     * @return A map storing the set of relevant documents for each qid.
     * @throws IOException
     */
    public static Map<String, Map<String, Integer>> loadQrels( File f ) throws IOException {
        Map<String, Map<String, Integer>> qrels = new HashMap<>();
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( f ), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            String[] splits = line.split( "\\s+" );
            String qid = splits[ 0 ];
            String docno = splits[ 2 ];
            qrels.putIfAbsent( qid, new HashMap<>() );
            int relval;
            if ( (relval = Integer.parseInt( splits[ 3 ] )) > 0 ) {
                qrels.get( qid ).put( docno, relval);
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

    /**
     * given the ranking of of a model for all queries, find the results which matter the most to the scores of this ranking for each query
     */
    static class Result{
        public double score;
        public String docno;
        Result() {
            this.docno = "";
            this.score = Double.MIN_VALUE;
        }
        Result(String docno, double score){
            this.docno = docno;
            this.score = score;
        }
    }
    public static ArrayList<String> findTopResults(ArrayList<String> results,  Map<String, Integer> qrel ,int top, int k) {
        ArrayList<String> finalrank = new ArrayList<>();
        PriorityQueue<Result> pq = new PriorityQueue(50, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                if (o1.score > o2.score){
                    return 1;
                }
                else if (o2.score == o1.score){
                    return 0;
                }
                return -1;
            }
        });
        if (top < results.size()){
            top = results.size();
        }
        double AP = Metrics.avgPrec(results, qrel, top);
        for (int i = 0; i < k; i++) {
           String removed = results.remove(i);
           double APnew = Metrics.avgPrec(results, qrel, k);
           double deltaAP = AP - APnew;
           Result r = new Result(removed, deltaAP);
           if(pq.size() < k){
               pq.add(r);
           }
           else {
               if (r.score > pq.peek().score) {
                   pq.poll();
                   pq.add(r);
               }
           }
           results.add(i, removed);
        }

        for (int i = 0; i < k; i++) {
            finalrank.add(pq.poll().docno);
        }
        return finalrank;

    }

}
