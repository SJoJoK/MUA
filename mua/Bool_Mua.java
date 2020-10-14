package mua;

public class Bool_Mua extends Value_Mua{
    boolean bool_value;
    Bool_Mua(String word_value)
    {
        super(word_value);
        Type_Mua=TYPE_MUA.BOOL;
        if(word_value.equals("true")) bool_value=true;
        else bool_value=false;
    }
    Bool_Mua(Value_Mua v)
    {
        super(v.literal);
        Type_Mua=TYPE_MUA.BOOL;
        if(v.literal.equals("true")) bool_value=true;
        else bool_value=false;
    }

}
