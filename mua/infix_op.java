package mua;

public class infix_op {
    enum INFIX_OP
    {ADD, SUB, MUL, DIV, MOD, LEFT, RIGHT}
    INFIX_OP op;
    int in_prior;
    int out_prior;
    infix_op(){}
    infix_op(char c_op)
    {
        switch (c_op)
        {
            case '+': op=INFIX_OP.ADD;in_prior = 3; out_prior = 2; break;
            case '-': op=INFIX_OP.SUB;in_prior = 3; out_prior = 2;break;
            case '*': op=INFIX_OP.MUL;in_prior = 5; out_prior = 4;break;
            case '/': op=INFIX_OP.DIV;in_prior = 5; out_prior = 4;break;
            case '%': op=INFIX_OP.MOD;in_prior = 5; out_prior = 4;break;
            case '(': op=INFIX_OP.LEFT;in_prior = 1; out_prior = 8;break;
            case ')': op=INFIX_OP.RIGHT;in_prior = 8; out_prior = 1;break;
            default:break;
        }
    }
    infix_op(String s_op)
    {
        switch (s_op)
        {
            case "+": op=INFIX_OP.ADD;in_prior = 3; out_prior = 2; break;
            case "-": op=INFIX_OP.SUB;in_prior = 3; out_prior = 2;break;
            case "*": op=INFIX_OP.MUL;in_prior = 5; out_prior = 4;break;
            case "/": op=INFIX_OP.DIV;in_prior = 5; out_prior = 4;break;
            case "%": op=INFIX_OP.MOD;in_prior = 5; out_prior = 4;break;
            case "(": op=INFIX_OP.LEFT;in_prior = 1; out_prior = 8;break;
            case ")": op=INFIX_OP.RIGHT;in_prior = 8; out_prior = 1;break;
            default:break;
        }
    }
    Number_Mua exe(Number_Mua a, Number_Mua b)
    {
        switch (op)
        {
            case ADD: return new Number_Mua(a.number_value+b.number_value);
            case SUB: return new Number_Mua(a.number_value-b.number_value);
            case MUL: return new Number_Mua(a.number_value*b.number_value);
            case DIV: return new Number_Mua(a.number_value/b.number_value);
            case MOD: return new Number_Mua(a.number_value%b.number_value);
            default:  return new Number_Mua(0);
        }
    }
}
