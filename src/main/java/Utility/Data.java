
package Utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
// This instance has: 3 different vehicles types, 52 time periods in time horizon

public class Data {
    public static Vector<Float> Costs_Data (File file,int m,int n) {
// this method reads input parameters from Data.txt and inserts all in a Vector
try{
BufferedReader reader = new BufferedReader(new FileReader(file));
String line = reader.readLine().trim();
String []elements1 = line.split("\\s+");
Vector<Float> c = new Vector<Float>();// this vector contains all costs for all vehicle types
for(int k=0;k<m;k++){
c.add(Float.parseFloat(elements1[k]));// add c_F^k 
}
line = reader.readLine().trim();// we move on the following line (the second) containing variable costs
 String []elements2 = line.split("\\s+");
for(int k=0;k<m;k++){
c.add(Float.parseFloat(elements2[k]));// add c_V^k 
}

line = reader.readLine().trim();// we move on the following line (the third) containing hiring costs
 String []elements3 = line.split("\\s+");
for(int k=0;k<m;k++){
c.add(Float.parseFloat(elements3[k]));// add c_H^k 
}
return c;
}


catch (IOException e) {e.printStackTrace();}
        return null;
}
   
  public static int[][] Vehicles_Requirements (File file,int m,int n) throws IOException {
int[][] v= new int[n][m];// create a matrix 52x3
BufferedReader reader = new BufferedReader(new FileReader(file));
String line = reader.readLine().trim();

 


// we are on line 1 of Data.txt file
   line = reader.readLine().trim();// we move on the following line (the second) 
   line = reader.readLine().trim();// we move on the following line (the third)  
    line = reader.readLine().trim();// we move on the following line (the fourth) containing time periods of time horizon

   for(int t=0;t<n;t++){// add v_kt
      line = reader.readLine().trim();// we move on the following line ( 4 + i + 1 )
      
      String []elements = line.split("\\s+");
        for(int k=0;k<m;k++){
        v[t][k]=Integer.parseInt(elements[k]);     
        }
    }
   return v;
  }  
   
  public static int[] Maximum_Vehicles_Requiremets (int[][]v){// create vector of v_k^(max)
  int [] v_MAX= new int[v[0].length];// vector size = 3
  for(int k=0;k<v[0].length;k++){
  v_MAX[k]=Max_in_Vector(v,k);
    }
   return v_MAX;
}
  
  
  
   private static int Max_in_Vector (int[][]v,int k){// for each vehicle type we calculate v_k^(max)
  int massimo= v[0][k];
  for(int t=0;t<v.length;t++){ 
      if(v[t][k]>massimo){massimo=v[t][k];}
  }
  return massimo;
}
   
 public static void Print_Costs(Vector<Float>c){
 for(int k=0;k<=c.size()-1;k++){
     
 if (k >= 0 & k<=2) {// fixed costs c_F^k
     int pos=k+1;
 System.out.println("Fixed Cost c_F^{" +pos+"}=" + c.get(k));
 }

 if (k >= 3 & k<=5) {// variable costs c_V^k
     int pos=k-2;
 System.out.println("Variable Cost c_V^{" + pos +"}=" + c.get(k));}

  if (k >= 6 & k<=8) {// hiring costs c_H^k
         int pos=k-5;
 System.out.println("Hiring Cost c_V^{" + pos +"}=" + c.get(k));}

   }
}
public static void Print_Requirements(int[][] x){
         System.out.println("Required number of vehicles:");
         
         for(int t=0;t<x.length;t++){
            for(int k=0;k<x[0].length;k++){
          int pos1=t+1;
         int pos2=k+1;
        System.out.print("v{k = " + pos2 + "}{t = " + pos1 + "}=" + x[t][k] + " ");
	System.out.println();

	}
 }
}
public static void Print_Max_Values(int[] x){
    System.out.println("v_k^(max)'s values are :");    
         for(int i=0;i<x.length;i++){
             int pos1=i+1;
           
   System.out.print("v_{" + pos1 + "}^(max)" + "=" + x[i] + "");
	System.out.println();

	}
}
}
