import java.io.*;
import java.util.*;

/* Parses the Data Input Stream to contruct a method entry object
*/
public class MethodEntry
{
	private String className;
	private int access_flags;
	private Code_Attribute code_Attribute;
	private LinkedList<Instruction> invokes;
	private String name;
	private String description;
	public MethodEntry(String inClassName, DataInputStream dis, ConstantPool constantPool) 
	throws IOException,InvalidConstantPoolIndex, CodeParsingException
																	
	{
		className = inClassName;
		access_flags = dis.readUnsignedShort();
		int name_index = dis.readUnsignedShort();
		int descriptor_index = dis.readUnsignedShort();
		name = ((ConstantUtf8)constantPool.getEntry(name_index)).getBytes();
		description = ((ConstantUtf8)constantPool.getEntry(descriptor_index)).getBytes();
		int attributes_count = dis.readUnsignedShort();
		code_Attribute = null;
		Attribute attribute;
		
		/*Parse the datainputstream skips through all attributes 
		except for the code attribute.*/
		for(int i=0; i<attributes_count; i++)
		{
			attribute = Attribute.parse(dis,constantPool);
			if(attribute instanceof Code_Attribute)
			{
				code_Attribute = (Code_Attribute)attribute;
			}
		}
		if(code_Attribute != null)
		{
			invokes = code_Attribute.findInvokes(constantPool);
		}

	}
	/* Returns the code attribute if any*/
	public Attribute getCodeAttribute()
	{
		return code_Attribute;
	}
	

	/*Returns the name & description for the method*/
	public String toString()
	{
		return className+"."+name+" "+description;
	}

	//Returns the name of the class that the method is in.
	public String getClassName()
	{
		return className;
	}

	//This method overides the .equals by only comparing name and description
	public boolean equals(String inName, String inDescription)
	{
		return (inName.equals(name) && inDescription.equals(description));
	}

	//Accessor to return invokes
	public LinkedList<Instruction> getInvocations()
	{
		return invokes;
	}

	//Determines if the method is an abstract method
	public boolean isAbstract()
	{
		boolean isAbs = false;
		//Remove the synthetic and/or strict flags
		final int SYNTHETIC = 0x1000;
		final int STRICT = 0x0800;
		final int ABSTRACT = 0x0400;

		int flagVal = access_flags;

		if(flagVal >= SYNTHETIC)
		{
			flagVal -= SYNTHETIC;
		}

		if (flagVal >= STRICT)
		{
			flagVal -= STRICT;	
		}
		if(flagVal >= ABSTRACT)
		{
			isAbs = true;
		}

		return isAbs;

	}

	
	
	
}