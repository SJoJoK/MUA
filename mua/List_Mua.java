package mua;
import java.util.ArrayList;
import java.util.Arrays;

public class List_Mua extends Value_Mua {
    String regex_func = "^\\[\\[.*\\] \\[.*\\]\\]";
    ArrayList<String> list_value;
    Boolean isfunc = false;
    List_Mua()
    {
        super();
        Type_Mua=TYPE_MUA.LIST;
        list_value = new ArrayList<String>();
    }
    List_Mua(boolean x)
    {
        super("[]");
        Type_Mua=TYPE_MUA.LIST;
        list_value = new ArrayList<String>();
    }
    List_Mua(String value)
    {
        super('['+value.trim()+']');
        Type_Mua=TYPE_MUA.LIST;
        String[] temp = value.trim().split(" ");
        list_value = new ArrayList<String>(Arrays.asList(temp));
    }
    List_Mua(StringBuilder literal)
    {
        super(literal.toString());
        Type_Mua=TYPE_MUA.LIST;
        literal.deleteCharAt(0);
        literal.deleteCharAt(literal.length() - 1);
        String[] nodes = literal.toString().replace("("," ( ").replace(")"," ) ")
                .replace("["," [ ").replace("]"," ] ")
                .replace("+", " + ").replace("-", " - ")
                .replace("*", " * ").replace("/", " / ")
                .replace("%", " % ").trim().split("\\s+");
        list_value = new ArrayList<String>(Arrays.asList(nodes));
        if(this.literal.matches(regex_func)) isfunc=true;
    }
    List_Mua(Value_Mua v)
    {
        super(v.literal);
        Type_Mua=TYPE_MUA.LIST;
        String value = v.literal.trim();
        value=value.substring(1,value.length()-1);
        String[] nodes = value.replace("("," ( ").replace(")"," ) ")
                .replace("["," [ ").replace("]"," ] ")
                .replace("+", " + ").replace("-", " - ")
                .replace("*", " * ").replace("/", " / ")
                .replace("%", " % ").trim().split("\\s+");
        list_value = new ArrayList<String>(Arrays.asList(nodes));
        if(this.literal.matches(regex_func)) isfunc=true;
    }
    void append(String e)
    {
        literal = literal.substring(0,literal.length()-2);
        literal = literal + " " + e + "]";
        list_value.add(e);
    }


}
