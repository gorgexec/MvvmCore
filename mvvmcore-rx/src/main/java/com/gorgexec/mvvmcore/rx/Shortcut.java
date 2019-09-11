package com.gorgexec.mvvmcore.rx;


import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;

public class Shortcut {

    public static <T1, T2, R> ObservableTransformer<T1, R> zip(BiFunction<T1, T2, R> zipper, ObservableTransformer<T1, T2> then) {

        return upstream -> upstream
                .publish(stream ->
                        Observable.zip(
                                stream,
                                stream.compose(then),
                                zipper
                        ));
    }


    public static <T> ObservableTransformer<T, T> ifThenObservable(Predicate<T> condition, ObservableTransformer<T, T> then) {

        return upstream -> upstream
                .publish(stream ->
                        Observable.merge(
                                stream.filter(condition).compose(then),
                                stream.filter(s -> !condition.test(s))
                        ));
    }

    public static <T> FlowableTransformer<T, T> ifThen(Predicate<T> condition, FlowableTransformer<T, T> then) {

        return upstream -> upstream
                .publish(stream ->
                        Flowable.merge(
                                stream.filter(condition).compose(then),
                                stream.filter(s -> !condition.test(s))
                        ));
    }

    public static <T> ObservableTransformer<T, T> ifElse(Predicate<T> condition, ObservableTransformer<T, T> then, ObservableTransformer<T, T> elseCondition) {

        return upstream -> upstream
                .publish(stream ->
                        Observable.merge(
                                stream.filter(condition).compose(then),
                                stream.filter(s -> !condition.test(s)).compose(elseCondition)
                        ));
    }

    public static <T> FlowableTransformer<T, T> ifElse(Predicate<T> condition, FlowableTransformer<T, T> then, FlowableTransformer<T, T> elseCondition) {

        return upstream -> upstream
                .publish(stream ->
                        Flowable.merge(
                                stream.filter(condition).compose(then),
                                stream.filter(s -> !condition.test(s)).compose(elseCondition)
                        ));
    }


    public static <T> ConditionThen<T> is(Predicate<T> condition) {
        return new ConditionThen<T>(condition);
    }

    public static class ConditionThen<T> {

        private Predicate<T> condition;

        private ConditionThen(Predicate<T> condition) {
            this.condition = condition;
        }

        public ObservableTransformer<T, T> then(ObservableTransformer<T, T> then) {

            return upstream -> upstream
                    .publish(stream ->
                            Observable.merge(
                                    stream.filter(condition).compose(then),
                                    stream.filter(s -> !condition.test(s))
                            ));

        }

    }
}
