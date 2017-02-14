import java.io.*;

/**
 * Parses and stores the Methods from a Java .class file.
 *
 */

public class Methods
{
	private MethodEntry[] methodentries;
	
	/**Parses the DataInputStream creating method objects
	 * 
	 */
	
	public Methods(String className, DataInputStream dis, ConstantPool constantPool) throws 
	IOException, InvalidConstantPoolIndex, CodeParsingException
	{
		int numMethods = dis.readUnsignedShort();
		methodentries = new MethodEntry[numMethods];
		for(int i=0; i<numMethods;i++)
		{
			methodentries[i] = new MethodEntry(className,dis,constantPool);
		}
	}
	
	public MethodEntry[] getMethodEntries()
	{
		return methodentries;
	}
	
	public MethodEntry getMethodEntry(int index) throws ArrayIndexOutOfBoundsException
	{
		return methodentries[index];
	}

	public MethodEntry findMethod(String methodname, String description)
	{
		MethodEntry found = null;
		for(MethodEntry method: methodentries)
		{
			if(method.equals(methodname,description))
			{
				found = method;
			}
		}
		return found;
	}
}