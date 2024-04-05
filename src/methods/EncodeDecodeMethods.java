package methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class EncodeDecodeMethods {

    //метод подсчета количества повторений каждого символа
    public TreeMap<Character, Integer> countQuantity(String text){
        TreeMap<Character, Integer> quantityMap = new TreeMap<>();

        for(int i = 0; i < text.length(); i++){
            Character c =text.charAt(i);
            Integer count = quantityMap.get(c);
            quantityMap.put(c, count != null ? count + 1 : 1);
        }
        return quantityMap;
    }

    //метод кодирования  Хаффмана
    public CodeTreeNode huffmanEncode(ArrayList<CodeTreeNode> codeTreeNodes){
        while (codeTreeNodes.size() > 1){
            Collections.sort(codeTreeNodes);
            CodeTreeNode left = codeTreeNodes.remove(codeTreeNodes.size() - 1);
            CodeTreeNode right = codeTreeNodes.remove(codeTreeNodes.size() - 1);

            CodeTreeNode parent = new CodeTreeNode(null, right.weight + left.weight, left, right);
            codeTreeNodes.add(parent);
        }
        return codeTreeNodes.get(0);
    }

    public String huffmanDecode(String encoded, CodeTreeNode tree) {
        StringBuilder decoded = new StringBuilder();

        CodeTreeNode node = tree;
        for (int i = 0; i < encoded.length(); i++) {
            node = encoded.charAt(i) == '0' ? node.left : node.right;
            if (node.content != null) {
                decoded.append(node.content);
                node = tree;
            }
        }
        return decoded.toString();
    }
}
