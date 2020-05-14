package nl.nl2312.pci.parser.data

fun <T1, T2, T3, R> zip3(s1: Sequence<T1>, s2: Sequence<T2>, s3: Sequence<T3>, f: (T1, T2, T3) -> R): Sequence<R> {
    val iter1 = s1.iterator();
    val iter2 = s2.iterator();
    val iter3 = s3.iterator();
    return sequence {
        while (iter1.hasNext() && iter2.hasNext() && iter3.hasNext()) {
            val e1 = iter1.next();
            val e2 = iter2.next();
            val e3 = iter3.next();
            yield(f(e1, e2, e3))
        }
    }
}