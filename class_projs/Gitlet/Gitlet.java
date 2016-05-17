import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.LinkedList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
/* 

 CURRENT ISSUES

/**
 * @author Ben Miroglio
 */
public class Gitlet  { 
    //Main class for cs61b proj2 

    //keeps info for 'status'
    private static TreeSet<String> addedFiles;
    private static TreeSet<String> removedFiles;
    //main commit Tree
    private static TreeMap<Integer, TreeMap<String, byte[]>> commitFiles;
    //correpsonding commit messages / Times
    private static TreeMap<Integer, String> commitMessages;
    private static TreeMap<Integer, String> commitTimes;
    private static LinkedList<Integer> currentLine;

    //assigned as a key to the above trees, updating by 1 after each commit
    private static int commitID;

    //current node
    private static int head;
    private static int currentHead;

    //stores all branches
    private static TreeSet<String> branches;
    private static String currentBranch;

    //since rebsae creates new commits, rebaseMap stores the orgininal commit IDS
    //for each branch for finding relevent head pointers.
    private static TreeSet<Integer> rebaseMap;

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * @init
     * initializes Gitlet system, creating the necesssary (mostly empty) data structures
     * and committing the initial commit */
    public static void init() {
        commitFiles = new TreeMap<Integer, TreeMap<String, byte[]>>();
        commitMessages = new TreeMap<Integer, String>();
        commitTimes = new TreeMap<Integer, String>();
        addedFiles = new TreeSet<String>();
        removedFiles = new TreeSet<String>();
        boolean wentThrough = false;
        File dotGitlet = new File("./.gitlet");
        if (!dotGitlet.exists()) {
            if (dotGitlet.mkdir()) {
                wentThrough = true;
            }
        } else {
            System.out.println(
                "A gitlet version control system "
                + " already exists in the current directory.");
        }
        if (wentThrough) {

            //create dummy inital commit file and commit it
            String cFile = ".gitlet/init.txt";
            Ser.createFile(cFile, "Repo Initialized");
            TreeMap<String, byte[]> initCommit = new TreeMap<String, byte[]>();
            initCommit.put("init.txt", Ser.readFile(cFile));
            commitFiles.put(0, initCommit);
            commitMessages.put(0, "initial commit");
            String d = df.format(new Date());
            commitTimes.put(0, d);
            commitID = 0;
            head = 0;
            int masterHead = head;
            Ser.serialize(new File(".gitlet/masterHead.ser"), (Object) masterHead);
            currentLine = new LinkedList<Integer>();
            currentLine.add(0);
            Ser.serialize(new File(".gitlet/masterCommits.ser"), (Object) currentLine);
            currentHead = masterHead;


            rebaseMap = new TreeSet<Integer>();
            //add "master" to the list of branches, and set it as currentBranch;
            branches = new TreeSet<String>();
            currentBranch = "master";
            branches.add(currentBranch);
            saveData();
            
            



        }

    }
    /**
     * @add
     * if the file is absent from the commit Tree or has been altered, adds filename to 
     * addedQueue, or if filename is the removedQueue, removes it from there.*/
    public static void add(String filename) throws FileNotFoundException, IOException {
        File requested = new File(filename);
        loadData();
        if (!requested.exists()) {
            System.out.println("File does not exist.");
        } else {
            if (removedFiles.contains(filename)) {
                removedFiles.remove(filename);
            } else if (commitFiles.get(currentHead).containsKey(filename)) {
                byte[] mostRecent = commitFiles.get(currentHead).get(filename);
                if (!Arrays.equals(mostRecent, Ser.readFile(filename))) {
                    addedFiles.add(filename);
                } else {
                    System.out.println("File has not been modified since the last commit.");
                }
            } else {
                addedFiles.add(filename);
            }
        }
        saveData();
    }

    /**
     * @commit
     * if anything is in the addedQueue, adds the relevent data to the commitMap.*/
     
