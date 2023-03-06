package com.eu.atit.student.service.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Banana {
    @Test
    void meh() {
        E e = new E(11111);
        E e1 = new E(11121);
        E e2 = new E(11112);
        E e3 = new E(11122);
        E e4 = new E(11113);
        E e5 = new E(11133);

        D d = new D(1111, new ArrayList<>() {{
            add(e);
        }});
        D d1 = new D(1111, new ArrayList<>() {{
            add(e1);
        }});
        D d2 = new D(1112, new ArrayList<>() {{
            add(e2);
        }});
        D d3 = new D(1112, new ArrayList<>() {{
            add(e3);
        }});
        D d4 = new D(1113, new ArrayList<>() {{
            add(e4);
        }});
        D d5 = new D(1113, new ArrayList<>() {{
            add(e5);
        }});

        C c = new C(111, "sss", d);
        C cc = new C(111, "sss", d1);
        A a = new A(1, new B(11, new ArrayList<>() {{
            add(c);
        }}, false), "s");
        A aa = new A(1, new B(11, new ArrayList<>() {{
            add(cc);
        }}, false), "s");
        C c1 = new C(112, "sss2222", d2);

        C cc1 = new C(112, "sss2222", d3);
        A a1 = new A(1, new B(11, new ArrayList<>() {{
            add(c1);
        }}, false), "s");
        A aa1 = new A(1, new B(11, new ArrayList<>() {{
            add(cc1);
        }}, false), "s");
        C c2 = new C(113, "sss3333", d4);
        C cc2 = new C(113, "sss3333", d5);
        A a2 = new A(1, new B(11, new ArrayList<>() {{
            add(c2);
        }}, false), "s");
        A aa2 = new A(1, new B(11, new ArrayList<>() {{
            add(cc2);
        }}, false), "s");
//        A aaa = new A(2, new B(21, new ArrayList<>(), true), "2s");

        D exD = new D(1111, List.of(e, e1));
        D exD2 = new D(1112, List.of(e2, e3));
        D exD3 = new D(1113, List.of(e4, e5));
        C exC = new C(111, "sss", exD);
        C exC2 = new C(112, "sss2222", exD2);
        C exC3 = new C(113, "sss3333", exD3);
        A ex = new A(1, new B(11, List.of(exC, exC2, exC3), false), "s");

        List<A> aaaas = Stream.of(a, aa, a1, aa1, a2, aa2).toList();
        Assertions.assertEquals(List.of(ex), first(aaaas));
    }

    /**
     * uvijek pocetna tocka, moram prvo grupirat sve iz liste pa ih napose pospajat
     * <p>
     * svaki objekt ima funkciju kao pocetnu tocku, ako objekt ima potomka sa listom onda pocetna tocka zove groupA ili groupB
     */
    private List<A> first(List<A> toMerge) {
        Map<Integer, List<A>> aGroupedById = new LinkedHashMap<>();
        for (A ax : toMerge) {
            aGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
        }

        for (Map.Entry<Integer, List<A>> groupedAs : aGroupedById.entrySet()) {
            A groupedA = A.second(groupedAs.getValue());      //todo metoda grupiranja ovisi je li objekt ima listu ili ima potomka sa listom
            aGroupedById.put(groupedAs.getKey(), List.of(groupedA));
        }

        return aGroupedById.values().stream().flatMap(Collection::stream).toList();
    }

    interface ID {
        int getId();
    }

    static class A implements ID {//has a descendant with a list
        private final int id;
        private B hasList;
        private final String s;

        //updating a single element
        static A second(List<A> toGroup) {
            A originalA = toGroup.get(0);
            originalA.hasList = B.first(toGroup.stream().map(v -> v.hasList).toList()).get(0);
            return originalA;
        }

        A(int id, B hasList, String s) {
            this.id = id;
            this.hasList = hasList;
            this.s = s;
        }

        @Override
        public String toString() {
            return "A{" +
                   "id=" + id +
                   ", b=" + hasList +
                   ", s='" + s + '\'' +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            if (id != a.id) return false;
            if (!Objects.equals(hasList, a.hasList)) return false;
            return Objects.equals(s, a.s);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (hasList != null ? hasList.hashCode() : 0);
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    static class B implements ID {
        private final int id;
        private List<C> listOfCs;
        private final boolean b;

//        static B groupedB(List<B> toGroup){
//            Map<Integer, List<B>> bGroupedById = new LinkedHashMap<>();
//            for (B ax : toGroup) {
//                bGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
//            }
//
//            for (Map.Entry<Integer, List<B>> groupedBs : bGroupedById.entrySet()) {
//                B originalB = groupedBs.getValue().get(0);
//                originalB.listOfCs =  C.first(groupedBs.getValue().stream().flatMap(b->b.listOfCs.stream()).collect(Collectors.toList()));
//                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
//            }
//            B originalB = toGroup.get(0);
//            originalB.listOfCs = bGroupedById.values().stream().flatMap(Collection::stream).flatMap(someB -> someB.listOfCs.stream()).collect(Collectors.toList());
//            return originalB;
//        }

        static List<B> first(List<B> toGroup) {
            Map<Integer, List<B>> bGroupedById = new LinkedHashMap<>();
            for (B ax : toGroup) {
                bGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
            }

            for (Map.Entry<Integer, List<B>> groupedBs : bGroupedById.entrySet()) {
                B originalB = B.second(groupedBs.getValue());
                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
            }

            return bGroupedById.values().stream().flatMap(Collection::stream).toList();
        }

        //updating a list
        static B second(List<B> toGroup) {
            B originalB = toGroup.get(0);
            originalB.listOfCs = C.first(toGroup.stream().flatMap(b -> b.listOfCs.stream()).collect(Collectors.toList()));
            return originalB;
        }

        B(int id, List<C> listOfCs, boolean b) {
            this.id = id;
            this.listOfCs = listOfCs;
            this.b = b;
        }

        @Override
        public String toString() {
            return "B{" +
                   "id=" + id +
                   ", cs=" + listOfCs +
                   ", b=" + b +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            B b1 = (B) o;

            if (id != b1.id) return false;
            if (b != b1.b) return false;
            return Objects.equals(listOfCs, b1.listOfCs);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (listOfCs != null ? listOfCs.hashCode() : 0);
            result = 31 * result + (b ? 1 : 0);
            return result;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    static class C implements ID {
        private final int id;
        private final String s;

        private D hasListInside;

        C(int id, String s, D hasListInside) {
            this.id = id;
            this.s = s;
            this.hasListInside = hasListInside;
        }

        static List<C> first(List<C> toMerge) {//todo isto kao mergeA
            Map<Integer, List<C>> cGroupedById = new LinkedHashMap<>();
            for (C ax : toMerge) {
                cGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
            }

            for (Map.Entry<Integer, List<C>> groupedCs : cGroupedById.entrySet()) {
                C groupedC = C.second(groupedCs.getValue());
                cGroupedById.put(groupedCs.getKey(), List.of(groupedC));
            }

            return cGroupedById.values().stream().flatMap(Collection::stream).toList();
        }

        //updating single element
        static C second(List<C> toMerge) {
            C originalC = toMerge.get(0);
            originalC.hasListInside = D.first(toMerge.stream().map(v -> v.hasListInside).toList()).get(0);
            return originalC;
        }

        @Override
        public String toString() {
            return "C{" +
                   "id=" + id +
                   ", s='" + s + '\'' +
                   ", d=" + hasListInside +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C c = (C) o;

            if (id != c.id) return false;
            if (!Objects.equals(s, c.s)) return false;
            return Objects.equals(hasListInside, c.hasListInside);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (s != null ? s.hashCode() : 0);
            result = 31 * result + (hasListInside != null ? hasListInside.hashCode() : 0);
            return result;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    static class D implements ID {
        private final int id;
        private List<E> listOfEs;

        D(int id, List<E> listOfEs) {
            this.id = id;
            this.listOfEs = listOfEs;
        }

//        static D groupedD(List<D> toGroup) {
//            Map<Integer, List<D>> bGroupedById = toGroup.stream().collect(Collectors.groupingBy(someA -> someA.id));
//            for (Map.Entry<Integer, List<D>> groupedBs : bGroupedById.entrySet()) {
//                D originalB = groupedBs.getValue().get(0);
//                originalB.listOfEs = E.mergeE(groupedBs.getValue().stream().flatMap(b -> b.listOfEs.stream()).collect(Collectors.toList()));
//                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
//            }
//            D originalB = toGroup.get(0);
//            originalB.listOfEs = bGroupedById.values().stream().flatMap(Collection::stream).flatMap(someB -> someB.listOfEs.stream()).collect(Collectors.toList());
//            return originalB;
//        }

        static List<D> first(List<D> toGroup) {
            Map<Integer, List<D>> bGroupedById = new LinkedHashMap<>();
            for (D ax : toGroup) {
                bGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
            }

            for (Map.Entry<Integer, List<D>> groupedBs : bGroupedById.entrySet()) {
                D originalB = D.second(groupedBs.getValue());
                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
            }

            return bGroupedById.values().stream().flatMap(Collection::stream).toList();
        }

        //updating a list
        static D second(List<D> toGroup) {
            D originalB = toGroup.get(0);
            originalB.listOfEs = E.first(toGroup.stream().flatMap(b -> b.listOfEs.stream()).collect(Collectors.toList()));
            return originalB;
        }

        @Override
        public String toString() {
            return "D{" +
                   "id=" + id +
                   ", es=" + listOfEs +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            D d = (D) o;

            if (id != d.id) return false;
            return Objects.equals(listOfEs, d.listOfEs);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (listOfEs != null ? listOfEs.hashCode() : 0);
            return result;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    static class E implements ID {
        private final int id;

        E(int id) {
            this.id = id;
        }

//        static List<E> mergeE(List<E> toMerge) {//todo isto kao mergeA i kao mergeC
//            Map<Integer, List<E>> eGroupedById = new LinkedHashMap<>();
//            for (E ax : toMerge) {
//                eGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
//            }
//
//            for (Map.Entry<Integer, List<E>> groupedCs : eGroupedById.entrySet()) {
//                E groupedC = E.groupedE(groupedCs.getValue());
//                eGroupedById.put(groupedCs.getKey(), List.of(groupedC));
//            }
//
//            return eGroupedById.values().stream().flatMap(Collection::stream).toList();
//        }

        static E groupedE(List<E> toMerge) {
            return toMerge.get(0);
        }

        static List<E> first(List<E> toGroup) {
            Map<Integer, List<E>> bGroupedById = new LinkedHashMap<>();
            for (E ax : toGroup) {
                bGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
            }

            for (Map.Entry<Integer, List<E>> groupedBs : bGroupedById.entrySet()) {
                E originalB = E.second(groupedBs.getValue());
                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
            }

            return bGroupedById.values().stream().flatMap(Collection::stream).toList();
        }

        //end of the line
        private static E second(List<E> toMerge) {
            return toMerge.get(0);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            E e = (E) o;

            return id == e.id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return "E{" +
                   "id=" + id +
                   '}';
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
