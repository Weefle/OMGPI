package tk.omgpi.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * OMGStream is a stream that can be randomized and collected easily.
 */
public class OMGStream<E> implements Stream<E> {
    public Stream<E> s;

    public OMGStream(Collection<E> c) {
        s = c.stream();
    }

    public OMGStream(Stream<E> c) {
        s = c;
    }

    public OMGStream<E> filter(Predicate<? super E> predicate) {
        s = s.filter(predicate);
        return this;
    }

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    public OMGStream<E> randomize() {
        s = s.sorted((o1, o2) -> new Random().nextInt(3) - 1);
        return this;
    }

    public <R> OMGStream<R> map(Function<? super E, ? extends R> mapper) {
        return new OMGStream<>(s.map(mapper));
    }

    public IntStream mapToInt(ToIntFunction<? super E> mapper) {
        return s.mapToInt(mapper);
    }

    public LongStream mapToLong(ToLongFunction<? super E> mapper) {
        return s.mapToLong(mapper);
    }

    public DoubleStream mapToDouble(ToDoubleFunction<? super E> mapper) {
        return s.mapToDouble(mapper);
    }

    public <R> OMGStream<R> flatMap(Function<? super E, ? extends Stream<? extends R>> mapper) {
        return new OMGStream<>(s.flatMap(mapper));
    }

    public IntStream flatMapToInt(Function<? super E, ? extends IntStream> mapper) {
        return s.flatMapToInt(mapper);
    }

    public LongStream flatMapToLong(Function<? super E, ? extends LongStream> mapper) {
        return s.flatMapToLong(mapper);
    }

    public DoubleStream flatMapToDouble(Function<? super E, ? extends DoubleStream> mapper) {
        return s.flatMapToDouble(mapper);
    }

    public OMGStream<E> distinct() {
        s = s.distinct();
        return this;
    }

    public OMGStream<E> sorted() {
        s = s.sorted();
        return this;
    }

    public OMGStream<E> sorted(Comparator<? super E> comparator) {
        s = s.sorted(comparator);
        return this;
    }

    public OMGStream<E> peek(Consumer<? super E> action) {
        s = s.peek(action);
        return this;
    }

    public OMGStream<E> limit(long maxSize) {
        s = s.limit(maxSize);
        return this;
    }

    public OMGStream<E> skip(long n) {
        s = s.skip(n);
        return this;
    }

    public void forEach(Consumer<? super E> action) {
        s.forEach(action);
    }

    public void forEachOrdered(Consumer<? super E> action) {
        s.forEachOrdered(action);
    }

    public Object[] toArray() {
        return s.toArray();
    }

    public <A> A[] toArray(IntFunction<A[]> generator) {
        return s.toArray(generator);
    }

    public E reduce(E identity, BinaryOperator<E> accumulator) {
        return s.reduce(identity, accumulator);
    }

    public Optional<E> reduce(BinaryOperator<E> accumulator) {
        return s.reduce(accumulator);
    }

    public <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator, BinaryOperator<U> combiner) {
        return s.reduce(identity, accumulator, combiner);
    }

    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super E> accumulator, BiConsumer<R, R> combiner) {
        return s.collect(supplier, accumulator, combiner);
    }

    public <R, A> R collect(Collector<? super E, A, R> collector) {
        return s.collect(collector);
    }

    public OMGList<E> collect() {
        return new OMGList<>(collect(Collectors.toList()));
    }

    public Optional<E> min(Comparator<? super E> comparator) {
        return s.min(comparator);
    }

    public Optional<E> max(Comparator<? super E> comparator) {
        return s.max(comparator);
    }

    public long count() {
        return s.count();
    }

    public boolean anyMatch(Predicate<? super E> predicate) {
        return s.anyMatch(predicate);
    }

    public boolean allMatch(Predicate<? super E> predicate) {
        return s.allMatch(predicate);
    }

    public boolean noneMatch(Predicate<? super E> predicate) {
        return s.noneMatch(predicate);
    }

    public Optional<E> findFirst() {
        return s.findFirst();
    }

    public Optional<E> findAny() {
        return s.findAny();
    }

    public Iterator<E> iterator() {
        return s.iterator();
    }

    public Spliterator<E> spliterator() {
        return s.spliterator();
    }

    public boolean isParallel() {
        return s.isParallel();
    }

    public OMGStream<E> sequential() {
        s = s.sequential();
        return this;
    }

    public OMGStream<E> parallel() {
        s = s.parallel();
        return this;
    }

    public OMGStream<E> unordered() {
        s = s.unordered();
        return this;
    }

    public OMGStream<E> onClose(Runnable closeHandler) {
        s = s.onClose(closeHandler);
        return this;
    }

    public void close() {
        s.close();
    }
}
