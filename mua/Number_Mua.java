package mua;

public class Number_Mua extends Value_Mua {
    double number_value;
    Number_Mua(String str_value)
    {
        super(str_value);
        Type_Mua=TYPE_MUA.NUMBER;
        number_value = Double.parseDouble(str_value);
    }
    Number_Mua(Value_Mua v)
    {
        super(v.literal.toString());
        Type_Mua=TYPE_MUA.NUMBER;
        number_value = Double.parseDouble(v.literal);

    }
    Number_Mua(double d)
    {
        super("" + d);
        Type_Mua=TYPE_MUA.NUMBER;
        number_value = d;

    }
}
