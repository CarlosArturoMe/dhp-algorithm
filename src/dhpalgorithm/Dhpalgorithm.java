
package dhpalgorithm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.Arrays;
//import static java.util.stream.Collectors.toCollection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.paukov.combinatorics3.Generator;

/**
 *
 * @author carlos
 */
public class Dhpalgorithm {
    
    private static final int S = 2;
    private static final int LARGE = 1;
    
    public Dhpalgorithm(){    
        ArrayList<String> listD = new ArrayList<>();
        //obtain transactions from DB
        try {
            listD = getTransactionsDB();
        } catch (SQLException ex) {
            Logger.getLogger(Dhpalgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(listD);
        //obtain a set from listD
        Set<String> cSet = new HashSet<>();
        for(String t : listD){
            String[] elements = t.split(",");
            for(String val : elements){
                cSet.add(val);
            }
        }
        //System.out.println(cSet);
        
        /* Part 1 */
        // Creates an empty TreeMap 
        TreeMap<String, Integer> hmap = new TreeMap<>();
        //HashMap<String, Integer> h = new HashMap<>();
        
        // creating a hash table, buckets of H2 to zero
        HashMap<String, Integer> h2 = new HashMap<>();
        for(String val : cSet){
            for(String compVal : cSet){
                if(!val.equals(compVal)){
                    if (h2.get(val+compVal) == null && h2.get(compVal+val) == null){
                    h2.put(val+compVal,0);
                    }
                }
            }  
        }
        //System.out.println("after filling: " + h2);
        for(String t : listD){
            // count 1 items ocurrence in h
            String[] elements = t.split(",");
            for(String val : elements){
                Integer c = hmap.get(val);
                // If this is first occurrence of element    
                if (hmap.get(val) == null){
                    hmap.put(val,1); 
                }else {
                // If elements already exists in hash map 
                    hmap.put(val, ++c); 
                }
                // count of 2-subsets
                for(String valComp : elements){
                    if(!val.equals(valComp)){
                        if(h2.get(val+valComp)!=null){
                            int countH2 = h2.get(val+valComp);
                            h2.replace(val+valComp,++countH2);
                        }
                    }
                        
                }
            }
        }
        
        //obtain L1 from values that satify c.count >= S
        List<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
        ArrayList<String> l1 = new ArrayList<>();
        for(String key : hmap.navigableKeySet()){
            int c = hmap.get(key);
            if(c>=S){
                l1.add(key);
            }
        }
        l.add(new ArrayList<>());
        l.add(new ArrayList<>());
        l.add(1,l1);
        //System.out.println("L1 candidates: "+l1);
        
        /* Part 2*/
        int k = 2;
        List<List<List<String>>> c = new ArrayList<List<List<String>>>();
        List<ArrayList<String>> d = new ArrayList<ArrayList<String>>();
        List<HashMap<String, Integer>> h = new ArrayList<HashMap<String, Integer>>();
        c.add(new ArrayList<>());
        c.add(new ArrayList<>());
        c.add(new ArrayList<>());
        d.add(new ArrayList<>());
        d.add(new ArrayList<>());
        d.add(new ArrayList<>());
        d.add(k,listD);
        h.add(new HashMap<>());
        h.add(new HashMap<>());
        h.add(new HashMap<>());
        h.add(k,h2);
        
        
        c.add(k,genCandidate(l.get(k-1),h.get(k),k));
        //set all the buckets of H k+1 to zero
        h.add(new HashMap<>());
        d.add(new ArrayList<>());
        //System.out.println(d.get(k));
        for(String t : d.get(k)){
            ArrayList<String> tcup = countSupport(t,c.get(k),k);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //psvm  
        Dhpalgorithm dhp = new Dhpalgorithm();
    }
    
    public void validateKComb(List<String> subset, List<List<String>> arr, HashMap<String, Integer> h, int k){
        //System.out.println(subset);
        int count = 0;
        String key = "";
        for(int i =0; i<subset.size(); i++){
            key += subset.get(i);
        }
        //System.out.println(key + " soporte: ");
        //System.out.println(h.get(key));
        if(h.get(key) != null && h.get(key) >= S){
            //System.out.println("Key añadida!");
            arr.add(subset);
        }
        
    }
    
    /**
     * @param l array with large itemsets (L_k-1)
     * @param h hashmap H_k  
     * @param c array of candidates to generate
     * @param k magnitude of combinations
     */
    public List<List<String>> genCandidate(ArrayList<String> l, HashMap<String, Integer> h, int k){
        //for all combinations of l1 where k-2 = 0
        //System.out.println(h);
        //System.out.println(l);
        List<List<String>> arr = new ArrayList<List<String>>();
        /*
        List<List<String>> list = Generator
        .combination(l)
        .simple(k)
        .stream()
        .collect(Collectors.<List<String>>toList());
        */
        //System.out.println(list);
        Generator.combination(l)
       .simple(k)
       .stream()
       .forEach(comb -> validateKComb(comb,arr,h,k)
       );
        /*
        for(int i = 0; i<l.size(); i++){
            for(int j = 0; j<l.size(); j++){
                if(h.get(l.get(i)+l.get(j)) != null){
                    if(h.get(l.get(i)+l.get(j)) >= S){
                        c.add(l.get(i)+l.get(j));
                    }
                }
            }
        }
        */
        //System.out.println(arr);
        return arr;
    }
    
    /**
     * @param t transaction of DB
     * @param c array with candidates, must be array of strings
     * @return t' 
     */
    public ArrayList<String> countSupport(String t, List<List<String>> c, int k){
        ArrayList<String> tprime = new ArrayList<>();
        //System.out.println("Candidates: ");
        //System.out.println(c);
        String[] elements = t.split(",");
        //System.out.println(elements);
        
        //int [] a = new int[k];
        HashMap<List<String>, Integer> candArrCount = new HashMap<>();
        HashMap<String, Integer> cCount = new HashMap<>();
        
        //for(int i=0; i<elements.length; i++){
        //for(String transaction : elements){ 
        
        for(List<String> candidateArr : c){
            //System.out.println("transaction: "+transaction);
            //System.out.println("candidate: "+candidateArr);
            boolean [] isSubset = new boolean[candidateArr.size()];
            //for(String candidate : candidateArr){
            for(int i =0; i<candidateArr.size();i++){
                for(String transaction : elements){ 
                    if (transaction.equals(candidateArr.get(i))){
                        isSubset[i] = true;
                        break;
                    }
                }
                //bool false
            }
            boolean isSubsetArr = true;
            for(int i=0; i<isSubset.length; i++){
                if(!isSubset[i]){
                    isSubsetArr = false;
                    break;
                }
            }
            if(isSubsetArr){
                System.out.println("candidate is subset of transaction.");
                if (candArrCount.get(candidateArr) == null){
                    candArrCount.put(candidateArr,1);
                }else {
                    int count = candArrCount.get(candidateArr);
                    candArrCount.put(candidateArr, ++count); 
                }
                for(String candidate : candidateArr){
                    if (cCount.get(candidate) == null){
                        cCount.put(candidate,1);
                    }else {
                        int count = cCount.get(candidate);
                        cCount.put(candidate, ++count); 
                    }
                }
            }
        }
        for(String transaction : elements){
            if(cCount.get(transaction)!=null && cCount.get(transaction) >= k){
                tprime.add(transaction);
            }
        }        
        System.out.println(tprime);
        return tprime;
    }
    
    public static ArrayList<String> getTransactionsDB() throws SQLException {
        ArrayList<String> listIds = new ArrayList<>();
        try{  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/ecobici","root","12345678");
            Statement stmt=con.createStatement();  
            ResultSet rs=stmt.executeQuery(
                "SELECT GROUP_CONCAT(Ciclo_Estacion_Retiro) as Estaciones_Retiro, Fecha_Retiro,\n" +
                "TIME_FORMAT( CONVERT(Hora_Retiro, TIME),'%H %i') as Hora_Retiro_N\n" +
                "FROM registrobicis \n" +
                "GROUP BY Hora_Retiro_N, Fecha_Retiro\n" +
                "LIMIT 100");
            while(rs.next())  
                //System.out.println(rs.getString("Estaciones_Retiro"));
               listIds.add(rs.getString("Estaciones_Retiro"));
            con.close();  
        }catch(Exception e){ 
            //sout
            System.out.println(e);
        }
        return listIds;
    }
    
    
}
