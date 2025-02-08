package org.adonix.postrise;

@FunctionalInterface
interface ConsumerThrows<T> {
    void accept(T t) throws Exception;
}
