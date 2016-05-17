import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.TreeSet;
import java.util.LinkedList;

public class TestGitlet {
	public void setUp() {
		Ser.recursiveDelete(new File(".gitlet/"));
		Gitlet.init();
		Ser.recursiveDelete(new File("a.txt.conflicted"));
		Ser.recursiveDelete(new File("aaa.txt.conflicted"));
		Ser.recursiveDelete(new File("b.txt.conflicted"));
		Ser.recursiveDelete(new File("bbb.txt.conflicted"));
		
	}

	@Test
	public void testBasicCheckout() throws IOException {
		//Testing basic checkout
		setUp();
		String originalText = "this is a test";
		Ser.createFile("test.txt", originalText);
		Gitlet.add("test.txt");
		Gitlet.commit("added test");
		Ser.createFile("test.txt", "hopefully this test works");
		Gitlet.checkout("test.txt", -1);
		assertEquals(originalText, Ser.getText("test.txt"));
	}

	@Test 
	public void testHipsterCheckout() throws IOException {
		//Hipster as opposed to Basic (ie "Complicated") but arguably the same
		//Tests checkout beyond most recent node

		//**** more checks on file changes in merge test....

		setUp();

		String originalText = "this is a test";
		Ser.createFile("test.txt", originalText);
		Gitlet.add("test.txt");
		Gitlet.commit("added test"); //commitID = 1

		Ser.createFile("test.txt", "hopefully this test works");
		Gitlet.add("test.txt");
		Gitlet.commit("changed test.txt"); // commitID = 2

		Ser.createFile("test.txt", "third commit");
		Gitlet.add("test.txt");
		Gitlet.commit("third times the charm"); // commitID = 3

		//change file and revert to various states
		Ser.createFile("test.txt","should be overridden");
		Gitlet.checkout("test.txt", -1);
		assertEquals("third commit", Ser.getText("test.txt"));

		Gitlet.checkout("test.txt", 1);
		assertEquals("this is a test", Ser.getText("test.txt"));
	}

	@Test
	public void testThoroughReset() throws IOException{
		setUp();
		//create three files, A, B, and C.txt. Then add and commit;
		String originalTextA = "this is A";
		String originalTextB = "this is B";
		Ser.createFile("A.txt", originalTextA);
		Ser.createFile("B.txt", originalTextB);
		Ser.createFile("C.txt", "haywire");
		Gitlet.add("A.txt");
		Gitlet.add("B.txt");
		Gitlet.add("C.txt");
		Gitlet.commit("added a and b and c"); //commitID = 1
		//alter all three files. Then add and commit
		Ser.createFile("A.txt", "A changed once");
		Ser.createFile("B.txt", "B changed once");
		Ser.createFile("C.txt", "weiufnwf \n wei\nfn  ()fjdn");
		Gitlet.add("A.txt");
		Gitlet.add("B.txt");
		Gitlet.add("C.txt");
		Gitlet.commit("a and b and c a second time"); // commitID = 2
		//alter all three files. Then add and commit
		Ser.createFile("A.txt", "third change to A");
		Ser.createFile("B.txt", "third change to B");
		Ser.createFile("C.txt", "2" + "\nBnMJhnHNMm");
		Gitlet.add("A.txt");
		Gitlet.add("B.txt");
		Gitlet.add("C.txt");
		Gitlet.commit("third and final change to a and b and c"); //commitI = 3

		//restore files based on their contents at the node specified in the reset function
		Gitlet.reset(1);
		assertEquals("this is A", Ser.getText("A.txt"));
		assertEquals("this is B", Ser.getText("B.txt"));
		assertEquals("haywire", Ser.getText("C.txt"));

		Gitlet.reset(2);
		assertEquals("A changed once", Ser.getText("A.txt"));
		assertEquals("B changed once", Ser.getText("B.txt"));
		assertEquals("weiufnwf \n wei\nfn  ()fjdn", Ser.getText("C.txt"));

		Gitlet.reset(3);
		assertEquals("third change to A", Ser.getText("A.txt"));
		assertEquals("third change to B", Ser.getText("B.txt"));
		assertEquals("2" + "\nBnMJhnHNMm", Ser.getText("C.txt"));

		//make sure we can keep resetting back and forth in no order
		Gitlet.reset(2);
		assertEquals("A changed once", Ser.getText("A.txt"));
		assertEquals("B changed once", Ser.getText("B.txt"));
		assertEquals("weiufnwf \n wei\nfn  ()fjdn", Ser.getText("C.txt"));

		Gitlet.reset(1);
		assertEquals("this is A", Ser.getText("A.txt"));
		assertEquals("this is B", Ser.getText("B.txt"));
		assertEquals("haywire", Ser.getText("C.txt"));

		Gitlet.reset(3);
		assertEquals("third change to A", Ser.getText("A.txt"));
		assertEquals("third change to B", Ser.getText("B.txt"));
		assertEquals("2" + "\nBnMJhnHNMm", Ser.getText("C.txt"));

		Gitlet.reset(2);
		assertEquals("A changed once", Ser.getText("A.txt"));
		assertEquals("B changed once", Ser.getText("B.txt"));
		assertEquals("weiufnwf \n wei\nfn  ()fjdn", Ser.getText("C.txt"));

	}

