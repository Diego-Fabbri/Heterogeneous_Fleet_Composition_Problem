
package com.mycompany.heterogeneous_fleet_composition_problem;

import ilog.concert.*;
import ilog.cplex.*;
import java.io.IOException;
import java.util.Vector;
public class HFC_Model {
    protected IloCplex modello;
   protected boolean Verify_Preliminary_Condition;
    protected Vector<Float> c;
   
   
    protected  int [][] v;
    protected int[] vMAX;
    protected int[] pMAX;
     
    protected IloIntVar z;// dummy variable
    protected IloIntVar[] p;
    protected IloIntVar[][] x;// matrix of x^k_t
    protected IloIntVar[][] y;// matrix of y^k_t
    

   public HFC_Model(Vector <Float> c, int [][]v, int[]vMAX) throws IOException, IloException{
    modello =new IloCplex();// Cplex model
   
     this.c=c;
     
  //  this.m=m; //=v[0].length
 //   this.n=n; //=v.length
   
    this.v=v;
    this.vMAX=vMAX;// vector of v_k^(max)
  
    p= new IloIntVar[v[0].length];// number of vehicles k to own in the fleet (this is the v_k in pdf formulation)
    x= new IloIntVar[v.length][v[0].length];
    y= new IloIntVar[v.length][v[0].length];
    this.Verify_Preliminary_Condition= Preliminary_Condition();// verify c_F^k+c_V^k <= c_H^k forall k=1,...,m
    }
   // VARIABLES
   public void addvariables () throws IloException{
    
    
   for(int k=0;k<p.length;k++){
       int pos=k+1;
    p[k] = modello.intVar(0,vMAX[k],"p_[" + pos + "]");// variables p_k
     }
    z = modello.intVar(1, 1, "z");// artificial variable whose value is 1
    
    for(int k=0;k<v[0].length;k++){
          int pos1=k+1;
          for(int t=0;t<v.length;t++){
              int pos2=t+1;
             
              x[t][k]= modello.intVar(0,1,"x[k "+pos1+"][t"+pos2+"]");// variable x_t^k
              y[t][k]= modello.intVar(0,vMAX[k],"y[k"+pos1+"][t"+pos2+"]");// variable y_t^k


          }
    
    }
    }
   // CONSTRAINS
   public void addconstrains () throws IloException{
    // CONSTRAINS (2) 
   //p_k - v_k^(max)*x_t^k >= v_kt - v_k^(max)
  for(int k=0;k<p.length;k++){ 
  for(int t=0;t<v.length;t++){
        
   IloLinearNumExpr expr7 = modello.linearNumExpr();
   expr7.addTerm(1,p[k]);
  expr7.addTerm(-vMAX[k],x[t][k]);
   modello.addGe(expr7,v[t][k]-vMAX[k] );
   }
    }
  // CONSTRAINS (3)
  //p_k - v_k^(max)*x_t^k <= v_kt 
for(int k=0;k<p.length;k++){ 
  for(int t=0;t<v.length;t++){
   IloLinearNumExpr expr8 = modello.linearNumExpr();
 expr8.addTerm(1,p[k]);
   expr8.addTerm(-vMAX[k],x[t][k]);
   modello.addLe(expr8,v[t][k]);
   }
    }
// CONSTRAINS (4)
//y_t^k <= v_{kt}
for(int k=0;k<p.length;k++){ 
  for(int t=0;t<v.length;t++){
   IloLinearNumExpr expr9 = modello.linearNumExpr();   
      expr9.addTerm(1,y[t][k]);
      modello.addLe(expr9,v[t][k]);
  }
}

// // CONSTRAINS (5)
//y_t^k - v_k^(max)*x_t^k <=0
for(int k=0;k<p.length;k++){ 
  for(int t=0;t<v.length;t++){
   IloLinearNumExpr expr10 = modello.linearNumExpr();   
      expr10.addTerm(1,y[t][k]);
      expr10.addTerm(-vMAX[k],x[t][k]);
      modello.addLe(expr10,0 );
  }
}



// // CONSTRAINS (6)
//y_t^k -p_k - v_k^(max)*x_t^k >= v_k^(max)
for(int k=0;k<p.length;k++){ 
  for(int t=0;t<v.length;t++){
   IloLinearNumExpr expr11 = modello.linearNumExpr();   
      expr11.addTerm(1,y[t][k]);
      expr11.addTerm(-1,p[k]);
      expr11.addTerm(-vMAX[k],x[t][k]);
      modello.addGe(expr11,-vMAX[k] );
  }
}



}
   
   
   
   
 // OBJECTIVE FUNCTION (1)  
protected void addObjective() throws IloException{
IloLinearNumExpr obj =modello.linearNumExpr();


// Fixed costs \sum_{k=1}^{m}  n*(c_F^k)*p_k *
for(int k=0;k<v[0].length;k++){
    obj.addTerm(v.length*c.get(k),p[k]);
 }
// Variables costs (first)
//  \sum_{k=1}^{m}\sum_{t=1}^{n} c_V^k * v_{kt} * x_t^k
for(int k=0;k<v[0].length;k++){
   for(int t=0;t<v.length;t++){
   obj.addTerm(x[t][k], v[t][k]*c.get(k+v[0].length));
 }
}
// Variables costs (second)
// \sum_{k=1}^{m}\sum_{t=1}^{n}  c_V^k* * p_{k} - c_V^k* * y_t^k
for(int k=0;k<v[0].length;k++){
   for(int t=0;t<v.length;t++){
   obj.addTerm(p[k], c.get(k+v[0].length));
   obj.addTerm(y[t][k], -c.get(k+v[0].length));
 }
}


// Hiring costs 
// \sum_{k=1}^{m}\sum_{t=1}^{n}c_H^k *(v_{kt}*z - v_{kt}*x_t^k - p_k + y_t^k)
for(int k=0;k<v[0].length;k++){
   for(int t=0;t<v.length;t++){
   obj.addTerm(z, v[t][k]*c.get(k+2*v[0].length));// add c_H^k *v_{kt}*z
   obj.addTerm(x[t][k], -v[t][k]*c.get(k+2*v[0].length));// add -c_H^k *v_{kt}*x_t^k
   obj.addTerm(p[k],-c.get(k+2*v[0].length));// add -p_k*c_H^k 
   obj.addTerm(y[t][k],c.get(k+2*v[0].length));// add y_t^k*c_H^k
 }
}


  IloObjective Obj = modello.addObjective(IloObjectiveSense.Minimize, obj);

}
 protected boolean Preliminary_Condition(){
   for(int k=0; k < v[0].length; k++){
       //c_V^k+c_F^k<=c_H^k
 float internal_cost =c.get(k)+c.get(k+v[0].length);//c_F^k+c_V^k
   
   
 if(internal_cost>c.get(k+2*v[0].length)){return false;} // condition NOT satisfied for all vehicles type
}
    return true;
 }
 
 
 
 
    public void solve() throws IloException, IOException {
        addvariables();
        addObjective();
        addconstrains();
        

        if (Verify_Preliminary_Condition == true) {
            
            System.out.println("Preliminary Condition is satisfied");
            modello.solve();
            modello.exportModel("Heterogeneous_Fleet_Composition_Problem.lp");
            if (modello.getStatus() == IloCplex.Status.Feasible
                    | modello.getStatus() == IloCplex.Status.Optimal) {
                System.out.println();
                System.out.println("Model Status = " + modello.getStatus());
                System.out.println();
                
                
                System.out.println("Objective value = " + modello.getObjValue());
                for (int k = 0; k < v[0].length; k++) {
                    System.out.println(p[k].getName() + " = " + modello.getValue(p[k]));
                }
            } else {
                System.out.println();
                System.out.println("Model Status = " + modello.getStatus());
                System.out.println();
                
                
            }

        } else {
            System.out.println("Preliminary Condition is not satisfied");
        }
    } 
}
