
import sys
import time
from itertools import chain, combinations
from collections import defaultdict
from optparse import OptionParser


def subsets(arr):
    return chain(*[combinations(arr, i + 1) for i, a in enumerate(arr)])

def return_items_with_min_support_p1(item_set, transaction_list, min_support, freq_set):
    _item_set = set()
    local_set = defaultdict(int)
    h2_set = defaultdict(int)

    for item in item_set:
        for transaction in transaction_list:
            if item.issubset(transaction):
                freq_set[item] += 1
                local_set[item] += 1
            trans_set = set()
            for transaction_comp in transaction:
                #print(transaction_comp)
                trans_set.add(frozenset((transaction_comp,)))
            #print("transaction: ",trans_set)
            two_subsets = join_set(trans_set, 2)
            #print("two_subsets: ",two_subsets)
            for element in two_subsets:
                #print("element to h2_set: ",element)
                h2_set[element] += 1

    for item, count in local_set.items():
        support = float(count)/len(transaction_list)
        #print(support)
        if support >= min_support:
                _item_set.add(item)
    #print("_item_set: ",_item_set)
    #print("h2_set: ",h2_set)
    return _item_set, h2_set

def return_items_with_min_support(item_set, transaction_list, min_support, freq_set):
    _item_set = set()
    local_set = defaultdict(int)

    for item in item_set:
        for transaction in transaction_list:
            if item.issubset(transaction):
                freq_set[item] += 1
                local_set[item] += 1

    for item, count in local_set.items():
            support = float(count)/len(transaction_list)
            #print(support)
            if support >= min_support:
                    _item_set.add(item)

    return _item_set


def join_set(item_set, length):
    #print("item_set join_set: ")
    #print(item_set)
    return set([i.union(j) for i in item_set for j in item_set if len(i.union(j)) == length])

def gen_candidate(l_set, h_set, k, min_support,transaction_list):
    candidates_with_support = set()
    #print("l_set: ",l_set)
    candidates = set([i.union(j) for i in l_set for j in l_set if len(i.intersection(j)) == k-2])
    #print("candidates: ",candidates)
    for c in candidates:
        #print("c: ",c)
        #if h_set[c] > 0:
            #print(h_set[c])
        support = float(h_set[c])/len(transaction_list)
        if support >= min_support:
            candidates_with_support.add(c)
    return candidates_with_support

def get_h_min_support_len(hash_set,min_support,transaction_list):
    support_count = 0
    for item, count in hash_set.items():
        support = float(count)/len(transaction_list)
        if support >= min_support:
            support_count+=1
    return support_count

def get_item_set_transaction_list(data_iterator):
    transaction_list = list()
    item_set = set()
    for record in data_iterator:
        #print("record: ",record)
        transaction = frozenset(record)
        transaction_list.append(transaction)
        for item in transaction:
            item_set.add(frozenset([item]))
    #print(item_set)
    #print(transaction_list)
    return item_set, transaction_list


def run_AprioriDHP(data_iter, min_support, min_confidence,large):
    item_set, transaction_list = get_item_set_transaction_list(data_iter)
    freq_set = defaultdict(int)
    large_set = dict()
    hash_set = dict()
    one_c_set, two_h_set = return_items_with_min_support_p1(item_set,transaction_list,min_support,freq_set)
    #print("one_c_set: ",one_c_set)
    #print("two_h_Set: ",two_h_set)
    current_l_set = one_c_set
    k = 2
    current_h_set = two_h_set.copy()
    hash_set[k] = two_h_set.copy()
    #while(get_h_min_support_len(hash_set[k],min_support,transaction_list) >= large):
    #print("current_h_set: ",current_h_set)
    current_c_set = gen_candidate(current_l_set, current_h_set ,k, min_support,transaction_list) # gen_candidate
    print("current_c_set: ",current_c_set)
    while(current_l_set != set([])):
        large_set[k-1] = current_l_set
        current_l_set = join_set(current_l_set, k) # new candidates
        currentCSet = return_items_with_min_support(current_l_set,
                                                transaction_list,
                                                min_support,
                                                freq_set)
        current_l_set = currentCSet
        k = k + 1

    def get_support(item):
        return float(freq_set[item])/len(transaction_list)

    to_ret_items = []
    for key, value in large_set.items():
        to_ret_items.extend([(tuple(item), get_support(item))
                           for item in value])
    #print "to_ret_items"
    #print to_ret_items
    to_ret_rules = []
    #print("large_set.items(): ")
    #print(list(large_set.items())[1:])
    #for key, value in large_set.items()[1:]:
    for key, value in list(large_set.items())[1:]:
        for item in value:
            _subsets = map(frozenset, [x for x in subsets(item)])
            #print "_subsets:"
            #print _subsets
            for element in _subsets:
                remain = item.difference(element)
                if len(remain) > 0:
                    confidence = get_support(item)/get_support(element)
                    if confidence >= min_confidence:
                        to_ret_rules.append(((tuple(element), tuple(remain)),
                                           confidence))
    return to_ret_items, to_ret_rules


def print_results(items, rules):
    #for item, support in sorted(items, key=lambda (item, support): support):
    for item, support in sorted(items, key=lambda t: t[1]):
        print ("item: %s , %.3f" % (str(item), support))
    print ("\nRULES:\n")
    #for rule, confidence in sorted(rules, key=lambda (rule, confidence): confidence):
    for rule, confidence in sorted(rules, key=lambda t: t[1]):
        pre, post = rule
        print("Rule: %s ==> %s , %.3f" % (str(pre), str(post), confidence))


def dataFromFile(fname):
    file_iter = open(fname, 'r',encoding='utf-8-sig')
    for line in file_iter:
        #print(line)
        line = line.strip().rstrip(',') #.replace('\xef\xbb\xbf','').replace('\ufeff1','')
        record = frozenset(line.split(','))
        #print("record: ",record)
        yield record


if __name__ == "__main__":
    opt_parser = OptionParser()
    opt_parser.add_option('-f', '--inputFile',
                         dest='input',
                         help='filename containing csv',
                         default=None)
    opt_parser.add_option('-s', '--min_support',
                         dest='minS',
                         help='minimum support value',
                         default=0.15,
                         type='float')
    opt_parser.add_option('-c', '--min_confidence',
                         dest='minC',
                         help='minimum confidence value',
                         default=0.6,
                         type='float')
    (options, args) = opt_parser.parse_args()

    in_file = None
    if options.input is None:
            in_file = sys.stdin
    elif options.input is not None:
            in_file = dataFromFile(options.input)
    else:
            print('No dataset filename specified, system with exit\n')
            sys.exit('System will exit')

    min_support = options.minS
    min_confidence = options.minC
    start_time = time.time()
    large = 2
    items, rules = run_AprioriDHP(in_file, min_support, min_confidence, large)
    #print ("Time to Execute apriori is : %s seconds " % (time.time() - start_time))
    print_results(items, rules)
