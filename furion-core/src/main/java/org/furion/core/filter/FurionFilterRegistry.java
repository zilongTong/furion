package org.furion.core.filter;

import org.furion.core.exception.UnknownFurionFilterException;
import org.furion.core.filter.filters.RouteFilter;

public final class FurionFilterRegistry {

    private static FurionFilterRegistry INSTANCE;
    private Node<FurionFilter> headFilter;

    private Node<FurionFilter> routeFilter;

    public Node<FurionFilter> getHeadFilter() {
        return headFilter;
    }

    public void setHeadFilter(Node<FurionFilter> headFilter) {
        this.headFilter = headFilter;
    }

    public static FurionFilterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FurionFilterRegistry(null);
        }
        return INSTANCE;
    }

    private FurionFilterRegistry(Node<FurionFilter> headFilter) {
        this.headFilter = new Node<>(null, null, new RouteFilter());
        this.routeFilter = headFilter;
    }

    /**
     * 删除原Filter对应的实例 TODO
     */
    public void registerFilter(String key, FurionFilter filter) {
        System.out.println("Filter注册 key:" + key + ",filterName:" + filter.getClass().getName());
    }

    public void registerFilter(FurionFilter filter) {
        if (filter == null) {
            throw new UnknownFurionFilterException();
        }
        if (FilterType.PRE.name().equalsIgnoreCase(filter.filterType())) {
            if (headFilter == routeFilter) {
                Node temp = headFilter;
                Node<FurionFilter> node = new Node<>(null, temp, filter);
                temp.prev = node;
                routeFilter = temp;
                headFilter = node;
            } else {
                Node<FurionFilter> index = headFilter;
                do {
                    if (index.item.compareTo(filter) > 0) {
                        index = index.next;
                    } else {

                    }
                } while (index.hasNext());
            }
        }

        if (FilterType.POST.name().equalsIgnoreCase(filter.filterType())) {


            Node temp = headFilter;

//            Node<FurionFilter> node = new Node<>(temp, filter);
//            headFilter = node;
        }
    }


    class Node<T> {

        private Node next;
        private Node prev;

        private T item;

        public Boolean hasNext() {
            if (next != null && next.item != null) {
                return true;
            }
            return false;
        }

        public Node getSuccessor(Node node) {
            return node.next;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node(Node prev, Node next, T item) {
            this.next = next;
            this.prev = prev;
            this.item = item;
        }
    }


}
