

package com.mycompany.heterogeneous_fleet_composition_problem;

import Utility.Data;
import ilog.concert.IloException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
public class Main {
    public static void main (String[] args) throws FileNotFoundException, IOException, IloException {
    System.setOut(new PrintStream("Heterogeneous_Fleet_Composition_Problem.log"));
   
  int m=3; //number of vehicles types (index k)
  int n=52; //numer of time periods in time horizon (index t)   
Vector<Float> c = Data.Costs_Data(new File(args[0]),m,n); 

   
        
 Data.Print_Costs(c);   
        
  int [][] v = Data.Vehicles_Requirements(new File(args[0]),m,n);// matrix of v_k^t 
 Data.Print_Requirements(v);
 
 
 // vector of v_k^max
 
int []vMAX=new int [m];
vMAX= Data.Maximum_Vehicles_Requiremets(v);// vettore dei valori di v_k^max
 Data.Print_Max_Values(vMAX);

 

 
 
 HFC_Model cplex = new HFC_Model (c,v,vMAX);
 cplex.solve();
}
}