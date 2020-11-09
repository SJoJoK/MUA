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
            return new Number_Mua(Double.parseDouble(literal));
        else if(Type_Mua==TYPE_MUA.WORD)
            return new Number_Mua(Double.parseDouble(literal.substring(1)));
        else
            return new Number_Mua(this);
    }
    public Bool_Mua toBool()
    {
        if(Type_Mua==TYPE_MUA.WORD)
            return new Bool_Mua(literal.substring(1));
        else if(Type_Mua==TYPE_MUA.BOOL)
            return new Bool_Mua(this);
        else
            return new Bool_Mua(true);
    }
    public Word_Mua toWord()
    {
        return new Word_Mua(this);
    }
    public List_Mua toList() { return new List_Mua(this); }
    public Bool_Mua isnumber()
    {
        if(Type_Mua==TYPE_MUA.NUMBER) return new Bool_Mua(true);
        if(Type_Mua==TYPE_MUA.WORD && this.literal.substring(1).matches("(^[0-9]+(.[0-9]+)?$)|(-?[0-9]+(.[0-9]+)?$)"))
            return new Bool_Mua(true);
        return new Bool_Mua(false);

    }
    public Bool_Mua isword()
    {
        if(Type_Mua==TYPE_MUA.WORD) return new Bool_Mua(true);
        return new Bool_Mua(false);
    }
    public Bool_Mua islist()
    {
        if(Type_Mua==TYPE_MUA.LIST) return new Bool_Mua(true);
        return new Bool_Mua(false);
    }
    public Bool_Mua isbool()
    {
        if(Type_Mua==TYPE_MUA.BOOL) return new Bool_Mua(true);
        return new Bool_Mua(false);
    }
    public Bool_Mua isempty()
    {
        if(Type_Mua==TYPE_MUA.WORD&&literal.equals("\"")) return new Bool_Mua(true);
        if(Type_Mua==TYPE_MUA.LIST&&literal.equals("[]")) return new Bool_Mua(true);
        return new Bool_Mua(false);
    }

}
