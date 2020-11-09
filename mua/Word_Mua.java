package mua;

public class Word_Mua extends Value_Mua {
    StringBuilder word_value;
    Word_Mua()
    {
        super();
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder();
    }
    Word_Mua(String l)
    {
        super(l);
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder(l.substring(1));
    }
    Word_Mua(String l, String value)
    {
        super(l);
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder(value);
    }
    Word_Mua(String l, double value)
    {
        super(l);
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder(value + "");
    }
    Word_Mua(String l, boolean value)
    {
        super(l);
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder(value + "");
    }
    Word_Mua(Value_Mua v)
    {
        super(v.literal);
        Type_Mua=TYPE_MUA.WORD;
        word_value = new StringBuilder(v.literal.substring(1));
    }

}
