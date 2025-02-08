package org.adonix.postrise;

@FunctionalInterface
public interface ConsumerThrows<T> {
    void accept(T t) throws Exception;
}
