
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

/**
 *
 * @author carlos
 */
public class Dhpalgorithm {
    
    private static final int S = 5;
    private static final int LARGE = 5;
    
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
                    h2.put(val+compVal,0);
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
              
                for(String valComp : elements){
                    if(!val.equals(valComp)){
                        int countH2 = h2.get(val+valComp);
                        h2.replace(val+valComp,++countH2);
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
        List<ArrayList<String>> c = new ArrayList<ArrayList<String>>();
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
        
        
        c.add(k,genCandidate(l.get(k-1),h.get(k),c.get(k)));
        //set all the buckets of H k+1 to zero
        h.add(new HashMap<>());
        d.add(new ArrayList<>());
        //System.out.println(d.get(k));
        for(String t : d.get(k)){
            countSupport(t,c.get(k),k);
            
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //psvm  
        Dhpalgorithm dhp = new Dhpalgorithm();
    }
    
    public ArrayList<String> genCandidate(ArrayList<String> l, HashMap<String, Integer> h, ArrayList<String> c){
        c = new ArrayList<>();
        for(int i = 0; i<l.size(); i++){
            for(int j = 0; j<l.size(); j++){
                if(h.get(l.get(i)+l.get(j)) != null){
                    if(h.get(l.get(i)+l.get(j)) >= S){
                        c.add(l.get(i)+l.get(j));
                    }
                }
            }
        }
        //System.out.println(c);
        return c;
    }
    
    /**
     * @param t transaction of DB
     * @param c array with candidates
     * @return t' 
     */
    public ArrayList<String> countSupport(String t, ArrayList<String> c, int k){
        ArrayList<String> tprime = new ArrayList<>();
        //System.out.println(c);
        String[] elements = t.split(",");
        System.out.println(elements.length);
        int [] a = new int[k];
        for(String candidate : c){
            int cCount = 0;
            //HashMap<String, Integer> h2 = new HashMap<>();
            for(String transaction : elements){
                System.out.println("transaction: "+transaction);
                System.out.println("candidate: "+candidate);
                if (transaction.contains(candidate)){
                    System.out.println("transaction contained in candidate.");
                    cCount ++;
                    for(int j=1; j<=k; j++){
                        a[j]++;
                    }
                }
            }
        }
        for(int i=0,j=0;i<elements.length; i++){
            tprime.add("");
            if(a[i] >= k){
                tprime.add(j,elements[i]);
                j++;
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
