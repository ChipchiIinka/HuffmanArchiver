package methods;

//Класс дерева кодов для кодирования методом Хаффмана
public class CodeTreeNode implements Comparable<CodeTreeNode> {
    Character content;
    int weight;
    CodeTreeNode left;
    CodeTreeNode right;

    public CodeTreeNode(Character content, int weight) {
        this.content = content;
        this.weight = weight;
    }

    public CodeTreeNode(Character content, int weight, CodeTreeNode left, CodeTreeNode right) {
        this.content = content;
        this.weight = weight;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(CodeTreeNode o) {
        return o.weight-weight;
    }

    public String getCodeForCharacter(Character ch, String parentPath){
        if(content == ch){
            return parentPath;
        } else {
            if (left != null){
                String path = left.getCodeForCharacter(ch, parentPath + 0);
                if (path != null){
                    return path;
                }
            }
            if (right != null){
                String path = right.getCodeForCharacter(ch, parentPath + 1);
                if (path != null){
                    return path;
                }
            }
        }
        return null;
    }
}
