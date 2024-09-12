package wicket.example.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeData {
	
	public List<Foo> foos = new ArrayList<>();

	public TreeData() {
		Foo fooA = new Foo("A");
		{
			Foo fooAA = new Foo(fooA, "AA");
			{
				new Foo(fooAA, "AAA");
				new Foo(fooAA, "AAB");
			}
			Foo fooAB = new Foo(fooA, "AB");
			{
				new Foo(fooAB, "ABA");
				Foo fooABB = new Foo(fooAB, "ABB");
				{
					new Foo(fooABB, "ABBA");
					Foo fooABBB = new Foo(fooABB, "ABBB");
					{
						new Foo(fooABBB, "ABBBA");
					}
				}
				new Foo(fooAB, "ABC");
				new Foo(fooAB, "ABD");
			}
			Foo fooAC = new Foo(fooA, "AC");
			{
				new Foo(fooAC, "ACA");
				new Foo(fooAC, "ACB");
			}
		}
		foos.add(fooA);

		Foo fooB = new Foo("B");
		{
			new Foo(fooB, "BA");
			new Foo(fooB, "BB");
		}
		foos.add(fooB);

		Foo fooC = new Foo("C");
		foos.add(fooC);
	}

	public Foo getFoo(String id)
    {
        return findFoo(foos, id);
    }

    private static Foo findFoo(List<Foo> foos, String id)
    {
        for (Foo foo : foos)
        {
            if (foo.getId().equals(id))
            {
                return foo;
            }

            Foo temp = findFoo(foo.getFoos(), id);
            if (temp != null)
            {
                return temp;
            }
        }

        return null;
    }
}
