import java.io.*;
import java.util.*;
/**
 * Parses and displays the call tree of a Java .class file.
 *
 * @author David Cooper
 * Modified by Mark Hew
 */
public class ClassFileParser
{
    public static void main(String[] args)
    {

        Scanner sc = new Scanner(System.in);
        if(args.length == 1)
        {
            try
            {
                ClassFile cf = new ClassFile(args[0]);
                MethodHandler mh = new MethodHandler();
                MethodEntry[] methodEntries = cf.getMethods().getMethodEntries();
                
            
                int numMethods = mh.outputListOfMethods(cf.getMethods(),cf.getConstantPool());
                System.out.println("Select Method");
                int methodNum = sc.nextInt();

                while(methodNum < 1 || methodNum > numMethods)
                {
                    System.out.println("Invalid choice, Select Method");
                    methodNum = sc.nextInt();
                }
               
                mh.outputCallTree(cf,methodNum-1);
               //System.out.println(cf);
            }
            catch(IOException e)
            {
                System.out.printf("Cannot read \"%s\": %s\n",
                    args[0], e.getMessage());
            }
            catch(ClassFileParserException e)
            {
                System.out.printf("Class file format error in \"%s\": %s\n",
                    args[0], e.getMessage());
            }
        }
        else
        {
            System.out.println("Usage: java ClassFileParser <class-file>");
        }
    }
}
