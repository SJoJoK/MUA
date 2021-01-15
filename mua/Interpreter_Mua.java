package mua;

import java.util.*;
import java.io.*;
public class Interpreter_Mua
{
    String regex_num = "(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)";
    String regex_ops = "[+\\-*/%()]";
    HashMap<String, Value_Mua> Variable_Map_Global;
    HashMap<String, HashMap<String, Value_Mua>> Env;
    Scanner scan;

    public void begin() {
        mua_init();
        scan = new Scanner(System.in);
        String inst = new String();
        while(scan.hasNextLine())
        {
            inst = scan.nextLine();
            String[] nodes = inst.replace("(", " ( ").replace(")", " ) ")
                    .replace("[", " [ ").replace("]", " ] ")
                    .replace("+", " + ").replace("-", " - ")
                    .replace("*", " * ").replace("/", " / ")
                    .replace("%", " % ").replace(":", " : ").trim().split("\\s+");
            ArrayList<String> nodes_list = new ArrayList<>(Arrays.asList(nodes));
            ArrayList<Value_Mua> ress = new ArrayList<>();
            try
            {
                interpret(nodes_list, ress, 0, "global");
            } catch (Throwable e)
            {
                break;
            }
        }
    }

    void mua_init() {
        Variable_Map_Global = new HashMap<>();
        Variable_Map_Global.put("pi",new Number_Mua(3.14159));
        Env = new HashMap<>();
        Env.put("global", Variable_Map_Global);
    }

