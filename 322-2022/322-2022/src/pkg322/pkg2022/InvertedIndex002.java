package pkg322.pkg2022 ;

import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry2 {
    public int doc_freq = 0;
    public int term_freq = 0;
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("* Number of terms = " + index.size());
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
                            index.put(word, new DictEntry2());
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
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }


    HashSet<Integer> intersectAND(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
        if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();
        while (itP1.hasNext() && itP2.hasNext()) {
            if (docId1 == docId2) {
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            }
            else if (docId1 < docId2) {
                if (itP1.hasNext())
                    docId1 = itP1.next();
                else return answer;

            } else {
                if (itP2.hasNext())
                    docId2 = itP2.next();
                else return answer;

            }
        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }
        return answer;
    }
    //-----------------------------------------------------------------------

    HashSet<Integer> intersectOR(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
        while (itP1.hasNext() || itP2.hasNext()) {
            if (itP1.hasNext()){
                docId1 = itP1.next();
                answer.add(docId1);
            }

            if (itP2.hasNext()) {
                docId2 = itP2.next();
                answer.add(docId2);
            }
        }

        return answer;
    }

    HashSet<Integer> intersectNot(HashSet<Integer> pL1,String[]files) {
        HashSet<Integer> answer = new HashSet<Integer>();;
        Iterator<Integer> itP1 = pL1.iterator();
        int docId1 = 0, docId2 = 0;
        //String [] cur;
        ArrayList<String> cur = new ArrayList<String>();
        while (itP1.hasNext()) {
            cur.add(sources.get(itP1.next()));
        }
        int i=0;
        for (String a : files){
            if(!cur.contains(a)){
                answer.add(i);
            }
            i++;
        }

        return answer;

    }

    public String find_01AND(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        // 3- apply the algorithm
        HashSet<Integer> answer = intersectAND(pL1, pL2);
        System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
//            0--> C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\1.txt"
//            1--> C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\2.txt"
//            2--> C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\3.txt"
//            3--> C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\4.txt"
//            4--> C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\5.txt"
        }
        //
        return result;
    }

    public String find_01OR(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        // 3- apply the algorithm
        HashSet<Integer> answer = intersectOR(pL1, pL2);
        System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

    public String find_01NOT(String phrase , String[]files) {
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

        // 3- apply the algorithm
        HashSet<Integer> answer = intersectNot(pL1,files);
        System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

    public void execute (String parse,String [] files){
        //not ahmed or ans
        HashMap<String,HashSet<Integer>> mapnot = new HashMap<>();
        HashMap<String,HashSet<Integer>> mapand = new HashMap<>();
        HashMap<String,HashSet<Integer>> mapor = new HashMap<>();
        //map.put("vishal", 10);
        ArrayList<String> arr = new ArrayList<String>();
        String[] words = parse.split(" ");
        for(int i=0;i<words.length;i++){
            arr.add(words[i]);
        }
        if(arr.contains("not")){
            for(int i=1;i<words.length;i++){
                if(words[i-1].equals("not")){
                    HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                    HashSet<Integer> answerNOT=intersectNot(pL1,files);
                    arr.remove(i-1);
                    mapnot.put(words[i],answerNOT);
                }
            }
            if(!arr.contains("and")&&!arr.contains("or")){
                String result = "";
                System.out.println("Found in: ");
                int si=mapnot.size();
                List<String> list = new ArrayList<String>(mapnot.keySet());
                String last=list.get(si-1);
                for (int num : mapnot.get(last)) {
                    result += "\t" + sources.get(num) + "\n";
                }
                System.out.println(result);
                return;
            }
        }
        if(arr.contains("and")){
            for(int i=1;i<arr.size();i++){
                HashSet<Integer> pL1,pL2;
                if(arr.get(i).equals("and")){
                    if(mapnot.keySet().contains(arr.get(i-1))){
                        pL1=mapnot.get(arr.get(i-1));
                    }
                    else if(mapand.keySet().contains(arr.get(i-1))){
                        pL1=mapand.get(arr.get(i-1));
                    }
                    else{
                        pL1 = new HashSet<Integer>(index.get(arr.get(i-1).toLowerCase()).postingList);
                    }
                    if(mapnot.keySet().contains(arr.get(i+1))){
                        pL2=mapnot.get(arr.get(i+1));
                    }
                    else if(mapand.keySet().contains(arr.get(i+1))){
                        pL2=mapand.get(arr.get(i+1));
                    }
                    else{
                        pL2 = new HashSet<Integer>(index.get(arr.get(i+1).toLowerCase()).postingList);
                    }
                    HashSet<Integer> answerand=intersectAND(pL1,pL2);
                    String new1 = arr.get(i+1).concat(arr.get(i-1));
                    mapand.put(new1,answerand);
                    arr.set(i+1,new1);
                    arr.remove(i-1);
                    arr.remove(i-1);
                }
            }
            if(!arr.contains("or")){
                String result = "";
                System.out.println("Found in: ");
                int si=mapand.size();
                List<String> list = new ArrayList<String>(mapand.keySet());
                String last=list.get(si-1);
                for (int num : mapand.get(last)) {
                    result += "\t" + sources.get(num) + "\n";
                }
                System.out.println(result);
            }
        }
        if(arr.contains("or")){
            for(int i=1;i<arr.size();i++){
                HashSet<Integer> pL1,pL2;
                if(arr.get(i).equals("or")){
                    if(mapnot.keySet().contains(arr.get(i-1))){
                        pL1=mapnot.get(arr.get(i-1));
                    }
                    else if(mapand.keySet().contains(arr.get(i-1))){
                        pL1=mapand.get(arr.get(i-1));
                    }
                    else if(mapor.keySet().contains(arr.get(i-1))){
                        pL1=mapor.get(arr.get(i-1));
                    }
                    else{
                        pL1 = new HashSet<Integer>(index.get(arr.get(i-1).toLowerCase()).postingList);
                    }
                    if(mapnot.keySet().contains(arr.get(i+1))){
                        pL2=mapnot.get(arr.get(i+1));
                    }
                    else if(mapand.keySet().contains(arr.get(i+1))){
                        pL2=mapand.get(arr.get(i+1));
                    }
                    else if(mapor.keySet().contains(arr.get(i-1))){
                        pL2=mapor.get(arr.get(i-1));
                    }
                    else{
                        pL2 = new HashSet<Integer>(index.get(arr.get(i+1).toLowerCase()).postingList);
                    }
                    HashSet<Integer> answeror=intersectOR(pL1,pL2);
                    String new1 = arr.get(i+1).concat(arr.get(i-1));
                    mapor.put(new1,answeror);
                    arr.set(i+1,new1);
                    arr.remove(i-1);
                    arr.remove(i-1);
                }
            }
            String result = "";
            System.out.println("Found in: ");
            int si=mapor.size();
            List<String> list = new ArrayList<String>(mapor.keySet());
            String last=list.get(si-1);
            for (int num : mapor.get(last)) {
                result += "\t" + sources.get(num) + "\n";
            }
            System.out.println(result);
        }
    }
//-----------------------------------------------------------------------
}

//=====================================================================
public class InvertedIndex002 {

    public static void main(String args[]) throws IOException {
        Index2 index = new Index2();
        String phrase = "";
        String[] files={
                "C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\1.txt",
                "C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\2.txt",
                "C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\3.txt",
                "C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\4.txt",
                "C:\\Users\\HP-PC\\IdeaProjects\\IR\\Docs\\5.txt"
        };
        index.buildIndex(files);
        String Query;
        Scanner in= new Scanner(System.in);
        while (true)
        {
            System.out.println("Enter the Query");
            Query=in.nextLine();
            index.execute(Query,files);
            System.out.println("Do you want to Continue ?");
            System.out.println("1- Yes");
            System.out.println("2- No");
            int choice = in.nextInt();
            if (choice==2)
                break;
        }

        //System.out.print(index.find_01AND("bahader rania"));
    }
}
