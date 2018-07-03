package job;
public class JobRunner{

   public static void main(String[] args){
      int status1 = -1;
	  int status2 = -1;
	  int status3 = -1;
	  int status4 = -1;
	  int status5 = -1;
	  
	  status1 = new MR1.run();
	  if(status1 == 1){
	     System.out.println("Step1 succedd. Start Step2");
		 status2 = new MR2().run();
	  }else{
	     System.out.println("Step1 fail.");
	  }
	  
	  if(status2 == 1){
	     System.out.println("Step2 succedd. Start Step3");
		 status3 = new MR3().run();
	  }else{
	     System.out.println("Step2 fail.");
	  }
	  
	  
	  if(status3 == 1){
	     System.out.println("Step3 succedd. Start Step4");
		 status4 = new MR4().run();
	  }else{
	     System.out.println("Step3 fail.");
	  }
	  
	  if(status4 == 1){
	     System.out.println("Step4 succedd. Start Step5");
		 status5 = new MR5().run();
	  }else{
	     System.out.println("Step4 fail.");
	  }
	  
	  if(status5 == 1){
	     System.out.println("Step5 succedd. End.");
	  }else{
	     System.out.println("Step5 fail.");
	  }
   }
}