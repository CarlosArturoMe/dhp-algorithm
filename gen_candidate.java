
public class gen_candidate {
	
	//INPUT:  Large itemets Lk-1, Hash table Hk
	//OUTPUT: Candidate itemsets Ck
	public void gen_candidate(){
		//Declare Ck as an empty set
		//For all itemsets i in Lk-1
			//For all itemsets i+1 in Lk-1
				//For elements e in i
					//If e in i+1
						//count++
				//If count == k-2
				//Merge i and i+1 in a set c (no duplicates)
				//If c achieves minimum support and c not in Ck 
					// Add it to Ck
		//Return Ck
	}

}
