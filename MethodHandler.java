import java.io.*;
import java.util.*;

/* Class which deals specifically with the method part of the class file*/
public class MethodHandler
{
	private LinkedList<MethodEntry> callStack;
	private int numMethodsCalled;
	public MethodHandler()
	{
		callStack = new LinkedList<MethodEntry>();
		numMethodsCalled = 0;
	}
	/* 	Outputs to terminal the list of methods available 
		in the class and will return the number
		methods available 									*/
	public int outputListOfMethods(Methods methods, ConstantPool constantPool) throws 
																InvalidConstantPoolIndex
	{
		MethodEntry[] methodEntries = methods.getMethodEntries();

		for(int i=0;i<methodEntries.length;i++)
		{
			System.out.println("("+(i+1)+") " +methodEntries[i]);
		}

		return methodEntries.length;
	}
	
	/*	The initial start of the call tree */
	public void outputCallTree(ClassFile cf, int index) throws InvalidConstantPoolIndex
	{
		Methods methods = cf.getMethods();
		ConstantPool constantPool = cf.getConstantPool();
		MethodEntry entry = methods.getMethodEntries()[index];
		System.out.println(entry);
		stepIntoMethod(methods,entry,constantPool,1);
		System.out.println("Number of methods called by this method :"+ numMethodsCalled);
	}
	
	public void stepIntoMethod(Methods methods, MethodEntry methodEntry, 
		ConstantPool constantPool, int numTabs) throws InvalidConstantPoolIndex
	{
		String methodClass = methodEntry.getClassName();
		callStack.add(methodEntry);

		/* If there are no invocations in the method, end*/
		if(methodEntry.getInvocations().size()==0)
		{
			callStack.removeLast();
		}
		else
		{

			//For each invoke
			for(Instruction in: methodEntry.getInvocations())
			{	
				try{
				byte[] bytes = in.getExtraBytes();
				
				int cpIdx = (bytes[0] << 8) | bytes[1];
				if(cpIdx <= 0)
				{
					System.out.println("Instruction :"+in.getOpcode().getMnemonic()+
						" IndexByte1 :"+(bytes[0]&0xFF)+" IndexByte2 :"+bytes[1]);
				}
				CPEntry cpentry = constantPool.getEntry(cpIdx);
				//Make sure that the Cpentry is a ConstantMethodRef using it
				if(cpentry instanceof ConstantMethodRef)
				{
					ConstantMethodRef methodRef = (ConstantMethodRef)constantPool.getEntry(cpIdx);
					
					//Get the details for the invoke, class, name and type
					String className = methodRef.getClassName();
					String name = methodRef.getName();
					String type = methodRef.getType();

					String methodDesc = className + "." + name + " "+ type;
					//
					numMethodsCalled++;
					MethodEntry calledMethod = null;
					
					indent(numTabs);
					System.out.print(methodDesc);
						
					//If the method is in the current class, look for it.
					if(className.equals(methodClass))
					{
						calledMethod = methods.findMethod(name,type);
						
						/*Determine if the method was called before, it is was end. if not,
							Step into it */
						if(callStack.contains(calledMethod))
						{
							System.out.println("\t[Recursive]");
						}
						else
						{
							if(calledMethod.isAbstract())
							{
								System.out.println("\t[Abstract]");
							}
							else
							{
								System.out.println();
								stepIntoMethod(methods,calledMethod,constantPool,numTabs+1);
							}
						}
						
						
					}
					else
					{
							/*	If the method to called is not found in the current class,
								try and look for it in another class in the same directory
								If such a class cannot be found, a [missing] will be added
								*/				
						File f = new File(className+".class");
						if(f.exists())
						{
							ClassFile newClassFile = new ClassFile(className+".class");
							ConstantPool newConstantPool = newClassFile.getConstantPool();
							Methods newMethods = newClassFile.getMethods();
							calledMethod = newMethods.findMethod(name,type);
							
							if(calledMethod.isAbstract())
							{
								System.out.println("\t[Abstract]");
							}
							else
							{	/* Make sure there is no recursion before calling it
									(i.e. the method was called from the previous class)
									*/
								if(callStack.contains(calledMethod))
								{
									System.out.println("\t[Recursive]");
								}
								else
								{
									System.out.println();
									stepIntoMethod(newMethods,calledMethod,newConstantPool,numTabs+1);
								}
							}
						}
						else
						{
							System.out.println("\t[Missing]");
						}
						
					}
					
					System.out.println();
				}
				
				}
				catch(Exception e)//Catch all exceptions
				{
					System.out.println(e.getMessage());
				}
			}
			
			

			/*Remove the method from the call stack if there 
			is any (not the first method called overall)*/
			if(callStack.size()>0)
			{
				callStack.removeLast();
			}
		}
	}
	

	/* This method prints a number of tabs for the call tree*/
	public void indent(int numTabs)
	{
		for(int i=0;i<numTabs;i++)
		{
			System.out.print("~");
		}
		System.out.print("> ");
	}


}