	@Test
	public void testUtils() throws IOException{

	////////testing Gitlet.findSplitPoint()
			setUp();
			LinkedList<Integer> t1 = new LinkedList<Integer>();
			LinkedList<Integer> t2 = new LinkedList<Integer>();
			t1.add(1);
			t2.add(1);
			t1.add(2);
			t2.add(2);
			t2.add(3);
			t1.add(4);
			t1.add(5);
			t2.add(6);
			// finding the max common value between t1 and t2
			// t1: 1, 2, 4, 5
			// t2: 1, 2, 3, 6
			Ser.serialize(new File(".gitlet/t1Commits.ser"), (Object) t1);
			Ser.serialize(new File(".gitlet/t2Commits.ser"), (Object) t2);
			assertEquals(2, Gitlet.findSplitPoint("t1", "t2")); // should be 2

	////////testing Gitlet.hasChanged()
			setUp();
			String originalText = "this is a test";
			Ser.createFile("test.txt", originalText);
			Gitlet.add("test.txt");
			Gitlet.commit("test");
			Ser.createFile("test.txt", "changed");
			assertTrue(Gitlet.hasChanged("test.txt"));
	}

	@Test
	public void testMergeAndCheckoutSortOf() throws IOException {
		
		setUp();
		Ser.createFile("a.txt", "a1");
		Gitlet.add("a.txt");
		Gitlet.commit("a1");

		//make new branch
		Gitlet.branch("newBranch");
		Gitlet.checkout("newBranch", -1);

		//make new a.txt to branch the tree
		Ser.createFile("a.txt", "a2");
		Gitlet.add("a.txt");
		Gitlet.commit("a2 on newBranch");

		//checkout to master
		Gitlet.checkout("master", -1);

		//checkout test just in case
		assertEquals("a1", Ser.getText("a.txt"));

		Gitlet.merge("newBranch");
		//should revert "a1" to "a2"
		assertEquals("a2", Ser.getText("a.txt"));
		
		//modify a.txt on master 
		Ser.createFile("a.txt", "a3");
		Gitlet.add("a.txt");
		Gitlet.commit("a3 on master");

		//merge should generate a .conflicted file now 
		Gitlet.merge("newBranch");
		assertTrue(new File("a.txt.conflicted").exists());

		//reset and test that nothing changes
		//when given branch has no mods

		setUp();
		Ser.createFile("a.txt", "a1");
		Gitlet.add("a.txt");
		Gitlet.commit("a1");

		//make new branch
		Gitlet.branch("newBranch");
		Gitlet.checkout("newBranch", -1);
		//dummy file to commit
		Ser.createFile("b.txt", "b");
		Gitlet.add("b.txt");
		Gitlet.commit("b");

		//back to master
		Gitlet.checkout("master", -1);
		Gitlet.add("b.txt");
		Gitlet.commit("b on master");

		Gitlet.merge("newBranch");
		assertEquals("a1", Ser.getText("a.txt"));

		Ser.createFile("b.txt", "not b");
		Gitlet.add("b.txt");
		Gitlet.commit("b");

		Gitlet.merge("newBranch");
		assertTrue(new File("b.txt.conflicted").exists()); //coming up as false

		

		//clean up director

	}

	@Test
	public void testRebase() throws IOException{
		//test idea of rebase inmplementation first for dev purposed

		setUp();
		Ser.createFile("a.txt", "a1");
		Gitlet.add("a.txt");
		Gitlet.commit("a1");

		Gitlet.branch("new");
		//Gitlet.status();
		Gitlet.checkout("new", -1);
		Ser.createFile("a.txt", "a2");
		Gitlet.add("a.txt");
		Gitlet.commit("a2");

		Ser.createFile("a.txt", "a3");
		Gitlet.add("a.txt");
		Gitlet.commit("a3");

		Gitlet.checkout("master", -1);
		Ser.createFile("a.txt", "a4");
		Gitlet.add("a.txt");
		Gitlet.commit("a4");

		Ser.createFile("a.txt", "a5");
		Gitlet.add("a.txt");
		Gitlet.commit("a5");

		Gitlet.checkout("new", -1);

		Gitlet.rebase("master");
		System.out.println("should be a3, a2, a5, a4, a1, 0");
		Gitlet.log();
		System.out.println("should print already up to date:");
		Gitlet.rebase("master");
	}

