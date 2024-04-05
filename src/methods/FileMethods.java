package methods;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class FileMethods {
    private static final EncodeDecodeMethods encodeDecodeMethods = new EncodeDecodeMethods();

    //Сохранение сжатой информации и таблицы частот в файл
    private static void saveToFile(Path outputPath, Map<Character, Integer> quantity, String bits) {
        try (DataOutputStream os =
                     new DataOutputStream(
                             new BufferedOutputStream(
                                     Files.newOutputStream(outputPath)))) {

            os.writeInt(quantity.size());
            for (Map.Entry<Character, Integer> entry : quantity.entrySet()) {
                os.writeChar(entry.getKey());
                os.writeInt(entry.getValue());
            }
            int compressedSizeBits = bits.length();
            BitArray bitArray = new BitArray(compressedSizeBits);
            for (int i = 0; i < bits.length(); i++) {
                bitArray.set(i, bits.charAt(i) != '0' ? 1 : 0);
            }

            os.writeInt(compressedSizeBits);
            os.write(bitArray.getBytes(), 0, bitArray.getSizeInBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Загрузка сжатой информации и таблицы частот из файла
    private static void loadFromFile(Path inputPath, Map<Character, Integer> quantities, StringBuilder bits) {
        try (DataInputStream is =
                     new DataInputStream(
                             new BufferedInputStream(
                                     Files.newInputStream(inputPath)))) {

            int quantityTableSize = is.readInt();
            for (int i = 0; i < quantityTableSize; i++) {
                quantities.put(is.readChar(), is.readInt());
            }
            int dataSizeBits = is.readInt();
            BitArray bitArray = new BitArray(dataSizeBits);
            is.read(bitArray.getBytes(), 0, bitArray.getSizeInBytes());

            for (int i = 0; i < bitArray.getSize(); i++) {
                bits.append(bitArray.get(i) != 0 ? "1" : "0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compressFile() {
        try {
            // загрузка содержимого файла в виде строки
            String content = new String(Files.readAllBytes(Paths.get("resources/inputFile.txt")));
            // вычисление таблицы частот с которыми встречаются символы в тексте
            TreeMap<Character, Integer> quantity = encodeDecodeMethods.countQuantity(content);

            ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>();

            // генерация листов будущего дерева для символов текста
            for (Character c : quantity.keySet()) {
                codeTreeNodes.add(new CodeTreeNode(c, quantity.get(c)));
            }
            // построение кодового дерева алгоритмом Хаффмана
            CodeTreeNode tree = encodeDecodeMethods.huffmanEncode(codeTreeNodes);

            // постоение таблицы префиксных кодов для символов исходного текста
            TreeMap<Character, String> codes = new TreeMap<>();
            for (Character c : quantity.keySet()) {
                codes.put(c, tree.getCodeForCharacter(c, ""));
            }

            // кодирование текста префиксными кодами
            StringBuilder encoded = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                encoded.append(codes.get(content.charAt(i)));
            }

            // сохранение сжатой информации в файл
            Path file = Paths.get("resources/archive.huf");
            saveToFile(file, quantity, encoded.toString());
            System.out.println("Файл успешно сжат.");
            System.out.println("Вес исходного файла: " + content.getBytes().length * 8 + " бит");
            System.out.println("Вес строки в сжатом файле: " + encoded.length() + " бит");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decompressFile() {
        try {
            ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>();
            TreeMap<Character, Integer> quantity = new TreeMap<>();
            StringBuilder encoded = new StringBuilder();


            // извлечение сжатой информации из файла
            Path file = Paths.get("resources/archive.huf");
            loadFromFile(file, quantity, encoded);

            // генерация листов и постоение кодового дерева Хаффмана на основе таблицы частот сжатого файла
            for (Character c : quantity.keySet()) {
                codeTreeNodes.add(new CodeTreeNode(c, quantity.get(c)));
            }
            CodeTreeNode tree = encodeDecodeMethods.huffmanEncode(codeTreeNodes);

            // декодирование обратно исходной информации из сжатой
            String decoded = encodeDecodeMethods.huffmanDecode(encoded.toString(), tree);

            // сохранение в файл декодированной информации
            Files.write(Paths.get("resources/outputFile.txt"), decoded.getBytes());
            System.out.println("Файл успешно расшифрован.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Run(){
        Scanner scanner = new Scanner(System.in);
        String massage = """
                Выберете действие введя цифру с нужной операцией:
                1 - Сжать файл.
                2 - Расшифровать сжатый файл.
                3 - Завершить программу.
                Чтобы снова вывести это сообщение, введите любой другой символ.
                """;
        System.out.println(massage);
        while (true){
            switch (scanner.next()) {
                case "1" -> {
                    compressFile();
                }
                case "2" -> {
                    decompressFile();
                }
                case "3" -> {
                    System.out.print("Программа успешно завершена.");
                    System.exit(0);
                }
                default -> {
                    System.out.println(massage);
                }
            }
        }
    }
}
