/**
 * @author Ben Miroglio
 */

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
 public class Ser { 

	public static Object deserialize(File filename) {
    byte[] ds;
    Object returned = null;
    try {
        InputStream file = new FileInputStream(filename);
        InputStream b = new BufferedInputStream(file);
        ObjectInputStream obj = new ObjectInputStream(b);
        ds = (byte[]) obj.readObject();
        ByteArrayInputStream bytes = new ByteArrayInputStream(ds);
        ObjectInputStream o = new ObjectInputStream(bytes);
        returned = o.readObject();
       

        
    } catch (IOException e) {
            String msg = "IOException while loading myCat.";
            System.out.println(msg);
    } catch (ClassNotFoundException e) {
        String msg = "ClassNotFoundException while loading myCat.";
        System.out.println(msg);
    }
    return returned;
	}

    //returns the content of filename as any array of bytes
	public static byte[] readFile(String fileName) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return encoded;
        } catch (IOException e) {
            return null;
        }


    }

    public static byte[] objectToBytes(Object o) throws IOException{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(b);
            objectOut.writeObject(o);
            objectOut.close();
            b.close();
            return b.toByteArray();
    }
    //helper method to save Object o to serilaized File f
    public static void serialize(File f, Object o) {
        try {
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(objectToBytes(o));
            oout.close();
            fout.close();
        } catch (IOException e) {
            System.out.println("IOException while saving");
        }
    }

      private static void writeFile(String fileName, String fileText) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, false);
            fw.write(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void bytesToFile(byte[] bytes, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            System.out.println("IO");
        }
    }


    public static String bytesToText(byte[] bytes) {
        File temp = new File("temp");
        bytesToFile(bytes, "temp");
        String s = getText("temp");
        recursiveDelete(temp);
        return s;
    
    }

    public static void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
    }

    public static String getText(String fileName) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(fileName));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public static void recursiveDelete(File d) {
        if (d.isDirectory()) {
            for (File f : d.listFiles()) {
                recursiveDelete(f);
            }
        }
        d.delete();
    }
}
    				