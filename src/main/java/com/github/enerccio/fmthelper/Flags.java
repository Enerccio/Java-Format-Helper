package com.github.enerccio.fmthelper;

import java.util.DuplicateFormatFlagsException;
import java.util.UnknownFormatFlagsException;

public class Flags {
    private int flags;

    public static final Flags NONE          = new Flags(0);      // ''

    public static final Flags LEFT_JUSTIFY  = new Flags(1<<0);   // '-'
    public static final Flags UPPERCASE     = new Flags(1<<1);   // '^'
    public static final Flags ALTERNATE     = new Flags(1<<2);   // '#'

    public static final Flags PLUS          = new Flags(1<<3);   // '+'
    public static final Flags LEADING_SPACE = new Flags(1<<4);   // ' '
    public static final Flags ZERO_PAD      = new Flags(1<<5);   // '0'
    public static final Flags GROUP         = new Flags(1<<6);   // ','
    public static final Flags PARENTHESES   = new Flags(1<<7);   // '('

    public static final Flags PREVIOUS      = new Flags(1<<8);   // '<'

    private Flags(int f) {
        flags = f;
    }

    public int valueOf() {
        return flags;
    }

    public boolean contains(Flags f) {
        return (flags & f.valueOf()) == f.valueOf();
    }

    public Flags dup() {
        return new Flags(flags);
    }

    public Flags add(Flags f) {
        flags |= f.valueOf();
        return this;
    }

    public Flags remove(Flags f) {
        flags &= ~f.valueOf();
        return this;
    }

    public static Flags parse(String s) {
        char[] ca = s.toCharArray();
        Flags f = new Flags(0);
        for (int i = 0; i < ca.length; i++) {
            Flags v = parse(ca[i]);
            if (f.contains(v))
                throw new DuplicateFormatFlagsException(v.toString());
            f.add(v);
        }
        return f;
    }

    public static Flags parse(char c) {
        switch (c) {
        case '-': return LEFT_JUSTIFY;
        case '#': return ALTERNATE;
        case '+': return PLUS;
        case ' ': return LEADING_SPACE;
        case '0': return ZERO_PAD;
        case ',': return GROUP;
        case '(': return PARENTHESES;
        case '<': return PREVIOUS;
        default:
            throw new UnknownFormatFlagsException(String.valueOf(c));
        }
    }

    public static String toString(Flags f) {
        return f.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (contains(LEFT_JUSTIFY))  sb.append('-');
        if (contains(UPPERCASE))     sb.append('^');
        if (contains(ALTERNATE))     sb.append('#');
        if (contains(PLUS))          sb.append('+');
        if (contains(LEADING_SPACE)) sb.append(' ');
        if (contains(ZERO_PAD))      sb.append('0');
        if (contains(GROUP))         sb.append(',');
        if (contains(PARENTHESES))   sb.append('(');
        if (contains(PREVIOUS))      sb.append('<');
        return sb.toString();
    }
}

