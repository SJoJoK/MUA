package mua;
import java.util.ArrayList;
import java.util.Arrays;

public class List_Mua extends Value_Mua {
    ArrayList<String> list_value;
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
    List_Mua(ArrayList<String> value)
    {
        super('['+value.toString().trim()+']');
        Type_Mua=TYPE_MUA.LIST;
        list_value = value;
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
        String[] temp = literal.toString().trim().split(" ");
        list_value = new ArrayList<String>(Arrays.asList(temp));
    }
    List_Mua(String l, String value)
    {
        super(l);
        Type_Mua=TYPE_MUA.LIST;
        value = value.trim();
        String[] temp = value.split(" ");
        list_value = new ArrayList<String>(Arrays.asList(temp));
    }
    List_Mua(Value_Mua v)
    {
        super(v.literal);
        Type_Mua=TYPE_MUA.LIST;
        String value = v.literal.trim();
        value=value.substring(1,value.length()-2);
        String[] temp = value.split(" ");
        list_value = new ArrayList<String>(Arrays.asList(temp));
    }
    void append(String e)
    {
        literal = literal.substring(0,literal.length()-2);
        literal = literal + " " + e + "]";
        list_value.add(e);
    }


}
