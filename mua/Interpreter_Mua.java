package mua;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.*;

public class Interpreter_Mua {
    String regex_num = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
    String regex_ops = "\\+|-|\\*|/|%|\\(|\\)";

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
            String[] nodes = inst.replace("("," ( ").replace(")"," ) ")
                                 .replace("["," [ ").replace("]"," ] ")
                                 .replace("+", " + ").replace("-", " - ")
                                 .replace("*", " * ").replace("/", " / ")
                                 .replace("%", " % ").trim().split("\\s+");
            ArrayList<String> nodes_list = new ArrayList<String>(Arrays.asList(nodes));
            interpret(nodes_list);
        }
    }
    Value_Mua interpret(List<String> nodes)
    {
        if(nodes.isEmpty()) return new Value_Mua("");
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
        else if(nodes.get(0).charAt(0)=='[')
        {
            Value_Mua l = build_list(nodes);
            return l;
        }
        //Infix
        else if(nodes.get(0).charAt(0)=='(')
        {
            Value_Mua l = infix(nodes);
            return l;
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
                case "erase":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return erase_mua(name);
                }
                case "isname":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    Word_Mua name = value.toWord();
                    return isname_mua(name);
                }
                case "run":
                {
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes);
                    List_Mua list = value.toList();
                    return run_mua(list);
                }
                //记得+run!
                case "eq":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = eq_mua(a,b);
                    return res;
                }
                case "gt":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = gt_mua(a,b);
                    return res;
                }
                case "lt":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = lt_mua(a,b);
                    return res;
                }
                case "and":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = and_mua(a.toBool(),b.toBool());
                    return res;
                }
                case "or":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = or_mua(a.toBool(),b.toBool());
                    return res;
                }
                case "not":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = not_mua(a.toBool());
                    return res;
                }
                case "if":
                {
                    nodes.remove(0);
                    Value_Mua j =interpret(nodes);
                    Value_Mua a =interpret(nodes);
                    Value_Mua b =interpret(nodes);
                    Value_Mua res = if_mua(j.toBool(),a.toList(),b.toList());
                    return res;
                }
                case "isnumber":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isnumber_mua(a);
                    return res;
                }
                case "isword":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isword_mua(a);
                    return res;
                }
                case "islist":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = islist_mua(a);
                    return res;
                }
                case "isbool":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isbool_mua(a);
                    return res;
                }
                case "isempty":
                {
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes);
                    Value_Mua res = isempty_mua(a);
                    return res;
                }
                default:
                {
                    //A word
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    String l = '\"' + temp;
                    return new Word_Mua(l, temp);
                }
            }
        }
    }
    Value_Mua interpret(List<String> nodes, List<Value_Mua> ress, int k)
    {
        if(nodes.isEmpty()) return new Value_Mua();
        //thing
        if(nodes.get(0).charAt(0)==':')
        {
            //ress.add(new Value_Mua());
            nodes.set(0, nodes.get(0).substring(1));
            Value_Mua value =interpret(nodes, ress, k);
            Word_Mua name = value.toWord();
            Value_Mua res=thing_mua(name);
            //ress.set(k,res);
            return res;
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
        else if(nodes.get(0).charAt(0)=='[')
        {
            String temp = nodes.get(0);
            Value_Mua l = build_list(nodes);
            return l;
        }
        else
        {
            switch (nodes.get(0))
            {
                case "make" :
                {
                    ress.add(new Value_Mua());
                    String l = nodes.get(1);
                    String v = nodes.get(1).substring(1);
                    nodes.remove(0);
                    nodes.remove(0);
                    Word_Mua name = new Word_Mua(l,v);
                    Value_Mua value =interpret(nodes, ress, k+1);
                    Value_Mua res = make_mua(name, value);
                    ress.set(k,res);
                    return res;
                }
                case "thing":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1);
                    Word_Mua name = value.toWord();
                    Value_Mua res = thing_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "print":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = print_mua(interpret(nodes, ress, k+1));
                    ress.set(k,res);
                    return res;
                }
                case "read":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = read_mua();
                    ress.set(k,res);
                    return res;
                }
                case "add":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = add_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "sub":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = sub_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mul":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = mul_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "div":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = div_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mod":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = mod_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "erase":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1);
                    Word_Mua name = value.toWord();
                    Value_Mua res = erase_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "isname":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1);
                    Word_Mua name = value.toWord();
                    Value_Mua res = isname_mua(name);
                    ress.set(k,res);
                    return res;
                }
                case "run":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1);
                    List_Mua list = value.toList();
                    Value_Mua res = run_mua(list);
                    ress.set(k,res);
                    return res;
                }
                case "eq":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = eq_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "gt":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = gt_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "lt":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = lt_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "and":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = and_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "or":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = or_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "not":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = not_mua(a.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "if":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua j =interpret(nodes, ress, k+1);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua b =interpret(nodes, ress, k+1);
                    Value_Mua res = if_mua(j.toBool(),a.toList(),b.toList());
                    ress.set(k,res);
                    return res;
                }
                case "isnumber":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = isnumber_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isword":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = isword_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "islist":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = islist_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isbool":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = isbool_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isempty":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1);
                    Value_Mua res = isempty_mua(a);
                    ress.set(k,res);
                    return res;
                }
                default:
                {
                    //A word
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    String l = '\"' + temp;
                    return new Word_Mua(l, temp);
                }
            }
        }
    }
    Value_Mua build_list(List<String> nodes)
    {
        StringBuilder literal = new StringBuilder("");
        Iterator<String> iter = nodes.iterator();
        while(iter.hasNext())
        {
            String str=iter.next();
            if("[".equals(str))
            {
                literal.append("[");
                iter.remove();
            }
            else if("]".equals(str))
            {
                literal.deleteCharAt(literal.length()-1);//移除空格
                literal.append("]");
                iter.remove();
                break;
            }
            else
            {
                literal.append(str).append(" ");
                iter.remove();
            }
        }
        return new List_Mua(literal);
    }
    Number_Mua infix(List<String> nodes)
    {
        Stack<Number_Mua> num_stack = new Stack<Number_Mua>();
        Stack<infix_op> op_stack = new Stack<infix_op>();
        int left=0;
        int right=0;
        int length=0;
        String str = "";
        int i = 0;
        //替换前缀
        while(true)
        {
            if(i>=nodes.size()) break;
            str = nodes.get(i);
            if(str.charAt(0)==':')
            {
                nodes.set(i, nodes.get(i).substring(1));
                Value_Mua value =interpret(nodes.subList(i, nodes.size()));
                Word_Mua name = value.toWord();
                nodes.add(i, thing_mua(name).literal);
                i++;
                continue;
            }
            switch (str)
            {
                case "add":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()));
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()));
                    nodes.add(i,add_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "sub":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()));
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()));
                    nodes.add(i,sub_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "mul":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()));
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()));
                    nodes.add(i,mul_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "div":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()));
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()));
                    nodes.add(i,div_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "mod":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()));
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()));
                    nodes.add(i,mod_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                default:;
            }
            i++;
        }
        Iterator<String> iter = nodes.iterator();
        //计算中缀长度
        while(iter.hasNext())
        {
            length++;
            str = iter.next();
            if(str.equals("(")) left++;
            if(str.equals(")")) right++;
            if(left==right) break;
        }
        i = 0;
        while(i<length)
        {
            str=nodes.get(0);
            //OPERATER
            if(str.matches("\\+|-|\\*|/|%|\\(|\\)"))
            {
                infix_op temp = new infix_op(str);
                if(op_stack.isEmpty())
                {
                    op_stack.push(temp);
                    i++;
                    nodes.remove(0);
                }
                else if(op_stack.peek().in_prior<temp.out_prior)
                {
                    op_stack.push(temp);
                    i++;
                    nodes.remove(0);
                }
                else if(op_stack.peek().in_prior==temp.out_prior)
                {
                    op_stack.pop();
                    i++;
                    nodes.remove(0);
                }
                else
                {
                    Number_Mua b = num_stack.pop();
                    Number_Mua a = num_stack.pop();
                    num_stack.push(op_stack.pop().exe(a,b));
                }
            }
            //NUMBER
            else if(str.matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
            {
                num_stack.push(new Number_Mua(str));
                i++;
                nodes.remove(0);
            }
            //PREFIX OP
            /*else
            {
                switch (str)
                {
                    case "add":
                    {
                        i++;
                        nodes.remove(0);
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                        break;
                    }
                    case "sub":
                    {
                        i++;
                        nodes.remove(0);
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(sub_mua(a.toNumber(),b.toNumber()));
                        break;
                    }
                    case "mul":
                    {
                        i++;
                        nodes.remove(0);
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(mul_mua(a.toNumber(),b.toNumber()));
                        break;
                    }
                    case "div":
                    {
                        i++;
                        nodes.remove(0);
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                        break;
                    }
                    case "mod":
                    {
                        i++;
                        nodes.remove(0);
                        Value_Mua a =interpret(nodes);
                        Value_Mua b =interpret(nodes);
                        num_stack.push(add_mua(a.toNumber(),b.toNumber()));
                        break;
                    }
                }
            }*/
        }
        return num_stack.pop();
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
    Value_Mua erase_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return Word_Map.remove(name);
        else if(Number_Map.containsKey(name)) return Number_Map.remove(name);
        else if(Bool_Map.containsKey(name)) return Bool_Map.remove(name);
        else if(List_Map.containsKey(name)) return List_Map.remove(name);
        else return new Value_Mua();
    }
    Bool_Mua isname_mua(Word_Mua word_name)
    {
        String name = word_name.word_value.toString();
        if(Word_Map.containsKey(name)) return new Bool_Mua(true);
        else if(Number_Map.containsKey(name)) return new Bool_Mua(true);
        else if(Bool_Map.containsKey(name)) return new Bool_Mua(true);
        else if(List_Map.containsKey(name)) return new Bool_Mua(true);
        else return new Bool_Mua(false);
    }
    Value_Mua run_mua(List_Mua list)
    {
        ArrayList<Value_Mua> ress = new ArrayList<Value_Mua>();
        int k=0;
        while(!list.list_value.isEmpty())   interpret(list.list_value, ress, k);
        return ress.get(ress.size()-1);
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
    Bool_Mua gt_mua(Value_Mua a, Value_Mua b)
    {
        if(a.Type_Mua== Value_Mua.TYPE_MUA.NUMBER)
            return new Bool_Mua(a.toNumber().number_value>b.toNumber().number_value);
        else
            return new Bool_Mua(a.literal.compareTo(b.literal) > 0);

    }
    Bool_Mua eq_mua(Value_Mua a, Value_Mua b)
    {
        return new Bool_Mua(a.literal.equals(b.literal));
    }
    Bool_Mua lt_mua(Value_Mua a, Value_Mua b)
    {
        if(a.Type_Mua== Value_Mua.TYPE_MUA.NUMBER)
            return new Bool_Mua(a.toNumber().number_value<b.toNumber().number_value);
        else
            return new Bool_Mua(a.literal.compareTo(b.literal) < 0);
    }
    Bool_Mua and_mua(Bool_Mua a, Bool_Mua b)
    {
        return new Bool_Mua(a.bool_value&&b.bool_value);
    }
    Bool_Mua or_mua(Bool_Mua a, Bool_Mua b)
    {
        return new Bool_Mua(a.bool_value||b.bool_value);
    }
    Bool_Mua not_mua(Bool_Mua a)
    {
        return new Bool_Mua(!a.bool_value);
    }
    Value_Mua if_mua(Bool_Mua j, List_Mua a, List_Mua b)
    {
        Value_Mua res;
        if(j.bool_value)
        {
            res = interpret(a.list_value);
        }
        else
        {
            res = interpret(b.list_value);
        }
        if(res.literal.equals("")) return new List_Mua("");
        return res;
    }
    Bool_Mua isnumber_mua(Value_Mua v)
    {
        return v.isnumber();
    }
    Bool_Mua isword_mua(Value_Mua v)
    {
        return v.isword();
    }
    Bool_Mua islist_mua(Value_Mua v)
    {
        return v.islist();
    }
    Bool_Mua isbool_mua(Value_Mua v)
    {
        return v.isbool();
    }
    Bool_Mua isempty_mua(Value_Mua v)
    {
        return v.isempty();
    }
}
