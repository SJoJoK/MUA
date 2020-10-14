package mua;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.*;

public class Interpreter_Mua {
    HashMap<String,Bool_Mua> Bool_Map;
    HashMap<String,List_Mua> List_Map;
    HashMap<String,Number_Mua> Number_Map;
    HashMap<String,Word_Mua> Word_Map;
    Scanner scan;
    public void begin()
    {
        Bool_Map = new HashMap<>();
        List_Map = new HashMap<>();
        Number_Map = new HashMap<>();
        Word_Map = new HashMap<>();
        scan = new Scanner(System.in);
        String inst = new String();
        while(scan.hasNextLine())
        {
            inst = scan.nextLine();
            if(inst.equals("")) break;
            // 改成循环
            String[] nodes = inst.split(" ");
            ArrayList<String> nodes_list = new ArrayList<String>(Arrays.asList(nodes));
            interpret(nodes_list);
        }
    }
    Value_Mua interpret(ArrayList<String> nodes)
    {
        //thing
        if(nodes.get(0).charAt(0)==':')
        {
            nodes.set(0, nodes.get(0).substring(1));
            Value_Mua value =interpret(nodes);
            Word_Mua name = value.toWord();
            return thing_mua(name);
        }
        //number
        else if(nodes.get(0).matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Number_Mua(temp);
        }
        //word
        else if(nodes.get(0).charAt(0)=='\"')
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            temp=temp.substring(1);
            String l = '\"' + temp;
            String v = temp;
            return new Word_Mua(l,v);
        }
        //boolean
        else if(nodes.get(0).equals("true")||nodes.get(0).equals("false"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Bool_Mua(temp);
        }
        //list
        else if(nodes.get(0).matches("(^\\[\\]$)"))
        {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new List_Mua('['+temp+']',temp);
        }
        else
        {
            switch (nodes.get(0))
            {
                case "make" :
                {
                    String l = nodes.get(1);
                    String v = nodes.get(1).substring(1);
                    nodes.remove(0);
                    nodes.remove(0);
                    Word_Mua name = new Word_Mua(l,v);
                    Value_Mua value =interpret(nodes);
                    return make_mua(name, value);
                }
                case "thing":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return thing_mua(name);
                }
                case "print":
                {
                    nodes.remove(0);
                    return print_mua(interpret(nodes));
                }
                case "read":
                {
                    nodes.remove(0);
                    return read_mua();
                }
                case "add":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return add_mua(a.toNumber(),b.toNumber());
                }
                case "sub":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return sub_mua(a.toNumber(),b.toNumber());
                }
                case "mul":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return mul_mua(a.toNumber(),b.toNumber());
                }
                case "div":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return div_mua(a.toNumber(),b.toNumber());
                }
                case "mod":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    return mod_mua(a.toNumber(),b.toNumber());
                }
                default:
                {
                    //A word
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    String l = '\"' + temp;
                    String v = temp;
                    return new Word_Mua(l,v);
                }
            }
        }
    }
    Value_Mua make_mua(Word_Mua name, Value_Mua value)
    {
        switch(value.Type_Mua)
        {
            case WORD:Word_Map.put(name.word_value.toString(),new Word_Mua(value));break;
            case LIST:List_Map.put(name.word_value.toString(),new List_Mua(value));break;
            case BOOL:Bool_Map.put(name.word_value.toString(), new Bool_Mua(value));break;
            case NUMBER:Number_Map.put(name.word_value.toString(), new Number_Mua(value));break;
            default:Word_Map.put(name.word_value.toString(),new Word_Mua(value));break;
        }
        return value;
    }
    Value_Mua thing_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return Word_Map.get(name);
        else if(Number_Map.containsKey(name)) return Number_Map.get(name);
        else if(Bool_Map.containsKey(name)) return Bool_Map.get(name);
        else if(List_Map.containsKey(name)) return List_Map.get(name);
        else return new Value_Mua();
    }
    Value_Mua print_mua(Value_Mua value)
    {
        String str;
        switch(value.Type_Mua)
        {
            case WORD:str = value.literal.substring(1);break;
            case LIST:str = value.literal.substring(1,value.literal.length()-2);break;
            case BOOL:str = value.literal;break;
            case NUMBER:str = value.literal;break;
            default:str="";break;
        }
        System.out.println(str);
        return value;
    }
    Value_Mua read_mua()
    {
        String str = "";
        Value_Mua v;
        if (scan.hasNextLine())
        {
            str = scan.nextLine();
        }
        String judge = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
        if(str.matches(judge))
        {
            v = new Value_Mua(str);
            v.Type_Mua= Value_Mua.TYPE_MUA.NUMBER;
        }
        else
        {
            v = new Value_Mua('\"'+str);
            v.Type_Mua= Value_Mua.TYPE_MUA.WORD;
        }
        return v;
    }
    Number_Mua add_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value+b.number_value);
    }
    Number_Mua sub_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value-b.number_value);
    }
    Number_Mua mul_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value*b.number_value);
    }
    Number_Mua div_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value/b.number_value);
    }
    Number_Mua mod_mua(Number_Mua a, Number_Mua b)
    {
        return new Number_Mua(a.number_value%b.number_value);
    }

}
