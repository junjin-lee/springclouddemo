package com.rooney.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;

import rx.Observable;
import rx.Observer;


// HystrixCommand或HystrixObservableCommand，简单用法见上面例子。两者主要区别是：
//
// 前者的命令逻辑写在run()；后者的命令逻辑写在construct()
// 前者的run()是由新创建的线程执行；后者的construct()是由调用程序线程执行
// 前者一个实例只能向调用程序发送（emit）单条数据，比如上面例子中run()只能返回一个String结果；
// 后者一个实例可以顺序发送多条数据，比如demo中顺序调用多个onNext()，便实现了向调用程序发送多条数据，甚至还能发送一个范围的数据集，下面举例说明

public class ObservableCommandNumbersToWords extends HystrixObservableCommand<ObservableCommandNumbersToWords.NumberWord> {
    private final List<Integer> numbers;

    // in the real world you'd probably want to replace this very simple code by using ICU or similar
    static Map<Integer, String> dict = new HashMap<>(11);
    static {
        dict.put(0, "zero");
        dict.put(1, "one");
        dict.put(2, "two");
        dict.put(3, "three");
        dict.put(4, "four");
        dict.put(5, "five");
        dict.put(6, "six");
        dict.put(7, "seven");
        dict.put(8, "eight");
        dict.put(9, "nine");
        dict.put(10, "ten");
    }

    public ObservableCommandNumbersToWords(final List<Integer> numbers) {
        super(HystrixCommandGroupKey.Factory.asKey("hoho"));
        this.numbers = numbers;
    }


    @Override
    protected Observable<NumberWord> construct() {
        System.out.println("construct! thread:" + Thread.currentThread().getName());
        return Observable.from(numbers).map(number -> {
            System.out.println("call! thread:" + Thread.currentThread().getName());
            return new NumberWord(number, dict.get(number));
        });

    }

    static class NumberWord {
        private final Integer number;
        private final String word;

        public NumberWord(final Integer number, final String word) {
            super();
            this.number = number;
            this.word = word;
        }

        public Integer getNumber() {
            return number;
        }

        public String getWord() {
            return word;
        }
    }


    public static void main(String[] args) throws Exception {

        List<Integer> in = new LinkedList<>();
        in.add(2);
        in.add(3);

        Observable<NumberWord> hotObservable = new ObservableCommandNumbersToWords(in).observe();
        hotObservable.subscribe(new Observer<NumberWord>() {

            // 先执行onNext再执行onCompleted
            // @Override
            @Override
            public void onCompleted() {
                System.out.println("hotObservable of ObservableCommand completed");
            }

            // @Override
            @Override
            public void onError(Throwable e) {
                System.out.println("hotObservable of ObservableCommand error");
                e.printStackTrace();
            }

            // @Override
            @Override
            public void onNext(NumberWord v) {
                System.out.println("hotObservable of ObservableCommand onNext: " + v.getWord());
            }

        });


    }

}
