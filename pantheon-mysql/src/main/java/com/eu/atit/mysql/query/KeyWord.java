package com.eu.atit.mysql.query;

import com.eu.atit.pantheon.annotation.misc.IgnoreCoverage;

@IgnoreCoverage
abstract class KeyWord {

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
