import java.util.ArrayList;

public class count_support {
	
	//Function adds to the count of the number of times each k-itemset in C_k appears in a transaction t in the database
	//Also counts the occurrence frequency of each item in transaction t 
	//Finally, it trims the items in the transaction that do not appear in at least k candidate k-itemsets 
	//INPUT:  transaction t, Candidate k-itemsets C_k, index k 
	//OUTPUT: trimmed transaction t_trim
	public void count_support(String t, ArrayList<String> Ck, int k) {
		//For each itemset c in Ck such that c is a subset of t
			//BEGIN
				//Increase the TOTAL count of that itemset by one
		        //For each item i in c, increase the LOCAL count of that item by one 
			//END
		//For each item i in transaction t, if its LOCAL count is >= k, 
		//then add that item to the trimmed transaction t_trim
		//Return t_trim 
	}
}
