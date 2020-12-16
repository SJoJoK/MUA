package mua;

import java.util.*;

public class Interpreter_Mua {
    String regex_num = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
    String regex_ops = "\\+|-|\\*|/|%|\\(|\\)";

    HashMap<String,Value_Mua> Variable_Map_Global;
    HashMap<String,HashMap<String, Value_Mua>> Env;
    Scanner scan;
    public void begin()
    {
        Variable_Map_Global = new HashMap<>();
        Env = new HashMap<>();
        Env.put("global",Variable_Map_Global);
        scan = new Scanner(System.in);
        StringBuilder program = new StringBuilder();
        while(scan.hasNextLine())
        {
            program.append(" ");
            program.append(scan.nextLine());
        }
        String[] nodes = program.toString().replace("("," ( ").replace(")"," ) ")
                             .replace("["," [ ").replace("]"," ] ")
                             .replace("+", " + ").replace("-", " - ")
                             .replace("*", " * ").replace("/", " / ")
                             .replace("%", " % ").trim().split("\\s+");
        ArrayList<String> nodes_list = new ArrayList<String>(Arrays.asList(nodes));
        ArrayList<Value_Mua> ress = new ArrayList<Value_Mua>();
        while(nodes_list.size()>0)
        {
            interpret(nodes_list, ress, 0, "global");
        }
    }
    Value_Mua interpret(List<String> nodes, List<Value_Mua> ress, int k, String env_name)
    {
        if(nodes.isEmpty()) return new Value_Mua();
        //thing
        if(nodes.get(0).charAt(0)==':')
        {
            //ress.add(new Value_Mua());
            nodes.set(0, nodes.get(0).substring(1));
            Value_Mua value =interpret(nodes, ress, k, env_name);
            Word_Mua name = value.toWord();
            Value_Mua res=thing_mua(name, env_name);
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
        //infix
        else if(nodes.get(0).charAt(0)=='(')
        {
            Value_Mua l = infix(nodes, env_name);
            return l;
        }
        else
        {
            switch (nodes.get(0))
            {
                case "make" :
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);//删除make
                    Word_Mua name = interpret(nodes,ress,k+1,env_name).toWord();
                    Value_Mua value =interpret(nodes, ress, k+2, env_name);
                    Value_Mua res = make_mua(name, value,env_name);
                    ress.set(k,res);
                    return res;
                }
                case "thing":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = thing_mua(name,env_name);
                    ress.set(k,res);
                    return res;
                }
                case "print":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = print_mua(interpret(nodes, ress, k+1, env_name));
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
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = add_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "sub":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = sub_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mul":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = mul_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "div":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = div_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "mod":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = mod_mua(a.toNumber(),b.toNumber());
                    ress.set(k,res);
                    return res;
                }
                case "erase":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = erase_mua(name,env_name);
                    ress.set(k,res);
                    return res;
                }
                case "isname":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = isname_mua(name,env_name);
                    ress.set(k,res);
                    return res;
                }
                case "run":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value =interpret(nodes, ress, k+1, env_name);
                    List_Mua list = value.toList();
                    Value_Mua res = run_mua(list,env_name);
                    ress.set(k,res);
                    return res;
                }
                case "eq":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = eq_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "gt":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = gt_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "lt":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = lt_mua(a,b);
                    ress.set(k,res);
                    return res;
                }
                case "and":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = and_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "or":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = or_mua(a.toBool(),b.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "not":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = not_mua(a.toBool());
                    ress.set(k,res);
                    return res;
                }
                case "if":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua j =interpret(nodes, ress, k+1, env_name);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua b =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = if_mua(j.toBool(),a.toList(),b.toList(),env_name);
                    ress.set(k,res);
                    return res;
                }
                case "isnumber":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = isnumber_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isword":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = isword_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "islist":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = islist_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isbool":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = isbool_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "isempty":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Value_Mua res = isempty_mua(a);
                    ress.set(k,res);
                    return res;
                }
                case "return":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    ress.set(k,a);
                    return a;
                }
                case "export":
                {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a =interpret(nodes, ress, k+1, env_name);
                    Word_Mua name = a.toWord();
                    Value_Mua res = export_mua(name, env_name);
                    ress.set(k,res);
                    return res;
                }
                default:
                {

                    String temp = nodes.get(0);
                    nodes.remove(0);
                    if(Env.get(env_name).containsKey(temp))
                    {
                        if(!Env.get(env_name).get(temp).islist().bool_value)
                        {
                            //如果不是表，则是字
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        }
                        else //如果是表，则可能是函数..
                            return Func_Mua(nodes,env_name,env_name, temp);
                    }
                    else if(Variable_Map_Global.containsKey(temp))
                    {
                        if(!Variable_Map_Global.get(temp).islist().bool_value)
                        {
                            //如果不是表，则是字
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        }
                        else  //如果是表，则可能是函数..
                            return Func_Mua(nodes,env_name,"global",temp);
                    }
                    else
                    {
                        String l = '\"' + temp;
                        return new Word_Mua(l, temp);
                    }
                }
            }
        }
    }
    Value_Mua build_list(List<String> nodes)
    {
        StringBuilder literal = new StringBuilder("");
        Iterator<String> iter = nodes.iterator();
        int lb=0;
        while(iter.hasNext())
        {
            String str=iter.next();
            if("[".equals(str))
            {
                lb++;
                literal.append("[");
                iter.remove();
            }
            else if("]".equals(str))
            {
                lb--;
                if(literal.charAt(literal.length()-1)==' ')
                literal.deleteCharAt(literal.length()-1);//移除空格
                if(lb==0)
                {
                    literal.append("]");
                    iter.remove();
                    break;
                }
                else
                {
                    literal.append("]").append(" ");
                    iter.remove();
                }
            }
            else
            {
                literal.append(str).append(" ");
                iter.remove();
            }
        }
        return new List_Mua(literal);
    }
    Number_Mua infix(List<String> nodes, String env_name)
    {
        Stack<Number_Mua> num_stack = new Stack<Number_Mua>();
        Stack<infix_op> op_stack = new Stack<infix_op>();
        int left=0;
        int right=0;
        int length=0;
        String str = "";
        int i = 0;
        Iterator<String> iter_s = nodes.iterator();
        //计算中缀长度
        while(iter_s.hasNext())
        {
            length++;
            str = iter_s.next();
            if(str.equals("(")) left++;
            if(str.equals(")")) right++;
            if(left==right) break;
        }
        //替换前缀
        while(i<length)
        {
            str = nodes.get(i);
            if(str.charAt(0)==':')
            {
                nodes.set(i, nodes.get(i).substring(1));
                Value_Mua value =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                Word_Mua name = value.toWord();
                nodes.add(i, thing_mua(name,env_name).literal);
                i++;
                continue;
            }
            switch (str)
            {
                case "add":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    nodes.add(i,add_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "sub":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    nodes.add(i,sub_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "mul":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    nodes.add(i,mul_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "div":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    nodes.add(i,div_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                case "mod":
                {
                    nodes.remove(i);
                    Value_Mua a =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    Value_Mua b =interpret(nodes.subList(i, nodes.size()),new ArrayList<Value_Mua>(),0, env_name);
                    nodes.add(i,mod_mua(a.toNumber(),b.toNumber()).literal);
                    break;
                }
                default:;
            }
            i++;
        }
        Iterator<String> iter = nodes.iterator();
        length = 0;
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
        for(i=0;i<length - 1;i++)
        {
            if(nodes.get(i+1).equals("-"))
            {
                if((!nodes.get(i).equals(")")) && !nodes.get(i).matches(regex_num))
                {
                    nodes.remove(i+1);
                    nodes.set(i+1,"-" + nodes.get(i+1));
                    length--;
                }
            }
        }
        i=0;
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
        }
        return num_stack.pop();
    }
    Value_Mua make_mua(Word_Mua name, Value_Mua value, String env_name)
    {
        HashMap<String,Value_Mua> local_env = Env.get(env_name);
        local_env.put(name.word_value.toString(), value);
        return value;
    }
    Value_Mua thing_mua(Word_Mua word_name, String env_name)
    {
        String name = word_name.word_value.toString();
        HashMap<String,Value_Mua> local_env = Env.get(env_name);
        if(local_env.containsKey(name)) return local_env.get(name);
        else if(Variable_Map_Global.containsKey(name)) return Variable_Map_Global.get(name);
        else return new Value_Mua();
    }
    Value_Mua print_mua(Value_Mua value)
    {
        String str;
        switch(value.Type_Mua)
        {
            case WORD:str = value.literal.substring(1);break;
            case LIST:str = value.literal.substring(1,value.literal.length()-2);break;
            case BOOL:
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
    Value_Mua erase_mua(Word_Mua word_name, String env_name)
    {
        String name = word_name.word_value.toString();
        HashMap<String,Value_Mua> local_env = Env.get(env_name);
        if(local_env.containsKey(name)) return local_env.remove(name);
        else if(Variable_Map_Global.containsKey(name)) return Variable_Map_Global.remove(name);
        else return new Value_Mua();
    }
    Bool_Mua isname_mua(Word_Mua word_name, String env_name)
    {
        String name = word_name.word_value.toString();
        HashMap<String,Value_Mua> local_env = Env.get(env_name);
        HashMap<String,Value_Mua> global_env = Env.get("global");
        if(local_env.containsKey(name)) return new Bool_Mua(true);
        else if(global_env.containsKey(name)) return new Bool_Mua(true);
        else return new Bool_Mua(false);
    }
    Value_Mua run_mua(List_Mua list, String env_name)
    {
        ArrayList<Value_Mua> ress = new ArrayList<Value_Mua>();
        int k=0;
        while(!list.list_value.isEmpty())   interpret(list.list_value, ress, k, env_name);
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
    Value_Mua if_mua(Bool_Mua j, List_Mua a, List_Mua b, String env_name)
    {
        Value_Mua res;
        if(j.bool_value)
        {
            res = interpret(a.list_value,new ArrayList<Value_Mua>(),0, env_name);
        }
        else
        {
            res = interpret(b.list_value,new ArrayList<Value_Mua>(),0, env_name);
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
    Value_Mua Func_Mua(List<String> father_nodes, String father_env_name, String func_from_name,String func_name)
    {
        HashMap<String,Value_Mua> father_env = Env.get(father_env_name);
        HashMap<String,Value_Mua> func_from_env = Env.get(func_from_name);
        HashMap<String,Value_Mua> this_env = new HashMap<>();
        List_Mua func = new List_Mua(func_from_env.get(func_name));//获得函数定义，新对象，以防函数只能用一次
        String this_env_name = father_env_name + "_" + func_name;

        Value_Mua res = new Value_Mua();

        ArrayList<String> temp_nodes = func.list_value;
        List_Mua func_argu_name = interpret(temp_nodes,new ArrayList<Value_Mua>(),0,func_name).toList();
        List_Mua func_code = interpret(temp_nodes,new ArrayList<Value_Mua>(),0,func_name).toList();
        int argc = func_argu_name.list_value.size();
        //传入参数
        ArrayList<Value_Mua> argu_value = new ArrayList<>();
        for(int i=0;i<argc;i++)
        {
            argu_value.add(new Value_Mua());
            argu_value.set(i, interpret(father_nodes, new ArrayList<Value_Mua>(), 0, father_env_name));
        }
        //Bonding
        for(int i=0;i<argc;i++)
        {
            this_env.put(func_argu_name.list_value.get(i),argu_value.get(i));
        }
        Env.put(this_env_name, this_env);
        while(func_code.list_value.size()>0)
        res = interpret(func_code.list_value,new ArrayList<Value_Mua>(),0,this_env_name);
        return res;
    }
    Value_Mua export_mua(Word_Mua name, String env_name)
    {
        String v_name = name.word_value.toString();
        Value_Mua v = Env.get(env_name).get(v_name);
        if(!Variable_Map_Global.containsKey(v_name))
        {
            Variable_Map_Global.put(v_name,v);
        }
        else
        {
            Variable_Map_Global.replace(v_name,v);
        }
        return v;
    }
}
