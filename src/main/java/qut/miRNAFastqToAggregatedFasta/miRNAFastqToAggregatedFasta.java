/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package qut.miRNAFastqToAggregatedFasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author an
 */
public class miRNAFastqToAggregatedFasta {

    public static void main(String[] args) throws IOException {
        String dir, output_fn;
        if (args.length != 2) {
            System.out.println("Usage: java -cp target/miRNAFastqToAggregatedFasta-1.0.jar qut.miRNAFastqToAggregatedFasta.miRNAFastqToAggregatedFasta fastq_folder prefix_output_filename");
            return;
        }
        dir = args[0];
        output_fn = args[1];
        miRNAFastqToAggregatedFasta fTf = new miRNAFastqToAggregatedFasta();
        Map<String, Integer> results = new TreeMap<>();
        File folder = new File(dir);
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                String fn = file.getCanonicalPath();
                if (fn.endsWith(".fq") || fn.endsWith(".fastq") || fn.endsWith(".fq.gz") || fn.endsWith(".fastq.gz")) {
                    System.out.println(fn);
                    String adapter = fTf.getAdapter(fn);
                    System.out.println("adapter=" + adapter);
                    if (!adapter.isEmpty()) {
                        fTf.toFa(fn, adapter, results);
                    }
                }
            }
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(output_fn+".aggregated.fa"));
        int rec_no = 0;
        for(String seq : results.keySet()){
            bw.write(">t"+(rec_no++)  +"\t"+results.get(seq)+ "\n");
            bw.write(seq+"\n");
        }
        bw.close();
    }
    
    void toFa(String fn, String adapter, Map<String, Integer> results) throws IOException{
        BufferedReader br;
        if (fn.endsWith(".gz")) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fn))));
        } else {
            br = new BufferedReader(new FileReader(fn));
        }
        String[] find_adapter_from_1000 = new String[10000];
        String line;
        while((line = br.readLine()) != null){
            line = br.readLine();
            if(!line.contains("N")){
                int pos = getSeq(line, adapter);
                if((pos >= 18) && (pos < 25)){
                    String seq = line.substring(0, pos);
                    if(results.get(seq) == null){
                        results.put(seq, 1);
                    } else {
                        results.put(seq, results.get(seq) + 1);
                    }
                }
            }
            br.readLine();
            br.readLine();
        }
        br.close();
    }
    
    int getSeq(String line, String adapter) {
        int idx = line.indexOf(adapter);
        if (idx > -1) {
            return idx;
        }
        int overlap_pos = OverlapPosition(line, adapter);
        return overlap_pos;
    }
    
    int OverlapPosition(String left, String right){
        for(int i = 1; i < left.length(); i++){
            if(right.startsWith(left.substring(i))){
                return i;
            }
        }
        return -1;
    }
    
    String getAdapter(String fn) throws IOException{
        BufferedReader br;
        if (fn.endsWith(".gz")) {
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(fn))));
        } else {
            br = new BufferedReader(new FileReader(fn));
        }
        String[] find_adapter_from_1000 = new String[10000];
        String line;
        int rec_no = 0;
        while((line = br.readLine()) != null){
            if(!line.startsWith("@")){
                System.err.println(fn+" is not fastq format");
                System.exit(0);
            }
            line = br.readLine();
            if(!line.contains("N")){
                find_adapter_from_1000[rec_no++] = line;
            }
            line = br.readLine();
            if(!line.startsWith("+")){
                System.err.println(fn+" is not fastq format");
                System.exit(0);
            }
            br.readLine();
            if(rec_no == find_adapter_from_1000.length){
                break;
            }
        }
        br.close();
        Map<String, Integer> adapter = new TreeMap<>();
        String candidate = "";
        int biggest_count = 0;
        for (int line_no = 0; line_no < 20; line_no++) {
            for (int i = 0; i < Math.min(30, find_adapter_from_1000[line_no].length()); i++) {
//System.out.println("i="+i);               
                String test_str = find_adapter_from_1000[line_no].substring(i, Math.min(find_adapter_from_1000[line_no].length(), i + 15));
                int num_match = 0;
                for (String find_adapter_from_10001 : find_adapter_from_1000) {
//System.out.println("find_adapter_from_10001="+find_adapter_from_10001);               
                    if ((find_adapter_from_10001.contains(test_str)) && (find_adapter_from_10001.indexOf(test_str) < 35)) {
                        num_match++;
                    }
                }
                if(num_match > find_adapter_from_1000.length * 0.5){
                    String seq = find_adapter_from_1000[line_no].substring(i, Math.min(i + 19, find_adapter_from_1000[line_no].length()));
                    if(adapter.get(seq) == null){
                        adapter.put(seq, 1);
                    }else{
                        adapter.put(seq, adapter.get(seq)+1);
                    }
                    if(adapter.get(seq) > biggest_count){
                        candidate = seq;
                        biggest_count = adapter.get(seq);
                    }
                }
            }
        }
        if(adapter.isEmpty()){
            return "";
        }
        System.out.println("biggest_count="+biggest_count+" candidate="+candidate);
        int num_overlap = 0;
        for (String seq : adapter.keySet()) {
            if (!candidate.equals(seq) && !isOverlap(candidate, seq)) {
                System.err.println("seq="+seq+" is not subset of " + candidate);
            }else{
                num_overlap++;
            }
        }
        if(num_overlap > adapter.size() * 0.5){
            return candidate;
        } else {
            return "";
        }
    }
    boolean isOverlap(String left, String right){
        for(int i = 1; i < left.length() / 2; i++){
            if(right.startsWith(left.substring(i))){
                return true;
            }
        }
        return false;
    }
}
