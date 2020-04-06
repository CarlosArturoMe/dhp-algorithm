
public class make_hasht {

	//Function that adds k+1-itemsets to the hash table Hk+1
	//This is done only if all the k-subsets of each k+1-itemset achieves minimum support
	//Also, for each item i in the hashed k+1-itemset, the LOCAL count of i is increased by 1
	//Finally, it trims the items in the transaction that do not appear in at least 1 hashed k+1-itemsets
	//INPUT:  transaction t, Hash table Hk, index k, Hash table Hk+1
	//OUTPUT: trimmed transaction t_trim
	public void make_hasht() {
		//For all k+1-subsets x of transaction t
			//If all k-subsets of x achieve minimum support in Hk
			 //BEGIN
			 	//Add x to the hash table Hk+1 (if already in hash table, increase value by 1)
				//For each item i in x, increase the LOCAL count of that item by 1
			 //END
		//For all items in transaction t, if its LOCAL count is greater than 0, then add it to
		//the trimmed transaction t_trim
		//Return t_trim
	}
	
}