    public static void commit(String message) throws IOException, FileNotFoundException {
        TreeMap<String, byte[]> ts = new TreeMap<String, byte[]>();
        loadData();
        if (addedFiles.isEmpty() && removedFiles.isEmpty()) {
            System.out.println("No changes added to the commit.");
        } else {
            for (String s : addedFiles) {
                byte[] bytes = Ser.readFile(s);
                ts.put(s, bytes);
            }
            
            TreeMap<String, byte[]> tm = commitFiles.get(currentHead);
            
            for (String s1 : tm.keySet()) {
                if (!ts.containsKey(s1) && !removedFiles.contains(s1)) {
                    ts.put(s1, tm.get(s1));
                }
            }
            commitFiles.put(commitID + 1, ts);
            commitMessages.put(commitID + 1, message);
            String d = df.format(new Date());
            commitTimes.put(commitID + 1, d);
            addedFiles = new TreeSet<String>();
            removedFiles = new TreeSet<String>();
            currentLine.add(commitID + 1);
            Ser.serialize(new File(".gitlet/" + currentBranch + "Commits.ser"),
                (Object) currentLine);
            commitID += 1;
            currentHead = currentLine.pollLast();

            saveData();
        }
    }
    /**
     * @log
     * prints reverse seqential information on the commits for the currentBranch;
     */
    public static void log() throws IOException {
        loadData();
        String sep = "====";
        Iterator<Integer> it = currentLine.descendingIterator();
        while (it.hasNext()) {
            int i = it.next();
            System.out.println(sep);
            System.out.println("Commit " + i + ".");
            System.out.println(commitTimes.get(i));
            System.out.println(commitMessages.get(i));
            System.out.println();
        }
    }
    /**
     * @global-log
     * prints reverse seqential info on all commits every made
     */
    public static void globalLog() throws IOException {
        loadData();
        String sep = "====";
        for (int i = commitID; i >= 0; i--) {
            System.out.println(sep);
            System.out.println("Commit " + i + ".");
            System.out.println(commitTimes.get(i));
            System.out.println(commitMessages.get(i));
            System.out.println();
        }
    }

    /**
     * @checkout
     * if branch exists by filename, switch to that branch and restore all files to their state 
     * in its head. Default checkout for files reverts the current file state to the most recent 
     * commit and uses -1 as the commit parameter. Anything other than -1 reverts files to their 
     * state at that commit ID.*/
    public static void checkout(String filename, int commit) throws IOException {
        branches = (TreeSet<String>) Ser.deserialize(new 
                File(".gitlet/branches.ser"));
        if (!new File(filename).exists() && !branches.contains(filename)) {
            System.out.println("File does not exist in the most"
                + " recent commit, or no such branch exists.");
        } else {
            loadData();
            if (currentBranch.equals(filename)) {
                System.out.println("No need to checkout the current branch.");
            } else if (branches.contains(filename)) {
                currentBranch = filename;
                currentHead = (int) Ser.deserialize(new 
                    File(".gitlet/" + filename + "Head.ser"));
                for (String s : commitFiles.get(currentHead).keySet()) {
                    byte[] b = commitFiles.get(currentHead).get(s);
                    Ser.bytesToFile(b, s);
                }
                if (new File(".gitlet/" + filename + "Commits.ser").exists()) {
                    currentLine = (LinkedList<Integer>) Ser.deserialize(new 
                        File(".gitlet/" + filename + "Commits.ser"));
                }
                saveData();
            } else {
                if (commit == -1) {
                    commit = currentHead;
                }
                if (!commitFiles.get(commit).containsKey(filename)) {
                    System.out.println("File does not exist in the most recent commit,"
                        + " or no such branch exists.");
                } else {
                    byte[] b = commitFiles.get(commit).get(filename);
                    Ser.bytesToFile(b, filename);
                }
            }
        }
    }
    
