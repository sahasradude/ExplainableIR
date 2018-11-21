import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Metrics {
    public static double nDCG(double dcg, ArrayList<Integer> rels_sorted, int n ) {
        double idcg = 0.0;
        int count = 0;
        int numrels = 0;
        for (Integer rel:rels_sorted) {

            count++;
            if (count >= n + 1){
                break;
            }
            if (rel > 0){
                idcg += (double) (Math.pow(2, rel)-1)*Math.log(2) / Math.log(count+1);
                numrels++;
            }
        }
        if (numrels ==0)
            return 0;
        return dcg / idcg;

    }
    public static double DCG(List<String> results, Map<String, Integer> qrel, int n ) {
        double dcg = 0.0;
        int count = 0;
        int numrels = 0;
        for (String docno: results){
            count++;
            if (count >= n + 1){
                break;
            }
            if (qrel.containsKey(docno) && qrel.get(docno) > 0){
                dcg += (double) (Math.pow(2, qrel.get(docno))-1)*Math.log(2) / (Math.log(count+1));
                numrels++;
            }
        }
        if (numrels ==0)
            return 0;
        return dcg;
    }
    public static double avgPrec(List<String> results, Map<String, Integer> qrel, int n ) {
        double numrel = 0;
        double sumprec = 0.0;
        int count = 0;
        int tot_rels = 0;
        for (Integer rel_val:qrel.values()) {
            if (rel_val > 0)
                tot_rels++;

        }
        for ( String docno : results ) {
            count++;
            if ( count >= n+1 ) {
                break;
            }
            if ( qrel.containsKey( docno ) && qrel.get(docno) > 0) {
                numrel++;
                sumprec += ( numrel / (count) );
            }
        }
        if (numrel == 0)
            return 0;
        return sumprec / tot_rels;
    }
    public static double[] makeArray(Map<String, Double> vals) {

        Double[] ap_arr = vals.values().toArray(new Double[0]);
        double[] tempArray = new double[ap_arr.length];
        int i = 0;
        for(Double d : ap_arr) {
            tempArray[i] = (double) d;
            i++;
        }
        return tempArray;
    } 
}
