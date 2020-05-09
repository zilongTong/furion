package org.furion.core.filter;


import com.sun.tools.javac.util.Assert;
import org.furion.core.exception.IllegalFilterUninstallException;
import org.furion.core.exception.UnknownFurionFilterException;
import org.furion.core.filter.filters.RouteFilter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.furion.core.constants.Constants.ROUTE;


public final class FurionFilterRegistry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ReentrantLock lock = new ReentrantLock();

//    private static volatile FurionFilterRegistry INSTANCE;

    transient int size = 0;

    private transient Node<FurionFilter> headFilter;

    private transient Node<FurionFilter> routeFilter;

    private transient Node<FurionFilter> lastFilter;

//    private final static Object lock = new Object();

    public FurionFilter getHeadFilter() {
        return headFilter.item;
    }


    public Boolean hasNext(FurionFilter furionFilter) {

        if (furionFilter == (headFilter.item)) {
            if (headFilter.next.item != null) {
                return true;
            }
        }
        while (headFilter.hasNext()) {
            Node<FurionFilter> index = headFilter.next;
            if (furionFilter == (index.item)) {
                if (index.next.item != null)
                    return true;
            }
        }
        return false;
    }


    public FurionFilter getSuccessor(FurionFilter furionFilter) {

        if (furionFilter == (headFilter.item)) {
            return headFilter.next.item;
        }
        while (headFilter.hasNext()) {
            Node<FurionFilter> index = headFilter.next;
            if (furionFilter == (index.item)) {
                return index.next.item;
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }


    public void setHeadFilter(Node<FurionFilter> headFilter) {
        this.headFilter = headFilter;
    }

//    public static FurionFilterRegistry getInstance() {
//        if (INSTANCE == null) {
//            synchronized (FurionFilterRegistry.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new FurionFilterRegistry();
//                }
//            }
//        }
//        return INSTANCE;
//    }

    public FurionFilterRegistry() {
        this.headFilter = new Node<>(null, null, new RouteFilter());
        this.routeFilter = headFilter;
        this.lastFilter = headFilter;
        size++;
    }

    /**
     * 删除原Filter对应的实例 TODO
     * Filter 应为单例
     */
    public void registerFilter(String className, FurionFilter filter) {
        System.out.println("Filter注册 className:" + className + ",filterName:" + filter.getClass().getName());
    }

    public void removeFilter(String filterName) {
        lock.lock();
        if (filterName.equalsIgnoreCase("RouteFilter")) {
            throw new IllegalFilterUninstallException();
        }


        if (filterName.equalsIgnoreCase(headFilter.item.getClass().getName())) {
            headFilter = headFilter.next;
            headFilter.next.prev = null;
            return;
        }

        while (headFilter.hasNext()) {
            headFilter = headFilter.next;
            if (filterName.equalsIgnoreCase(headFilter.item.getClass().getName())) {
                headFilter.prev = headFilter.next;
                headFilter.next.prev = headFilter;
                return;
            }
        }
        lock.unlock();
    }

    public void registerFilter(FurionFilter filter) {

        lock.lock();
        if (filter == null) {
            throw new UnknownFurionFilterException();
        }
        if (FilterType.PRE.name().equalsIgnoreCase(filter.filterType())) {
            if (headFilter == routeFilter) {
                linkBefore(filter, headFilter);
            } else {
                insertPreNode(filter, headFilter);
            }
        }
        if (FilterType.POST.name().equalsIgnoreCase(filter.filterType())) {
            insertPostNode(filter, routeFilter);
        }
        lock.unlock();
    }

    public void iterator() {
        Node<FurionFilter> current = headFilter;
        while (current != null) {
            System.out.println(current.item.toString());
            current = current.next;
        }
    }


    private void insertPreNode(FurionFilter filter, Node<FurionFilter> headFilter) {
        while (headFilter.item != null && headFilter.hasNext()) {
            if (headFilter.item.compareTo(filter) <= 0 || headFilter.next.item.filterType().equalsIgnoreCase(ROUTE)) {
                linkBefore(filter, headFilter);
                return;
            }
            headFilter = headFilter.next;
        }
    }

    private void insertPostNode(FurionFilter filter, Node<FurionFilter> routeFilter) {
        if (!routeFilter.hasNext()) {
            routeFilter.next = new Node<>(routeFilter, null, filter);
            verifyNode();
            return;
        }
        routeFilter = routeFilter.next;
        while (routeFilter != null) {
            if (routeFilter.item.compareTo(filter) <= 0) {
                linkBefore(filter, routeFilter);
                return;
            }
            routeFilter = routeFilter.next;
            if (routeFilter == null) {
                lastFilter.next = new Node<>(lastFilter, null, filter);
            }
        }
    }

    /**
     * Inserts element e before non-null Node succ.
     */
    void linkBefore(FurionFilter e, Node<FurionFilter> succ) {
        // assert succ != null;
        final Node<FurionFilter> pred = succ.prev;
        final Node<FurionFilter> newNode = new Node<>(pred, succ, e);
        succ.prev = newNode;
        if (pred == null)
            headFilter = newNode;
        else
            pred.next = newNode;
        size++;
        verifyNode();
    }

    private void verifyNode() {
        Node<FurionFilter> current = headFilter;
        while (current != null) {
            if (current.item.filterType().equalsIgnoreCase(ROUTE)) {
                routeFilter = current;
            }
            if (!current.hasNext()) {
                lastFilter = current;
            }
            current = current.next;

        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(FurionFilter o) {
        if (o == null) {
            for (Node x = headFilter; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node x = headFilter; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    FurionFilter unlink(Node<FurionFilter> x) {
        // assert x != null;
        final FurionFilter element = x.item;
        final Node<FurionFilter> next = x.next;
        final Node<FurionFilter> prev = x.prev;

        if (prev == null) {
            headFilter = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            lastFilter = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        return element;
    }


    private static class Node<T> {

        /**
         * 非static inner class
         * synthetic parent this$0
         * 访问父对象的非私有成员;
         **/
        private Node<T> next;
        private Node<T> prev;

        private T item;

        //标记
//        private String tag;

        public Boolean hasNext() {
            if (next != null && next.item != null) {
                return true;
            }
            return false;
        }


//        public String getTag() {
//            return tag;
//        }
//
//        public void setTag(String tag) {
//            this.tag = tag;
//        }

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
//            Assert.checkNull(item);
            this.next = next;
            this.prev = prev;
            this.item = item;
//            this.tag = tag;
        }
    }


}
