package mua;

public class Value_Mua {
    String literal;
    enum TYPE_MUA
    {
        VALUE, NUMBER, WORD, LIST, BOOL
    }
    TYPE_MUA Type_Mua;
    Value_Mua()
    {
        Type_Mua=TYPE_MUA.VALUE;
    }
    Value_Mua(String l)
    {
        literal= l;
        Type_Mua=TYPE_MUA.VALUE;
    }
    public Number_Mua toNumber()
    {
        if(Type_Mua==TYPE_MUA.NUMBER)
            return new Number_Mua(Double.valueOf(literal));
        else if(Type_Mua==TYPE_MUA.WORD)
            return new Number_Mua(Double.valueOf(literal.substring(1)));
        else
            return new Number_Mua(0);
    }
    public Word_Mua toWord()
    {
        return new Word_Mua(this);
    }

}