    Value_Mua interpret(List<String> nodes, List<Value_Mua> ress, int k, String env_name) {
        if (nodes.isEmpty()) {
            if(scan.hasNextLine())
                get_next_line(nodes);
            else return new Value_Mua();
        }
        //number
        if (nodes.get(0).equals("")) {
            nodes.remove(0);
            return new Value_Mua();
        } else if (nodes.get(0).matches(regex_num)) {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Number_Mua(temp);
        }
        //word
        else if (nodes.get(0).charAt(0) == '\"') {
            String temp = nodes.get(0);
            nodes.remove(0);
            temp = temp.substring(1);
            String l = '\"' + temp;
            String v = temp;
            return new Word_Mua(l, v);
        }
        //boolean
        else if (nodes.get(0).equals("true") || nodes.get(0).equals("false")) {
            String temp = nodes.get(0);
            nodes.remove(0);
            return new Bool_Mua(temp);
        }
        //list
        else if (nodes.get(0).charAt(0) == '[') {
            return build_list(nodes);
        }
        //infix
        else if (nodes.get(0).charAt(0) == '(') {

            int left = 0;
            int right = 0;
            int length = 0;
            String str;
            //计算中缀长度
            for (String node : nodes) {
                length++;
                str = node;
                if (str.equals("(")) left++;
                if (str.equals(")")) right++;
                if (left == right) break;
            }
            return infix(nodes.subList(0, length), env_name);
        } else {
            switch (nodes.get(0)) {
                case "make": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);//删除make
                    Word_Mua name = interpret(nodes, ress, k + 1, env_name).toWord();
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = make_mua(name, value, env_name);
                    ress.set(k, res);
                    return res;
                }
                case ":": {
                    nodes.remove(0);
                    nodes.set(0, "\"" + nodes.get(0));
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    Word_Mua name = value.toWord();
                    return thing_mua(name, env_name);
                }
                case "thing": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = thing_mua(name, env_name);
                    ress.set(k, res);
                    return res;
                }
                case "print": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = print_mua(interpret(nodes, ress, k + 1, env_name));
                    ress.set(k, res);
                    return res;
                }
                case "read": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = read_mua();
                    ress.set(k, res);
                    return res;
                }
                case "add": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = add_mua(a.toNumber(), b.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "sub": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = sub_mua(a.toNumber(), b.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "mul": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = mul_mua(a.toNumber(), b.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "div": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = div_mua(a.toNumber(), b.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "mod": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = mod_mua(a.toNumber(), b.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "erase": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = erase_mua(name, env_name);
                    ress.set(k, res);
                    return res;
                }
                case "isname": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    Word_Mua name = value.toWord();
                    Value_Mua res = isname_mua(name, env_name);
                    ress.set(k, res);
                    return res;
                }
                case "run": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua value = interpret(nodes, ress, k + 1, env_name);
                    List_Mua list = value.toList();
                    Value_Mua res = run_mua(list, env_name);
                    ress.set(k, res);
                    return res;
                }
                case "eq": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = eq_mua(a, b);
                    ress.set(k, res);
                    return res;
                }
                case "gt": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = gt_mua(a, b);
                    ress.set(k, res);
                    return res;
                }
                case "lt": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = lt_mua(a, b);
                    ress.set(k, res);
                    return res;
                }
                case "and": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = and_mua(a.toBool(), b.toBool());
                    ress.set(k, res);
                    return res;
                }
                case "or": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = or_mua(a.toBool(), b.toBool());
                    ress.set(k, res);
                    return res;
                }
                case "not": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = not_mua(a.toBool());
                    ress.set(k, res);
                    return res;
                }
                case "if": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua j = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = if_mua(j.toBool(), a.toList(), b.toList(), env_name);
                    ress.set(k, res);
                    return res;
                }
                case "isnumber": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = isnumber_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "isword": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = isword_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "islist": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = islist_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "isbool": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = isbool_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "isempty": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = isempty_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "return": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    ress.set(k, a);
                    return a;
                }
                case "export": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Word_Mua name = a.toWord();
                    Value_Mua res = export_mua(name, env_name);
                    ress.set(k, res);
                    return res;
                }
                case "word": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    String l;
                    if (b.isword().bool_value) l = a.literal + b.literal.substring(1);
                    else l = a.literal + b.literal;
                    Value_Mua res = new Word_Mua(l);
                    ress.set(k, res);
                    return res;
                }
                case "sentence": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    List_Mua res = sentence_mua(a, b);
                    ress.set(k, res);
                    return res;
                }
                case "list": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    List_Mua res = list_mua(a, b);
                    ress.set(k, res);
                    return res;
                }
                case "join": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua b = interpret(nodes, ress, k + 1, env_name);
                    List_Mua res = join_mua(a.toList(), b);
                    ress.set(k, res);
                    return res;
                }
                case "first": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = first_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "last": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = last_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "butfirst": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = butfirst_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "butlast": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Value_Mua res = butlast_mua(a);
                    ress.set(k, res);
                    return res;
                }
                case "save": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    try {
                        Value_Mua res = save_mua(a.toWord(), env_name);
                        ress.set(k, res);
                        return res;
                    } catch (Throwable e) {
                        ;
                    }
                }
                case "load": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    try {
                        Value_Mua res = load_mua(a.toWord(), env_name);
                        ress.set(k, res);
                        return res;
                    } catch (Throwable e) {
                        ;
                    }
                }
                case "erall": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua res = erall_mua(env_name);
                    ress.set(k, res);
                    return res;

                }
                case "poall": {
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    List_Mua res = poall_mua(env_name);
                    ress.set(k, res);
                    return res;
                }
                case "random":{
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Number_Mua res = random_mua(a.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "int":{
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Number_Mua res = int_mua(a.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "sqrt":{
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    Value_Mua a = interpret(nodes, ress, k + 1, env_name);
                    Number_Mua res = sqrt_mua(a.toNumber());
                    ress.set(k, res);
                    return res;
                }
                case "readlist":{
                    ress.add(new Value_Mua());
                    nodes.remove(0);
                    List_Mua res = readlist_mua();
                    ress.set(k, res);
                    return res;
                }
                default: {
                    String temp = nodes.get(0);
                    nodes.remove(0);
                    if (temp.equals("-"))//如果是负号..
                    {
                        temp = temp + nodes.get(0);
                        nodes.remove(0);
                        return new Number_Mua(temp);
                    }
                    if (Env.get(env_name).containsKey(temp)) {
                        if (!Env.get(env_name).get(temp).islist().bool_value) {
                            //如果不是表，则是字
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        } else if (!Env.get(env_name).get(temp).toList().isfunc) {
                            //是表，但不是函数
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        } else
                            return Func_Mua(nodes, env_name, env_name, temp);
                    } else if (Variable_Map_Global.containsKey(temp)) {
                        if (!Variable_Map_Global.get(temp).islist().bool_value) {
                            //如果不是表，则是字
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        } else if (!Variable_Map_Global.get(temp).toList().isfunc) {
                            String l = '\"' + temp;
                            return new Word_Mua(l, temp);
                        } else
                            return Func_Mua(nodes, env_name, "global", temp);
                    } else {
                        String l = '\"' + temp;
                        return new Word_Mua(l, temp);
                    }
                }
            }
        }
    }

    Value_Mua build_list(List<String> nodes) {
        StringBuilder literal = new StringBuilder();
        int lb = 0;
        while(true)
        {
            String str = nodes.get(0);
            if("[".equals(str))
            {
                lb++;
                literal.append("[");
                nodes.remove(0);
            }
            else if("]".equals(str))
            {
                lb--;
                if(literal.length()>0)
                    if (literal.charAt(literal.length() - 1) == ' ')
                        literal.deleteCharAt(literal.length() - 1);//移除空格
                if(lb==0)
                {
                    literal.append("]");
                    nodes.remove(0);
                    break;
                }
                else
                {
                    literal.append("]").append(" ");
                    nodes.remove(0);
                }
            }
            else
            {
                literal.append(str).append(" ");
                nodes.remove(0);
            }
            if(nodes.isEmpty()) get_next_line(nodes);
        }
        return new List_Mua(literal);
    }

    static Value_Mua build_list_static (List<String> nodes) {
        StringBuilder literal = new StringBuilder();
        Iterator<String> iter = nodes.iterator();
        int lb = 0;
        while (iter.hasNext()) {
            String str = iter.next();
            if ("[".equals(str)) {
                lb++;
                literal.append("[");
                iter.remove();
            } else if ("]".equals(str)) {
                lb--;
                if (literal.length() > 0)
                    if (literal.charAt(literal.length() - 1) == ' ')
                        literal.deleteCharAt(literal.length() - 1);//移除空格
                if (lb == 0) {
                    literal.append("]");
                    iter.remove();
                    break;
                } else {
                    literal.append("]").append(" ");
                    iter.remove();
                }
            } else {
                literal.append(str).append(" ");
                iter.remove();
            }
        }
        return new List_Mua(literal);
    }

    void get_next_line(List<String>nodes) {
        String inst = new String();
        if(scan.hasNextLine())
        {
            inst = scan.nextLine();
            //if(inst.equals("")) break;
            // 改成循环
            String[] takens = inst.replace("("," ( ").replace(")"," ) ")
                    .replace("["," [ ").replace("]"," ] ")
                    .replace("+", " + ").replace("-", " - ")
                    .replace("*", " * ").replace("/", " / ")
                    .replace("%", " % ").replace(":", " : ").trim().split("\\s+");
            nodes.addAll(new ArrayList<String>(Arrays.asList(takens)));//读取下一行
        }
    }

    Number_Mua infix(List<String> nodes, String env_name) {
        Stack<Number_Mua> num_stack = new Stack<>();
        Stack<infix_op> op_stack = new Stack<>();
        Value_Mua tmp;
        String str;
        int i = 0;
        int length = 0;
        int left = 0;
        int right = 0;
        //替换前缀
        while (i < nodes.size()) {
            str = nodes.get(i);
            if ((!str.matches(regex_num)) && (!str.matches(regex_ops))) {
                tmp = interpret(nodes.subList(i, nodes.size()), new ArrayList<>(), 0, env_name);
                nodes.add(i, tmp.literal);
            }
            i++;
        }
        //添加正负
        for (i = 0; i < nodes.size() - 1; i++) {
            if (nodes.get(i + 1).equals("-")) {
                if ((!nodes.get(i).equals(")")) && !nodes.get(i).matches(regex_num)) {
                    nodes.remove(i + 1);
                    nodes.set(i + 1, "-" + nodes.get(i + 1));
                }
            }
        }
        i = 0;
        for (String node : nodes) {
            length++;
            str = node;
            if (str.equals("(")) left++;
            if (str.equals(")")) right++;
            if (left == right) break;
        }
        while (i < length) {
            str = nodes.get(0);
            //OPERATER
            if (str.matches(regex_ops)) {
                infix_op temp = new infix_op(str);
                if (op_stack.isEmpty()) {
                    op_stack.push(temp);
                    i++;
                    nodes.remove(0);
                } else if (op_stack.peek().in_prior < temp.out_prior) {
                    op_stack.push(temp);
                    i++;
                    nodes.remove(0);
                } else if (op_stack.peek().in_prior == temp.out_prior) {
                    op_stack.pop();
                    i++;
                    nodes.remove(0);
                } else {
                    Number_Mua b = num_stack.pop();
                    Number_Mua a = num_stack.pop();
                    num_stack.push(op_stack.pop().exe(a, b));
                }
            }
            //NUMBER
            else if (str.matches(regex_num)) {
                num_stack.push(new Number_Mua(str));
                i++;
                nodes.remove(0);
            }
        }
        return num_stack.pop();
    }

    Value_Mua make_mua(Word_Mua name, Value_Mua value, String env_name) {
        HashMap<String, Value_Mua> local_env = Env.get(env_name);
        local_env.put(name.word_value.toString(), value);
        return value;
    }

    Value_Mua thing_mua(Word_Mua word_name, String env_name) {
        String name = word_name.word_value.toString();
        HashMap<String, Value_Mua> local_env = Env.get(env_name);
        if (local_env.containsKey(name)) return local_env.get(name);
        else if (Variable_Map_Global.containsKey(name)) return Variable_Map_Global.get(name);
        else return new Value_Mua();
    }

    Value_Mua print_mua(Value_Mua value) {
        switch (value.Type_Mua) {
            case WORD:
                System.out.println(value.literal.substring(1));
                break;
//            case LIST:System.out.println(value.literal.substring(1,value.literal.length()-2));break;
            case LIST:
                value.toList().print_list_mua();
                break;
            case BOOL:
                System.out.println(value.literal);
                break;
            case NUMBER:
                System.out.println(value.literal);
                break;
            default:
                break;
        }
        return value;
    }

    Value_Mua read_mua() {
        String str = "";
        Value_Mua v;
        if (scan.hasNextLine())
        {
            str = scan.nextLine();
        }
        if (str.matches(regex_num)) {
            v = new Number_Mua(str);
        } else {
            v = new Word_Mua('\"' + str);
        }
        return v;
    }

    Value_Mua erase_mua(Word_Mua word_name, String env_name) {
        String name = word_name.word_value.toString();
        HashMap<String, Value_Mua> local_env = Env.get(env_name);
        if (local_env.containsKey(name)) return local_env.remove(name);
        else if (Variable_Map_Global.containsKey(name)) return Variable_Map_Global.remove(name);
        else return new Value_Mua();
    }

    Bool_Mua isname_mua(Word_Mua word_name, String env_name) {
        String name = word_name.word_value.toString();
        HashMap<String, Value_Mua> local_env = Env.get(env_name);
        HashMap<String, Value_Mua> global_env = Env.get("global");
        if (local_env.containsKey(name)) return new Bool_Mua(true);
        else if (global_env.containsKey(name)) return new Bool_Mua(true);
        else return new Bool_Mua(false);
    }

    Value_Mua run_mua(List_Mua list, String env_name) {
        ArrayList<Value_Mua> ress = new ArrayList<>();
        int k = 0;
        while (!list.list_value.isEmpty()) interpret(list.list_value, ress, k, env_name);
        return ress.get(ress.size() - 1);
    }

    Number_Mua add_mua(Number_Mua a, Number_Mua b) {
        return new Number_Mua(a.number_value + b.number_value);
    }

    Number_Mua sub_mua(Number_Mua a, Number_Mua b) {
        return new Number_Mua(a.number_value - b.number_value);
    }

    Number_Mua mul_mua(Number_Mua a, Number_Mua b) {
        return new Number_Mua(a.number_value * b.number_value);
    }

    Number_Mua div_mua(Number_Mua a, Number_Mua b) {
        return new Number_Mua(a.number_value / b.number_value);
    }

    Number_Mua mod_mua(Number_Mua a, Number_Mua b) {
        return new Number_Mua(a.number_value % b.number_value);
    }

    Bool_Mua gt_mua(Value_Mua a, Value_Mua b) {
        if (a.Type_Mua == Value_Mua.TYPE_MUA.NUMBER)
            return new Bool_Mua(a.toNumber().number_value > b.toNumber().number_value);
        else
            return new Bool_Mua(a.literal.compareTo(b.literal) > 0);

    }

    Bool_Mua eq_mua(Value_Mua a, Value_Mua b) {
        if (a.isnumber().bool_value && b.isnumber().bool_value)
            return new Bool_Mua(a.toNumber().number_value == b.toNumber().number_value);
        else
            return new Bool_Mua(a.literal.equals(b.literal));
    }

    Bool_Mua lt_mua(Value_Mua a, Value_Mua b) {
        if (a.Type_Mua == Value_Mua.TYPE_MUA.NUMBER)
            return new Bool_Mua(a.toNumber().number_value < b.toNumber().number_value);
        else
            return new Bool_Mua(a.literal.compareTo(b.literal) < 0);
    }

    Bool_Mua and_mua(Bool_Mua a, Bool_Mua b) {
        return new Bool_Mua(a.bool_value && b.bool_value);
    }

    Bool_Mua or_mua(Bool_Mua a, Bool_Mua b) {
        return new Bool_Mua(a.bool_value || b.bool_value);
    }

    Bool_Mua not_mua(Bool_Mua a) {
        return new Bool_Mua(!a.bool_value);
    }

    Value_Mua if_mua(Bool_Mua j, List_Mua a, List_Mua b, String env_name) {
        Value_Mua res = new Value_Mua();
        if (j.bool_value) {
            while (!a.list_value.isEmpty()) res = interpret(a.list_value, new ArrayList<>(), 0, env_name);
        } else {
            while (!b.list_value.isEmpty()) res = interpret(b.list_value, new ArrayList<>(), 0, env_name);
        }
        return res;
    }

    Bool_Mua isnumber_mua(Value_Mua v) {
        return v.isnumber();
    }

    Bool_Mua isword_mua(Value_Mua v) {
        return v.isword();
    }

    Bool_Mua islist_mua(Value_Mua v) {
        return v.islist();
    }

    Bool_Mua isbool_mua(Value_Mua v) {
        return v.isbool();
    }

    Bool_Mua isempty_mua(Value_Mua v) {
        return v.isempty();
    }

    Value_Mua Func_Mua(List<String> father_nodes, String father_env_name, String func_from_name, String func_name) {
        //HashMap<String,Value_Mua> father_env = Env.get(father_env_name);
        HashMap<String, Value_Mua> func_from_env = Env.get(func_from_name);
        HashMap<String, Value_Mua> this_env = new HashMap<>();
        List_Mua func = new List_Mua(func_from_env.get(func_name));//获得函数定义，新对象，以防函数只能用一次
        String this_env_name = father_env_name + "_" + func_name;

        Value_Mua res = new Value_Mua();

        ArrayList<String> temp_nodes = func.list_value;
        List_Mua func_argu_name = interpret(temp_nodes, new ArrayList<>(), 0, func_name).toList();
        List_Mua func_code = interpret(temp_nodes, new ArrayList<>(), 0, func_name).toList();
        int argc = func_argu_name.list_value.size();
        //传入参数
        ArrayList<Value_Mua> argu_value = new ArrayList<>();
        for (int i = 0; i < argc; i++) {
            argu_value.add(new Value_Mua());
            argu_value.set(i, interpret(father_nodes, new ArrayList<>(), 0, father_env_name));
        }
        //绑定
        for (int i = 0; i < argc; i++) {
            this_env.put(func_argu_name.list_value.get(i), argu_value.get(i));
        }
        Env.put(this_env_name, this_env);
        while (func_code.list_value.size() > 0)
            res = interpret(func_code.list_value, new ArrayList<>(), 0, this_env_name);
        Env.remove(this_env_name);
        return res;
    }

    Value_Mua export_mua(Word_Mua name, String env_name) {
        String v_name = name.word_value.toString();
        Value_Mua v = Env.get(env_name).get(v_name);
        if (!Variable_Map_Global.containsKey(v_name)) {
            Variable_Map_Global.put(v_name, v);
        } else {
            Variable_Map_Global.replace(v_name, v);
        }
        return v;
    }

    List_Mua sentence_mua(Value_Mua a, Value_Mua b) {
        ArrayList<Value_Mua> vs = new ArrayList<>();
        if (a.islist().bool_value) {
            vs.addAll(a.toList().list_mua_value);
        } else {
            vs.add(a);
        }
        if (b.islist().bool_value) {
            vs.addAll(b.toList().list_mua_value);
        } else {
            vs.add(b);
        }
        return new List_Mua(vs);
    }

    List_Mua list_mua(Value_Mua a, Value_Mua b) {
        ArrayList<Value_Mua> vs = new ArrayList<>();
        vs.add(a);
        vs.add(b);
        return new List_Mua(vs);
    }

    List_Mua join_mua(List_Mua a, Value_Mua b) {
        ArrayList<Value_Mua> vs = new ArrayList<>();
        vs.addAll(a.list_mua_value);
        vs.add(b);
        return new List_Mua(vs);
    }

    Value_Mua first_mua(Value_Mua a) {
        Value_Mua tmp = new Value_Mua();
        if (a.islist().bool_value) {
            List_Mua list_a = a.toList();
            tmp = list_a.list_mua_value.get(0);
        } else {
            if (a.isword().bool_value) tmp = new Word_Mua("\"" + a.literal.charAt(1));
            else tmp = new Word_Mua("\"" + a.literal.charAt(0));
        }
        return tmp;
    }

    Value_Mua last_mua(Value_Mua a) {
        Value_Mua tmp = new Value_Mua();
        if (a.islist().bool_value) {
            List_Mua list_a = a.toList();
            tmp = list_a.list_mua_value.get(list_a.size - 1);
        } else {
            if (a.isword().bool_value) tmp = new Word_Mua("\"" + a.literal.charAt(a.literal.length() - 1));
            else tmp = new Word_Mua("\"" + a.literal.charAt(a.literal.length() - 1));
        }
        return tmp;
    }

    Value_Mua butfirst_mua(Value_Mua a) {
        Value_Mua tmp = new Value_Mua();
        if (a.islist().bool_value) {
            List_Mua list_a = a.toList();
            ArrayList<Value_Mua> tmp_vs = new ArrayList<>(list_a.list_mua_value.subList(1, list_a.list_mua_value.size()));
            tmp = new List_Mua(tmp_vs);
        } else {
            if (a.isword().bool_value) tmp = new Word_Mua("\"" + a.literal.substring(2));
            else tmp = new Word_Mua("\"" + a.literal.substring(1));

        }
        return tmp;
    }

    Value_Mua butlast_mua(Value_Mua a) {
        Value_Mua tmp = new Value_Mua();
        if (a.islist().bool_value) {
            List_Mua list_a = a.toList();
            ArrayList<Value_Mua> tmp_vs = new ArrayList<>(list_a.list_mua_value.subList(0, list_a.list_mua_value.size() - 1));
            tmp = new List_Mua(tmp_vs);
        } else {
            if (a.isword().bool_value) tmp = new Word_Mua("\"" + a.literal.substring(1, a.literal.length() - 1));
            else tmp = new Word_Mua("\"" + a.literal.substring(0, a.literal.length() - 1));
        }
        return tmp;
    }

    Value_Mua save_mua(Word_Mua a, String env_name) throws IOException {
        String filename = a.word_value.toString();
        File file = new File(filename);
        FileOutputStream fout = new FileOutputStream(file);
        HashMap<String, Value_Mua> namespace_mua = Env.get(env_name);
        Iterator<Map.Entry<String, Value_Mua>> iter = namespace_mua.entrySet().iterator();
        while (iter.hasNext()) {
            StringBuilder inst = new StringBuilder("make ");
            Map.Entry<String, Value_Mua> entry = iter.next();
            String name = entry.getKey();
            Value_Mua value = entry.getValue();
            inst.append("\"");
            inst.append(name);
            inst.append(" ");
            inst.append(value.literal);
            inst.append("\n");
            fout.write(inst.toString().getBytes());
        }
        fout.close();
        return a;
    }

    Bool_Mua load_mua(Word_Mua b, String env_name) throws IOException {
        String filename = b.word_value.toString();
        File file = new File(filename);
        Scanner scan = new Scanner(file);
        StringBuilder program = new StringBuilder();
        while (scan.hasNextLine()) {
            program.append(" ");
            program.append(scan.nextLine());
        }
        String[] nodes = program.toString().replace("(", " ( ").replace(")", " ) ")
                .replace("[", " [ ").replace("]", " ] ")
                .replace("+", " + ").replace("-", " - ")
                .replace("*", " * ").replace("/", " / ")
                .replace("%", " % ").replace(":", " : ").trim().split("\\s+");
        ArrayList<String> nodes_list = new ArrayList<>(Arrays.asList(nodes));
        ArrayList<Value_Mua> ress = new ArrayList<>();
        while (!nodes_list.isEmpty()) {
            try {
                interpret(nodes_list, ress, 0, env_name);
            } catch (Throwable e) {
                break;
            }
        }
        return new Bool_Mua(true);

    }

    Bool_Mua erall_mua(String env_name) {
        Env.get(env_name).clear();
        return new Bool_Mua(true);
    }

    List_Mua poall_mua(String env_name) {
        ArrayList<Value_Mua> vs = new ArrayList<>();
        HashMap<String, Value_Mua> namespace_mua = Env.get(env_name);
        Iterator<Map.Entry<String, Value_Mua>> iter = namespace_mua.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Value_Mua> entry = iter.next();
            String name = entry.getKey();
            vs.add(new Word_Mua("\"" + name));
        }
        List_Mua res = new List_Mua(vs);
        return res;
    }

    Number_Mua random_mua(Number_Mua num) {
        double rnd = new Random().nextDouble();
        return new Number_Mua(num.number_value*rnd);
    }

    Number_Mua int_mua(Number_Mua num) {
        int x = (int) Math.floor(num.number_value);
        return new Number_Mua(x);
    }

    Number_Mua sqrt_mua(Number_Mua num) {
        double x = Math.sqrt(num.number_value);
        return new Number_Mua(x);
    }

    List_Mua readlist_mua() {
        ArrayList<String> nodes_list = new ArrayList<>();
        ArrayList<Value_Mua> vs = new ArrayList<>();
        String str = "";
        Value_Mua v;
        if (scan.hasNextLine()) {
            str = scan.nextLine();
            String[] nodes = str.trim().split("\\s+");
            nodes_list = new ArrayList<>(Arrays.asList(nodes));
        }
        for (int i=0;i<nodes_list.size();i++) {
            vs.add(new Word_Mua("\""+nodes_list.get(i)));
        }
        return new List_Mua(vs);
    }
}