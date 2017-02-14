import java.io.*;
import java.util.*;

/* The Class to handle Attributes */
public abstract class Attribute
{

	/*Parses through the datainputstream to get the attributes
		Loosely based off Dave Cooper's CPEntry class	*/
	public static Attribute parse(DataInputStream dis, ConstantPool constantPool)
	throws InvalidConstantPoolIndex, IOException
	{
		Attribute att = null;

		/* Read the index from the data input stream for the index which contains the name
			of the type of attribute parsed, If the CPEntry is not a ConstantUtf8 an error
			will be output	*/
		int index = dis.readUnsignedShort();
		CPEntry entry = constantPool.getEntry(index);
		if(entry instanceof ConstantUtf8)
		{
			String att_name = ((ConstantUtf8)entry).getBytes();
			if(att_name.equals("Code"))
			{
				att = new Code_Attribute(dis,constantPool);
			}
			else
			{
				att = new Not_Code_Attribute(index,dis);
			}
		}
		else
		{
				throw new InvalidConstantPoolIndex("Index: "+index
					+" read does not point to a ConstantUtf8");		
		}
		return att;
	}
}

/*	Class to handle non-code attributes, only the 
	length is obtained to skip over the rest*/
class Not_Code_Attribute extends Attribute
{
	private int skippedBytes;
	/* This constructor is called from Abstract.parse() as the index is known and the
		datainputstream should not parse to retrieve in 	*/
	public Not_Code_Attribute(int index, DataInputStream dis) throws IOException
	{
		int length = dis.readInt();
		skippedBytes = dis.skipBytes(length);
	}

	/*	This constructor is called when the code is already found, this constructor is
		called by the method entry class to skip over the remaining attributes 	*/
	public Not_Code_Attribute(DataInputStream dis) throws IOException
	{
		//Skip over the name index as we already know that it cant be a code attribute
		//As this constructor is only called after the code attribute has been found
		skippedBytes = dis.skipBytes(2);
		int length = dis.readInt();
		skippedBytes += dis.skipBytes(length);
	}

}
/* Class to store the code attribute information*/
class Code_Attribute extends Attribute
{
	private int skippedBytes;
	private byte[] code;

	//	Only the code array is needed 
	public Code_Attribute(DataInputStream dis, ConstantPool constantPool) 
	throws InvalidConstantPoolIndex, IOException
	{
		int length = dis.readInt();
		//	Skip over the max stack bytes and local bytes
		int skippedBytes = dis.skipBytes(4);
		


		//	Stores the code array 
		int code_length = dis.readInt();
		code = new byte[code_length];
		dis.readFully(code);

		//Get the exception table length and skip over the exception table
		int exception_length = dis.readUnsignedShort();
		for(int i=0; i<exception_length;i++)
		{
			/*	Skip over each entry in the exception table which contains
				start_pc, end_pc, handler_pc and catch_type which give a combined 
				lenght of 8 */
			skippedBytes += dis.skipBytes(8);
		}

		/*	Skip over attributes in the code attribute as it would be illogical
			for the code attribute to contain a code attribute of its own	*/
		int attribute_length = dis.readUnsignedShort();
		Attribute[] attributesInCode = new Attribute[attribute_length];
		for(Attribute att: attributesInCode)
		{
			att = new Not_Code_Attribute(dis);
		}
	}

	/*	Returns the code array */
	public byte[] getCode()
	{
		return code;
	}

	public String codeToString()
	{
		final int HEX = 0xFF;

		String str = new String();
		for(int i=0;i<code.length;i++)
		{
			str += (code[i] & HEX) + " ";
		}
		return str;
	}

	//Finds the invocations of other methods if any and appends them to the instruction list 
	public LinkedList<Instruction> findInvokes(ConstantPool constantPool) throws CodeParsingException
	{
		/* First get the number of invokes*/
		LinkedList<Instruction> invokes = new LinkedList<Instruction>();
		int numInvokes = 0;

		for(int i=0;i<code.length-2;i++)
		{
			int iByte = code[i] & 0xff;
			if(iByte >= 182 && iByte <= 185)
			{
				//Only add instructions which will give a valid constant pool index
				int cpIdx = (code[i+1]<<8)|code[i+2];
				if(cpIdx >0 && cpIdx < constantPool.getSize())
				{
					invokes.add(new Instruction(code,i));			
				}
			}
		}

		return invokes;

	}
}