package core;

import render.CS61BCharacter;
import render.ConcreteNode;
import render.NewLine;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by rycla on 12/19/2016.
 */
public class IO {

    public static void saveFile(ConcreteNode rootNode, String fileName){
        File file = new File(fileName);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));

            Node node = rootNode;
            while(node != null){
                node = node.next;
                if(node instanceof NewLine){
                    writer.write((int)'\n');
                }
                else if (node instanceof CS61BCharacter){
                    CS61BCharacter character = (CS61BCharacter)node;
                    writer.write((int)character.getCharacter().charAt(0));
                }
            }
            writer.write((int)1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadFile(String arg, ConcreteNode rootNode, Cursor cursor) throws IOException {
        // replace this with a known encoding if possible
        Charset encoding = Charset.defaultCharset();
        File file = new File(arg);
        handleFile(file, encoding, rootNode, cursor);
    }

    private static void handleFile(File file, Charset encoding, ConcreteNode rootNode, Cursor cursor)
            throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer, rootNode, cursor);
        }
    }

    private static void handleCharacters(Reader reader, ConcreteNode rootNode, Cursor cursor)
            throws IOException {

        Node currentNode = rootNode;

        int r;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            Node newNode = null;
            if(ch == '\n'){
                NewLine newLine = new NewLine();
                newNode = newLine;
            }
            else {
                CS61BCharacter character = new CS61BCharacter(Character.toString(ch));
                newNode = character;
            }
            currentNode.addNodeAfterThis(newNode);
            currentNode = newNode;
        }
        currentNode.addNodeAfterThis(cursor);
    }
}
