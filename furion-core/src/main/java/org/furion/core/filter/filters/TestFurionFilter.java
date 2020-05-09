package org.furion.core.filter.filters;

import org.furion.core.filter.FurionFilter;
import org.furion.core.filter.FurionFilterRegistry;

import java.util.LinkedList;
import java.util.List;

public class TestFurionFilter extends FurionFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }


    public static void main(String[] args) {
        FurionFilterRegistry registry = new FurionFilterRegistry();


        FurionFilter furionFilter1 = new FurionFilter() {

            @Override
            public String toString() {
                return "furionFilter1";
            }

            @Override
            public String filterType() {
                return "pre";
            }

            @Override
            public int filterOrder() {
                return 10000;
            }
        };

        FurionFilter furionFilter2 = new FurionFilter() {

            @Override
            public String toString() {
                return "furionFilter2";
            }

            @Override
            public String filterType() {
                return "pre";
            }

            @Override
            public int filterOrder() {
                return 101;
            }
        };


        FurionFilter furionFilter3 = new FurionFilter() {

            @Override
            public String toString() {
                return "furionFilter3";
            }

            @Override
            public String filterType() {
                return "post";
            }

            @Override
            public int filterOrder() {
                return 101;
            }
        };

        FurionFilter furionFilter4 = new FurionFilter() {

            @Override
            public String toString() {
                return "furionFilter4";
            }

            @Override
            public String filterType() {
                return "post";
            }

            @Override
            public int filterOrder() {
                return 0;
            }
        };

        registry.registerFilter(new TestFurionFilter());
        registry.iterator();
        System.out.println(registry.getSize());
        System.out.println("test--------------------");

        registry.registerFilter(furionFilter1);
        registry.iterator();
        System.out.println(registry.getSize());
        System.out.println("1--------------------");

        registry.registerFilter(furionFilter2);
        registry.iterator();
        System.out.println(registry.getSize());
        System.out.println("2--------------------");

        registry.registerFilter(furionFilter3);
        registry.iterator();
        System.out.println(registry.getSize());
        System.out.println("3--------------------");

        registry.registerFilter(furionFilter4);
        registry.iterator();
        System.out.println(registry.getSize());
        System.out.println("4--------------------");
    }

}
