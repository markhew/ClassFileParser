import java.util.*;

public class Tests
{

	public void normalMethod()
	{
		int y = anotherMethod(1,2);
	}

	public int anotherMethod(int x, int y)
	{
		return x + y;
	}

	public LinkedList<Integer> javaClassTest()
	{
		LinkedList<Integer> intList = new LinkedList<Integer>();
		Scanner sc = new Scanner(System.in);

		int num = sc.nextInt();
		for(int i=0;i<num;i++)
		{
			intList.add(new Integer(i*5));
		}
		return intList;
	}

	public void sameMethodRecursion(int count)
	{
		if(count > 15)
		{
			System.out.println(count);
			sameMethodRecursion(count-1);
		}
	}

	public int diffMethodRecursion(int count, int anotherNum)
	{

		sameMethodRecursion(17);
		
		if(anotherNum % count > 7)
		{
			anotherNum = anotherRecursionMethod(count-1);
		}

		return anotherNum;
	}

	public int anotherRecursionMethod(int number)
	{
		return diffMethodRecursion(number-1,number*2);
	}


	public AnotherClass callAnotherClass()
	{
		AnotherClass another = new AnotherClass();
		another.aMethodInAnotherClass();
		return another;
	}

	public void callAbstractMethod()
	{
		Extended ext = new Extended();
		AnAbstract abs = ext;
		abs.doSomething();
	}

	public void all()
	{
		normalMethod();
		anotherMethod(1,2);
		LinkedList<Integer> list = javaClassTest();
		sameMethodRecursion(20);
		int x = diffMethodRecursion(20,15);
		int y = anotherRecursionMethod(3);
		AnotherClass a = callAnotherClass();
		callAbstractMethod();

	}
}