    /**
     * @remove
     * if a file is in the addedQueue, remove it
     * otherwise add it to the removedQueue (if it exists in both cases)*/
    public static void remove(String filename) throws IOException {
        loadData();
        //System.out.println(addedFiles);
        if (addedFiles.contains(filename)) {
            addedFiles.remove(filename);
            //Ser.recursiveDelete(new File(".gitlet/" + filename + ".record.ser"));
        } else if (!addedFiles.contains(filename)
            && !commitFiles.get(currentHead).containsKey(filename)) {
            System.out.println("No reason to remove the file.");
        } else {
            removedFiles.add(filename);
        }
        saveData();
    }

    /**
     * @status
     * prints info about the current state of the system ie branch, staged and marked files. 
     */
    public static void status() throws IOException {
        loadData();
        System.out.println("=== Branches ===");
        for (String s0 : branches) {
            if (s0.equals(currentBranch)) {
                System.out.println("*" + s0);
            } else {
                System.out.println(s0);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String s : addedFiles) {
            System.out.println(s);
        }
        System.out.println("\n=== Files Marked For Removal ===");
        for (String s1 : removedFiles) {
            System.out.println(s1);
        }
        System.out.println();
    }

    /**
     * @branch
     * creates a new branch, but does not switch to it automatically.
     * contains all revelent data from the current head and its predecessors
     */
    public static void branch(String branchName) throws IOException {
        loadData();
        if (branches.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {

            File newBranch = new File(".gitlet/" + branchName + ".ser");
            File commitLineFile = new File(".gitlet/" + branchName + "Commits.ser");
            File branchHeadFile = new File(".gitlet/" + branchName + "Head.ser");

            int branchHead = currentHead;
            LinkedList<Integer> commitLine = new LinkedList<Integer>();
            for (int i : currentLine) {
                commitLine.add(i);
            }
            branches.add(branchName);
            Ser.serialize(new File(".gitlet/" + branchName + "Head.ser"), (Object) branchHead);
            Ser.serialize(new File(".gitlet/" + branchName + "Commits.ser"), (Object) commitLine);
        }
        saveData();
    }


    /**
     *@find
     * prints out the commitIDs, line by line, that were committed with the passed in
     * commit message */
    public static void find(String commitMessage) throws IOException {
        loadData();
        for (int i = 0; i < commitMessages.size(); i++) {
            if (commitMessages.get(i).equals(commitMessage)) {
                System.out.println(i);
            }
        }
    }

    /**
     * @rm-branch
     * removes the passed in branch (ie deletes the pointer), keeps all commits
     */
    public static void rmBranch(String branchName) throws IOException {
        loadData();
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (currentBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branches.remove(branchName);
            Ser.recursiveDelete(new File(".gitlet/" + branchName + "Commits.ser"));
            Ser.recursiveDelete(new File(".gitlet/" + branchName + "Head.ser"));
        }
        saveData();
    }

    /**
     *@reset
     * reverts all files their state at the commitID, changes current head also
     */
    public static void reset(int id) throws IOException {
        loadData();
        if (!commitFiles.keySet().contains(id)) {
            System.out.println("No commit with that id exists.");
        } else {
            TreeMap<String, byte[]> node = commitFiles.get(id);
            for (String filePath : node.keySet()) {
                Ser.bytesToFile(node.get(filePath), filePath);
            }
            currentHead = id;
        }
        saveData();
    }

    //helper reset method that doesnt change the currentHead
    public static void reset2(int id) throws IOException {
        loadData();
        if (!commitFiles.keySet().contains(id)) {
            System.out.println("No commit with that id exists.");
        } else {
            TreeMap<String, byte[]> node = commitFiles.get(id);
            for (String filePath : node.keySet()) {
                Ser.bytesToFile(node.get(filePath), filePath);
            }
        }
        saveData();
    }

    //helper method to check for files in the given branch that were 
    // added before the split point
    public static void mergeCheckTracked(String branchName) throws IOException {
        loadData();
        int splitPoint = findSplitPoint(currentBranch, branchName);
        int givenHead = (int) Ser.deserialize(new File(".gitlet/" + branchName + "Head.ser"));
        LinkedList<Integer> givenLine = (LinkedList<Integer>) Ser.deserialize(new 
            File(".gitlet/" + branchName + "Commits.ser"));
        TreeMap<String, byte[]> commitAtSplit = commitFiles.get(splitPoint); //split point data
        TreeMap<String, byte[]> commitAtGiven = commitFiles.get(givenHead);
        TreeMap<String, byte[]> commitAtCurrent = commitFiles.get(currentHead);
        boolean givenMod = false;
        boolean currentMod = false;
        TreeSet<String> givenModified = new TreeSet<String>();
        TreeSet<String> currentModified = new TreeSet<String>();

        for (int i : givenLine) {
            if (i > splitPoint) {
                TreeMap<String, byte[]> tm = commitFiles.get(i);
                for (String s : tm.keySet()) {
                    if (!commitAtSplit.containsKey(s)) {
                        givenMod = true;
                        givenModified.add(s);
                    }
                }

            }
        }
        for (int i : currentLine) {
            if (i > splitPoint) {
                TreeMap<String, byte[]> tm = commitFiles.get(i);
                for (String s : tm.keySet()) {
                    if (!commitAtSplit.containsKey(s)) {
                        currentMod = true;
                        currentModified.add(s);
                    }
                }

            }
        }
        if (givenMod && !currentMod) {
            for (String s : givenModified) {
                Ser.bytesToFile(commitAtGiven.get(s), s);
            }
        }
        if (givenMod && currentMod) {
            for (String s1 : givenModified) {
                for (String s2 : currentModified) {
                    if (s1.equals(s2)) {
                        Ser.bytesToFile(commitAtCurrent.get(s1), s1 + ".conflicted");

                    } else {
                        Ser.bytesToFile(commitAtGiven.get(s1), s1);
                    }

                }
            }
        }

    }
    /**
     * @merge
     * if the given branch has files different from current and current hasnt changed 
     * since the split point, revert all relevent data in current
     * to their state in given, if both have changed, do nothing except create .conflicted file
     * otherwise do nothing */
    public static void merge(String branchName) throws IOException {
        loadData();
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (currentBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
        } else {
            int splitPoint = findSplitPoint(currentBranch, branchName);
    
            int givenHead = (int) Ser.deserialize(new File(".gitlet/" + branchName + "Head.ser"));
            LinkedList<Integer> givenLine = (LinkedList<Integer>) Ser.deserialize(new 
                File(".gitlet/" + branchName + "Commits.ser"));
            TreeMap<String, byte[]> commitAtSplit = commitFiles.get(splitPoint); //split point data
            TreeMap<String, byte[]> commitAtGiven = commitFiles.get(givenHead);
            TreeMap<String, byte[]> commitAtCurrent = commitFiles.get(currentHead);
            for (String s : commitAtSplit.keySet()) {
                boolean givenMod = false;
                boolean currentMod = false;
                byte[] splitFile = commitAtSplit.get(s);
                if (commitAtGiven.keySet().contains(s)) {
                    if (!Arrays.equals(splitFile, commitAtGiven.get(s))) {
                        givenMod = true;
                    }
                } 
                
                if (commitAtCurrent.keySet().contains(s)) {
                    if (!Arrays.equals(splitFile, commitAtCurrent.get(s))) {
                        currentMod = true;
                    }
                } 

                if (!givenMod && currentMod) {
                    continue;
                } else if (givenMod && !currentMod) {
                    Ser.bytesToFile(commitAtGiven.get(s), s);
                } else if (givenMod && currentMod) {
                    Ser.bytesToFile(commitAtCurrent.get(s), s + ".conflicted");
                } else {
                    mergeCheckTracked(branchName);
                }   
            }
        }

    }


    public static void rebase(String branchName) throws IOException {
        loadData();
        int givenHead = (int) Ser.deserialize(new File(".gitlet/" + branchName + "Head.ser"));
        String rbf = ".gitlet/" + currentBranch + "RebaseMap.ser";
        TreeSet<Integer> rbm = null;
        if (new File(rbf).exists()) {
            rbm = (TreeSet<Integer>) Ser.deserialize(
                new File(rbf));
        }
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (currentBranch.equals(branchName)) {
            System.out.println("Cannot rebase a branch onto itself.");
        } else if (currentLine.contains(givenHead)) {
            System.out.println("Already up-to-date.");
        } else if (rbm != null && rbm.contains(givenHead)) {
            System.out.println("Already up-to-date.");
        } else {
            LinkedList<Integer> givenBranchLine = (LinkedList<Integer>) Ser.deserialize(new
                File(".gitlet/" 
                + branchName + "Commits.ser"));
            LinkedList<Integer> toBeAdded = new LinkedList<Integer>();
            LinkedList<Integer> lineHolder = new LinkedList<Integer>();
            TreeSet<Integer> rebaseMp = new TreeSet<Integer>();
            for (int i : currentLine) {
                if (i > findSplitPoint(currentBranch, branchName)) {
                    lineHolder.add(i);
                }   
            }
            for (int i : givenBranchLine) {
                if (i <= findSplitPoint(currentBranch, branchName)) {
                    toBeAdded.add(i);
                } else {
                    TreeMap<String, byte[]> files = commitFiles.get(i);
                    Gitlet.reset2(i);

                    for (String s : files.keySet()) {
                        if (hasChanged(s)) {
                            Gitlet.add(s);
                        }
                    }
                    Gitlet.commit(commitMessages.get(i));
                    toBeAdded.add(commitID);
                    rebaseMp.add(i);
                }

            }
            for (int i : lineHolder) {
                toBeAdded.add(i);
            }
            currentLine = toBeAdded;
            currentHead = toBeAdded.getLast();
            Ser.serialize(new File(".gitlet/" + currentBranch + "Commits.ser"), 
                (Object) currentLine);
            Ser.serialize(new File(".gitlet/" + currentBranch + "RebaseMap.ser"),
                (Object) rebaseMp);
            saveData();



        }
    }

    public static boolean rebaseFailed(String branchName) throws IOException {
        loadData();
        int givenHead = (int) Ser.deserialize(new File(".gitlet/" + branchName + "Head.ser"));
        String rbf = ".gitlet/" + currentBranch + "RebaseMap.ser";
        TreeSet<Integer> rbm = null;
        if (new File(rbf).exists()) {
            rbm = (TreeSet<Integer>) Ser.deserialize(
                new File(rbf));
        }
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        } else if (currentBranch.equals(branchName)) {
            System.out.println("Cannot rebase a branch onto itself.");
            return true;
        } else if (currentLine.contains(givenHead)) {
            System.out.println("Already up-to-date.");
            return true;
        } else if (rbm != null && rbm.contains(givenHead)) {
            System.out.println("Already up-to-date.");
            return true;
        } 
        return false;
    }

    public static void saveRebaseState(LinkedList<Integer> line, TreeSet<Integer> map) {
        currentLine = line;
        currentHead = line.getLast();
        Ser.serialize(new File(".gitlet/" + currentBranch + "Commits.ser"), 
            (Object) currentLine);
        Ser.serialize(new File(".gitlet/" + currentBranch + "RebaseMap.ser"),
            (Object) map);
        saveData();
    }


    

    public static void interactiveRebase(String branchName) throws IOException {
        int givenHead = (int) Ser.deserialize(new File(".gitlet/" + branchName + "Head.ser"));
        String rbf = ".gitlet/" + currentBranch + "RebaseMap.ser";
        TreeSet<Integer> rbm = null;
        if (new File(rbf).exists()) {
            rbm = (TreeSet<Integer>) Ser.deserialize(
                new File(rbf));
        } 
        if (!rebaseFailed(branchName)) {
            LinkedList<Integer> givenBranchLine = (LinkedList<Integer>) Ser.deserialize(new
                File(".gitlet/" 
                + branchName + "Commits.ser"));
            LinkedList<Integer> toBeAdded = new LinkedList<Integer>();
            LinkedList<Integer> lineHolder = new LinkedList<Integer>();
            TreeSet<Integer> rebaseMp = new TreeSet<Integer>();
            TreeMap<String, byte[]> files = null;
            boolean interacting;
            for (int i : currentLine) {
                if (i > findSplitPoint(currentBranch, branchName)) {
                    lineHolder.add(i);
                }   
            }
            for (int i : givenBranchLine) {
                interacting = true;
                if (i <= findSplitPoint(currentBranch, branchName)) {
                    toBeAdded.add(i);
                } else {
                    System.out.println("\nCurrenly replaying:");
                    System.out.println("\nCommit " + i + ".");
                    System.out.println(commitTimes.get(i) + "\n" + commitMessages.get(i) + "\n");
                    while (interacting) {
                        System.out.println("Would you like to (c)ontinue, "
                            + "(s)kip this commit, or change this commit's (m)essage?");
                        String line = StdIn.readLine();
                        String answer = line.split(" ")[0];
                        switch (answer) {
                            case "c":
                                files = commitFiles.get(i);
                                Gitlet.reset2(i);
                                for (String s : files.keySet()) {
                                    if (hasChanged(s)) {
                                        Gitlet.add(s);
                                    }
                                }
                                Gitlet.commit(commitMessages.get(i));
                                toBeAdded.add(commitID);
                                rebaseMp.add(i);
                                interacting = false;
                                break;
                            case "m":
                                System.out.println("\nPlease enter a new message for this commit.");
                                System.out.println();
                                String newMessage = StdIn.readLine();
                                files = commitFiles.get(i);
                                Gitlet.reset2(i);
                                for (String s : files.keySet()) {
                                    if (hasChanged(s)) {
                                        Gitlet.add(s);
                                    }
                                }
                                Gitlet.commit(newMessage);
                                toBeAdded.add(commitID);
                                rebaseMp.add(i);
                                interacting = false;
                                break;
                            case "s":
                                interacting = false;
                                break;
                            default:
                                break;
                        } 
                    }
                }
            }
            for (int j : lineHolder) {
                toBeAdded.add(j);
            }
            saveRebaseState(toBeAdded, rebaseMp);
        }
    }




    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                if (args[0].equals("init")) {
                    init();
                } else if (args[0].equals("commit")) {
                    System.out.println("Please enter a commit message.");
                } else if (args[0].equals("log")) {
                    log();
                } else if (args[0].equals("status")) {
                    status();
                } else if (args[0].equals("global-log")) {
                    globalLog();
                } 
            }
            if (args.length == 2) {
                String command = args[0];
                if (command.equals("reset2")) {
                    reset2(Integer.parseInt(args[1]));
                }
                if (command.equals("rebase")) {
                    if (dangerApproved()) {
                        rebase(args[1]);
                    }
                }
                if (command.equals("i-rebase")) {
                    if (dangerApproved()) {
                        interactiveRebase(args[1]);
                    }
                }
                if (command.equals("reset")) {
                    if (dangerApproved()) {
                        reset(Integer.parseInt(args[1]));
                    }
                }
                if (command.equals("merge")) {
                    if (dangerApproved()) {
                        merge(args[1]);
                    }    
                }
                if (command.equals("add")) {
                    add(args[1]);
                } else if (command.equals("commit")) {
                    commit(args[1]);
                } else if (command.equals("checkout")) {
                    if (dangerApproved()) {
                        checkout(args[1], -1);
                    }
                } else if (command.equals("rm")) {
                    remove(args[1]);
                } else if (command.equals("branch")) {
                    branch(args[1]);
                } else if (command.equals("find")) {
                    find(args[1]);
                } else if (command.equals("rm-branch")) {
                    rmBranch(args[1]);
                } 

            } 
            if (args.length == 3) {
                if (args[0].equals("checkout")) {
                    if (dangerApproved()) {
                        checkout(args[1], Integer.parseInt(args[2]));
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        
        }
    }

/////////////////////// UTILITY FUNCTIONS ////////////////////////

    public static boolean hasChanged(String filename) throws IOException {
        loadData();
        TreeMap<String, byte[]> tm = commitFiles.get(currentHead);
        if (tm.containsKey(filename)) {
            byte[] mostRecent = tm.get(filename);
            byte[] current = Ser.readFile(filename);
            if (!Arrays.equals(mostRecent, current)) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static int findSplitPoint(String branch1, String branch2) {
        LinkedList<Integer> b1Commits = (LinkedList<Integer>) Ser.deserialize(new File(".gitlet/"
            + branch1 + "Commits.ser"));
        LinkedList<Integer> b2Commits = (LinkedList<Integer>) Ser.deserialize(new File(".gitlet/"
            + branch2 + "Commits.ser"));
        int max = 0;

        for (int b1 : b1Commits) {
            if (b2Commits.contains(b1)) {
                if (b1 > max) {
                    max = b1;
                }
            }
        }
        return max;
    }


    public static boolean dangerApproved() {
        while (true) {
            System.out.print("Warning: The command you entered "
                + "may alter the files in your working directory. "
                + "Uncommitted changes may be lost. "
                + "Are you sure you want to continue? (yes/no)\n");
            String line = StdIn.readLine();
            String answer = line.split(" ")[0];
            switch (answer) {
                case "yes":
                    return true;
                default:
                    return false;
            }
        }
    }

    //load all currently serialized data...wastes memory/runtime for now, will
    //be more selective later
    public static void loadData() throws IOException, FileNotFoundException {
        addedFiles = (TreeSet<String>) Ser.deserialize(new File(".gitlet/addedQueue.ser"));
        removedFiles = (TreeSet<String>) Ser.deserialize(new
                                                File(".gitlet/removedQueue.ser"));
        commitID = (Integer) Ser.deserialize(new File(".gitlet/commitID.ser"));
        commitMessages = (TreeMap<Integer, String>) Ser.deserialize(new 
                                                File(".gitlet/commitMessages.ser"));
        commitTimes = (TreeMap<Integer, String>) Ser.deserialize(new 
                                                File(".gitlet/commitTimes.ser"));
        commitFiles = (TreeMap<Integer, TreeMap<String, byte[]>>) Ser.deserialize(new
                                                File(".gitlet/commitFiles.ser"));
        head =   (Integer) Ser.deserialize(new File(".gitlet/head.ser"));

        branches = (TreeSet<String>) Ser.deserialize(new
            File(".gitlet/branches.ser"));

        currentBranch = (String) Ser.deserialize(new File(".gitlet/currentBranch.ser"));
        currentHead = (int) Ser.deserialize(new File(".gitlet/" + currentBranch + "Head.ser"));
        currentLine = (LinkedList<Integer>) Ser.deserialize(new File(".gitlet/"
            + currentBranch + "Commits.ser"));
        
       

    }
    //saved all updated files to replace their old serialized version
    //will be more selective later
    public static void saveData() {
        Ser.serialize(new File(".gitlet/addedQueue.ser"), (Object) addedFiles);
        Ser.serialize(new File(".gitlet/removedQueue.ser"), (Object) removedFiles);
        Ser.serialize(new File(".gitlet/commitID.ser"), (Object) commitID);
        Ser.serialize(new File(".gitlet/commitMessages.ser"), (Object) commitMessages);
        Ser.serialize(new File(".gitlet/commitFiles.ser"), (Object) commitFiles);
        Ser.serialize(new File(".gitlet/commitTimes.ser"), (Object) commitTimes);
        Ser.serialize(new File(".gitlet/head.ser"), (Object) head);
        Ser.serialize(new File(".gitlet/currentBranch.ser"), (Object) currentBranch);
        Ser.serialize(new File(".gitlet/branches.ser"), (Object) branches);
        Ser.serialize(new File(".gitlet/currentHead.ser"), (Object) currentHead);

        //System.out.println(currentLine);
        //Ser.serialize(new File(".gitlet/" + currentBranch + "Commits.ser"), (Object) currentLine);
        Ser.serialize(new File(".gitlet/" + currentBranch + "Head.ser"), (Object) currentHead);
    }

}
                    


