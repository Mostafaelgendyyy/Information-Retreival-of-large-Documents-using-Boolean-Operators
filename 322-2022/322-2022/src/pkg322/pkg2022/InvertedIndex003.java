/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg322.pkg2022;

/**
 *
 * @author ehab
 */
/*
 * InvertedIndex - Given a set of text files, implement a program to create an 
 * inverted index. Also create a user interface to do a search using that inverted 
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory. 
 * 

 */
import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry3 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry3() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index3 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry3> index; // THe inverted index
    //--------------------------------------------

    Index3() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry3>();
    }

    //---------------------------------------------
    public void printPostingList(HashSet<Integer> hset) {
        Iterator<Integer> it2 = hset.iterator();
        while (it2.hasNext()) {
            System.out.print(it2.next() + ", ");
        }
        System.out.println("");
    }
    
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            DictEntry3 dd = (DictEntry3) pair.getValue();
//            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
//            //it.remove(); // avoids a ConcurrentModificationException
//             printPostingList(dd.postingList);
//        }
        System.out.println("------------------------------------------------------");
        System.out.println("*****    Number of terms = " + index.size());
        System.out.println("------------------------------------------------------");

    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry3());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
         printDictionary();
    }

    //--------------------------------------------------------------------------
    // query inverted index
    // takes a string of terms as an argument
    public String find(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        for (String word : words) {
            res.retainAll(index.get(word).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");
            return "";
        }
       // String result = "Found in: \n";
        for (int num : res) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

    //----------------------------------------------------------------------------  
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
//        INTERSECT ( p1 , p2 )
//          1 answer ←   {}
        // answer = 
//          2 while p1  != NIL and p2  != NIL
        if (itP1.hasNext()) 
            docId1 = itP1.next();        
        if (itP2.hasNext()) 
            docId2 = itP2.next();
        
        while (itP1.hasNext() && itP2.hasNext()) {

//          3 do if docID ( p 1 ) = docID ( p2 )
            if (docId1 == docId2) {
//          4   then ADD ( answer, docID ( p1 ))
//          5       p1 ← next ( p1 )                    
//          6       p2 ← next ( p2 )
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            } //          7   else if docID ( p1 ) < docID ( p2 )
            //          8        then p1 ← next ( p1 )
            else if (docId1 < docId2) {               
                if (itP1.hasNext()) 
                    docId1 = itP1.next();
                 else return answer;
                
            } else {
//          9        else p2 ← next ( p2 )
                if (itP2.hasNext()) 
                    docId2 = itP2.next();
                else return answer;
                
            }
          
        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }

//          10 return answer
        return answer;
    }
    //-----------------------------------------------------------------------   

    public String find_01(String phrase) { // 2 term phrase  2 postingsLists
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list/        result = "Found in: \n";
        ////        for (int num : answer) {
        ////            //System.out.println("\t" + sources.get(num));
        ////            result += "\t" + sources.get(num) + "\n";
        ////        }
        //        return
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
      //  printPostingList(pL1);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
    //    printPostingList(pL2);

        // 3- apply the algorithm
        HashSet<Integer> answer = intersect(pL1, pL2);
        //printPostingList(answer);
       return result;
    }
//-----------------------------------------------------------------------         

    public String find_02(String phrase) { // 3 lists
 
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        //printPostingList(pL1);
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        //printPostingList(pL2);
        HashSet<Integer> answer1 = intersect(pL1, pL2);
        //printPostingList(answer1);
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);        
        //printPostingList(pL3);
        HashSet<Integer> answer2 = intersect(pL3, answer1);
               // printPostingList(answer2);

//        result = "Found in: \n";
        for (int num : answer2) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }        
        return result;

    }
    //-----------------------------------------------------------------------         

    public String find_03(String phrase) { // any mumber of terms non-optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        int i = 1;
        while (i < len) {
            res = intersect(res, index.get(words[i].toLowerCase()).postingList);
            i++;
        }
        for (int num : res) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }
    
        //-----------------------------------------------------------------------         
    String[] rearrange(String[] words, int[] freq, int len) {
        boolean sorted = false;
        int temp;
        String sTmp;
        for (int i = 0; i < len - 1; i++) {
            freq[i] = index.get(words[i].toLowerCase()).doc_freq;
        }
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < len - 1; i++) {
                if (freq[i] > freq[i + 1]) {
                    temp = freq[i];
                    sTmp = words[i];
                    freq[i] = freq[i + 1];
                    words[i] = words[i + 1];
                    freq[i + 1] = temp;
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    //-----------------------------------------------------------------------         
    public String find_04(String phrase) { // any mumber of terms optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        //int [] freq = new int[len];
        words = rearrange(words, new int[len], len);
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        int i = 1;
        while (i < len) {
            res = intersect(res, index.get(words[i].toLowerCase()).postingList);
            i++;
        }
        for (int num : res) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //-----------------------------------------------------------------------         

    public void compare(String phrase) { // optimized search 
        long iterations = 5000000;
        String result = "";
        long startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find(phrase);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) elapsed = " + estimatedTime + " ms.");
        //-----------------------------      
        System.out.println(" result = " + result);
        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_03(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_03 non-optimized intersect  elapsed = " + estimatedTime + " ms.");
        System.out.println(" result = " + result);
//        //-----------------------------       
        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_04(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_04 optimized intersect elapsed = " + estimatedTime + " ms.");
        System.out.println(" result = " + result);
    }
}

//=====================================================================
public class InvertedIndex003 {

    public static void main(String args[]) throws IOException {
        Index3 index = new Index3();
        String phrase = "";
 /**/ 
        index.buildIndex(new String[]{
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\500.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\501.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\502.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\503.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\504.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\100.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\101.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\102.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\103.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\104.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\105.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\106.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\107.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\108.txt",
            "E:\\FCAI MATERIALS\\Faculty\\IR Ass\\docs\\109.txt"
        });  
  /**/        
        
 
   /**/     
      //   index.find_01("agile cost");
     //    index.compare("and agile");
     //    System.out.println(" result = " +index.find_02("different system agile"));
         index.compare("agile and can ehab should only");
//        index.compare("different system should results are in cost and can only computing elements");
//        do {
//            System.out.println("Print search phrase: ");
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            phrase = in.readLine();
//            System.out.println(index.find(phrase));
//        } while (!phrase.isEmpty());
    }
}
