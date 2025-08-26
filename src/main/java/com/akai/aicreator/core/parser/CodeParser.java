package com.akai.aicreator.core.parser;

public interface CodeParser<T> {
    T parseCode(String codeContent);
}
