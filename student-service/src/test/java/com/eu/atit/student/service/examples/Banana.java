package com.eu.atit.student.service.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Banana {
    @Test
    void meh(){
        E e = new E(11111);
        E e1 = new E(11121);
        E e2 = new E(11112);
        E e3 = new E(11122);
        E e4 = new E(11113);
        E e5 = new E(11133);

        D d = new D(1111, new ArrayList<>() {{add(e);}});
        D d1 = new D(1111, new ArrayList<>() {{add(e1);}});
        D d2 = new D(1112, new ArrayList<>() {{add(e2);}});
        D d3 = new D(1112, new ArrayList<>() {{add(e3);}});
        D d4 = new D(1113, new ArrayList<>() {{add(e4);}});
        D d5 = new D(1113, new ArrayList<>() {{add(e5);}});

        C c = new C(111, "sss", d);
        C cc = new C(111, "sss", d1);
        A a = new A(1, new B(11,  new ArrayList<>(){{add(c);}}, false), "s");
        A aa = new A(1, new B(11,  new ArrayList<>(){{add(cc);}}, false), "s");
        C c1 = new C(112, "sss2222", d2);

        C cc1 = new C(112, "sss2222", d3);
        A a1 = new A(1, new B(11, new ArrayList<>(){{add(c1);}}, false), "s");
        A aa1 = new A(1, new B(11, new ArrayList<>(){{add(cc1);}}, false), "s");
        C c2 = new C(113, "sss3333", d4);
        C cc2 = new C(113, "sss3333", d5);
        A a2 = new A(1, new B(11,  new ArrayList<>(){{add(c2);}}, false), "s");
        A aa2 = new A(1, new B(11,  new ArrayList<>(){{add(cc2);}}, false), "s");
//        A aaa = new A(2, new B(21, new ArrayList<>(), true), "2s");

        D exD = new D(1111, List.of(e, e1));
        D exD2 = new D(1112, List.of(e2, e3));
        D exD3 = new D(1113, List.of(e4, e5));
        C exC = new C(111, "sss", exD);
        C exC2 = new C(112, "sss2222", exD2);
        C exC3 = new C(113, "sss3333", exD3);
        A ex = new A(1, new B(11, List.of(exC, exC2, exC3), false), "s");

        List<A> aaaas = Stream.of(a, aa, a1, aa1, a2, aa2).toList();
        Assertions.assertEquals(List.of(ex), A.mergeListWithFurtherDescendantsContainingLists(aaaas));
    }

    private void printGroup(List<A> aaaas){
        Map<Integer, List<A>> aGroupedById = aaaas.stream().collect(Collectors.groupingBy(ax -> ax.id));

        for (Map.Entry<Integer, List<A>> groupedAs : aGroupedById.entrySet()) {
            List<B> listOfBsInA = groupedAs.getValue().stream().map(v -> v.hasList).toList();

            Map<Integer, List<B>> bGroupedById = listOfBsInA.stream().collect(Collectors.groupingBy(someA -> someA.id));

            for (Map.Entry<Integer, List<B>> groupedBs : bGroupedById.entrySet()) {

                //B has a LIST of C objects, however C has a CHILD with a LIST also
                //we need to deduplicate all C objects because of this by iterating them and merging the lists in the child object
                Map<Integer, List<C>> cGroupedById = groupedBs.getValue().stream().flatMap(someB -> someB.listOfCs.stream()).collect(Collectors.groupingBy(someC -> someC.id));

                Map<Integer, List<C>> cGrouped = new HashMap<>();
                for (B b : groupedBs.getValue()) {
                    for (C listOfC : b.listOfCs) {
                        if (cGrouped.containsKey(listOfC.id)){
                            cGrouped.get(listOfC.id).add(listOfC);
                        } else {
                            cGrouped.put(listOfC.id, new ArrayList<>(){{add(listOfC);}});
                        }

                    }
                }


                for (Map.Entry<Integer, List<C>> groupedCs : cGroupedById.entrySet()) {
                    //these objects have a LIST in them AND are at the bottom of the chain
                    // so their lists just need to be merged
                    List<D> listOfDsInC = groupedCs.getValue().stream().map(v->v.hasListInside).toList();

                    //take one of the objects with a list to merge the lists into
                    D originalD = listOfDsInC.get(0);

                    //linkedHashSet so the merged list has no duplicates
                    LinkedHashSet<E> mergedListOfEs = new LinkedHashSet<>(originalD.listOfEs);
                    listOfDsInC.forEach(dx-> mergedListOfEs.addAll(dx.listOfEs));
                    originalD.listOfEs = new ArrayList<>(mergedListOfEs);

                    //take the first object from the group and update the list object it contains
                    C originalC = groupedCs.getValue().get(0);
                    originalC.hasListInside = originalD;

                    //reduce the group to the updated object
                    cGroupedById.put(groupedCs.getKey(), List.of(originalC));
                }

                List<C> deduplicateListOfC = cGroupedById.values().stream().flatMap(Collection::stream).toList();

                //take the first object from the group and update the list object it contains
                B originalB = groupedBs.getValue().get(0);
                originalB.listOfCs = deduplicateListOfC;
                //reduce the group to the updated object
                bGroupedById.put(groupedBs.getKey(), List.of(originalB));
            }

            B originalB = listOfBsInA.get(0);
            LinkedHashSet<C> valueToSet = new LinkedHashSet<>(originalB.listOfCs);

//            bGroupedById.values().stream().flatMap(Collection::stream).forEach(bx-> valueToSet.addAll(bx.listOfCs));

            originalB.listOfCs = bGroupedById.values().stream().flatMap(Collection::stream).flatMap(someB -> someB.listOfCs.stream()).collect(Collectors.toList());
            A e122 = groupedAs.getValue().get(0);
            e122.hasList = originalB;
            aGroupedById.put(groupedAs.getKey(), List.of(e122));
        }

        System.out.println(aGroupedById.get(1).get(0));
    }

    static class A {//has a descendant with a list
        private final int id;
        private B hasList;
        private final String s;

        static List<A> mergeListWithFurtherDescendantsContainingLists(List<A>aToGroup){//entry point always
            Map<Integer, List<A>> aGroupedById = new LinkedHashMap<>();
            for (A ax : aToGroup) {
                aGroupedById.computeIfAbsent(ax.id, k -> new ArrayList<>()).add(ax);
            }
            for (Map.Entry<Integer, List<A>> groupedAs : aGroupedById.entrySet()) {
                A originalA = groupedAs.getValue().get(0);
                A newA = new A(originalA.id, originalA.hasList, originalA.s);
                newA.hasList = B.groupElementWithListInIt(groupedAs.getValue().stream().map(v -> v.hasList).toList());
                aGroupedById.put(groupedAs.getKey(), List.of(newA));
            }

            return aGroupedById.values().stream().flatMap(Collection::stream).toList();
        }

        A(int id, B hasList, String s){
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
    }
    static class B {
        private final int id;
        private List<C> listOfCs;
        private final boolean b;

        static B groupElementWithListInIt(List<B> listOfBsInA){
            Map<Integer, List<B>> bGroupedById = listOfBsInA.stream().collect(Collectors.groupingBy(someA -> someA.id));
            for (Map.Entry<Integer, List<B>> groupedBs : bGroupedById.entrySet()) {
                B originalB = groupedBs.getValue().get(0);
                bGroupedById.put(groupedBs.getKey(), List.of(new B(originalB.id, C.mergeListWithFurtherDescendantsContainingLists(groupedBs), originalB.b)));
            }
            B originalB = listOfBsInA.get(0);
            B newB = new B(originalB.id, originalB.listOfCs, originalB.b);
            newB.listOfCs = bGroupedById.values().stream().flatMap(Collection::stream).flatMap(someB -> someB.listOfCs.stream()).collect(Collectors.toList());
            return newB;
        }

        B(int id, List<C> listOfCs, boolean b){
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
    }
    static class C {
        private final int id;
        private final String s;

        private D hasListInside;

        C(int id, String s, D hasListInside){
            this.id = id;
            this.s = s;
            this.hasListInside = hasListInside;
        }

        static List<C> mergeListWithFurtherDescendantsContainingLists(Map.Entry<Integer, List<B>> groupedBs){
            Map<Integer, List<C>> cGroupedById = new LinkedHashMap<>();
            for (B someB : groupedBs.getValue()) {
                for (C someC : someB.listOfCs) {
                    List<C> mappedList;
                    if (cGroupedById.containsKey(someC.id)){
                        mappedList = cGroupedById.get(someC.id);
                    } else {
                        mappedList = new ArrayList<>();
                        cGroupedById.put(someC.id, mappedList);
                    }

                    mappedList.add(someC);
                }
            }

            for (Map.Entry<Integer, List<C>> groupedCs : cGroupedById.entrySet()) {

                //take the first object from the group and update the list object it contains
                C originalC = groupedCs.getValue().get(0);
                C newC = new C(originalC.id,  originalC.s, originalC.hasListInside);
                newC.hasListInside = D.groupElementWithListInIt(groupedCs.getValue().stream().map(v -> v.hasListInside).toList());

                //reduce the group to the updated object
                cGroupedById.put(groupedCs.getKey(), List.of(newC));
            }

            return cGroupedById.values().stream().flatMap(Collection::stream).toList();
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
    }
    static class D {
        private final int id;
        private List<E> listOfEs;

        D(int id, List<E> listOfEs){
            this.id = id;
            this.listOfEs = listOfEs;
        }

        public static D groupElementWithListInIt(List<D> listOfDsInC) {
            Map<Integer, List<D>> dGroupedById = new LinkedHashMap<>();
            for (D someD : listOfDsInC) {
                dGroupedById.computeIfAbsent(someD.id, k -> new ArrayList<>()).add(someD);
            }

            for (Map.Entry<Integer, List<D>> groupedDs : dGroupedById.entrySet()) {
                D originalD = groupedDs.getValue().get(0);
                dGroupedById.put(groupedDs.getKey(), List.of(new D(originalD.id, E.mergeListNoFurtherDescendantsWithList(groupedDs))));
            }
            D originalD = listOfDsInC.get(0);
            D newD = new D(originalD.id, originalD.listOfEs);
            newD.listOfEs = dGroupedById.values().stream().flatMap(Collection::stream).flatMap(someB -> someB.listOfEs.stream()).collect(Collectors.toList());
            return newD;
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
    }
    static class E {
        private final int id;
        E(int id){
            this.id = id;
        }

        public static List<E> mergeListNoFurtherDescendantsWithList(Map.Entry<Integer, List<D>> groupedDs) { //always end of the line
            HashSet<E> list = new LinkedHashSet<>();
            for (D x : groupedDs.getValue()) {
                list.addAll(x.listOfEs);
            }
            return new ArrayList<>(list);
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
    }
}
