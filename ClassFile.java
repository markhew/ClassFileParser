import java.io.*;


/**
 * Parses and stores a Java .class file. Parsing is currently incomplete.
 *
 * @author David Cooper
 */
public class ClassFile
{
    private String filename;
    private long magic;
    private int minorVersion;
    private int majorVersion;
    private ConstantPool constantPool;

    // Code implemented by Mark Jin Hew
    private int accessflag;
    private int this_class;
    private int super_class;
    private int numInterfaces;  
    private int numFields;
    private Methods methods;
    /**
     * Parses a class file an constructs a ClassFile object. At present, this
     * only parses the header and constant pool.
     */
    public ClassFile(String filename) throws ClassFileParserException,
                                             IOException
    {
    	
        DataInputStream dis =
            new DataInputStream(new FileInputStream(filename));

        this.filename = filename;
        magic = (long)dis.readUnsignedShort() << 16 | dis.readUnsignedShort();
        minorVersion = dis.readUnsignedShort();
        majorVersion = dis.readUnsignedShort();
        constantPool = new ConstantPool(dis);

        // Parse the rest of the class file
        
        accessflag = dis.readUnsignedShort();
        this_class = dis.readUnsignedShort(); //Gets the index in the
        //constantpool for the name of the class
        
        super_class = dis.readUnsignedShort();
        
        numInterfaces = dis.readUnsignedShort();//Read but do nothing with

        int skippedBytes = dis.skipBytes(numInterfaces);//Skip the interface bits
        numFields = dis.readUnsignedShort();
        
        //Skip over the fields
        for(int j=0;j<numFields;j++)
        {
        	skippedBytes += dis.skipBytes(6); 
        	int fieldAttCount = dis.readUnsignedShort();
        	for(int k=0; k<fieldAttCount;k++)
        	{
        		skippedBytes += dis.skipBytes(2);
        		int attLength = dis.readInt();
        		skippedBytes += dis.skipBytes(attLength);
        	}
        }
        String thisClassName =((ConstantClass)constantPool.getEntry(this_class)).getName();
        methods = new Methods(thisClassName,dis,constantPool);
            
    }

    /** Returns the contents of the class file as a formatted String. */
    public String toString()
    {
        return String.format(
            "Filename: %s\n" +
            "Magic: 0x%08x\n" +
            "Class file format version: %d.%d\n\n" +
            "Constant pool:\n\n%s",
            filename, magic, majorVersion, minorVersion, constantPool);
    }

    /* Returns the methods from the class file*/
    public Methods getMethods()
    {
        return methods;
    }

    /* Returns the constant pool*/
    public ConstantPool getConstantPool()
    {
        return constantPool;
    }

    public int getThisClassIdx()
    {
        return this_class;
    }
}

