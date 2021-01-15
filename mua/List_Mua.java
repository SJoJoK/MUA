package mua;
import java.util.ArrayList;
import java.util.Arrays;

public class List_Mua extends Value_Mua {
    String regex_func = "^\\[\\[.*\\] \\[.*\\]\\]";
    String regex_num = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
    boolean isfunc;
    ArrayList<String> list_value;
    ArrayList<Value_Mua> list_mua_value;
    int size;
//    static StringBuilder vs2l(ArrayList<Value_Mua> vs)
//    {
//        StringBuilder literal = new StringBuilder("[");
//        for(int i=0;i<vs.size();i++)
//        {
//            literal.append(vs.get(i).literal);
//            literal.append(" ");
//        }
//        literal.deleteCharAt(literal.length() - 1);
//        literal.append("]");
//        return literal;
//    }
    List_Mua(ArrayList<Value_Mua> vs)
    {
        super();
        Type_Mua=TYPE_MUA.LIST;
        list_mua_value = new ArrayList<>(vs);
        size = list_mua_value.size();
        StringBuilder literal = new StringBuilder("[");
        for(int i=0;i<vs.size();i++)
        {
            literal.append(vs.get(i).literal);
            literal.append(" ");
        }
        if(literal.charAt(literal.length()-1) == ' ')
            literal.deleteCharAt(literal.length() - 1);
        literal.append("]");
       this.literal=literal.toString();
        literal.deleteCharAt(0);
        literal.deleteCharAt(literal.length() - 1);
        String[] nodes = literal.toString().replace("("," ( ").replace(")"," ) ")
                .replace("["," [ ").replace("]"," ] ")
                .replace("+", " + ").replace("-", " - ")
                .replace("*", " * ").replace("/", " / ")
                .replace("%", " % ").trim().split("\\s+");
        list_value = new ArrayList<>(Arrays.asList(nodes));
        if(this.literal.matches(regex_func)) isfunc=true;

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
        list_value = new ArrayList<>(Arrays.asList(nodes));
        list_mua_value = new ArrayList<>();
        if(this.literal.matches(regex_func)) isfunc=true;
        ArrayList<String> list_mua_value_tmp = new ArrayList<>(list_value);
        while(!list_mua_value_tmp.isEmpty())
        {
            if (list_mua_value_tmp.get(0).equals(""))
            {
                list_mua_value_tmp.remove(0);
            }
            else if(list_mua_value_tmp.get(0).matches(regex_num))
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                list_mua_value.add(new Number_Mua(temp));
            }
            //word
            else if(list_mua_value_tmp.get(0).charAt(0)=='\"')
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                temp=temp.substring(1);
                String l = '\"' + temp;
                String v = temp;
                list_mua_value.add(new Word_Mua(l,v));
            }
            //boolean
            else if(list_mua_value_tmp.get(0).equals("true")||list_mua_value_tmp.get(0).equals("false"))
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                list_mua_value.add(new Bool_Mua(temp));
            }
            //list
            else if(list_mua_value_tmp.get(0).charAt(0)=='[')
            {
                list_mua_value.add(Interpreter_Mua.build_list(list_mua_value_tmp));
            }
            //word without "
            else
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                String l = '\"' + temp;
                list_mua_value.add(new Word_Mua(l, temp));
            }
        }
        size = list_mua_value.size();
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
        list_mua_value = new ArrayList<>();
        if(this.literal.matches(regex_func)) isfunc=true;
        ArrayList<String> list_mua_value_tmp = new ArrayList<>(list_value);
        while(!list_mua_value_tmp.isEmpty())
        {
            if (list_mua_value_tmp.get(0).equals(""))
            {
                list_mua_value_tmp.remove(0);
            }
            else if(list_mua_value_tmp.get(0).matches(regex_num))
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                list_mua_value.add(new Number_Mua(temp));
            }
            //word
            else if(list_mua_value_tmp.get(0).charAt(0)=='\"')
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                temp=temp.substring(1);
                String l = '\"' + temp;
                String vv = temp;
                list_mua_value.add(new Word_Mua(l,vv));
            }
            //boolean
            else if(list_mua_value_tmp.get(0).equals("true")||list_mua_value_tmp.get(0).equals("false"))
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                list_mua_value.add(new Bool_Mua(temp));
            }
            //list
            else if(list_mua_value_tmp.get(0).charAt(0)=='[')
            {
                list_mua_value.add(Interpreter_Mua.build_list(list_mua_value_tmp));
            }
            //word without "
            else
            {
                String temp = list_mua_value_tmp.get(0);
                list_mua_value_tmp.remove(0);
                String l = '\"' + temp;
                list_mua_value.add(new Word_Mua(l, temp));
            }
        }
        size = list_mua_value.size();
    }
    void print_list_mua()
    {
        int i=0;
        for(i=0;i<list_mua_value.size() - 1;i++)
        {
            Value_Mua value = list_mua_value.get(i);
            switch(value.Type_Mua)
            {
                case WORD:System.out.print(value.literal.substring(1));break;
                case LIST:System.out.print(value.literal);break;
//                case LIST:value.toList().print_list_mua();break;
                case BOOL:System.out.print(value.literal);break;
                case NUMBER:System.out.print(value.literal);break;
                default:break;
            }
            System.out.print(" ");
        }
        Value_Mua value = list_mua_value.get(i);
        switch(value.Type_Mua)
        {
            case WORD:System.out.print(value.literal.substring(1));break;
            case LIST:System.out.print(value.literal);break;
//                case LIST:value.toList().print_list_mua();break;
            case BOOL:System.out.print(value.literal);break;
            case NUMBER:System.out.print(value.literal);break;
            default:break;
        }
        System.out.println();
    }
}