	@Test
	public void testDoubleBranchRebase() throws IOException {
		setUp();
		Ser.createFile("a.txt", "a1");
		Gitlet.add("a.txt");
		Gitlet.commit("a1");

		Gitlet.branch("new1");
		Gitlet.checkout("new1", -1);
		Ser.createFile("a.txt", "a2");
		Gitlet.add("a.txt");
		Gitlet.commit("a2"); // part 2 of rebase

		Gitlet.branch("new2");
		Gitlet.checkout("new2", -1);
		Ser.createFile("a.txt", "a3");
		Gitlet.add("a.txt");
		Gitlet.commit("a3");

		Gitlet.checkout("new1", -1);
		Ser.createFile("a.txt", "a4");
		Gitlet.add("a.txt");
		Gitlet.commit("a4"); // part 1 of rebase

		Gitlet.checkout("master", -1);
		Ser.createFile("a.txt", "a5");
		Gitlet.add("a.txt");
		Gitlet.commit("a5"); //part 4

		Ser.createFile("a.txt", "a6");
		Gitlet.add("a.txt");
		Gitlet.commit("a6"); //part 3

		Gitlet.checkout("new1", -1);
		
		Gitlet.rebase("master");
		Gitlet.log(); //works!! should be (a4, a2, a6, a5, a1, 0)
	}

	@Test
	public void misc() throws IOException {
		//testing remove Branch
		setUp();
		Ser.createFile("a.txt", "a1");
		Gitlet.add("a.txt");
		Gitlet.commit("a1");

		Gitlet.branch("new1");
		Gitlet.checkout("new1", -1);
		Ser.createFile("a.txt", "a2");
		Gitlet.add("a.txt");
		Gitlet.commit("a2"); 

		Gitlet.checkout("master", -1);
		assertTrue(new File(".gitlet/new1Commits.ser").exists());
		assertTrue(new File(".gitlet/new1Head.ser").exists());

		Gitlet.rmBranch("new1");
		assertFalse(new File(".gitlet/new1Commits.ser").exists());
		assertFalse(new File(".gitlet/new1Head.ser").exists());
		System.out.println("should only have master as a branch:");
		Gitlet.status();

	}

	@Test
	public void semiComplicatedBranchingAndMerging() throws IOException {
		setUp();
		Ser.createFile("aaa.txt", "a1"); 
		Gitlet.add("aaa.txt");
		Gitlet.commit("a1");// 0 -> a1

		Gitlet.branch("new1");
		Gitlet.checkout("new1", -1);
		Ser.createFile("aaa.txt", "a2");
		Gitlet.add("aaa.txt");
		Gitlet.commit("a2"); // 0 -> a1          master
							//     \ -> a2       new1
		Gitlet.branch("new2");
		Gitlet.checkout("new2", -1);
		Ser.createFile("aaa.txt", "a3");
		Gitlet.add("aaa.txt");
		Gitlet.commit("a3");  // 0 -> a1                master
							  //        \ -> a2         new1 
							  //              \ -> a3   new2

		Gitlet.checkout("master", -1);
		Gitlet.branch("new3");
		Gitlet.checkout("new3", -1);
		Ser.createFile("aaa.txt", "a4");
		Gitlet.add("aaa.txt");
		Gitlet.commit("a4"); 
		//        -> a4           new3    
		//       /
		// 0 -> a1                master
		//        \ -> a2         new1 
		//              \ -> a3   new2

		//quick checks
		Gitlet.checkout("master", -1);
		Gitlet.log();
		Gitlet.checkout("new3", -1);

		Gitlet.merge("new1");
		assertTrue(new File("aaa.txt.conflicted").exists());

		Ser.createFile("bbb.txt", "b1"); 
		Gitlet.add("bbb.txt");
		Gitlet.commit("b1");

		Gitlet.checkout("new2", -1);
		Ser.createFile("bbb.txt", "b2"); 
		Gitlet.add("bbb.txt");
		Gitlet.commit("b2");

		Gitlet.merge("new3");
		assertTrue(new File("bbb.txt.conflicted").exists());

		Gitlet.checkout("new3", -1);
		Gitlet.branch("new3 child");
		Gitlet.checkout("new3 child", -1);
		Ser.createFile("aaa.txt", "new 3 child text"); 
		Gitlet.add("aaa.txt");
		Gitlet.commit("child");
		//            / -> n3 child   new3 child
		//        -> a4               new3    
		//       /
		// 0 -> a1                    master
		//        \ -> a2             new1 
		//              \ -> a3       new2

		Gitlet.checkout("new3", -1);
		Gitlet.merge("new3 child");
		assertEquals("new 3 child text", Ser.getText("aaa.txt"));
		



	}
	


	public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestGitlet.class);
    }
}